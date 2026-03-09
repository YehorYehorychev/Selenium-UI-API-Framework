package yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.LolPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import yehorychev.selenium.context.DriverContext;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for the League of Legends page.
 *
 * Covers: page load, champion search, tier list, builds sections.
 * PicoContainer injects DriverContext per-scenario.
 */
public class LolSteps {


    private final LolPage lolPage;

    public LolSteps(DriverContext driverContext) {
        this.lolPage = new LolPage(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Given("I open the LoL page")
    public void iOpenTheLolPage() {
        lolPage.open();
    }

    @Then("the LoL page is loaded")
    public void theLolPageIsLoaded() {
        assertTrue(lolPage.isLoaded(), "Expected the LoL page heading to be visible");
    }

    @Then("the LoL page heading should contain {string}")
    public void theLolPageHeadingShouldContain(String expected) {
        String actual = lolPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected LoL heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Champion search ───────────────────────────────────────────────────────

    @When("I search for champion {string}")
    public void iSearchForChampion(String championName) {
        lolPage.searchChampion(championName);
    }

    @Then("there should be at least {int} champion cards displayed")
    public void thereShouldBeAtLeastChampionCards(int minCount) {
        int actual = lolPage.getChampionCardCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " champion cards but found: " + actual
        );
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    @Then("the tier list section should be visible")
    public void theTierListSectionShouldBeVisible() {
        assertTrue(lolPage.isTierListVisible(), "Expected the tier list section to be visible");
    }

    @Then("the builds section should be visible")
    public void theBuildsSectionShouldBeVisible() {
        assertTrue(lolPage.isBuildsVisible(), "Expected the builds section to be visible");
    }
}

