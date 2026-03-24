package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.utils.AllureUtils;
import com.yehorychev.selenium.utils.ScreenshotUtils;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.MDC;

/**
 * WebDriver lifecycle hooks — starts and tears down a browser for every non-@api scenario.
 */
public class DriverHooks {

    private static final Logger log = new Logger(DriverHooks.class);

    @BeforeAll
    public static void cleanUpOldScreenshots() {
        if (TestConfig.SCREENSHOT_ON_FAILURE) {
            ScreenshotUtils.cleanupOldScreenshots(TestConfig.SCREENSHOT_DIR, 7);
        }
    }

    private final DriverContext driverContext;

    public DriverHooks(DriverContext driverContext) {
        this.driverContext = driverContext;
    }

    @Before(value = "not @api", order = 0)
    public void setUp(Scenario scenario) {
        MDC.put("scenario", scenario.getName());
        MDC.put("scenarioId", scenario.getId());
        log.step("▶ Starting scenario: [" + scenario.getId() + "] " + scenario.getName());
        driverContext.setUp();
    }

    @After(value = "not @api", order = 10)
    public void captureFailureScreenshot(Scenario scenario) {
        if (!scenario.isFailed()) return;
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
            ScreenshotUtils.attachConsoleLogs(driverContext.getDriver(), "console-" + sanitise(scenario.getName()));
            AllureUtils.attachPageSource(driverContext.getDriver(), "page-source-" + sanitise(scenario.getName()));
        } catch (Exception e) {
            log.warn("Failed to capture failure artifacts: " + e.getMessage());
        }
    }

    @After(value = "not @api", order = 0)
    public void tearDown(Scenario scenario) {
        try {
            driverContext.tearDown();
        } finally {
            log.step("■ Finished scenario: [" + scenario.getId() + "] "
                    + scenario.getName() + " — " + scenario.getStatus());
            MDC.clear();
        }
    }

    private static String sanitise(String name) {
        if (name == null) return "unknown";
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
