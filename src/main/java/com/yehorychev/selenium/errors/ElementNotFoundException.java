package com.yehorychev.selenium.errors;

/**
 * Thrown when an expected element cannot be found or is not in the required state.
 *
 * Example:
 *   throw new ElementNotFoundException("button[data-testid='submit']", 15_000);
 */
public class ElementNotFoundException extends FrameworkException {

    /**
     * @param descriptor human-readable element description (e.g. CSS selector, XPath)
     * @param timeoutMs  how long we waited before giving up, in milliseconds
     */
    public ElementNotFoundException(String descriptor, long timeoutMs) {
        super("Element not found: \"" + descriptor + "\" (waited " + timeoutMs + "ms)");
    }

    /**
     * @param descriptor human-readable element description
     * @param timeoutMs  wait duration in milliseconds
     * @param cause      underlying exception from WebDriver
     */
    public ElementNotFoundException(String descriptor, long timeoutMs, Throwable cause) {
        super("Element not found: \"" + descriptor + "\" (waited " + timeoutMs + "ms)", cause);
    }

    /**
     * Convenience constructor without a timeout — used when the wait duration
     * is not meaningful in context.
     *
     * @param descriptor human-readable element description
     */
    public ElementNotFoundException(String descriptor) {
        super("Element not found: \"" + descriptor + "\"");
    }
}

