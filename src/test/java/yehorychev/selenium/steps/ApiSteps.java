package yehorychev.selenium.steps;

import com.yehorychev.selenium.data.GraphqlQueries;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import yehorychev.selenium.context.ApiContext;
import yehorychev.selenium.context.ScenarioContext;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Step definitions for REST and GraphQL API tests.
 *
 * Stores the last API response in ScenarioContext under key "lastResponse"
 * so assertion steps can access it without coupling to specific endpoints.
 *
 * PicoContainer injects ApiContext and ScenarioContext per-scenario.
 */
public class ApiSteps {

    private static final Logger log = new Logger(ApiSteps.class);
    private static final String LAST_RESPONSE = "lastResponse";

    private final ApiContext api;
    private final ScenarioContext scenarioContext;

    public ApiSteps(ApiContext api, ScenarioContext scenarioContext) {
        this.api = api;
        this.scenarioContext = scenarioContext;
    }

    // ── Generic HTTP steps ────────────────────────────────────────────────────

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        Response response = api.get(endpoint);
        scenarioContext.set(LAST_RESPONSE, response);
        log.step("GET " + endpoint + " → " + response.getStatusCode());
    }

    @When("I send a POST request to {string} with body:")
    public void iSendAPostRequestToWithBody(String endpoint, String body) {
        Response response = api.post(endpoint, body);
        scenarioContext.set(LAST_RESPONSE, response);
        log.step("POST " + endpoint + " → " + response.getStatusCode());
    }

    // ── GraphQL steps ─────────────────────────────────────────────────────────

    @When("I query the current user via GraphQL")
    public void iQueryTheCurrentUserViaGraphQL() {
        Response response = api.graphql(GraphqlQueries.GET_CURRENT_USER);
        scenarioContext.set(LAST_RESPONSE, response);
        log.step("GraphQL GetCurrentUser → " + response.getStatusCode());
    }

    @When("I query the list of supported games via GraphQL")
    public void iQueryTheListOfSupportedGamesViaGraphQL() {
        Response response = api.graphql(GraphqlQueries.GET_GAMES);
        scenarioContext.set(LAST_RESPONSE, response);
        log.step("GraphQL GetGames → " + response.getStatusCode());
    }

    @When("I query summoner stats for {string} in region {string}")
    public void iQuerySummonerStatsFor(String summonerName, String region) {
        Map<String, Object> variables = Map.of(
                "summonerName", summonerName,
                "region", region
        );
        Response response = api.graphql(GraphqlQueries.GET_SUMMONER_STATS, variables);
        scenarioContext.set(LAST_RESPONSE, response);
        log.step("GraphQL GetSummonerStats(" + summonerName + ", " + region + ") → " + response.getStatusCode());
    }

    @When("I query user profile for id {string} via GraphQL")
    public void iQueryUserProfileFor(String userId) {
        Map<String, Object> variables = Map.of("userId", userId);
        Response response = api.graphql(GraphqlQueries.GET_USER_PROFILE, variables);
        scenarioContext.set(LAST_RESPONSE, response);
        log.step("GraphQL GetUserProfile(" + userId + ") → " + response.getStatusCode());
    }

    // ── Response assertions ───────────────────────────────────────────────────

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatus) {
        Response response = scenarioContext.get(LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call a request step first?");
        assertEquals(
                response.getStatusCode(),
                expectedStatus,
                "Expected HTTP " + expectedStatus + " but got " + response.getStatusCode()
        );
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expected) {
        Response response = scenarioContext.get(LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        assertTrue(
                response.getBody().asString().contains(expected),
                "Expected response body to contain \"" + expected
                        + "\" but body was:\n" + response.getBody().asString()
        );
    }

    @Then("the response JSON path {string} should not be null")
    public void theResponseJsonPathShouldNotBeNull(String jsonPath) {
        Response response = scenarioContext.get(LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        Object value = response.jsonPath().get(jsonPath);
        assertNotNull(value, "Expected JSON path \"" + jsonPath + "\" to be non-null");
    }

    @Then("the response JSON path {string} should equal {string}")
    public void theResponseJsonPathShouldEqual(String jsonPath, String expected) {
        Response response = scenarioContext.get(LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        String actual = response.jsonPath().getString(jsonPath);
        assertEquals(actual, expected,
                "Expected JSON path \"" + jsonPath + "\" = \"" + expected
                        + "\" but was \"" + actual + "\"");
    }

    @And("I save response JSON path {string} as {string}")
    public void iSaveResponseJsonPathAs(String jsonPath, String key) {
        Response response = scenarioContext.get(LAST_RESPONSE);
        assertNotNull(response, "No API response stored");
        Object value = response.jsonPath().get(jsonPath);
        scenarioContext.set(key, value);
        log.debug("Saved JSON path \"" + jsonPath + "\" as \"" + key + "\": " + value);
    }
}

