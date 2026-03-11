package com.yehorychev.selenium.runner;

import com.yehorychev.selenium.config.TestConfig;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer — re-runs a failed test method up to TestConfig.RETRY_COUNT times.
 *
 * Wired into CucumberRunner via @Test(retryAnalyzer = RetryAnalyzer.class) on runScenario().
 * TestNG creates a fresh instance per test method, so attemptCount is scoped per scenario.
 *
 * How it works:
 *   1. TestNG calls retry() after every failed method invocation.
 *   2. If attemptCount < RETRY_COUNT the method is re-queued and true is returned.
 *   3. Once the limit is reached false is returned and the failure is recorded.
 *
 * Configuration:
 *   # config.properties
 *   retry.count=2    — 0 = no retry, N = up to N re-runs after first failure
 *
 *   # env var override
 *   RETRY_COUNT=2
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    /** How many times this particular test has already been retried. */
    private int attemptCount = 0;

    /**
     * Called by TestNG after a test method fails.
     * Returns true to retry, false when the limit is reached or retries are disabled.
     *
     * @param result the result of the most-recently failed invocation
     * @return true — retry the test; false — do not retry
     */
    @Override
    public boolean retry(ITestResult result) {
        if (TestConfig.RETRY_COUNT <= 0) {
            return false;
        }

        if (attemptCount < TestConfig.RETRY_COUNT) {
            attemptCount++;
            System.out.printf(
                    "[RetryAnalyzer] Retrying '%s' — attempt %d / %d%n",
                    result.getName(), attemptCount, TestConfig.RETRY_COUNT
            );
            return true;
        }

        return false;
    }

    /**
     * Returns the current retry attempt count (1-based after first failure).
     *
     * @return number of retries performed so far
     */
    public int getAttemptCount() {
        return attemptCount;
    }
}
