package yehorychev.selenium.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

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
 *   tags       — run only scenarios matching this tag expression
 *
 * Parallel execution is controlled by TestNG's @DataProvider(parallel = true)
 * together with Maven Surefire's thread count configuration.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "yehorychev.selenium.hooks",
                "yehorychev.selenium.steps"
        },
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "json:target/cucumber-reports/cucumber.json",
                "html:target/cucumber-reports/cucumber.html"
        },
        monochrome = true,
        tags = "not @ignore"
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables parallel scenario execution when parallel = true.
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
}
