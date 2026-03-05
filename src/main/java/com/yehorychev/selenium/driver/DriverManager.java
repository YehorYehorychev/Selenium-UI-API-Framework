package com.yehorychev.selenium.driver;

import com.yehorychev.selenium.config.DriverConfig;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Thread-safe WebDriver registry.
 *
 * <p>Each test thread owns its own {@link WebDriver} instance stored in a
 * {@link ThreadLocal}. This makes the class safe for parallel execution without
 * any synchronisation overhead on the happy path.
 *
 * <p>Lifecycle:
 * <pre>{@code
 *   // Before test
 *   DriverManager.initDriver();
 *
 *   // Inside test / page object
 *   WebDriver driver = DriverManager.getDriver();
 *
 *   // After test
 *   DriverManager.quitDriver();
 * }</pre>
 */
public final class DriverManager {

    private static final Logger log = new Logger(DriverManager.class);

    /**
     * One WebDriver instance per thread.
     */
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Not instantiable — all members are static.
     */
    private DriverManager() {
    }

    // ── Lifecycle ───────────────────────────────────────────────────────────

    /**
     * Creates a new {@link WebDriver} using {@link DriverConfig} and binds it
     * to the current thread. If a driver is already running on this thread,
     * it is quit first to avoid leaking browser processes.
     */
    public static void initDriver() {
        if (DRIVER_THREAD_LOCAL.get() != null) {
            log.warn("Driver already initialised on this thread — quitting old instance first.");
            quitDriver();
        }
        log.step("Initialising WebDriver for thread: " + Thread.currentThread().getName());
        WebDriver driver = DriverConfig.createDriver();
        DRIVER_THREAD_LOCAL.set(driver);
        log.info("WebDriver ready", driver.getClass().getSimpleName());
    }

    /**
     * Initialises a {@link WebDriver} for the specified browser on the current thread.
     *
     * @param browser {@code chrome} | {@code firefox} | {@code edge}
     */
    public static void initDriver(String browser) {
        if (DRIVER_THREAD_LOCAL.get() != null) {
            log.warn("Driver already initialised on this thread — quitting old instance first.");
            quitDriver();
        }
        log.step("Initialising WebDriver [" + browser + "] for thread: " + Thread.currentThread().getName());
        WebDriver driver = DriverConfig.createDriver(browser);
        DRIVER_THREAD_LOCAL.set(driver);
        log.info("WebDriver ready", driver.getClass().getSimpleName());
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the {@link WebDriver} bound to the current thread.
     *
     * @return active {@link WebDriver}
     * @throws IllegalStateException if {@link #initDriver()} has not been called on this thread
     */
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

    /**
     * Returns {@code true} if a driver is currently active on this thread.
     *
     * @return whether the driver is initialised
     */
    public static boolean isDriverInitialised() {
        return DRIVER_THREAD_LOCAL.get() != null;
    }

    // ── Teardown ─────────────────────────────────────────────────────────────

    /**
     * Quits the {@link WebDriver} on the current thread and removes it from the
     * {@link ThreadLocal} to prevent memory leaks.
     * Safe to call even if no driver is active (no-op in that case).
     */
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
}

