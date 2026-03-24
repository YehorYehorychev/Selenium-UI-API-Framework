package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Centralized WebDriverWait factory to keep timeout defaults consistent.
 */
public final class WaitFactory {

    private WaitFactory() {
    }

    public static WebDriverWait defaultWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
    }

    public static WebDriverWait shortWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofMillis(TestConfig.SHORT_TIMEOUT_MS));
    }
}

