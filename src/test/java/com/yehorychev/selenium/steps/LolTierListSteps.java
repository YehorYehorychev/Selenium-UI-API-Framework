package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.LolTierListPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for the LoL Tier List page.
 *
 * Covers: page load, champion link presence, methodology section,
 * patch info, and filter interaction.
 * PicoContainer injects DriverContext per-scenario.
 */
@Feature("UI — League of Legends")
@Story("LoL Tier List")
public class LolTierListSteps {

    private final LolTierListPage tierListPage;

    public LolTierListSteps(DriverContext driverContext) {
        this.tierListPage = new LolTierListPage(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Given("I open the LoL Tier List page")
    public void iOpenTheLolTierListPage() {
        tierListPage.open();
    }

    @Then("the LoL Tier List page is loaded")
    public void theLolTierListPageIsLoaded() {
        assertTrue(tierListPage.isLoaded(), "Expected the LoL Tier List page heading to be visible");
    }

    @Then("the LoL Tier List heading should contain {string}")
    public void theLolTierListHeadingShouldContain(String expected) {
        String actual = tierListPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected LoL Tier List heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Content ───────────────────────────────────────────────────────────────

    @Then("there should be at least {int} champion links on the LoL Tier List")
    public void thereShouldBeAtLeastChampionLinksOnLolTierList(int minCount) {
        int actual = tierListPage.getChampionLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " champion links on Tier List but found: " + actual
        );
    }

    @Then("the LoL Tier List methodology section should be present")
    public void theLolTierListMethodologySectionShouldBePresent() {
        assertTrue(
                tierListPage.isMethodologySectionPresent(),
                "Expected the Methodology section to be present on the Tier List page"
        );
    }

    @Then("the LoL Tier List patch info should be present")
    public void theLolTierListPatchInfoShouldBePresent() {
        assertTrue(
                tierListPage.isPatchInfoPresent(),
                "Expected patch information to be visible on the Tier List page"
        );
    }

    @Then("the LoL Tier List filter button should be present")
    public void theLolTierListFilterButtonShouldBePresent() {
        assertTrue(
                tierListPage.isFilterButtonPresent(),
                "Expected the Filters button to be present on the Tier List page"
        );
    }

    @When("I click the LoL Tier List filters")
    public void iClickTheLolTierListFilters() {
        tierListPage.clickFilters();
    }
}

