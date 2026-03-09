package yehorychev.selenium.steps;

import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.pages.HomePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import yehorychev.selenium.context.DriverContext;
import yehorychev.selenium.context.ScenarioContext;

import static org.testng.Assert.assertTrue;

/**
 * Common navigation steps shared across all UI feature files.
 *
 * Covers: opening the home page, asserting page title, URL,
 * and storing transient data in ScenarioContext.
 *
 * PicoContainer injects DriverContext and ScenarioContext per-scenario.
 */
public class CommonSteps {

    private static final Logger log = new Logger(CommonSteps.class);

    private final HomePage homePage;
    private final ScenarioContext scenarioContext;

    public CommonSteps(DriverContext driverContext, ScenarioContext scenarioContext) {
        this.homePage = new HomePage(driverContext.getDriver());
        this.scenarioContext = scenarioContext;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Given("I open the homepage")
    public void iOpenTheHomepage() {
        homePage.open();
    }

    @When("I navigate to the homepage")
    public void iNavigateToTheHomepage() {
        homePage.open();
    }

    // ── Page title assertions ─────────────────────────────────────────────────

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expected) {
        String actual = homePage.getTitle();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected title to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── URL assertions ────────────────────────────────────────────────────────

    @Then("the current URL should contain {string}")
    public void theCurrentUrlShouldContain(String expected) {
        String actual = homePage.getCurrentUrl();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected URL to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── ScenarioContext helpers ───────────────────────────────────────────────

    @And("I save the current URL as {string}")
    public void iSaveCurrentUrlAs(String key) {
        String url = homePage.getCurrentUrl();
        scenarioContext.set(key, url);
        log.debug("Saved URL as \"" + key + "\": " + url);
    }

    @Then("the saved URL {string} should contain {string}")
    public void theSavedUrlShouldContain(String key, String expected) {
        String saved = scenarioContext.get(key);
        assertTrue(
                saved != null && saved.contains(expected),
                "Expected saved URL for key \"" + key + "\" to contain \""
                        + expected + "\" but was: \"" + saved + "\""
        );
    }
}

