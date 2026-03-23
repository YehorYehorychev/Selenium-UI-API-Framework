package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.context.ScenarioContextKeys;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Retry tracking hook — records attempt counts, enriches Allure with retry metadata,
 * and labels retried-but-passed scenarios as flaky.
 */
public class RetryHook {

    // Keyed by Scenario#getId() — survives PicoContainer resets between retries
    private static final ConcurrentHashMap<String, AtomicInteger> ATTEMPT_COUNTERS =
            new ConcurrentHashMap<>();

    private static final Logger log = new Logger(RetryHook.class);

    private final ScenarioContext scenarioContext;

    public RetryHook(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = -10)
    public void trackAttempt(Scenario scenario) {
        String id = scenario.getId();
        AtomicInteger counter = ATTEMPT_COUNTERS.computeIfAbsent(id, k -> new AtomicInteger(0));
        int attemptNumber = counter.incrementAndGet();
        boolean wasRetried = attemptNumber > 1;
        int maxAttempts = TestConfig.RETRY_COUNT + 1;

        scenarioContext.set(ScenarioContextKeys.RETRY_ATTEMPT_NUMBER, attemptNumber);
        scenarioContext.set(ScenarioContextKeys.RETRY_TOTAL_ATTEMPTS, maxAttempts);
        scenarioContext.set(ScenarioContextKeys.RETRY_WAS_RETRIED, wasRetried);

        if (wasRetried) {
            log.step(String.format("↺ RETRY attempt %d / %d for scenario: [%s] %s",
                    attemptNumber, maxAttempts, id, scenario.getName()));
            Allure.parameter("Retry Attempt", attemptNumber + " / " + maxAttempts);
            Allure.description("This scenario is being retried (attempt " + attemptNumber
                    + " of " + maxAttempts + ").");
        } else {
            log.debug(String.format("Attempt 1 / %d for scenario: [%s] %s",
                    maxAttempts, id, scenario.getName()));
        }
    }

    @After(order = 20)
    public void recordOutcome(Scenario scenario) {
        String id = scenario.getId();
        boolean wasRetried = Boolean.TRUE.equals(scenarioContext.<Boolean>get(ScenarioContextKeys.RETRY_WAS_RETRIED));
        boolean passed = !scenario.isFailed();
        int attemptNumber = scenarioContext.getOrDefault(ScenarioContextKeys.RETRY_ATTEMPT_NUMBER, 1);
        int maxAttempts = scenarioContext.getOrDefault(ScenarioContextKeys.RETRY_TOTAL_ATTEMPTS, 1);

        if (passed && wasRetried) {
            log.warn(String.format("⚠ FLAKY scenario passed on attempt %d / %d: [%s] %s",
                    attemptNumber, maxAttempts, id, scenario.getName()));
            Allure.label("flaky", "true");
            Allure.label("testType", "flaky");
            Allure.description("Flaky scenario — passed on retry attempt " + attemptNumber + " of " + maxAttempts + ".");
            ATTEMPT_COUNTERS.remove(id);
        } else if (!passed) {
            int retriesLeft = maxAttempts - attemptNumber;
            if (retriesLeft > 0) {
                log.warn(String.format("✗ Scenario failed on attempt %d / %d — %d retry(ies) remaining: [%s] %s",
                        attemptNumber, maxAttempts, retriesLeft, id, scenario.getName()));
            } else {
                log.warn(String.format("✗ Scenario FAILED after %d attempt(s) — retries exhausted: [%s] %s",
                        attemptNumber, id, scenario.getName()));
                Allure.label("flaky", "exhausted");
                ATTEMPT_COUNTERS.remove(id);
            }
        } else {
            ATTEMPT_COUNTERS.remove(id);
            log.debug("Scenario passed on first attempt — counter removed for: " + id);
        }
    }
}
