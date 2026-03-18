package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.components.FeaturesComponent;
import com.yehorychev.selenium.components.FooterComponent;
import com.yehorychev.selenium.components.GameCardsComponent;
import com.yehorychev.selenium.components.HeroComponent;
import com.yehorychev.selenium.pages.HomePage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Home Page")
@Story("Home Page Components")
public class HomePageSteps {

    private final HomePage homePage;
    private final HeroComponent hero;
    private final GameCardsComponent gameCards;
    private final FooterComponent footer;
    private final FeaturesComponent features;

    public HomePageSteps(DriverContext driverContext) {
        this.homePage    = new HomePage(driverContext.getDriver());
        this.hero        = new HeroComponent(driverContext.getDriver());
        this.gameCards   = new GameCardsComponent(driverContext.getDriver());
        this.footer      = new FooterComponent(driverContext.getDriver());
        this.features    = new FeaturesComponent(driverContext.getDriver());
    }


    @Then("the home page is loaded")
    public void theHomePageIsLoaded() {
        assertTrue(homePage.isLoaded(), "Expected the home page hero section to be visible");
    }

    @Then("the header should be visible")
    public void theHeaderShouldBeVisible() {
        assertTrue(homePage.isHeaderVisible(), "Expected the site header to be visible");
    }


    @Then("the hero heading should contain {string}")
    public void theHeroHeadingShouldContain(String expected) {
        String actual = homePage.getHeroHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected hero heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }


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


    @Then("there should be at least {int} social links")
    public void thereShouldBeAtLeastSocialLinks(int minCount) {
        int actual = homePage.getSocialLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " social links but found: " + actual
        );
    }


    @Then("the hero CTA should be visible")
    public void theHeroCtaShouldBeVisible() {
        assertTrue(hero.isCtaVisible(), "Expected hero CTA buttons to be visible");
    }


    @Then("there should be at least {int} game cards on the home page")
    public void thereShouldBeAtLeastGameCards(int minCount) {
        int actual = gameCards.getCardCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " game cards but found: " + actual
        );
    }

    @When("I click the game tile for {string}")
    public void iClickTheGameTileFor(String gameSlug) {
        gameCards.clickGameTileByHref(gameSlug);
    }

    @Then("the home page should have a game tile for {string}")
    public void theHomePageShouldHaveAGameTileFor(String gameSlug) {
        assertTrue(
                gameCards.hasTileForHref(gameSlug),
                "Expected a game tile with href containing \"" + gameSlug + "\""
        );
    }


    @Then("the footer should be visible")
    public void theFooterShouldBeVisible() {
        assertTrue(footer.isVisible(), "Expected the footer to be visible");
    }

    @Then("the footer copyright text should be present")
    public void theFooterCopyrightTextShouldBePresent() {
        String copyright = footer.getCopyrightText();
        assertTrue(
                copyright != null && !copyright.isBlank(),
                "Expected footer copyright text to be non-empty"
        );
    }

    @Then("the footer should have social media icons")
    public void theFooterShouldHaveSocialMediaIcons() {
        assertTrue(footer.areSocialIconsVisible(), "Expected footer social media icons to be visible");
    }


    @Then("the features section should be visible")
    public void theFeaturesSectionShouldBeVisible() {
        assertTrue(features.isVisible(), "Expected the features section to be visible");
    }

    @Then("there should be at least {int} features displayed")
    public void thereShouldBeAtLeastFeaturesDisplayed(int minCount) {
        int actual = features.getFeatureCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " features but found: " + actual
        );
    }
}

