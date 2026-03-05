package yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

/**
 * API lifecycle hooks — manages global RestAssured state for @api-tagged scenarios.
 *
 * Responsibilities:
 *   - @Before("@api", order=1) — configure RestAssured base URI and logging filters
 *   - @After("@api",  order=5) — reset RestAssured global state between scenarios
 *
 * Per-scenario ApiContext is managed by PicoContainer independently —
 * inject it directly in step definitions, not here.
 *
 * Hook order relative to DriverHooks:
 *   Before "not @api" order=0 — DriverHooks.setUp       (UI only)
 *   Before "@api"     order=1 — ApiHooks.setUpApi        (this class)
 *   After  "@api"     order=5 — ApiHooks.tearDownApi     (this class)
 *   After  "not @api" order=10 — DriverHooks.captureFailure (UI only)
 *   After  "not @api" order=0  — DriverHooks.tearDown    (UI only)
 */
public class ApiHooks {

    private static final Logger log = new Logger(ApiHooks.class);

    // ── Before ───────────────────────────────────────────────────────────────

    /**
     * Fires before each @api-tagged scenario.
     * Sets RestAssured global baseline (base URI, logging) so every request
     * uses the correct environment config without per-request setup in steps.
     *
     * @param scenario Cucumber Scenario metadata
     */
    @Before(value = "@api", order = 1)
    public void setUpApi(Scenario scenario) {
        log.step("▶ [API] Setting up RestAssured for scenario: " + scenario.getName());

        RestAssured.baseURI = TestConfig.API_BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Verbose request/response logging when running non-headless (local debug)
        if (!TestConfig.HEADLESS) {
            RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        }

        log.info("RestAssured ready — baseURI: " + TestConfig.API_BASE_URL);
    }

    // ── After ─────────────────────────────────────────────────────────────────

    /**
     * Fires after each @api-tagged scenario.
     * Resets all global RestAssured config (base URI, filters, auth)
     * to prevent cross-scenario pollution.
     *
     * @param scenario Cucumber Scenario metadata
     */
    @After(value = "@api", order = 5)
    public void tearDownApi(Scenario scenario) {
        log.step("■ [API] Resetting RestAssured after scenario: "
                + scenario.getName() + " — " + scenario.getStatus());
        RestAssured.reset();
    }
}
