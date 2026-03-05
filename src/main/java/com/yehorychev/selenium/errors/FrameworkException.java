package com.yehorychev.selenium.errors;

/**
 * Base class for all framework-specific runtime exceptions.
 *
 * All custom exceptions extend this class so callers can catch
 * the entire framework error family with a single catch (FrameworkException e).
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}

