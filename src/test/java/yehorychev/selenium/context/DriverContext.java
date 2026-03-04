package yehorychev.selenium.context;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.driver.DriverManager;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WebDriver lifecycle and access layer for Cucumber scenarios.
 *
 * <p>Injected via PicoContainer into hooks, steps, and page objects. Manages the
 * thread-local WebDriver instance for the current scenario. Responsible for driver
 * initialisation and teardown.
 *
 * <p>This is the primary DI context for UI tests — analogous to Playwright's
 * {@code test.extend({ page, context })} fixture pattern.
 *
 * <p>Usage in hooks:
 * <pre>{@code
 *   public class Hooks {
 *       private final DriverContext driverContext;
 *
 *       public Hooks(DriverContext driverContext) {
 *           this.driverContext = driverContext;
 *       }
 *
 *       @Before
 *       public void setUp() {
 *           driverContext.setUp();
 *       }
 *
 *       @After
 *       public void tearDown() {
 *           driverContext.tearDown();
 *       }
 *   }
 * }</pre>
 *
 * <p>Usage in step definitions:
 * <pre>{@code
 *   public class LoginSteps {
 *       private final LoginPage loginPage;
 *
 *       public LoginSteps(DriverContext driverContext) {
 *           this.loginPage = new LoginPage(driverContext.getDriver());
 *       }
 *   }
 * }</pre>
 */
public class DriverContext {

    private static final Logger log = new Logger(DriverContext.class);

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Initialises the WebDriver for this scenario using the browser from config.
     * Call from a {@code @Before} Cucumber hook.
     */
    public void setUp() {
        log.step("Setting up DriverContext");
        DriverManager.initDriver();
    }

    /**
     * Initialises the WebDriver with an explicit browser override.
     *
     * @param browser {@code chrome} | {@code firefox} | {@code edge}
     */
    public void setUp(String browser) {
        log.step("Setting up DriverContext with browser: " + browser);
        DriverManager.initDriver(browser);
    }

    /**
     * Quits the WebDriver and releases all resources for this scenario.
     * Call from an {@code @After} Cucumber hook.
     */
    public void tearDown() {
        log.step("Tearing down DriverContext");
        DriverManager.quitDriver();
    }

    // ── Driver access ────────────────────────────────────────────────────────

    /**
     * Returns the active {@link WebDriver} for this thread/scenario.
     *
     * @return active {@link WebDriver}
     * @throws IllegalStateException if {@link #setUp()} has not been called
     */
    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    /**
     * Creates and returns a {@link WebDriverWait} with the default timeout.
     *
     * @return configured {@link WebDriverWait}
     */
    public WebDriverWait getWait() {
        return new WebDriverWait(getDriver(), Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
    }

    /**
     * Creates and returns a {@link WebDriverWait} with a custom timeout.
     *
     * @param timeoutMs timeout in milliseconds
     * @return configured {@link WebDriverWait}
     */
    public WebDriverWait getWait(long timeoutMs) {
        return new WebDriverWait(getDriver(), Duration.ofMillis(timeoutMs));
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

