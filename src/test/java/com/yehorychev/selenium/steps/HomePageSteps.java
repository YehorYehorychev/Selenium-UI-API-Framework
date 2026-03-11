package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.HomePage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions specific to the Mobalytics home page.
 *
 * Covers: hero section, download CTA, nav game links.
 * PicoContainer injects DriverContext per-scenario.
 */
@Feature("UI — Home Page")
@Story("Home Page Components")
public class HomePageSteps {

    private final HomePage homePage;

    public HomePageSteps(DriverContext driverContext) {
        this.homePage = new HomePage(driverContext.getDriver());
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    @Then("the home page is loaded")
    public void theHomePageIsLoaded() {
        assertTrue(homePage.isLoaded(), "Expected the home page hero section to be visible");
    }

    @Then("the header should be visible")
    public void theHeaderShouldBeVisible() {
        assertTrue(homePage.isHeaderVisible(), "Expected the site header to be visible");
    }

    // ── Hero section ──────────────────────────────────────────────────────────

    @Then("the hero heading should contain {string}")
    public void theHeroHeadingShouldContain(String expected) {
        String actual = homePage.getHeroHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected hero heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    // ── Game navigation ───────────────────────────────────────────────────────

    @Then("the nav game {string} should be present")
    public void theNavGameShouldBePresent(String gameName) {
        assertTrue(
                homePage.isNavGamePresent(gameName),
                "Expected nav game link \"" + gameName + "\" to be present"
        );
    }

    @When("the user clicks on the {string} nav game link")
    public void theUserClicksOnTheNavGameLink(String gameName) {
        homePage.clickNavGame(gameName);
    }

    // ── Download CTA ──────────────────────────────────────────────────────────

    @When("I click the download CTA")
    public void iClickTheDownloadCta() {
        homePage.clickDownloadCta();
    }

    @Then("the download CTA href should not be empty")
    public void theDownloadCtaHrefShouldNotBeEmpty() {
        String href = homePage.getDownloadCtaHref();
        assertTrue(
                href != null && !href.isBlank(),
                "Expected download CTA href to be non-empty"
        );
    }

    // ── Social links ──────────────────────────────────────────────────────────

    @Then("there should be at least {int} social links")
    public void thereShouldBeAtLeastSocialLinks(int minCount) {
        int actual = homePage.getSocialLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " social links but found: " + actual
        );
    }
}

