package com.yehorychev.selenium.errors;

public class AuthenticationException extends FrameworkException {

    public AuthenticationException() {
        super("Authentication failed");
    }

    public AuthenticationException(String reason) {
        super("Authentication failed: " + reason);
    }

    public AuthenticationException(String reason, Throwable cause) {
        super("Authentication failed: " + reason, cause);
    }
}
