package com.yehorychev.selenium.errors;

public class ElementNotFoundException extends FrameworkException {

    public ElementNotFoundException(String descriptor, long timeoutMs) {
        super("Element not found: \"" + descriptor + "\" (waited " + timeoutMs + "ms)");
    }

    public ElementNotFoundException(String descriptor, long timeoutMs, Throwable cause) {
        super("Element not found: \"" + descriptor + "\" (waited " + timeoutMs + "ms)", cause);
    }

    public ElementNotFoundException(String descriptor) {
        super("Element not found: \"" + descriptor + "\"");
    }
}
