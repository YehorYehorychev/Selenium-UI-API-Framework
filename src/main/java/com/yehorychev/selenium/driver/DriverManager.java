package com.yehorychev.selenium.driver;

import com.yehorychev.selenium.config.DriverConfig;
import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Thread-safe WebDriver registry using ThreadLocal — each test thread owns its own instance.
 */
public final class DriverManager {

    private static final Logger log = new Logger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() {
        if (DRIVER_THREAD_LOCAL.get() != null) {
            log.warn("Driver already initialised on this thread — quitting old instance first.");
            quitDriver();
        }
        log.step(targetDescription(TestConfig.BROWSER));
        WebDriver driver = DriverConfig.createDriver();
        DRIVER_THREAD_LOCAL.set(driver);
        log.info("WebDriver ready", driver.getClass().getSimpleName());
    }

    public static void initDriver(String browser) {
        if (DRIVER_THREAD_LOCAL.get() != null) {
            log.warn("Driver already initialised on this thread — quitting old instance first.");
            quitDriver();
        }
        log.step(targetDescription(browser));
        WebDriver driver = DriverConfig.createDriver(browser);
        DRIVER_THREAD_LOCAL.set(driver);
        log.info("WebDriver ready", driver.getClass().getSimpleName());
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "No WebDriver found on thread '" + Thread.currentThread().getName() +
                            "'. Call DriverManager.initDriver() before accessing the driver."
            );
        }
        return driver;
    }

    public static boolean isDriverInitialised() {
        return DRIVER_THREAD_LOCAL.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            log.step("Quitting WebDriver on thread: " + Thread.currentThread().getName());
            try {
                driver.quit();
            } catch (Exception e) {
                log.error("Error while quitting WebDriver: " + e.getMessage());
            } finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
    }

    private static String targetDescription(String browser) {
        String target = TestConfig.REMOTE_ENABLED
                ? "RemoteWebDriver → " + TestConfig.REMOTE_URL
                : "Local WebDriver";
        return "Initialising " + target + " [" + browser + "] for thread: " + Thread.currentThread().getName();
    }
}
