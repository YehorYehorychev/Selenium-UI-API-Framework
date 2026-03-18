package com.yehorychev.selenium.errors;

public class PageLoadException extends FrameworkException {

    public PageLoadException(String url, long timeoutMs) {
        super("Page \"" + url + "\" did not finish loading within " + timeoutMs + "ms");
    }

    public PageLoadException(String url, long timeoutMs, Throwable cause) {
        super("Page \"" + url + "\" did not finish loading within " + timeoutMs + "ms", cause);
    }
}
