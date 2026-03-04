package yehorychev.selenium.handlers;

import com.yehorychev.selenium.utils.ScreenshotUtils;
import yehorychev.selenium.context.TestContext;

/**
 * Centralizes test failure handling logic.
 *
 * <p>Responsible for capturing diagnostics when a test fails, including:
 * <ul>
 *   <li>Screenshot capture</li>
 *   <li>Browser logs (future enhancement)</li>
 *   <li>Network logs (future enhancement)</li>
 *   <li>Page source (future enhancement)</li>
 * </ul>
 *
 * <p>Usage in Cucumber hooks:
 * <pre>{@code
 *   @After
 *   public void tearDown(Scenario scenario) {
 *       if (scenario.isFailed()) {
 *           FailureHandler.handleFailure(context, scenario.getName());
 *       }
 *       context.quit();
 *   }
 * }</pre>
 */
public final class FailureHandler {

    private FailureHandler() {}

    /**
     * Handles test failure by capturing diagnostic information.
     *
     * @param context      test context containing WebDriver
     * @param scenarioName name of the failed scenario
     */
    public static void handleFailure(TestContext context, String scenarioName) {
        if (context == null || !context.isReady()) {
            System.err.println("[FailureHandler] Cannot capture failure diagnostics - context not ready");
            return;
        }

        try {
            // Capture screenshot
            ScreenshotUtils.attachFullPage(context.getDriver(), "failure-" + scenarioName);

            // Future enhancements:
            // - Capture browser console logs
            // - Capture network traffic logs
            // - Capture page source HTML
            // - Capture cookies/localStorage

        } catch (Exception e) {
            System.err.println("[FailureHandler] Failed to capture diagnostics: " + e.getMessage());
        }
    }

    /**
     * Handles step failure by capturing diagnostic information with step context.
     *
     * @param context      test context containing WebDriver
     * @param scenarioName name of the scenario
     * @param stepInfo     information about the failed step
     */
    public static void handleStepFailure(TestContext context, String scenarioName, String stepInfo) {
        if (context == null || !context.isReady()) {
            System.err.println("[FailureHandler] Cannot capture step failure diagnostics - context not ready");
            return;
        }

        try {
            // Capture screenshot with step context
            ScreenshotUtils.attachViewport(context.getDriver(), "step-failure-" + scenarioName + "-" + stepInfo);

        } catch (Exception e) {
            System.err.println("[FailureHandler] Failed to capture step diagnostics: " + e.getMessage());
        }
    }
}

