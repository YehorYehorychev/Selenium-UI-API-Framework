package yehorychev.selenium.context;

import com.yehorychev.selenium.driver.DriverManager;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Shared test context passed between Cucumber hooks, step definitions and
 * page objects via PicoContainer dependency injection.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>WebDriver lifecycle — {@link #init()}, {@link #init(String)}, {@link #quit()}</li>
 *   <li>Driver access — {@link #getDriver()}, {@link #isReady()}</li>
 * </ul>
 *
 * <p>Navigation, assertions and page interactions belong exclusively to
 * Page Object classes that extend {@code BasePage}.
 *
 * <p>Usage example in Cucumber hooks:
 * <pre>{@code
 *   public class Hooks {
 *       private final TestContext context;
 *
 *       public Hooks(TestContext context) { this.context = context; }
 *
 *       @Before
 *       public void setUp()    { context.init(); }
 *
 *       @After
 *       public void tearDown() { context.quit(); }
 *   }
 * }</pre>
 *
 * <p>Usage example in a step definition:
 * <pre>{@code
 *   public class LoginSteps {
 *       private final LoginPage loginPage;
 *
 *       public LoginSteps(TestContext context) {
 *           this.loginPage = new LoginPage(context.getDriver());
 *       }
 *   }
 * }</pre>
 */
public class TestContext {

    private static final Logger log = new Logger(TestContext.class);

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Initialises the WebDriver using the browser defined in config.
     * Call from a {@code @Before} Cucumber hook.
     */
    public void init() {
        log.step("Initialising TestContext for scenario");
        DriverManager.initDriver();
    }

    /**
     * Initializes the WebDriver with an explicit browser override.
     *
     * @param browser {@code chrome} | {@code firefox} | {@code edge}
     */
    public void init(String browser) {
        log.step("Initialising TestContext with browser: " + browser);
        DriverManager.initDriver(browser);
    }

    /**
     * Quits the WebDriver and releases all resources for this scenario.
     * Call from an {@code @After} Cucumber hook.
     */
    public void quit() {
        log.step("Tearing down TestContext");
        DriverManager.quitDriver();
    }

    // ── Driver access ────────────────────────────────────────────────────────

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
     * Returns {@code true} if a WebDriver is currently active on this thread.
     *
     * @return driver initialisation status
     */
    public boolean isReady() {
        return DriverManager.isDriverInitialised();
    }
}


