package com.yehorychev.selenium.errors;

/**
 * Thrown when API or UI authentication fails.
 *
 * Example:
 *   throw new AuthenticationException("loginViaApi returned HTTP 401");
 */
public class AuthenticationException extends FrameworkException {

    public AuthenticationException() {
        super("Authentication failed");
    }

    /**
     * @param reason human-readable explanation of why authentication failed
     */
    public AuthenticationException(String reason) {
        super("Authentication failed: " + reason);
    }

    /**
     * @param reason human-readable explanation
     * @param cause  underlying exception
     */
    public AuthenticationException(String reason, Throwable cause) {
        super("Authentication failed: " + reason, cause);
    }
}

