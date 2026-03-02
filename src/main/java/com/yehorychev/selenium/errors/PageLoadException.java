package com.yehorychev.selenium.errors;

/**
 * Thrown when a page fails to load within the allowed timeout.
 *
 * <p>Analogue of {@code PageLoadError} in {@code test-errors.ts}.
 *
 * <p>Example:
 * <pre>{@code
 *   throw new PageLoadException("https://mobalytics.gg", 30_000);
 * }</pre>
 */
public class PageLoadException extends FrameworkException {

    /**
     * @param url       URL that failed to load
     * @param timeoutMs timeout that was exceeded, in milliseconds
     */
    public PageLoadException(String url, long timeoutMs) {
        super("Page \"" + url + "\" did not finish loading within " + timeoutMs + "ms");
    }

    /**
     * @param url       URL that failed to load
     * @param timeoutMs timeout that was exceeded, in milliseconds
     * @param cause     underlying exception from WebDriver
     */
    public PageLoadException(String url, long timeoutMs, Throwable cause) {
        super("Page \"" + url + "\" did not finish loading within " + timeoutMs + "ms", cause);
    }
}

