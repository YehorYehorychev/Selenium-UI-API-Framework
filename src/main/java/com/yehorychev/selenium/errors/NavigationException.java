package com.yehorychev.selenium.errors;

/**
 * Thrown when a navigation action does not produce the expected URL.
 *
 * <p>Analogue of {@code NavigationError} in {@code test-errors.ts}.
 *
 * <p>Example:
 * <pre>{@code
 *   throw new NavigationException(driver.getCurrentUrl(), ".*lol.*");
 * }</pre>
 */
public class NavigationException extends FrameworkException {

    /**
     * @param actualUrl       the URL that was reached
     * @param expectedPattern the expected URL pattern (String or regex)
     */
    public NavigationException(String actualUrl, String expectedPattern) {
        super("Navigation failed — actual URL: \"" + actualUrl
                + "\", expected to match: \"" + expectedPattern + "\"");
    }

    /**
     * @param actualUrl       the URL that was reached
     * @param expectedPattern the expected URL pattern
     * @param cause           underlying exception
     */
    public NavigationException(String actualUrl, String expectedPattern, Throwable cause) {
        super("Navigation failed — actual URL: \"" + actualUrl
                + "\", expected to match: \"" + expectedPattern + "\"", cause);
    }
}

