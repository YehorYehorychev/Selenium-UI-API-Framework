package yehorychev.selenium.steps;

import com.yehorychev.selenium.data.GraphqlQueries;
import com.yehorychev.selenium.data.TestData;
import com.yehorychev.selenium.errors.AuthenticationException;
import com.yehorychev.selenium.helpers.AuthHelper;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import yehorychev.selenium.context.ApiContext;
import yehorychev.selenium.context.DriverContext;
import yehorychev.selenium.context.ScenarioContext;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Step definitions for authentication flows — API-based sign-in/sign-out.
 *
 * Auth is cookie-based via GraphQL signIn mutation.
 * All requests go through ApiContext so the session cookie (CookieFilter)
 * is shared across subsequent GraphQL calls within the same scenario.
 *
 * PicoContainer injects DriverContext, ApiContext and ScenarioContext per-scenario.
 */
public class AuthSteps {

    private static final Logger log = new Logger(AuthSteps.class);
    private static final String AUTH_TOKEN_KEY = "authToken";
    private static final String LAST_RESPONSE  = "lastResponse";

    private final DriverContext driverContext;
    private final ApiContext api;
    private final ScenarioContext scenarioContext;

    public AuthSteps(DriverContext driverContext, ApiContext api, ScenarioContext scenarioContext) {
        this.driverContext = driverContext;
        this.api = api;
        this.scenarioContext = scenarioContext;
    }

    // ── API login steps ───────────────────────────────────────────────────────

    @Given("I am authenticated via API")
    public void iAmAuthenticatedViaApi() {
        log.step("Authenticating via API using configured test credentials");

        // Sign in through ApiContext so the session cookie is stored in the shared
        // RestAssured CookieFilter — subsequent api.graphql() calls in this
        // scenario will automatically include the session cookie.
        Map<String, Object> vars = Map.of(
                "email", TestData.Credentials.LOGIN,
                "password", TestData.Credentials.PASSWORD,
                "continueFrom", ""
        );
        Response signInResponse = api.graphql(GraphqlQueries.SIGN_IN, vars);
        boolean success = Boolean.TRUE.equals(signInResponse.jsonPath().getBoolean("data.signIn"));

        if (!success) {
            throw new AuthenticationException(
                    "GraphQL signIn returned false for: " + TestData.Credentials.LOGIN);
        }

        scenarioContext.set(AUTH_TOKEN_KEY, "true");
        log.info("API authentication successful for: " + TestData.Credentials.LOGIN);
    }

    @When("I sign in via API with email {string} and password {string}")
    public void iSignInViaApiWithEmailAndPassword(String email, String password) {
        log.step("Attempting sign-in via API: " + email);
        // Uses a fresh ApiContext (no cookie injection needed — testing negative path)
        Map<String, Object> vars = Map.of(
                "email", email,
                "password", password,
                "continueFrom", ""
        );
        Response response = api.graphql(GraphqlQueries.SIGN_IN, vars);
        scenarioContext.set(LAST_RESPONSE, response);
        log.step("Sign-in attempt → HTTP " + response.getStatusCode());
    }

    @When("I authenticate and inject session into the browser")
    public void iAuthenticateAndInjectSessionIntoBrowser() {
        log.step("Injecting authenticated session into WebDriver");
        Map<String, String> authData = AuthHelper.loginViaApi();
        AuthHelper.injectAuthIntoDriver(driverContext.getDriver(), authData);
        scenarioContext.set(AUTH_TOKEN_KEY, authData.get(AuthHelper.KEY_SIGNED_IN));
    }

    // ── API logout steps ──────────────────────────────────────────────────────

    @When("I log out via API")
    public void iLogOutViaApi() {
        String signedIn = scenarioContext.get(AUTH_TOKEN_KEY);
        assertNotNull(signedIn, "No auth session in ScenarioContext — did you log in first?");
        log.step("Logging out via API");
        api.graphql(GraphqlQueries.SIGN_OUT);
        scenarioContext.set(AUTH_TOKEN_KEY, null);
        log.info("API logout complete");
    }

    // ── Assertion steps ───────────────────────────────────────────────────────

    @Then("an auth token should be stored in the scenario context")
    public void anAuthTokenShouldBeStoredInScenarioContext() {
        String signedIn = scenarioContext.get(AUTH_TOKEN_KEY);
        assertNotNull(signedIn, "Expected auth session marker to be stored in ScenarioContext");
        assertEquals(signedIn, "true", "Expected auth session marker to be 'true' but was: " + signedIn);
        log.info("Auth session verified in ScenarioContext");
    }

    @Then("the auth token should no longer be present")
    public void theAuthTokenShouldNoLongerBePresent() {
        String signedIn = scenarioContext.get(AUTH_TOKEN_KEY);
        assertNull(signedIn, "Expected auth token to be null after logout but was: " + signedIn);
    }

    @Then("the configured test credentials should be available")
    public void theConfiguredTestCredentialsShouldBeAvailable() {
        assertTrue(
                TestData.Credentials.areConfigured(),
                "Expected TEST_USER_LOGIN and TEST_USER_PASSWORD env vars to be configured"
        );
    }

    @Then("the sign-in should have failed")
    public void theSignInShouldHaveFailed() {
        Response response = scenarioContext.get(LAST_RESPONSE);
        assertNotNull(response, "No sign-in response stored — did you call the sign-in step?");
        String body = response.getBody().asString();
        boolean hasFalse  = body.contains("\"signIn\":false") || body.contains("\"signIn\": false");
        boolean hasErrors = body.contains("\"errors\"");
        boolean httpError = response.getStatusCode() >= 400;
        assertTrue(hasFalse || hasErrors || httpError,
                "Expected sign-in to fail (false/errors/4xx) but response was:\n" + body);
        log.info("Sign-in failure verified");
    }
}
