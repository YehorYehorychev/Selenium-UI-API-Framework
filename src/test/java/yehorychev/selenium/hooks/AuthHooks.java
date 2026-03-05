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
 * Authentication setup hook — analogous to the {@code authenticatedPage} auto-fixture
 * in Playwright that calls {@code loginViaAPI()} before authenticated scenarios.
 *
 * <p>Only scenarios tagged with {@code @authenticated} trigger this hook.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>{@code @Before("@authenticated", order = 2)} — calls {@link AuthHelper#loginAndInject(org.openqa.selenium.WebDriver)}
 *       to authenticate via the REST API and inject the session token/cookies
 *       into the active {@link org.openqa.selenium.WebDriver}</li>
 *   <li>Stores the auth token in {@link ScenarioContext} under key {@code "authToken"}
 *       so step definitions can access it without re-authenticating</li>
 * </ul>
 *
 * <p>Hook execution order:
 * <ol>
 *   <li>{@code Before order=0} — DriverHooks.setUp — driver starts</li>
 *   <li>{@code Before order=1} — ApiHooks.setUpApi — API client configured (if {@code @api})</li>
 *   <li>{@code Before order=2} — {@link #setUpAuthentication(Scenario)} — login via API + inject cookies</li>
 *   <li>scenario runs as an authenticated user</li>
 *   <li>{@code After order=5}  — ApiHooks.tearDownApi — API state reset</li>
 *   <li>{@code After order=10} — DriverHooks.captureFailure</li>
 *   <li>{@code After order=0}  — DriverHooks.tearDown — driver quits</li>
 * </ol>
 *
 * <p>PicoContainer injects {@link DriverContext} and {@link ScenarioContext}
 * per-scenario — no static state.
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
     * Fires before each {@code @authenticated}-tagged scenario.
     *
     * <p>Authenticates via the REST API and injects the resulting session
     * token / cookies into the WebDriver so that subsequent page navigations
     * are already authenticated — no UI login form required.
     *
     * <p>The auth token is stored in {@link ScenarioContext} under key
     * {@code "authToken"} for use in step definitions.
     *
     * @param scenario Cucumber {@link Scenario} metadata
     */
    @Before(value = "@authenticated", order = 2)
    public void setUpAuthentication(Scenario scenario) {
        log.step("Setting up authenticated session for scenario: " + scenario.getName());

        if (!driverContext.isReady()) {
            throw new IllegalStateException(
                    "DriverContext is not ready — AuthHooks requires DriverHooks to run first (order 0)");
        }

        try {
            // Authenticate via REST API and inject cookies/token into WebDriver
            Map<String, String> authData = AuthHelper.loginViaApi();
            AuthHelper.injectAuthIntoDriver(driverContext.getDriver(), authData);

            // Persist token in ScenarioContext for use in step definitions
            String token = authData.get("token");
            if (token != null) {
                scenarioContext.set("authToken", token);
                log.debug("Auth token stored in ScenarioContext");
            }

            log.info("Authenticated session established for: " + scenario.getName());
        } catch (Exception e) {
            log.warn("Authentication setup failed: " + e.getMessage());
            throw e;
        }
    }

    // ── After ────────────────────────────────────────────────────────────────

    /**
     * Fires after each {@code @authenticated}-tagged scenario.
     *
     * <p>Clears browser cookies and removes the auth token from {@link ScenarioContext}
     * to ensure no auth state leaks between scenarios.
     *
     * <p>Runs at {@code order = 3} — after steps but well before driver quit (order 0).
     *
     * @param scenario Cucumber {@link Scenario} metadata
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

