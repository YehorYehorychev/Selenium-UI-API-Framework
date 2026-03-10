package com.yehorychev.selenium.context;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.driver.DriverManager;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WebDriver lifecycle and access layer for Cucumber scenarios.
 *
 * Injected via PicoContainer into hooks, steps, and page objects.
 * Manages the thread-local WebDriver instance for the current scenario.
 *
 * Usage in hooks:
 *   public class DriverHooks {
 *       private final DriverContext driverContext;
 *       public DriverHooks(DriverContext driverContext) { this.driverContext = driverContext; }
 *
 *       @Before public void setUp()    { driverContext.setUp(); }
 *       @After  public void tearDown() { driverContext.tearDown(); }
 *   }
 *
 * Usage in step definitions:
 *   public class LoginSteps {
 *       private final LoginPage loginPage;
 *       public LoginSteps(DriverContext ctx) { this.loginPage = new LoginPage(ctx.getDriver()); }
 *   }
 */
public class DriverContext {

    private static final Logger log = new Logger(DriverContext.class);

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Initialises the WebDriver for this scenario using the browser from config.
     * Call from a @Before Cucumber hook.
     */
    public void setUp() {
        log.step("Setting up DriverContext");
        DriverManager.initDriver();
    }

    /**
     * Initialises the WebDriver with an explicit browser override.
     *
     * @param browser chrome | firefox | edge
     */
    public void setUp(String browser) {
        log.step("Setting up DriverContext with browser: " + browser);
        DriverManager.initDriver(browser);
    }

    /**
     * Quits the WebDriver and releases all resources for this scenario.
     * Call from an @After Cucumber hook.
     */
    public void tearDown() {
        log.step("Tearing down DriverContext");
        DriverManager.quitDriver();
    }

    // ── Driver access ────────────────────────────────────────────────────────

    /**
     * Returns the active WebDriver for this thread/scenario.
     *
     * @return active WebDriver instance
     * @throws IllegalStateException if setUp() has not been called
     */
    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    /**
     * Creates and returns a WebDriverWait with the default timeout from TestConfig.
     *
     * @return configured WebDriverWait
     */
    public WebDriverWait getWait() {
        return new WebDriverWait(getDriver(), Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
    }

    /**
     * Creates and returns a WebDriverWait with a custom timeout.
     *
     * @param timeoutMs timeout in milliseconds
     * @return configured WebDriverWait
     */
    public WebDriverWait getWait(long timeoutMs) {
        return new WebDriverWait(getDriver(), Duration.ofMillis(timeoutMs));
    }

    /**
     * Returns true if a WebDriver is currently active on this thread.
     *
     * @return driver initialisation status
     */
    public boolean isReady() {
        return DriverManager.isDriverInitialised();
    }
}

