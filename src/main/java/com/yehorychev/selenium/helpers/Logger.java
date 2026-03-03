package com.yehorychev.selenium.helpers;

import org.slf4j.LoggerFactory;

/**
 * Lightweight structured logger — analogue of {@code logger.ts}.
 *
 * <p>Wraps SLF4J with convenience methods for structured test logging:
 * {@code debug / info / warn / error / step}.  The {@code step()} method
 * mirrors the {@code log.step("→ message")} pattern used in TypeScript.
 *
 * <p>Log verbosity is controlled by SLF4J / the underlying implementation
 * (Simple, Logback, etc.) via the standard {@code org.slf4j.simpleLogger.defaultLogLevel}
 * system property or {@code simplelogger.properties} on the classpath.
 *
 * <p>Usage:
 * <pre>{@code
 *   public class HomePage extends BasePage {
 *       private final Logger log = new Logger(HomePage.class);
 *
 *       public void navigate() {
 *           log.step("Navigating to home page");
 *           driver.get(TestConfig.BASE_URL);
 *       }
 *   }
 * }</pre>
 */
public class Logger {

    private final org.slf4j.Logger log;

    /**
     * Creates a logger scoped to the given class.
     *
     * @param clazz the class that owns this logger (used as the logger name)
     */
    public Logger(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    /**
     * Creates a logger with an arbitrary string name.
     * Useful for non-class contexts such as static helpers.
     *
     * @param name logger name (e.g. {@code "AuthHelper"}, {@code "Fixtures"})
     */
    public Logger(String name) {
        this.log = LoggerFactory.getLogger(name);
    }

    // ── Logging methods ─────────────────────────────────────────────────────

    /**
     * Logs an important milestone — analogue of {@code log.step("→ message")} in TS.
     * Always logged at INFO level, prefixed with {@code →} for visual distinction.
     *
     * @param message step description
     */
    public void step(String message) {
        log.info("→ {}", message);
    }

    /**
     * Logs an informational message.
     *
     * @param message message text
     */
    public void info(String message) {
        log.info(message);
    }

    /**
     * Logs an informational message with a structured data object.
     *
     * @param message message text
     * @param data    additional context (toString() is used)
     */
    public void info(String message, Object data) {
        log.info("{} | {}", message, data);
    }

    /**
     * Logs a debug-level message (only visible at DEBUG log level).
     *
     * @param message message text
     */
    public void debug(String message) {
        log.debug(message);
    }

    /**
     * Logs a debug-level message with structured data.
     *
     * @param message message text
     * @param data    additional context
     */
    public void debug(String message, Object data) {
        log.debug("{} | {}", message, data);
    }

    /**
     * Logs a warning message.
     *
     * @param message warning description
     */
    public void warn(String message) {
        log.warn(message);
    }

    /**
     * Logs a warning with an optional cause.
     *
     * @param message warning description
     * @param cause   optional throwable (may be {@code null})
     */
    public void warn(String message, Throwable cause) {
        log.warn(message, cause);
    }

    /**
     * Logs an error message.
     *
     * @param message error description
     */
    public void error(String message) {
        log.error(message);
    }

    /**
     * Logs an error message with a cause.
     *
     * @param message error description
     * @param cause   throwable that caused the error
     */
    public void error(String message, Throwable cause) {
        log.error(message, cause);
    }
}

