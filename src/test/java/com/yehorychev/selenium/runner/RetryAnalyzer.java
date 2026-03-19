package com.yehorychev.selenium.runner;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer — re-runs a failed scenario up to {@code TestConfig.RETRY_COUNT} times.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = new Logger(RetryAnalyzer.class);

    private int attemptCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (TestConfig.RETRY_COUNT <= 0) return false;

        if (attemptCount < TestConfig.RETRY_COUNT) {
            attemptCount++;
            log.info("Retrying '" + result.getName() + "' — attempt "
                    + attemptCount + " / " + TestConfig.RETRY_COUNT);
            return true;
        }

        return false;
    }

    public int getAttemptCount() {
        return attemptCount;
    }
}
