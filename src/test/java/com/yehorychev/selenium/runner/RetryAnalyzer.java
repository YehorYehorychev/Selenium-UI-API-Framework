package com.yehorychev.selenium.runner;

import com.yehorychev.selenium.config.TestConfig;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer — re-runs a failed scenario up to {@code TestConfig.RETRY_COUNT} times.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attemptCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (TestConfig.RETRY_COUNT <= 0) return false;

        if (attemptCount < TestConfig.RETRY_COUNT) {
            attemptCount++;
            System.out.printf("[RetryAnalyzer] Retrying '%s' — attempt %d / %d%n",
                    result.getName(), attemptCount, TestConfig.RETRY_COUNT);
            return true;
        }

        return false;
    }

    public int getAttemptCount() {
        return attemptCount;
    }
}
