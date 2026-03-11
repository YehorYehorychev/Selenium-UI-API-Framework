package com.yehorychev.selenium.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Cucumber TestNG runner — connects Cucumber's BDD engine to TestNG.
 *
 * Scenarios are discovered, executed, and reported through the standard
 * Maven Surefire / Allure pipeline.
 *
 * Configuration:
 *   features   — path to .feature files
 *   glue       — packages containing step definitions, hooks, and context
 *   plugin     — report formatters (pretty console, Allure JSON)
 *   monochrome — clean console output without ANSI colour codes
 *   tags       — run only scenarios NOT tagged with @wip (work-in-progress)
 *
 * Parallel execution is controlled by TestNG's @DataProvider(parallel = true)
 * together with Maven Surefire's thread count configuration.
 *
 * Retry behaviour is governed by RetryAnalyzer and TestConfig.RETRY_COUNT.
 * The default is 2 retries (configurable via retry.count in config.properties
 * or the RETRY_COUNT environment variable). Set to 0 to disable retries.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.yehorychev.selenium.hooks",
                "com.yehorychev.selenium.steps"
        },
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "json:target/cucumber-reports/cucumber.json",
                "html:target/cucumber-reports/cucumber.html"
        },
        monochrome = true,
        tags = "not @wip"
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables parallel scenario execution and wires the RetryAnalyzer.
     *
     * retryAnalyzer is honoured by TestNG for every method that consumes this DataProvider,
     * which maps to every Cucumber scenario. When a scenario fails, TestNG calls
     * RetryAnalyzer.retry() and re-runs the scenario up to TestConfig.RETRY_COUNT times.
     *
     * The thread count is governed by the TestNG suite XML or Surefire config.
     * Set parallel = false to run scenarios sequentially (useful for debugging).
     *
     * @return scenario data provider for TestNG
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    /**
     * Overrides the TestNG test method that executes each Cucumber scenario,
     * attaching RetryAnalyzer so failed scenarios are automatically retried.
     *
     * This override is required because AbstractTestNGCucumberTests declares
     * runScenario() without a retry analyzer — we add it here.
     */
    @Test(description = "Runs Cucumber Scenarios",
            dataProvider = "scenarios",
            retryAnalyzer = RetryAnalyzer.class)
    @Override
    public void runScenario(io.cucumber.testng.PickleWrapper pickleWrapper,
                            io.cucumber.testng.FeatureWrapper featureWrapper) {
        super.runScenario(pickleWrapper, featureWrapper);
    }
}

