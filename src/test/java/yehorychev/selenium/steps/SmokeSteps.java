package yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.HomePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import yehorychev.selenium.context.DriverContext;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for smoke-level navigation scenarios.
 *
 * Uses HomePage Page Object — no raw WebDriver calls in step definitions.
 * PicoContainer injects DriverContext; parallel-safe.
 */
public class SmokeSteps {

    private final HomePage homePage;

    public SmokeSteps(DriverContext driverContext) {
        this.homePage = new HomePage(driverContext.getDriver());
    }

    // ── Navigation steps ─────────────────────────────────────────────────────

    @Given("the browser is open on the home page")
    public void theBrowserIsOpenOnTheHomePage() {
        homePage.open();
    }

    @When("the user clicks on the {string} nav game link")
    public void theUserClicksNavGame(String gameName) {
        homePage.clickNavGame(gameName);
    }

    // ── Assertion steps ───────────────────────────────────────────────────────

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expected) {
        String actual = homePage.getTitle();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected page title to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    @Then("the current URL should contain {string}")
    public void theCurrentUrlShouldContain(String expected) {
        String actual = homePage.getCurrentUrl();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected URL to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    @Then("the home page is loaded")
    public void theHomePageIsLoaded() {
        assertTrue(homePage.isLoaded(), "Expected the home page hero section to be visible");
    }

    @Then("the header should be visible")
    public void theHeaderShouldBeVisible() {
        assertTrue(homePage.isHeaderVisible(), "Expected the site header to be visible");
    }

    @Then("the nav game {string} should be present")
    public void theNavGameShouldBePresent(String gameName) {
        assertTrue(
                homePage.isNavGamePresent(gameName),
                "Expected nav item \"" + gameName + "\" to be present in the header"
        );
    }

    @And("the hero heading should contain {string}")
    public void theHeroHeadingShouldContain(String expected) {
        String actual = homePage.getHeroHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected hero heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }
}
