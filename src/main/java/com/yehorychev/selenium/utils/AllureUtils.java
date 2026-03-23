package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.helpers.Logger;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Centralised Allure report utilities — all direct {@code Allure.*} calls
 * in hooks and steps should go through here for consistency.
 */
public final class AllureUtils {

    private static final Logger log = new Logger(AllureUtils.class);

    private AllureUtils() {
    }

    /**
     * Attaches the current page HTML source as an {@code .html} attachment.
     * Silently skips if the driver is unresponsive.
     */
    public static void attachPageSource(WebDriver driver, String name) {
        try {
            String source = driver.getPageSource();
            if (source == null || source.isBlank()) return;
            Allure.addAttachment(name, "text/html",
                    new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), ".html");
        } catch (Exception e) {
            log.debug("Could not attach page source: " + e.getMessage());
        }
    }

    /**
     * Attaches plain text content as a {@code .txt} attachment.
     * No-op if content is blank.
     */
    public static void attachText(String content, String name) {
        if (content == null || content.isBlank()) return;
        Allure.addAttachment(name, "text/plain",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), ".txt");
    }

    /**
     * Runs {@code body} inside a named Allure step, reporting pass/fail automatically.
     */
    public static void step(String name, Runnable body) {
        String uuid = UUID.randomUUID().toString();
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(name).setStatus(Status.PASSED));
        try {
            body.run();
            Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.PASSED));
        } catch (Throwable t) {
            Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.FAILED));
            throw t;
        } finally {
            Allure.getLifecycle().stopStep(uuid);
        }
    }

    /**
     * Runs {@code body} inside a named Allure step, returns its result, and reports pass/fail.
     */
    public static <T> T step(String name, Callable<T> body) {
        String uuid = UUID.randomUUID().toString();
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(name).setStatus(Status.PASSED));
        try {
            T result = body.call();
            Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.PASSED));
            return result;
        } catch (Throwable t) {
            Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.FAILED));
            if (t instanceof RuntimeException re) throw re;
            throw new RuntimeException(t);
        } finally {
            Allure.getLifecycle().stopStep(uuid);
        }
    }

    /**
     * Adds a key-value parameter to the current Allure test result.
     */
    public static void addParameter(String name, Object value) {
        Allure.parameter(name, value);
    }
}

