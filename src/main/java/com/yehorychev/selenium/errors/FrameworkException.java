package com.yehorychev.selenium.errors;

/**
 * Base class for all framework-specific runtime exceptions.
 * Catch {@code FrameworkException} to handle the entire framework error family in one handler.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
