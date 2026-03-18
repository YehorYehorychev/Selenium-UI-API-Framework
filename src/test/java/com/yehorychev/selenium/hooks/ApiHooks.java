package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.slf4j.MDC;

/**
 * API lifecycle hooks — configures RestAssured before @api scenarios and resets it after.
 */
public class ApiHooks {

    private static final Logger log = new Logger(ApiHooks.class);

    @Before(value = "@api", order = 1)
    public void setUpApi(Scenario scenario) {
        MDC.put("scenario", scenario.getName());
        MDC.put("scenarioId", scenario.getId());
        log.step("▶ [API] Setting up RestAssured for scenario: " + scenario.getName());

        RestAssured.baseURI = TestConfig.API_BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Verbose logging in non-headless (local debug) mode
        if (!TestConfig.HEADLESS) {
            RestAssured.replaceFiltersWith(new RequestLoggingFilter(), new ResponseLoggingFilter());
        }

        log.info("RestAssured ready — baseURI: " + TestConfig.API_BASE_URL);
    }

    @After(value = "@api", order = 5)
    public void tearDownApi(Scenario scenario) {
        log.step("■ [API] Resetting RestAssured after scenario: "
                + scenario.getName() + " — " + scenario.getStatus());
        RestAssured.reset();
        MDC.clear();
    }
}
