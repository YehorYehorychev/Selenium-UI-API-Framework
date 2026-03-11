package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Retry tracking hook — records how many times a scenario has been attempted,
 * enriches the Allure report with retry metadata, and marks retried-but-passed
 * scenarios as flaky so they are visible in the report.
 * Hook order relative to other hooks:
 *
 * @Before order = -10  — RetryHook.trackAttempt   (runs before everything else)
 * @Before order =  0   — DriverHooks.setUp
 * @Before order =  1   — ApiHooks.setUpApi
 * @Before order =  2   — AuthHooks.setUpAuthentication
 * @After order = 20   — RetryHook.recordOutcome  (runs after all teardown)
 * @After order = 10   — DriverHooks.captureFailure
 * @After order =  5   — ApiHooks.tearDownApi
 * @After order =  3   — AuthHooks.tearDown
 * @After order =  0   — DriverHooks.tearDown
 * Context keys written to ScenarioContext:
 * retry.attemptNumber — 1-based attempt index for the current run
 * retry.totalAttempts — total allowed attempts (RETRY_COUNT + 1)
 * retry.wasRetried    — true if at least one previous attempt failed
 * Requires RETRY_COUNT > 0 in TestConfig to have any effect.
 * When retries are disabled the hook is still registered but simply logs attempt 1/1.
 * PicoContainer injects ScenarioContext per-scenario — no static mutable state
 * is shared between scenarios.
 */
public class RetryHook {

    // ── Constants ─────────────────────────────────────────────────────────────

    /**
     * ScenarioContext key — current attempt number (1-based).
     */
    public static final String KEY_ATTEMPT_NUMBER = "retry.attemptNumber";

    /**
     * ScenarioContext key — total allowed attempts.
     */
    public static final String KEY_TOTAL_ATTEMPTS = "retry.totalAttempts";

    /**
     * ScenarioContext key — true when this is a retry run.
     */
    public static final String KEY_WAS_RETRIED = "retry.wasRetried";

    // ── Cross-retry state (static — must survive between PicoContainer resets) ──

    /**
     * Tracks per-scenario-ID attempt counts across TestNG retries.
     * Keyed by Scenario#getId() which is stable across retries.
     * ConcurrentHashMap — safe for parallel execution.
     */
    private static final ConcurrentHashMap<String, AtomicInteger> ATTEMPT_COUNTERS =
            new ConcurrentHashMap<>();

    // ── Instance state ────────────────────────────────────────────────────────

    private static final Logger log = new Logger(RetryHook.class);

    private final ScenarioContext scenarioContext;

    /**
     * PicoContainer constructor injection.
     *
     * @param scenarioContext per-scenario state store
     */
    public RetryHook(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    // ── Before ───────────────────────────────────────────────────────────────

    /**
     * Fires before every scenario (order = -10, so before all other hooks).
     * Increments the attempt counter and stores retry metadata in ScenarioContext
     * and as Allure parameters.
     *
     * @param scenario current Cucumber scenario
     */
    @Before(order = -10)
    public void trackAttempt(Scenario scenario) {
        String id = scenario.getId();

        // Atomically increment — handles parallel runs correctly
        AtomicInteger counter = ATTEMPT_COUNTERS.computeIfAbsent(id, k -> new AtomicInteger(0));
        int attemptNumber = counter.incrementAndGet();

        boolean wasRetried = attemptNumber > 1;
        int maxAttempts = TestConfig.RETRY_COUNT + 1; // first run + retries

        scenarioContext.set(KEY_ATTEMPT_NUMBER, attemptNumber);
        scenarioContext.set(KEY_TOTAL_ATTEMPTS, maxAttempts);
        scenarioContext.set(KEY_WAS_RETRIED, wasRetried);

        if (wasRetried) {
            log.step(String.format(
                    "↺ RETRY attempt %d / %d for scenario: [%s] %s",
                    attemptNumber, maxAttempts, id, scenario.getName()
            ));
            Allure.parameter("Retry Attempt", attemptNumber + " / " + maxAttempts);
            Allure.description("This scenario is being retried (attempt " + attemptNumber
                    + " of " + maxAttempts + ").");
        } else {
            log.debug(String.format(
                    "Attempt 1 / %d for scenario: [%s] %s",
                    maxAttempts, id, scenario.getName()
            ));
        }
    }

    // ── After ─────────────────────────────────────────────────────────────────

    /**
     * Fires after every scenario (order = 20, so after all teardown hooks).
     * Outcomes handled:
     * - Passed after retry  — adds Allure labels flaky=true, testType=flaky
     * - Failed, retries remaining — logs how many attempts are left
     * - Failed, retries exhausted — logs final failure, cleans up counter
     * - Passed first time   — cleans up the attempt counter
     *
     * @param scenario current Cucumber scenario (result is available here)
     */
    @After(order = 20)
    public void recordOutcome(Scenario scenario) {
        String id = scenario.getId();
        boolean wasRetried = Boolean.TRUE.equals(scenarioContext.<Boolean>get(KEY_WAS_RETRIED));
        boolean passed = !scenario.isFailed();
        int attemptNumber = scenarioContext.getOrDefault(KEY_ATTEMPT_NUMBER, 1);
        int maxAttempts = scenarioContext.getOrDefault(KEY_TOTAL_ATTEMPTS, 1);

        if (passed && wasRetried) {
            // Retried-but-passed → mark as flaky
            log.warn(String.format(
                    "⚠ FLAKY scenario passed on attempt %d / %d: [%s] %s",
                    attemptNumber, maxAttempts, id, scenario.getName()
            ));
            Allure.label("flaky", "true");
            Allure.label("testType", "flaky");
            Allure.description("Flaky scenario — passed on retry attempt "
                    + attemptNumber + " of " + maxAttempts + ".");
            // Keep counter so subsequent runs can detect continued flakiness
        } else if (!passed) {
            // Still failing
            int retriesLeft = maxAttempts - attemptNumber;
            if (retriesLeft > 0) {
                log.warn(String.format(
                        "✗ Scenario failed on attempt %d / %d — %d retry(ies) remaining: [%s] %s",
                        attemptNumber, maxAttempts, retriesLeft, id, scenario.getName()
                ));
            } else {
                log.warn(String.format(
                        "✗ Scenario FAILED after %d attempt(s) — retries exhausted: [%s] %s",
                        attemptNumber, id, scenario.getName()
                ));
                Allure.label("flaky", "exhausted");
                // Clean up counter on definitive failure so a re-run starts fresh
                ATTEMPT_COUNTERS.remove(id);
            }
        } else {
            // Passed first time — clean up counter
            ATTEMPT_COUNTERS.remove(id);
            log.debug("Scenario passed on first attempt — counter removed for: " + id);
        }
    }
}
