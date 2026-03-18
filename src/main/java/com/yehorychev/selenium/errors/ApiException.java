package com.yehorychev.selenium.errors;

public class ApiException extends FrameworkException {

    private final int statusCode;
    private final String endpoint;

    public ApiException(int statusCode, String message, String endpoint) {
        super("API error (HTTP " + statusCode + ") [" + endpoint + "]: " + message);
        this.statusCode = statusCode;
        this.endpoint   = endpoint;
    }

    public ApiException(int statusCode, String message) {
        super("API error (HTTP " + statusCode + "): " + message);
        this.statusCode = statusCode;
        this.endpoint   = null;
    }

    public ApiException(int statusCode, String message, String endpoint, Throwable cause) {
        super("API error (HTTP " + statusCode + ") [" + endpoint + "]: " + message, cause);
        this.statusCode = statusCode;
        this.endpoint   = endpoint;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
