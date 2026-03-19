package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.errors.NavigationException;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Fluent wait, retry, and polling helpers. All methods are static.
 */
public final class WaitUtils {

    private static final Logger log = new Logger(WaitUtils.class);

    private WaitUtils() {
    }

    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition) {
        return waitFor(driver, condition, TestConfig.DEFAULT_TIMEOUT_MS);
    }

    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition, long timeoutMs) {
        return new WebDriverWait(driver, Duration.ofMillis(timeoutMs)).until(condition);
    }

    public static void waitForUrl(WebDriver driver, String urlFragment) {
        log.step("Waiting for URL to contain: " + urlFragment);
        try {
            waitFor(driver, ExpectedConditions.urlContains(urlFragment));
        } catch (TimeoutException e) {
            throw new NavigationException(driver.getCurrentUrl(), urlFragment, e);
        }
    }

    public static void waitForTitle(WebDriver driver, String titleFragment) {
        log.step("Waiting for title to contain: " + titleFragment);
        try {
            waitFor(driver, ExpectedConditions.titleContains(titleFragment));
        } catch (TimeoutException e) {
            throw new NavigationException(driver.getTitle(), titleFragment, e);
        }
    }

    public static void waitForPageLoad(WebDriver driver) {
        log.step("Waiting for page to fully load");
        waitFor(driver, d -> {
            String state = (String) ((JavascriptExecutor) d).executeScript("return document.readyState");
            return "complete".equals(state);
        });
    }


    public static void waitForTextChange(WebDriver driver, WebElement element, String oldText) {
        log.step("Waiting for text to change from: \"" + oldText + "\"");
        waitFor(driver, ExpectedConditions.not(
                ExpectedConditions.textToBePresentInElement(element, oldText)));
    }

    public static void waitForAttributeValue(WebDriver driver, WebElement element, String attribute, String value) {
        log.step("Waiting for attribute \"" + attribute + "\" to equal: " + value);
        waitFor(driver, ExpectedConditions.attributeToBe(element, attribute, value));
    }

    public static String pollUntilNotEmpty(Supplier<String> supplier, long timeoutMs, long pollIntervalMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            String value = supplier.get();
            if (value != null && !value.isBlank()) return value;
            sleep(pollIntervalMs);
        }
        throw new RuntimeException("pollUntilNotEmpty: no non-empty value obtained within " + timeoutMs + " ms");
    }

    public static String pollUntilNotEmpty(Supplier<String> supplier) {
        return pollUntilNotEmpty(supplier, TestConfig.DEFAULT_TIMEOUT_MS, 500);
    }

    public static void retry(int maxAttempts, Runnable action) {
        Exception last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                action.run();
                return;
            } catch (Exception e) {
                last = e;
                log.warn("Attempt " + attempt + "/" + maxAttempts + " failed: " + e.getMessage());
                if (attempt < maxAttempts) sleep(500);
            }
        }
        throw new RuntimeException("All " + maxAttempts + " attempts failed", last);
    }

    public static <T> T retry(int maxAttempts, Callable<T> action) {
        Exception last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.call();
            } catch (Exception e) {
                last = e;
                log.warn("Attempt " + attempt + "/" + maxAttempts + " failed: " + e.getMessage());
                if (attempt < maxAttempts) sleep(500);
            }
        }
        throw new RuntimeException("All " + maxAttempts + " attempts failed", last);
    }

    /**
     * Pauses execution — prefer explicit waits; use only as a last resort.
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("sleep interrupted: " + e.getMessage());
        }
    }
}