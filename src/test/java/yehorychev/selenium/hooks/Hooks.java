package yehorychev.selenium.hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import yehorychev.selenium.context.TestContext;

import java.io.ByteArrayInputStream;

/**
 * Cucumber lifecycle hooks — analogue of Playwright's {@code test.beforeEach} /
 * {@code test.afterEach} fixture hooks.
 *
 * <p>PicoContainer injects the shared {@link TestContext} for every scenario,
 * so there is no static state: each scenario gets its own driver lifecycle.
 *
 * <p>Hook order (lowest number = first):
 * <ul>
 *   <li>{@code @Before(order = 0)} — driver init</li>
 *   <li>{@code @AfterStep}         — screenshot on step failure (if enabled)</li>
 *   <li>{@code @After(order = 0)}  — screenshot on scenario failure + driver quit</li>
 * </ul>
 */
public class Hooks {

    private final TestContext context;

    /**
     * PicoContainer constructor injection — do NOT add a no-arg constructor.
     *
     * @param context shared test context for this scenario
     */
    public Hooks(TestContext context) {
        this.context = context;
    }

    // ── Before ───────────────────────────────────────────────────────────────

    /**
     * Fires before each scenario. Initialises the WebDriver.
     *
     * @param scenario Cucumber {@link Scenario} metadata (tags, name, id)
     */
    @Before(order = 0)
    public void setUp(Scenario scenario) {
        System.out.println("▶ Starting scenario: [" + scenario.getId() + "] " + scenario.getName());
        context.init();
    }

    // ── After ────────────────────────────────────────────────────────────────

    /**
     * Fires after each scenario. Captures a screenshot on failure (when enabled
     * via {@code screenshot.on.failure=true}) and quits the driver.
     *
     * @param scenario Cucumber {@link Scenario} metadata
     */
    @After(order = 0)
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && context.isReady()) {
                captureScreenshot(scenario.getName());
            }
        } finally {
            context.quit();
            System.out.println("■ Finished scenario: [" + scenario.getId() + "] "
                    + scenario.getName() + " — " + scenario.getStatus());
        }
    }

    // ── AfterStep ─────────────────────────────────────────────────────────────

    /**
     * Fires after every step. Captures a screenshot when the step has failed so
     * that failures in intermediate steps are also visible in the report.
     *
     * @param scenario Cucumber {@link Scenario} metadata
     */
    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed() && context.isReady()) {
            captureScreenshot("step-failure-" + scenario.getName());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Takes a screenshot of the current browser state and attaches it to the
     * Allure report.
     *
     * @param name descriptive name for the attachment
     */
    private void captureScreenshot(String name) {
        try {
            byte[] screenshot = ((TakesScreenshot) context.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
        } catch (Exception e) {
            System.err.println("[Hooks] Could not capture screenshot: " + e.getMessage());
        }
    }
}

