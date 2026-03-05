package yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import yehorychev.selenium.context.DriverContext;

/**
 * WebDriver lifecycle hooks — analogous to the {@code browser} / {@code page} auto-fixtures
 * in Playwright that set up and tear down a browser for every scenario.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>{@code @Before(order = 0)} — initialize WebDriver via {@link DriverContext}</li>
 *   <li>{@code @After(order = 10)} — capture a full-page AShot screenshot and attach it
 *       to the Allure report when the scenario has failed (analogous to the
 *       {@code screenshotOnFailure} auto-fixture)</li>
 *   <li>{@code @After(order = 0)} — quit WebDriver and release resources</li>
 * </ul>
 *
 * <p>Hook execution order (lower number fires first):
 * <ol>
 *   <li>{@code Before "not @api" order=0}  — {@link #setUp(Scenario)} — driver starts</li>
 *   <li>scenario runs</li>
 *   <li>{@code After "not @api" order=10} — {@link #captureFailureScreenshot(Scenario)} — screenshot if failed</li>
 *   <li>{@code After "not @api" order=0}  — {@link #tearDown(Scenario)} — driver quits</li>
 * </ol>
 *
 * <p>Pure {@code @api} scenarios skip all three hooks — no browser is launched.
 *
 * <p>PicoContainer injects {@link DriverContext} per-scenario — no static state.
 */
public class DriverHooks {

    private static final Logger log = new Logger(DriverHooks.class);

    private final DriverContext driverContext;

    /**
     * PicoContainer constructor injection — do NOT add a no-arg constructor.
     *
     * @param driverContext scenario-scoped driver context
     */
    public DriverHooks(DriverContext driverContext) {
        this.driverContext = driverContext;
    }

    // ── Before ───────────────────────────────────────────────────────────────

    /**
     * Fires before each scenario that requires a browser (all except {@code @api}-only).
     *
     * <p>Pure API scenarios tagged {@code @api} (without {@code @ui}) skip this hook
     * entirely — no browser is started, saving time and resources.
     *
     * @param scenario Cucumber {@link Scenario} metadata (tags, name, id)
     */
    @Before(value = "not @api", order = 0)
    public void setUp(Scenario scenario) {
        log.step("▶ Starting scenario: [" + scenario.getId() + "] " + scenario.getName());
        driverContext.setUp();
    }

    // ── After ────────────────────────────────────────────────────────────────

    /**
     * Fires after each scenario that had a browser — captures a full-page screenshot
     * and attaches it to the Allure report when the scenario has failed
     * <strong>and</strong> {@link TestConfig#SCREENSHOT_ON_FAILURE} is {@code true}.
     *
     * <p>Runs at {@code order = 10} so it executes <em>before</em> the driver is
     * quit (order 0), giving us a live browser window to screenshot.
     * Skipped for {@code @api}-only scenarios (no browser).
     *
     * @param scenario Cucumber {@link Scenario} metadata
     */
    @After(value = "not @api", order = 10)
    public void captureFailureScreenshot(Scenario scenario) {
        if (!scenario.isFailed()) {
            return;
        }
        if (!TestConfig.SCREENSHOT_ON_FAILURE) {
            log.debug("Screenshot on failure disabled via config — skipping");
            return;
        }
        if (!driverContext.isReady()) {
            log.warn("Driver not ready — cannot capture failure screenshot");
            return;
        }

        try {
            String name = "failure-" + sanitise(scenario.getName());
            log.step("Capturing failure screenshot: " + name);
            ScreenshotUtils.attachFullPage(driverContext.getDriver(), name);
        } catch (Exception e) {
            log.warn("Failed to capture failure screenshot: " + e.getMessage());
        }
    }

    /**
     * Fires after each scenario that had a browser. Quits the WebDriver and
     * releases all resources.
     *
     * <p>Runs at {@code order = 0} — always last so the driver is available for
     * the screenshot hook (order 10) and any other cleanup hooks.
     * Skipped for {@code @api}-only scenarios (no browser was started).
     *
     * @param scenario Cucumber {@link Scenario} metadata
     */
    @After(value = "not @api", order = 0)
    public void tearDown(Scenario scenario) {
        try {
            driverContext.tearDown();
        } finally {
            log.step("■ Finished scenario: [" + scenario.getId() + "] "
                    + scenario.getName() + " — " + scenario.getStatus());
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Replaces characters that are unsafe in file names with underscores.
     *
     * @param name raw scenario name
     * @return file-safe string
     */
    private static String sanitise(String name) {
        if (name == null) return "unknown";
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}


