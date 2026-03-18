package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.Borderlands4Page;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for the Borderlands 4 page.
 *
 * Covers: page load, builds section, content links.
 * PicoContainer injects DriverContext per-scenario.
 */
@Feature("UI — Borderlands 4")
@Story("Borderlands 4 Page")
public class Borderlands4Steps {

    private final Borderlands4Page borderlands4Page;

    public Borderlands4Steps(DriverContext driverContext) {
        this.borderlands4Page = new Borderlands4Page(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Given("I open the Borderlands 4 page")
    public void iOpenTheBorderlands4Page() {
        borderlands4Page.open();
    }

    @Then("the Borderlands 4 page is loaded")
    public void theBorderlands4PageIsLoaded() {
        assertTrue(borderlands4Page.isLoaded(), "Expected the Borderlands 4 page heading to be visible");
    }

    @Then("the Borderlands 4 page heading should contain {string}")
    public void theBorderlands4PageHeadingShouldContain(String expected) {
        String actual = borderlands4Page.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected Borderlands 4 heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    @Then("the Borderlands 4 builds section should be visible")
    public void theBorderlands4BuildsSectionShouldBeVisible() {
        assertTrue(
                borderlands4Page.isBuildsSectionPresent(),
                "Expected the Builds section to be present on the Borderlands 4 page"
        );
    }

    @Then("there should be at least {int} Borderlands 4 content links")
    public void thereShouldBeAtLeastBorderlands4ContentLinks(int minCount) {
        int actual = borderlands4Page.getContentLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " Borderlands 4 content links but found: " + actual
        );
    }
}

