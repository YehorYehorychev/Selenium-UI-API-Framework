package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.data.GraphqlQueries;
import com.yehorychev.selenium.data.TestData;
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

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

@Feature("API — Registration")
@Story("Account Sign-Up")
public class RegistrationSteps {

    private static final Logger log = new Logger(RegistrationSteps.class);

    private final ApiContext api;
    private final ScenarioContext scenarioContext;
    private final ScenarioSoftAssertions soft;

    public RegistrationSteps(ApiContext api, ScenarioContext scenarioContext, ScenarioSoftAssertions soft) {
        this.api = api;
        this.scenarioContext = scenarioContext;
        this.soft = soft;
    }

    @When("I sign up via API with email {string}, password {string} and name {string}")
    public void iSignUpViaApiWithEmailPasswordAndName(String email, String password, String name) {
        Map<String, Object> vars = Map.of(
                "email", email,
                "password", password,
                "name", name,
                "continueFrom", ""
        );
        Response response = api.graphql(GraphqlQueries.SIGN_UP, vars);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
        log.step("SignUp(" + email + ") → HTTP " + response.getStatusCode());
    }

    @When("I sign up via API with the configured test email, password {string} and name {string}")
    public void iSignUpViaApiWithConfiguredTestEmail(String password, String name) {
        iSignUpViaApiWithEmailPasswordAndName(TestData.Credentials.LOGIN, password, name);
    }

    @Then("the sign-up should have succeeded")
    public void theSignUpShouldHaveSucceeded() {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call a sign-up step first?");
        Object signUpValue = response.jsonPath().get("data.signUp");
        assertEquals(signUpValue, Boolean.TRUE,
                "Expected signUp to return true but got: " + response.getBody().asString());
        log.info("Sign-up success verified");
    }

    @Then("the sign-up should have failed with error {string}")
    public void theSignUpShouldHaveFailedWithError(String expectedError) {
        Response response = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE);
        assertNotNull(response, "No API response stored — did you call a sign-up step first?");
        List<String> errorMessages = response.jsonPath().getList("errors.message");
        assertTrue(
                errorMessages != null && errorMessages.contains(expectedError),
                "Expected GraphQL error \"" + expectedError + "\" in errors[*].message "
                        + "but response was:\n" + response.getBody().asString());
        log.info("Sign-up error \"" + expectedError + "\" verified via jsonPath errors.message");
    }
}