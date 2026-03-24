package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.components.FeaturesComponent;
import com.yehorychev.selenium.components.FooterComponent;
import com.yehorychev.selenium.components.GameCardsComponent;
import com.yehorychev.selenium.components.HeroComponent;
import com.yehorychev.selenium.pages.HomePage;
import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.context.ScenarioSoftAssertions;
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
    private final ScenarioSoftAssertions soft;

    public HomePageSteps(DriverContext driverContext, ScenarioSoftAssertions soft) {
        this.homePage = new HomePage(driverContext.getDriver());
        this.hero = new HeroComponent(driverContext.getDriver());
        this.gameCards = new GameCardsComponent(driverContext.getDriver());
        this.footer = new FooterComponent(driverContext.getDriver());
        this.features = new FeaturesComponent(driverContext.getDriver());
        this.soft = soft;
    }


    @Then("the home page is loaded")
    public void theHomePageIsLoaded() {
        assertTrue(homePage.isLoaded(), "Expected the home page hero section to be visible");
    }

    @Then("the header should be visible")
    public void theHeaderShouldBeVisible() {
        soft.assertThat(homePage.isHeaderVisible()).as("Expected the site header to be visible").isTrue();
    }

    @Then("the hero heading should contain {string}")
    public void theHeroHeadingShouldContain(String expected) {
        String actual = homePage.getHeroHeadingText();
        soft.assertThat(actual.toLowerCase().contains(expected.toLowerCase()))
                .as("Expected hero heading to contain \"%s\" but was: \"%s\"", expected, actual)
                .isTrue();
    }

    @Then("the nav game {string} should be present")
    public void theNavGameShouldBePresent(String gameName) {
        soft.assertThat(homePage.isNavGamePresent(gameName))
                .as("Expected nav game link \"%s\" to be present", gameName)
                .isTrue();
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
        soft.assertThat(href != null && !href.isBlank())
                .as("Expected download CTA href to be non-empty")
                .isTrue();
    }

    @Then("there should be at least {int} social links")
    public void thereShouldBeAtLeastSocialLinks(int minCount) {
        int actual = homePage.getSocialLinkCount();
        soft.assertThat(actual)
                .as("Expected at least %d social links but found: %d", minCount, actual)
                .isGreaterThanOrEqualTo(minCount);
    }

    @Then("the hero CTA should be visible")
    public void theHeroCtaShouldBeVisible() {
        soft.assertThat(hero.isCtaVisible()).as("Expected hero CTA buttons to be visible").isTrue();
    }

    @Then("there should be at least {int} game cards on the home page")
    public void thereShouldBeAtLeastGameCards(int minCount) {
        int actual = gameCards.getCardCount();
        soft.assertThat(actual)
                .as("Expected at least %d game cards but found: %d", minCount, actual)
                .isGreaterThanOrEqualTo(minCount);
    }

    @When("I click the game tile for {string}")
    public void iClickTheGameTileFor(String gameSlug) {
        gameCards.clickGameTileByHref(gameSlug);
    }

    @Then("the home page should have a game tile for {string}")
    public void theHomePageShouldHaveAGameTileFor(String gameSlug) {
        soft.assertThat(gameCards.hasTileForHref(gameSlug))
                .as("Expected a game tile with href containing \"%s\"", gameSlug)
                .isTrue();
    }

    @Then("the footer should be visible")
    public void theFooterShouldBeVisible() {
        soft.assertThat(footer.isVisible()).as("Expected the footer to be visible").isTrue();
    }

    @Then("the footer copyright text should be present")
    public void theFooterCopyrightTextShouldBePresent() {
        String copyright = footer.getCopyrightText();
        soft.assertThat(copyright != null && !copyright.isBlank())
                .as("Expected footer copyright text to be non-empty")
                .isTrue();
    }

    @Then("the footer should have social media icons")
    public void theFooterShouldHaveSocialMediaIcons() {
        soft.assertThat(footer.areSocialIconsVisible()).as("Expected footer social media icons to be visible").isTrue();
    }

    @Then("the features section should be visible")
    public void theFeaturesSectionShouldBeVisible() {
        soft.assertThat(features.isVisible()).as("Expected the features section to be visible").isTrue();
    }

    @Then("there should be at least {int} features displayed")
    public void thereShouldBeAtLeastFeaturesDisplayed(int minCount) {
        int actual = features.getFeatureCount();
        soft.assertThat(actual)
                .as("Expected at least %d features but found: %d", minCount, actual)
                .isGreaterThanOrEqualTo(minCount);
    }
}

