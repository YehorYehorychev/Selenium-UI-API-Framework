package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.data.GraphqlQueries;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.context.ApiContext;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.context.ScenarioContextKeys;
import com.yehorychev.selenium.context.ScenarioSoftAssertions;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;

import java.util.Map;

import static org.testng.Assert.*;

@Feature("API — Password Reset")
@Story("Password Reset Flow")
public class PasswordResetSteps {

    private static final Logger log = new Logger(PasswordResetSteps.class);

    private final ApiContext api;
    private final ScenarioContext scenarioContext;
    private final ScenarioSoftAssertions soft;

    public PasswordResetSteps(ApiContext api, ScenarioContext scenarioContext, ScenarioSoftAssertions soft) {
        this.api = api;
        this.scenarioContext = scenarioContext;
        this.soft = soft;
    }

    @When("I request a password reset for email {string}")
    public void iRequestAPasswordResetForEmail(String email) {
        Map<String, Object> vars = Map.of(
                "email", email,
                "redirectUrl", "https://mobalytics.gg"
        );
        Response response = api.graphql(GraphqlQueries.REQUEST_PASSWORD_RESET, vars);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("RequestPasswordReset(" + email + ") → HTTP " + response.getStatusCode());
    }

    @When("I reset my password using token {string} and new password {string}")
    public void iResetMyPasswordUsingTokenAndNewPassword(String token, String password) {
        Map<String, Object> vars = Map.of("token", token, "password1", password);
        Response response = api.graphql(GraphqlQueries.RESET_PASSWORD, vars);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("ResetPassword(token=" + token + ") → HTTP " + response.getStatusCode());
    }

    @When("I update my password from {string} to {string}")
    public void iUpdateMyPasswordFromTo(String oldPassword, String newPassword) {
        Map<String, Object> vars = Map.of("oldPassword", oldPassword, "newPassword", newPassword);
        Response response = api.graphql(GraphqlQueries.UPDATE_PASSWORD, vars);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("UpdatePassword → HTTP " + response.getStatusCode());
    }

    @Then("the password reset request should have returned true")
    public void thePasswordResetRequestShouldHaveReturnedTrue() {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call a password reset step first?");
        Boolean result = response.jsonPath().getBoolean("data.requestPasswordReset");
        assertEquals(result, Boolean.TRUE,
                "Expected requestPasswordReset to return true but got: " + response.getBody().asString());
        log.info("Password reset request returned true");
    }
}

