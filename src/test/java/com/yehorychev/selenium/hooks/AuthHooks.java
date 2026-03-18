package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.helpers.AuthHelper;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.Map;

/**
 * Authentication hooks — logs in via API before @authenticated scenarios and clears state after.
 */
public class AuthHooks {

    private static final Logger log = new Logger(AuthHooks.class);

    private final DriverContext driverContext;
    private final ScenarioContext scenarioContext;

    public AuthHooks(DriverContext driverContext, ScenarioContext scenarioContext) {
        this.driverContext = driverContext;
        this.scenarioContext = scenarioContext;
    }

    @Before(value = "@authenticated", order = 2)
    public void setUpAuthentication(Scenario scenario) {
        log.step("Setting up authenticated session for scenario: " + scenario.getName());

        // For pure API scenarios auth is handled by the step definition
        if (!driverContext.isReady()) {
            log.debug("API-only scenario — auth will be handled by step definition, skipping hook");
            return;
        }

        try {
            Map<String, String> authData = AuthHelper.loginViaApi();
            AuthHelper.injectAuthIntoDriver(driverContext.getDriver(), authData);
            scenarioContext.set("authToken", authData.get(AuthHelper.KEY_SIGNED_IN));
            log.info("Authenticated session established for: " + scenario.getName());
        } catch (Exception e) {
            log.warn("Authentication setup failed: " + e.getMessage());
            throw e;
        }
    }

    @After(value = "@authenticated", order = 3)
    public void tearDownAuthentication(Scenario scenario) {
        log.step("■ [Auth] Clearing authenticated session for: " + scenario.getName());
        try {
            if (driverContext.isReady()) {
                driverContext.getDriver().manage().deleteAllCookies();
                log.debug("Browser cookies cleared");
            }
            scenarioContext.remove("authToken");
        } catch (Exception e) {
            log.warn("Failed to clear auth session: " + e.getMessage());
        }
    }
}
