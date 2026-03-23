package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.context.ScenarioSoftAssertions;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

/**
 * Evaluates all accumulated soft assertions at the end of every scenario.
 *
 * <p><b>Execution order:</b> {@code @After(order = 15)} — runs between
 * {@code RetryHook} (order 20, first) and {@code DriverHooks.captureFailureScreenshot}
 * (order 10, third), so a screenshot is captured when soft assertions fail.
 *
 * <p>All soft assertion failures are:
 * <ol>
 *   <li>Logged via the framework logger with the scenario name.</li>
 *   <li>Attached to the Allure report as a plain-text attachment.</li>
 *   <li>Re-thrown so Cucumber marks the scenario as {@code FAILED}.</li>
 * </ol>
 */
public class SoftAssertionsHook {

    private static final Logger log = new Logger(SoftAssertionsHook.class);

    private final ScenarioSoftAssertions softAssertions;

    public SoftAssertionsHook(ScenarioSoftAssertions softAssertions) {
        this.softAssertions = softAssertions;
    }

    @After(order = 15)
    public void assertAllSoft(Scenario scenario) {
        try {
            softAssertions.assertAll();
        } catch (AssertionError e) {
            String details = e.getMessage() != null ? e.getMessage() : e.toString();
            log.warn("Soft assertion failure(s) in scenario [" + scenario.getName() + "]:\n" + details);
            Allure.addAttachment("Soft Assertion Failures", "text/plain", details);
            throw e;
        }
    }
}

