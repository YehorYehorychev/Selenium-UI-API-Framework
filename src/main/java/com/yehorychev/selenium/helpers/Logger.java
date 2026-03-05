package com.yehorychev.selenium.helpers;

import org.slf4j.LoggerFactory;

/**
 * Lightweight structured logger — wraps SLF4J with test-friendly convenience methods.
 *
 * Methods:
 *   - step()  — marks a key test milestone, always at INFO level with a → prefix
 *   - info()  — general informational message
 *   - debug() — verbose detail, only visible at DEBUG log level
 *   - warn()  — non-fatal issue worth noting
 *   - error() — failure or unexpected condition
 *
 * Log verbosity is controlled via simplelogger.properties on the classpath
 * or the org.slf4j.simpleLogger.defaultLogLevel system property.
 *
 * Usage:
 *   private final Logger log = new Logger(HomePage.class);
 *   log.step("Navigating to home page");
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
     *
     * @param name logger name (e.g. "AuthHelper")
     */
    public Logger(String name) {
        this.log = LoggerFactory.getLogger(name);
    }

    // ── Logging methods ─────────────────────────────────────────────────────

    /**
     * Logs an important milestone — always at INFO level, prefixed with → for visual distinction.
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
     * @param cause   optional throwable (may be null)
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
