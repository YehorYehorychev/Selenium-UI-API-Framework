package com.yehorychev.selenium.errors;

public class NavigationException extends FrameworkException {

    public NavigationException(String actualUrl, String expectedPattern) {
        super("Navigation failed — actual URL: \"" + actualUrl
                + "\", expected to match: \"" + expectedPattern + "\"");
    }

    public NavigationException(String actualUrl, String expectedPattern, Throwable cause) {
        super("Navigation failed — actual URL: \"" + actualUrl
                + "\", expected to match: \"" + expectedPattern + "\"", cause);
    }
}
