package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.data.GraphqlQueries;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.context.ApiContext;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.context.ScenarioContextKeys;
import com.yehorychev.selenium.context.ScenarioSoftAssertions;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;

import java.util.Map;

import static org.testng.Assert.*;

@Feature("API — Account Management")
@Story("Account Info & Password Updates")
public class AccountManagementSteps {

    private static final Logger log = new Logger(AccountManagementSteps.class);

    private final ApiContext api;
    private final ScenarioContext scenarioContext;
    private final ScenarioSoftAssertions soft;

    public AccountManagementSteps(ApiContext api, ScenarioContext scenarioContext, ScenarioSoftAssertions soft) {
        this.api = api;
        this.scenarioContext = scenarioContext;
        this.soft = soft;
    }

    @When("I update my account login to {string}")
    public void iUpdateMyAccountLoginTo(String login) {
        Map<String, Object> vars = Map.of("login", login);
        Response response = api.graphql(GraphqlQueries.UPDATE_ACCOUNT_INFO, vars);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("UpdateAccountInfo(login=" + login + ") → HTTP " + response.getStatusCode());
    }

    @When("I update my account login to the saved context value {string}")
    public void iUpdateMyAccountLoginToTheSavedContextValue(String contextKey) {
        String login = scenarioContext.get(contextKey);
        assertNotNull(login, "No value found in scenario context under key: \"" + contextKey + "\"");
        iUpdateMyAccountLoginTo(login);
    }

    @Then("the account info update should have succeeded")
    public void theAccountInfoUpdateShouldHaveSucceeded() {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call an update step first?");
        String body = response.getBody().asString();
        assertFalse(body.contains("\"errors\""),
                "Expected updateAccountInfo to succeed but got errors:\n" + body);
        assertNotNull(response.jsonPath().get("data.updateAccountInfo"),
                "Expected data.updateAccountInfo to be non-null but body was:\n" + body);
        log.info("Account info update verified as successful");
    }

    @And("the response should contain GraphQL error {string}")
    public void theResponseShouldContainGraphQLError(String expectedError) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        String body = response.getBody().asString();
        assertTrue(body.contains(expectedError),
                "Expected GraphQL error \"" + expectedError + "\" in response body but body was:\n" + body);
        log.info("GraphQL error \"" + expectedError + "\" verified");
    }
}

