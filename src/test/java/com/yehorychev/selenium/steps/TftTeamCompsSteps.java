package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.TftTeamCompsPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for the TFT Team Comps page.
 *
 * Covers: page load, comp card presence, unit links, tier list link.
 * PicoContainer injects DriverContext per-scenario.
 */
@Feature("UI — Teamfight Tactics")
@Story("TFT Team Comps")
public class TftTeamCompsSteps {

    private final TftTeamCompsPage teamCompsPage;

    public TftTeamCompsSteps(DriverContext driverContext) {
        this.teamCompsPage = new TftTeamCompsPage(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Given("I open the TFT Team Comps page")
    public void iOpenTheTftTeamCompsPage() {
        teamCompsPage.open();
    }

    @Then("the TFT Team Comps page is loaded")
    public void theTftTeamCompsPageIsLoaded() {
        assertTrue(teamCompsPage.isLoaded(), "Expected the TFT Team Comps page heading to be visible");
    }

    @Then("the TFT Team Comps heading should contain {string}")
    public void theTftTeamCompsHeadingShouldContain(String expected) {
        String actual = teamCompsPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected TFT Team Comps heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Content checks ────────────────────────────────────────────────────────

    @Then("the TFT Team Comps page should have comp cards")
    public void theTftTeamCompsPageShouldHaveCompCards() {
        assertTrue(
                teamCompsPage.hasCompCards(),
                "Expected comp cards to be present on the TFT Team Comps page"
        );
    }

    @Then("the TFT Team Comps page should have unit links")
    public void theTftTeamCompsPageShouldHaveUnitLinks() {
        assertTrue(
                teamCompsPage.hasUnitLinks(),
                "Expected unit links to be present on the TFT Team Comps page"
        );
    }

    @Then("the TFT Team Comps page should have a tier list link")
    public void theTftTeamCompsPageShouldHaveATierListLink() {
        assertTrue(
                teamCompsPage.hasTierListLink(),
                "Expected a link to the TFT Tier List to be present on the Team Comps page"
        );
    }
}

