package yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import yehorychev.selenium.context.DriverContext;

/**
 * WebDriver lifecycle hooks — sets up and tears down a browser for every scenario.
 *
 * Responsibilities:
 *   - @Before("not @api", order=0)  — start WebDriver via DriverContext
 *   - @After("not @api",  order=10) — capture AShot screenshot on failure
 *   - @After("not @api",  order=0)  — quit WebDriver and release resources
 *
 * Pure @api scenarios skip all three hooks — no browser is launched.
 * PicoContainer injects DriverContext per-scenario — no static state.
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
     * Fires before each scenario that requires a browser (all except @api-only).
     * No browser is started for pure API scenarios — saves time and resources.
     *
     * @param scenario Cucumber Scenario metadata (tags, name, id)
     */
    @Before(value = "not @api", order = 0)
    public void setUp(Scenario scenario) {
        log.step("▶ Starting scenario: [" + scenario.getId() + "] " + scenario.getName());
        driverContext.setUp();
    }

    // ── After ────────────────────────────────────────────────────────────────

    /**
     * Fires after each scenario that had a browser.
     * Captures a full-page AShot screenshot and attaches it to the Allure report
     * when the scenario failed AND TestConfig.SCREENSHOT_ON_FAILURE is true.
     * Runs at order=10 so the driver is still alive when the screenshot is taken.
     *
     * @param scenario Cucumber Scenario metadata
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
     * Fires after each scenario that had a browser.
     * Quits the WebDriver and releases all resources.
     * Runs at order=0 — always last, after the screenshot hook (order=10).
     *
     * @param scenario Cucumber Scenario metadata
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
     * Replaces characters unsafe in file names with underscores.
     *
     * @param name raw scenario name
     * @return file-safe string
     */
    private static String sanitise(String name) {
        if (name == null) return "unknown";
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}

