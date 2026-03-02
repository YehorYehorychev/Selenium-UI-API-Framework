package com.yehorychev.selenium.errors;

/**
 * Base class for all framework-specific runtime exceptions.
 *
 * <p>Analogue of the typed error hierarchy in {@code test-errors.ts}.
 * All custom exceptions extend this class so callers can catch the
 * entire framework error family with a single {@code catch (FrameworkException e)}.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}

