package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.context.ScenarioContextKeys;
import com.yehorychev.selenium.helpers.AuthHelper;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.Map;
import java.util.Set;

/**
 * Authentication hooks — logs in via API before @authenticated scenarios and clears state after.
 */
public class AuthHooks {

    private static final Logger log = new Logger(AuthHooks.class);

    /** Keys in the authData map that are metadata, not actual HTTP cookies. */
    private static final Set<String> NON_COOKIE_KEYS = Set.of(AuthHelper.KEY_SIGNED_IN, "email");

    private final DriverContext driverContext;
    private final ScenarioContext scenarioContext;

    public AuthHooks(DriverContext driverContext, ScenarioContext scenarioContext) {
        this.driverContext = driverContext;
        this.scenarioContext = scenarioContext;
    }

    @Before(value = "@authenticated", order = 2)
    public void setUpAuthentication(Scenario scenario) {
        log.step("Setting up authenticated session for scenario: " + scenario.getName());

        if (!driverContext.isReady()) {
            log.debug("API-only scenario — auth will be handled by step definition, skipping hook");
            return;
        }

        try {
            Map<String, String> authData = AuthHelper.loginViaApi();
            AuthHelper.injectAuthIntoDriver(driverContext.getDriver(), authData);
            scenarioContext.set(ScenarioContextKeys.IS_AUTHENTICATED, authData.get(AuthHelper.KEY_SIGNED_IN));

            // Store only the real session cookies (exclude metadata keys) so teardown can call signOut
            Map<String, String> sessionCookies = authData.entrySet().stream()
                    .filter(e -> !NON_COOKIE_KEYS.contains(e.getKey()))
                    .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            scenarioContext.set(ScenarioContextKeys.AUTH_COOKIES, sessionCookies);

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
            Map<String, String> cookies = scenarioContext.get(ScenarioContextKeys.AUTH_COOKIES);
            if (cookies != null && !cookies.isEmpty()) {
                try {
                    AuthHelper.logoutViaApi(cookies);
                    log.debug("Server-side session invalidated via signOut");
                } catch (Exception e) {
                    log.warn("Server-side logout failed (session may already be expired): " + e.getMessage());
                }
            }

            if (driverContext.isReady()) {
                driverContext.getDriver().manage().deleteAllCookies();
                log.debug("Browser cookies cleared");
            }

            scenarioContext.remove(ScenarioContextKeys.IS_AUTHENTICATED);
            scenarioContext.remove(ScenarioContextKeys.AUTH_COOKIES);
        } catch (Exception e) {
            log.warn("Failed to clear auth session: " + e.getMessage());
        }
    }
}