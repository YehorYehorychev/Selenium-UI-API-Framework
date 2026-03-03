package yehorychev.selenium.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Cucumber TestNG runner — analogue of the Playwright test runner configuration.
 *
 * <p>Connects Cucumber's BDD engine to TestNG so that scenarios are discovered,
 * executed, and reported through the standard Maven Surefire / Allure pipeline.
 *
 * <p>Configuration:
 * <ul>
 *   <li>{@code features}   — path to {@code .feature} files</li>
 *   <li>{@code glue}       — packages containing step definitions, hooks, and context</li>
 *   <li>{@code plugin}     — report formatters (pretty console, Allure JSON)</li>
 *   <li>{@code monochrome} — clean console output without ANSI colour codes</li>
 *   <li>{@code tags}       — run only scenarios matching this tag expression</li>
 * </ul>
 *
 * <p>Parallel execution is controlled by TestNG's {@code @DataProvider(parallel = true)}
 * together with Maven Surefire's thread count configuration.
 */
@CucumberOptions(
        features   = "src/test/resources/features",
        glue       = {
                "yehorychev.selenium.hooks",
                "yehorychev.selenium.steps",
                "yehorychev.selenium.context"
        },
        plugin     = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "json:target/cucumber-reports/cucumber.json",
                "html:target/cucumber-reports/cucumber.html"
        },
        monochrome = true,
        tags       = "not @ignore"
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables parallel scenario execution when {@code parallel = true}.
     *
     * <p>The thread count is governed by the TestNG suite XML or Surefire config.
     * Set {@code parallel = false} to run scenarios sequentially (useful for debugging).
     *
     * @return scenario data provider for TestNG
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}

