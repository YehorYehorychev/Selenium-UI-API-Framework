package com.yehorychev.selenium.errors;

import lombok.Getter;

/**
 * Thrown when an API call returns an unexpected HTTP status code or error payload.
 *
 * Example:
 *   throw new ApiException(401, "Unauthorized", "/api/graphql/v1/query");
 */
@Getter
public class ApiException extends FrameworkException {

    /** The HTTP status code returned by the API. */
    private final int statusCode;

    /** The API endpoint that produced the error (may be null). */
    private final String endpoint;

    /**
     * @param statusCode HTTP status code (e.g. 401, 500)
     * @param message    human-readable error description
     * @param endpoint   the API endpoint path (used for diagnostics)
     */
    public ApiException(int statusCode, String message, String endpoint) {
        super("API error (HTTP " + statusCode + ") [" + endpoint + "]: " + message);
        this.statusCode = statusCode;
        this.endpoint   = endpoint;
    }

    /**
     * @param statusCode HTTP status code
     * @param message    human-readable error description
     */
    public ApiException(int statusCode, String message) {
        super("API error (HTTP " + statusCode + "): " + message);
        this.statusCode = statusCode;
        this.endpoint   = null;
    }

    /**
     * @param statusCode HTTP status code
     * @param message    human-readable error description
     * @param endpoint   the API endpoint path
     * @param cause      underlying exception
     */
    public ApiException(int statusCode, String message, String endpoint, Throwable cause) {
        super("API error (HTTP " + statusCode + ") [" + endpoint + "]: " + message, cause);
        this.statusCode = statusCode;
        this.endpoint   = endpoint;
    }
}

