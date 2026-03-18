package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.TftTierListPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for the TFT Tier List page.
 *
 * Note: /tft/tier-list redirects to /tft/tier-list/team-comps.
 * Covers: page load, unit links, team comp links.
 * PicoContainer injects DriverContext per-scenario.
 */
@Feature("UI — Teamfight Tactics")
@Story("TFT Tier List")
public class TftTierListSteps {

    private final TftTierListPage tierListPage;

    public TftTierListSteps(DriverContext driverContext) {
        this.tierListPage = new TftTierListPage(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Given("I open the TFT Tier List page")
    public void iOpenTheTftTierListPage() {
        tierListPage.open();
    }

    @Then("the TFT Tier List page is loaded")
    public void theTftTierListPageIsLoaded() {
        assertTrue(tierListPage.isLoaded(), "Expected the TFT Tier List page heading to be visible");
    }

    @Then("the TFT Tier List heading should contain {string}")
    public void theTftTierListHeadingShouldContain(String expected) {
        String actual = tierListPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected TFT Tier List heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Content ───────────────────────────────────────────────────────────────

    @Then("there should be at least {int} unit links on the TFT Tier List")
    public void thereShouldBeAtLeastUnitLinksOnTftTierList(int minCount) {
        int actual = tierListPage.getUnitLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " unit links on TFT Tier List but found: " + actual
        );
    }

    @Then("the TFT Tier List should have team comp links")
    public void theTftTierListShouldHaveTeamCompLinks() {
        assertTrue(
                tierListPage.hasTeamCompLinks(),
                "Expected team comp links to be present on the TFT Tier List page"
        );
    }
}

