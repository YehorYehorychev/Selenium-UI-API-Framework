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
 * API lifecycle hooks — manages global RestAssured state for {@code @api}-tagged scenarios.
 *
 * <p>Only scenarios tagged with {@code @api} trigger these hooks, so UI-only
 * scenarios are not affected.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>{@code @Before("@api", order = 1)} — configure RestAssured global baseline
 *       (base URI, logging filters) before the scenario runs</li>
 *   <li>{@code @After("@api", order = 5)}  — reset RestAssured global state so
 *       subsequent scenarios start from a clean slate</li>
 * </ul>
 *
 * <p>Note: Per-scenario {@link yehorychev.selenium.context.ApiContext} is managed
 * independently by PicoContainer — inject it directly in step definitions.
 *
 * <p>Hook execution order relative to {@link DriverHooks}:
 * <ol>
 *   <li>{@code Before "not @api" order=0} — DriverHooks.setUp — driver starts (UI only)</li>
 *   <li>{@code Before "@api" order=1} — {@link #setUpApi(Scenario)} — RestAssured configured</li>
 *   <li>scenario runs</li>
 *   <li>{@code After "@api" order=5}  — {@link #tearDownApi(Scenario)} — RestAssured reset</li>
 *   <li>{@code After "not @api" order=10} — DriverHooks.captureFailure (UI only)</li>
 *   <li>{@code After "not @api" order=0}  — DriverHooks.tearDown — driver quits (UI only)</li>
 * </ol>
 *
 * <p>No constructor injection required — this hook has no scenario-scoped dependencies.
 */
public class ApiHooks {

    private static final Logger log = new Logger(ApiHooks.class);

    // ── Before ───────────────────────────────────────────────────────────────

    /**
     * Fires before each {@code @api}-tagged scenario.
     *
     * <p>Sets the RestAssured global baseline (base URI, logging filters) so every
     * request in the scenario uses the correct environment configuration without
     * requiring per-request setup in step definitions.
     *
     * @param scenario Cucumber {@link Scenario} metadata
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
     * Fires after each {@code @api}-tagged scenario.
     *
     * <p>Resets all global RestAssured configuration (base URI, filters, auth) so
     * subsequent scenarios start from a clean state — prevents cross-scenario pollution.
     *
     * <p>Runs at {@code order = 5} so it executes after step definitions complete
     * but before any driver teardown.
     *
     * @param scenario Cucumber {@link Scenario} metadata
     */
    @After(value = "@api", order = 5)
    public void tearDownApi(Scenario scenario) {
        log.step("■ [API] Resetting RestAssured after scenario: "
                + scenario.getName() + " — " + scenario.getStatus());
        RestAssured.reset();
    }
}
