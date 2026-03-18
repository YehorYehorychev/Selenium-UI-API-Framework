package com.yehorychev.selenium.helpers;

import org.slf4j.LoggerFactory;

/**
 * Lightweight structured logger wrapping SLF4J with test-friendly convenience methods.
 */
public class Logger {

    private final org.slf4j.Logger log;

    public Logger(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    public Logger(String name) {
        this.log = LoggerFactory.getLogger(name);
    }

    /** Key test milestone — always at INFO level, prefixed with → for visual distinction. */
    public void step(String message) {
        log.info("→ {}", message);
    }

    public void info(String message) {
        log.info(message);
    }

    public void info(String message, Object data) {
        log.info("{}  {}", message, data);
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void debug(String message, Object data) {
        log.debug("{}  {}", message, data);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void warn(String message, Throwable cause) {
        log.warn(message, cause);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Throwable cause) {
        log.error(message, cause);
    }
}
