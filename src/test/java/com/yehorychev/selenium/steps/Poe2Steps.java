package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.Poe2Page;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for the Path of Exile 2 page.
 *
 * Covers: page load, class selection, build search, guides section.
 * PicoContainer injects DriverContext per-scenario.
 */
@Feature("UI — Path of Exile 2")
@Story("PoE2 Page")
public class Poe2Steps {

    private final Poe2Page poe2Page;

    public Poe2Steps(DriverContext driverContext) {
        this.poe2Page = new Poe2Page(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Given("I open the PoE2 page")
    public void iOpenThePoe2Page() {
        poe2Page.open();
    }

    @Then("the PoE2 page is loaded")
    public void thePoe2PageIsLoaded() {
        assertTrue(poe2Page.isLoaded(), "Expected the PoE2 page heading to be visible");
    }

    @Then("the PoE2 page heading should contain {string}")
    public void thePoe2PageHeadingShouldContain(String expected) {
        String actual = poe2Page.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected PoE2 heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Class selector ────────────────────────────────────────────────────────

    @Then("the class selector should be visible")
    public void theClassSelectorShouldBeVisible() {
        assertTrue(poe2Page.isClassSelectorVisible(), "Expected the class selector to be visible");
    }

    @When("I select class {string}")
    public void iSelectClass(String className) {
        poe2Page.selectClass(className);
    }

    // ── Build search ──────────────────────────────────────────────────────────

    @When("I search for PoE2 builds with keyword {string}")
    public void iSearchForPoe2BuildsWithKeyword(String keyword) {
        poe2Page.searchBuilds(keyword);
    }

    @Then("there should be at least {int} build cards displayed")
    public void thereShouldBeAtLeastBuildCards(int minCount) {
        int actual = poe2Page.getBuildCardCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " build cards but found: " + actual
        );
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    @Then("the guides section should be visible")
    public void theGuidesSectionShouldBeVisible() {
        assertTrue(poe2Page.isGuidesSectionVisible(), "Expected the guides section to be visible");
    }
}

