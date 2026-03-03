package yehorychev.selenium.context;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.driver.DriverManager;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Shared test context — analogue of Playwright's {@code test.use()} / fixture scope.
 *
 * <p>Provides a unified access point for the current {@link WebDriver} instance
 * and exposes convenience methods that every test class, step definition, and
 * page object uses without depending directly on {@link DriverManager}.
 *
 * <p>In a Cucumber scenario, one {@code TestContext} is created per scenario and
 * passed through PicoContainer dependency injection.
 *
 * <p>Usage example in Cucumber hooks:
 * <pre>{@code
 *   public class Hooks {
 *       private final TestContext context;
 *
 *       public Hooks(TestContext context) {
 *           this.context = context;
 *       }
 *
 *       @Before
 *       public void setUp() {
 *           context.init();
 *       }
 *
 *       @After
 *       public void tearDown() {
 *           context.quit();
 *       }
 *   }
 * }</pre>
 *
 * <p>Usage example in a step definition:
 * <pre>{@code
 *   public class LoginSteps {
 *       private final TestContext context;
 *
 *       public LoginSteps(TestContext context) {
 *           this.context = context;
 *       }
 *
 *       @Given("the user opens the home page")
 *       public void openHomePage() {
 *           context.getDriver().get(TestConfig.BASE_URL);
 *       }
 *   }
 * }</pre>
 */
public class TestContext {

    private static final Logger log = new Logger(TestContext.class);

    // ── Lifecycle ───────────────────────────────────────────────────────────

    /**
     * Initialises the WebDriver for this test scenario using the browser
     * defined in {@link TestConfig}.
     *
     * <p>Call this inside a {@code @Before} Cucumber hook.
     */
    public void init() {
        log.step("Initialising TestContext for scenario");
        DriverManager.initDriver();
    }

    /**
     * Initialises the WebDriver with an explicit browser override.
     *
     * @param browser {@code chrome} | {@code firefox} | {@code edge}
     */
    public void init(String browser) {
        log.step("Initialising TestContext with browser: " + browser);
        DriverManager.initDriver(browser);
    }

    /**
     * Quits the WebDriver and cleans up all resources for this scenario.
     *
     * <p>Call this inside an {@code @After} Cucumber hook.
     */
    public void quit() {
        log.step("Tearing down TestContext");
        DriverManager.quitDriver();
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the {@link WebDriver} bound to the current thread.
     *
     * @return active {@link WebDriver}
     * @throws IllegalStateException if {@link #init()} has not been called
     */
    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    /**
     * Returns {@code true} if a WebDriver is currently active for this context.
     *
     * @return driver initialisation status
     */
    public boolean isReady() {
        return DriverManager.isDriverInitialised();
    }

    // ── Convenience navigation ────────────────────────────────────────────────

    /**
     * Navigates the browser to the application's base URL defined in {@link TestConfig}.
     */
    public void openBaseUrl() {
        log.step("Navigating to base URL: " + TestConfig.BASE_URL);
        getDriver().get(TestConfig.BASE_URL);
    }

    /**
     * Navigates the browser to an arbitrary URL.
     *
     * @param url the full URL to navigate to
     */
    public void navigateTo(String url) {
        log.step("Navigating to: " + url);
        getDriver().get(url);
    }

    /**
     * Returns the current page title reported by the browser.
     *
     * @return page title string
     */
    public String getPageTitle() {
        return getDriver().getTitle();
    }

    /**
     * Returns the current URL of the browser.
     *
     * @return current URL string
     */
    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
}


