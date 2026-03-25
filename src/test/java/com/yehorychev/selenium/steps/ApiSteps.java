package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.GraphqlQueries;
import com.yehorychev.selenium.data.TestData;
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

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.*;

@Feature("API — GraphQL & REST")
@Story("API Requests & Assertions")
public class ApiSteps {

    private static final Logger log = new Logger(ApiSteps.class);

    private final ApiContext api;
    private final ScenarioContext scenarioContext;
    private final ScenarioSoftAssertions soft;

    public ApiSteps(ApiContext api, ScenarioContext scenarioContext, ScenarioSoftAssertions soft) {
        this.api = api;
        this.scenarioContext = scenarioContext;
        this.soft = soft;
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        Response response = api.get(endpoint);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("GET " + endpoint + " → " + response.getStatusCode());
    }

    @When("I send a POST request to {string} with body:")
    public void iSendAPostRequestToWithBody(String endpoint, String body) {
        Response response = api.post(endpoint, body);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("POST " + endpoint + " → " + response.getStatusCode());
    }

    @When("I run the GraphQL health check")
    public void iRunTheGraphqlHealthCheck() {
        Response response = api.graphql(GraphqlQueries.HEALTH_CHECK);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("GraphQL HealthCheck → " + response.getStatusCode());
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatus) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call a request step first?");
        assertEquals(response.getStatusCode(), expectedStatus,
                "Expected HTTP " + expectedStatus + " but got " + response.getStatusCode());
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expected) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        assertTrue(response.getBody().asString().contains(expected),
                "Expected response body to contain \"" + expected + "\" but body was:\n"
                        + response.getBody().asString());
    }

    @Then("the response JSON path {string} should not be null")
    public void theResponseJsonPathShouldNotBeNull(String jsonPath) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        assertNotNull(response.jsonPath().get(jsonPath),
                "Expected JSON path \"" + jsonPath + "\" to be non-null");
    }

    @Then("the response JSON path {string} should equal {string}")
    public void theResponseJsonPathShouldEqual(String jsonPath, String expected) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        String actual = response.jsonPath().getString(jsonPath);
        assertEquals(actual, expected,
                "Expected JSON path \"" + jsonPath + "\" = \"" + expected + "\" but was \"" + actual + "\"");
    }

    @And("I save response JSON path {string} as {string}")
    public void iSaveResponseJsonPathAs(String jsonPath, String key) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        Object value = response.jsonPath().get(jsonPath);
        scenarioContext.set(key, value);
        log.debug("Saved JSON path \"" + jsonPath + "\" as \"" + key + "\": " + value);
    }

    @When("I query the current account via GraphQL")
    public void iQueryTheCurrentAccountViaGraphQL() {
        Response response = api.graphql(GraphqlQueries.ACCOUNT_QUERY);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("GraphQL Account query → " + response.getStatusCode());
    }

    @When("I query the account with partial field selection")
    public void iQueryTheAccountWithPartialFieldSelection() {
        Response response = api.graphql(GraphqlQueries.ACCOUNT_QUERY_PARTIAL);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("GraphQL Account (partial) query → " + response.getStatusCode());
    }

    @And("the account uid should be a valid identifier")
    public void theAccountUidShouldBeAValidIdentifier() {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        String uid = response.jsonPath().getString("data.account.uid");
        soft.assertThat(uid).as("Expected data.account.uid to be non-null").isNotNull();
        if (uid != null) {
            soft.assertThat(uid.length())
                    .as("Expected uid to be at least 8 chars (UUID/nanoid), but was: %s", uid)
                    .isGreaterThanOrEqualTo(8);
        }
        log.info("Account uid check recorded");
    }

    @And("the account email should match the configured test email")
    public void theAccountEmailShouldMatchTheConfiguredTestEmail() {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        String returnedEmail = response.jsonPath().getString("data.account.email");
        String expectedEmail = TestData.Credentials.LOGIN;
        assertNotNull(expectedEmail, "TEST_USER_LOGIN env var is not configured");
        assertEquals(returnedEmail.toLowerCase(), expectedEmail.toLowerCase(),
                "Account email mismatch: expected " + expectedEmail + " but got " + returnedEmail);
        log.info("Account email matches: " + returnedEmail);
    }

    @And("the unauthenticated account response should be rejected")
    public void theUnauthenticatedAccountResponseShouldBeRejected() {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        List<Object> errors = response.jsonPath().getList("errors");
        Object account = response.jsonPath().get("data.account");
        boolean rejected = (errors != null && !errors.isEmpty())
                || account == null
                || response.getStatusCode() >= 400;
        assertTrue(rejected,
                "Expected unauthenticated request to be rejected (errors array present, null account, or 4xx) "
                        + "but response was:\n" + response.getBody().asString());
        log.info("Unauthenticated rejection verified via jsonPath");
    }

    /**
     * Validates the last stored response body against a JSON Schema file on the classpath.
     * Schema files live in {@code src/test/resources/schemas/<schemaName>.json}.
     */
    @Then("the response should match the {string} schema")
    public void theResponseShouldMatchSchema(String schemaName) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call a request step first?");
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/" + schemaName + ".json"));
        log.info("Schema validation passed: schemas/" + schemaName + ".json");
    }

    /**
     * Asserts that the last stored response arrived within the configured API SLA.
     * Uses a soft assertion so it accumulates alongside other failures in the same step.
     */
    @Then("the response time should be within SLA")
    public void theResponseTimeShouldBeWithinSla() {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call a request step first?");
        long actualMs = response.getTime();
        soft.assertThat(actualMs)
                .as("Response time %d ms exceeded API SLA of %d ms", actualMs, TestConfig.API_TIMEOUT_MS)
                .isLessThan(TestConfig.API_TIMEOUT_MS);
        log.info("Response time: " + actualMs + " ms (SLA: " + TestConfig.API_TIMEOUT_MS + " ms)");
    }
}
