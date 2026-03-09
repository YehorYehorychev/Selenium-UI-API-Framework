package yehorychev.selenium.hooks;

import com.yehorychev.selenium.helpers.AuthHelper;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import yehorychev.selenium.context.DriverContext;
import yehorychev.selenium.context.ScenarioContext;

import java.util.Map;

/**
 * Authentication setup hook — logs in via API before @authenticated scenarios.
 *
 * Responsibilities:
 *   - @Before("@authenticated", order=2) — POST /api/auth/login, inject token+cookies into WebDriver
 *   - @After("@authenticated",  order=3) — clear browser cookies, remove authToken from ScenarioContext
 *
 * Hook order:
 *   Before "not @api"      order=0 — DriverHooks.setUp        (driver starts)
 *   Before "@api"          order=1 — ApiHooks.setUpApi         (if also @api)
 *   Before "@authenticated" order=2 — AuthHooks.setUpAuthentication (this class)
 *   After  "@authenticated" order=3 — AuthHooks.tearDownAuthentication (this class)
 *   After  "@api"          order=5 — ApiHooks.tearDownApi
 *   After  "not @api"      order=10 — DriverHooks.captureFailure
 *   After  "not @api"      order=0  — DriverHooks.tearDown
 *
 * PicoContainer injects DriverContext and ScenarioContext per-scenario — no static state.
 */
public class AuthHooks {

    private static final Logger log = new Logger(AuthHooks.class);

    private final DriverContext driverContext;
    private final ScenarioContext scenarioContext;

    /**
     * PicoContainer constructor injection — do NOT add a no-arg constructor.
     *
     * @param driverContext   scenario-scoped driver context
     * @param scenarioContext scenario-scoped state store
     */
    public AuthHooks(DriverContext driverContext, ScenarioContext scenarioContext) {
        this.driverContext = driverContext;
        this.scenarioContext = scenarioContext;
    }

    // ── Before ───────────────────────────────────────────────────────────────

    /**
     * Fires before each @authenticated scenario.
     * Authenticates via the REST API and injects the session token/cookies into
     * the WebDriver — no UI login form required.
     * Stores the token in ScenarioContext under key "authToken" for use in steps.
     *
     * @param scenario Cucumber Scenario metadata
     */
    @Before(value = "@authenticated", order = 2)
    public void setUpAuthentication(Scenario scenario) {
        log.step("Setting up authenticated session for scenario: " + scenario.getName());

        if (!driverContext.isReady()) {
            throw new IllegalStateException(
                    "DriverContext is not ready — AuthHooks requires DriverHooks to run first (order 0)");
        }

        try {
            // Authenticate via GraphQL signIn and inject session cookies into WebDriver
            Map<String, String> authData = AuthHelper.loginViaApi();
            AuthHelper.injectAuthIntoDriver(driverContext.getDriver(), authData);

            // Persist the signedIn marker in ScenarioContext for use in step definitions
            scenarioContext.set("authToken", authData.get(AuthHelper.KEY_SIGNED_IN));
            log.debug("Auth session marker stored in ScenarioContext");

            log.info("Authenticated session established for: " + scenario.getName());
        } catch (Exception e) {
            log.warn("Authentication setup failed: " + e.getMessage());
            throw e;
        }
    }

    // ── After ────────────────────────────────────────────────────────────────

    /**
     * Fires after each @authenticated scenario.
     * Clears browser cookies and removes authToken from ScenarioContext
     * so no auth state leaks between scenarios.
     *
     * @param scenario Cucumber Scenario metadata
     */
    @After(value = "@authenticated", order = 3)
    public void tearDownAuthentication(Scenario scenario) {
        log.step("■ [Auth] Clearing authenticated session for: " + scenario.getName());

        try {
            if (driverContext.isReady()) {
                driverContext.getDriver().manage().deleteAllCookies();
                log.debug("Browser cookies cleared");
            }
            scenarioContext.remove("authToken");
        } catch (Exception e) {
            log.warn("Failed to clear auth session: " + e.getMessage());
        }
    }
}
