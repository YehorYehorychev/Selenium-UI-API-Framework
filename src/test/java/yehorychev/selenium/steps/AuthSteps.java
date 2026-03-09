package yehorychev.selenium.steps;

import com.yehorychev.selenium.data.TestData;
import com.yehorychev.selenium.helpers.AuthHelper;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import yehorychev.selenium.context.DriverContext;
import yehorychev.selenium.context.ScenarioContext;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Step definitions for authentication flows — both API-based and UI-based.
 *
 * API auth: uses AuthHelper to POST credentials and inject session into WebDriver.
 * Token storage: persists the auth token in ScenarioContext under "authToken"
 * for use by subsequent steps.
 *
 * PicoContainer injects DriverContext and ScenarioContext per-scenario.
 */
public class AuthSteps {

    private static final Logger log = new Logger(AuthSteps.class);
    private static final String AUTH_TOKEN_KEY = "authToken";
    private static final String AUTH_DATA_KEY  = "authData";

    private final DriverContext driverContext;
    private final ScenarioContext scenarioContext;

    public AuthSteps(DriverContext driverContext, ScenarioContext scenarioContext) {
        this.driverContext = driverContext;
        this.scenarioContext = scenarioContext;
    }

    // ── API login steps ───────────────────────────────────────────────────────

    @Given("I am authenticated via API")
    public void iAmAuthenticatedViaApi() {
        log.step("Authenticating via API using configured test credentials");
        Map<String, String> authData = AuthHelper.loginViaApi();
        scenarioContext.set(AUTH_TOKEN_KEY, authData.get("token"));
        scenarioContext.set(AUTH_DATA_KEY, authData);
        log.info("API authentication successful");
    }

    @Given("I am authenticated via API with email {string} and password {string}")
    public void iAmAuthenticatedViaApiWith(String email, String password) {
        log.step("Authenticating via API: " + email);
        Map<String, String> authData = AuthHelper.loginViaApi(email, password);
        scenarioContext.set(AUTH_TOKEN_KEY, authData.get("token"));
        scenarioContext.set(AUTH_DATA_KEY, authData);
        log.info("API authentication successful for: " + email);
    }

    @When("I authenticate and inject session into the browser")
    public void iAuthenticateAndInjectSessionIntoBrowser() {
        log.step("Injecting authenticated session into WebDriver");
        Map<String, String> authData = AuthHelper.loginViaApi();
        AuthHelper.injectAuthIntoDriver(driverContext.getDriver(), authData);
        scenarioContext.set(AUTH_TOKEN_KEY, authData.get("token"));
        scenarioContext.set(AUTH_DATA_KEY, authData);
    }

    // ── API logout steps ──────────────────────────────────────────────────────

    @When("I log out via API")
    public void iLogOutViaApi() {
        String token = scenarioContext.get(AUTH_TOKEN_KEY);
        assertNotNull(token, "No auth token in ScenarioContext — did you log in first?");
        log.step("Logging out via API");
        AuthHelper.logoutViaApi(token);
        scenarioContext.set(AUTH_TOKEN_KEY, null);
        log.info("API logout complete");
    }

    // ── Assertion steps ───────────────────────────────────────────────────────

    @Then("an auth token should be stored in the scenario context")
    public void anAuthTokenShouldBeStoredInScenarioContext() {
        String token = scenarioContext.get(AUTH_TOKEN_KEY);
        assertNotNull(token, "Expected an auth token to be stored in ScenarioContext");
        assertFalse(token.isBlank(), "Expected auth token to be non-blank");
        log.info("Auth token verified in ScenarioContext");
    }

    @Then("the auth token should no longer be present")
    public void theAuthTokenShouldNoLongerBePresent() {
        String token = scenarioContext.get(AUTH_TOKEN_KEY);
        assertNull(token, "Expected auth token to be null after logout but was: " + token);
    }

    @Then("the configured test credentials should be available")
    public void theConfiguredTestCredentialsShouldBeAvailable() {
        assertTrue(
                TestData.Credentials.areConfigured(),
                "Expected TEST_USER_LOGIN and TEST_USER_PASSWORD env vars to be configured"
        );
    }
}
