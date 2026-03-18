package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.LolChampionsPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for the LoL Champions list page.
 *
 * Covers: page load, champion count, search field, and individual champion links.
 * PicoContainer injects DriverContext per-scenario.
 */
@Feature("UI — League of Legends")
@Story("LoL Champions List")
public class LolChampionsSteps {

    private final LolChampionsPage championsPage;

    public LolChampionsSteps(DriverContext driverContext) {
        this.championsPage = new LolChampionsPage(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Given("I open the LoL Champions page")
    public void iOpenTheLolChampionsPage() {
        championsPage.open();
    }

    @Then("the LoL Champions page is loaded")
    public void theLolChampionsPageIsLoaded() {
        assertTrue(championsPage.isLoaded(), "Expected the LoL Champions page heading to be visible");
    }

    @Then("the LoL Champions heading should contain {string}")
    public void theLolChampionsHeadingShouldContain(String expected) {
        String actual = championsPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected LoL Champions heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Champion count ────────────────────────────────────────────────────────

    @Then("there should be at least {int} champions listed on the LoL Champions page")
    public void thereShouldBeAtLeastChampionsListed(int minCount) {
        int actual = championsPage.getChampionCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " champions listed but found: " + actual
        );
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Then("the LoL Champions search input should be visible")
    public void theLolChampionsSearchInputShouldBeVisible() {
        assertTrue(
                championsPage.isSearchInputVisible(),
                "Expected the champion search input to be visible on the Champions page"
        );
    }

    @When("I search for champion {string} on the LoL Champions page")
    public void iSearchForChampionOnLolChampionsPage(String championName) {
        championsPage.searchChampion(championName);
    }

    // ── Champion-specific ─────────────────────────────────────────────────────

    @Then("champion {string} should be present on the LoL Champions page")
    public void championShouldBePresentOnLolChampionsPage(String championSlug) {
        assertTrue(
                championsPage.isChampionPresent(championSlug),
                "Expected champion \"" + championSlug + "\" to be present on the Champions page"
        );
    }

    @When("I click champion {string} on the LoL Champions page")
    public void iClickChampionOnLolChampionsPage(String championSlug) {
        championsPage.clickChampion(championSlug);
    }
}

