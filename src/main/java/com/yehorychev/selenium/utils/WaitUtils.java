package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Fluent wait, retry and polling helpers.
 *
 * <p>All methods are static — no instantiation needed.
 *
 * <p>Usage:
 * <pre>{@code
 *   // Wait for a custom condition with default timeout
 *   WaitUtils.waitFor(driver, d -> d.getTitle().contains("Dashboard"));
 *
 *   // Poll until a supplier returns a non-null / non-empty value
 *   String token = WaitUtils.pollUntil(() -> getAuthToken(), 10_000);
 *
 *   // Retry a flaky action up to N times
 *   WaitUtils.retry(3, () -> driver.findElement(By.id("btn")).click());
 *
 *   // Wait for the page to finish loading
 *   WaitUtils.waitForPageLoad(driver);
 * }</pre>
 */
public final class WaitUtils {

    private static final Logger log = new Logger(WaitUtils.class);

    private WaitUtils() {
    }

    // ── Generic condition waits ───────────────────────────────────────────────

    /**
     * Waits up to the default timeout for the given condition to become true.
     *
     * @param driver    active WebDriver
     * @param condition expected condition to evaluate
     * @param <T>       return type of the condition
     * @return the condition's return value
     */
    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition) {
        return waitFor(driver, condition, TestConfig.DEFAULT_TIMEOUT_MS);
    }

    /**
     * Waits up to {@code timeoutMs} for the given condition to become true.
     *
     * @param driver    active WebDriver
     * @param condition expected condition to evaluate
     * @param timeoutMs maximum wait time in milliseconds
     * @param <T>       return type of the condition
     * @return the condition's return value
     */
    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition, long timeoutMs) {
        return new WebDriverWait(driver, Duration.ofMillis(timeoutMs)).until(condition);
    }

    /**
     * Waits until the given URL fragment appears in the browser address bar.
     *
     * @param driver      active WebDriver
     * @param urlFragment substring expected to be present in the URL
     */
    public static void waitForUrl(WebDriver driver, String urlFragment) {
        log.step("Waiting for URL to contain: " + urlFragment);
        waitFor(driver, ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Waits until the page title contains the expected fragment.
     *
     * @param driver        active WebDriver
     * @param titleFragment substring expected in the title
     */
    public static void waitForTitle(WebDriver driver, String titleFragment) {
        log.step("Waiting for title to contain: " + titleFragment);
        waitFor(driver, ExpectedConditions.titleContains(titleFragment));
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    /**
     * Waits until the browser's {@code document.readyState} equals {@code "complete"},
     * meaning all resources have been loaded.
     *
     * @param driver active WebDriver
     */
    public static void waitForPageLoad(WebDriver driver) {
        log.step("Waiting for page to fully load");
        waitFor(driver, d -> {
            String state = (String) ((JavascriptExecutor) d)
                    .executeScript("return document.readyState");
            return "complete".equals(state);
        });
    }

    /**
     * Waits until any active jQuery AJAX requests have finished.
     * Safe to call even on pages that do not use jQuery — returns immediately.
     *
     * @param driver active WebDriver
     */
    public static void waitForAjax(WebDriver driver) {
        log.step("Waiting for jQuery AJAX to finish");
        try {
            waitFor(driver, d -> {
                Object active = ((JavascriptExecutor) d)
                        .executeScript("return (typeof jQuery !== 'undefined') ? jQuery.active : 0");
                return Long.parseLong(active.toString()) == 0;
            });
        } catch (Exception e) {
            log.warn("waitForAjax skipped: " + e.getMessage());
        }
    }

    // ── Element state waits ───────────────────────────────────────────────────

    /**
     * Waits until the element's text changes to a value different from the one
     * captured before an action.
     *
     * @param driver  active WebDriver
     * @param element the element whose text is being observed
     * @param oldText the text that was present before triggering the change
     */
    public static void waitForTextChange(WebDriver driver, WebElement element, String oldText) {
        log.step("Waiting for text to change from: \"" + oldText + "\"");
        waitFor(driver, ExpectedConditions.not(
                ExpectedConditions.textToBePresentInElement(element, oldText)));
    }

    /**
     * Waits until the element's attribute equals the expected value.
     *
     * @param driver    active WebDriver
     * @param element   target element
     * @param attribute attribute name (e.g. {@code "value"}, {@code "class"})
     * @param value     expected attribute value
     */
    public static void waitForAttributeValue(
            WebDriver driver, WebElement element, String attribute, String value) {
        log.step("Waiting for attribute \"" + attribute + "\" to equal: " + value);
        waitFor(driver, ExpectedConditions.attributeToBe(element, attribute, value));
    }

    // ── Polling ───────────────────────────────────────────────────────────────

    /**
     * Polls the given {@link Supplier} every {@code pollIntervalMs} until it
     * returns a non-null, non-empty string, or until {@code timeoutMs} elapses.
     *
     * <p>Useful for waiting on async state that is not directly reflected in the DOM
     * (e.g. a value stored in localStorage, a network response flag, etc.).
     *
     * @param supplier       value provider to poll
     * @param timeoutMs      maximum total wait time in milliseconds
     * @param pollIntervalMs sleep duration between polls in milliseconds
     * @return the first non-null, non-empty value returned by the supplier
     * @throws RuntimeException if the timeout expires before a value is obtained
     */
    public static String pollUntilNotEmpty(
            Supplier<String> supplier, long timeoutMs, long pollIntervalMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            String value = supplier.get();
            if (value != null && !value.isBlank()) {
                return value;
            }
            sleep(pollIntervalMs);
        }
        throw new RuntimeException(
                "pollUntilNotEmpty: no non-empty value obtained within " + timeoutMs + " ms");
    }

    /**
     * Polls the given {@link Supplier} every 500 ms until it returns a non-null,
     * non-empty string, using the default timeout from {@link TestConfig}.
     *
     * @param supplier value provider to poll
     * @return the first non-null, non-empty value returned by the supplier
     */
    public static String pollUntilNotEmpty(Supplier<String> supplier) {
        return pollUntilNotEmpty(supplier, TestConfig.DEFAULT_TIMEOUT_MS, 500);
    }

    // ── Retry ─────────────────────────────────────────────────────────────────

    /**
     * Retries a {@link Runnable} action up to {@code maxAttempts} times.
     *
     * <p>Each failed attempt is logged. If all attempts fail, the last exception
     * is re-thrown.
     *
     * @param maxAttempts maximum number of attempts (must be ≥ 1)
     * @param action      action to execute
     * @throws RuntimeException wrapping the last caught exception if all attempts fail
     */
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

    /**
     * Retries a {@link Callable} action up to {@code maxAttempts} times and
     * returns the result on the first success.
     *
     * @param maxAttempts maximum number of attempts (must be ≥ 1)
     * @param action      callable returning a result
     * @param <T>         return type
     * @return result of the first successful call
     * @throws RuntimeException wrapping the last caught exception if all attempts fail
     */
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

    // ── Hard pause (use sparingly) ────────────────────────────────────────────

    /**
     * Pauses execution for the given number of milliseconds.
     *
     * <p><strong>Prefer explicit waits</strong> over this method. Use only as a
     * last resort for cases where no DOM/state condition can be observed.
     *
     * @param ms sleep duration in milliseconds
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

