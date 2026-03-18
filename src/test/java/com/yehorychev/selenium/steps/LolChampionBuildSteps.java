package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.LolChampionBuildPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — League of Legends")
@Story("LoL Champion Build Pages")
public class LolChampionBuildSteps {

    private final LolChampionBuildPage championBuildPage;

    public LolChampionBuildSteps(DriverContext driverContext) {
        this.championBuildPage = new LolChampionBuildPage(driverContext.getDriver());
    }

    @Given("I open the build page for champion {string}")
    public void iOpenTheBuildPageForChampion(String championSlug) {
        championBuildPage.open(championSlug);
    }

    @Then("the champion build page is loaded")
    public void theChampionBuildPageIsLoaded() {
        assertTrue(championBuildPage.isLoaded(), "Expected the champion build page heading to be visible");
    }

    @Then("the champion build heading should contain {string}")
    public void theChampionBuildHeadingShouldContain(String expected) {
        String actual = championBuildPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected champion build heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    @Then("the champion builds section should be present")
    public void theChampionBuildsSectionShouldBePresent() {
        assertTrue(
                championBuildPage.isBuildsSectionPresent(),
                "Expected the Builds section to be present on the champion build page"
        );
    }

    @Then("the champion runes section should be present")
    public void theChampionRunesSectionShouldBePresent() {
        assertTrue(
                championBuildPage.isRunesSectionPresent(),
                "Expected the Runes section to be present on the champion build page"
        );
    }

    @Then("the champion matchups section should be present")
    public void theChampionMatchupsSectionShouldBePresent() {
        assertTrue(
                championBuildPage.isMatchupsSectionPresent(),
                "Expected the Matchups section to be present on the champion build page"
        );
    }

    @Then("there should be at least {int} counter links on the champion build page")
    public void thereShouldBeAtLeastCounterLinksOnBuildPage(int minCount) {
        int actual = championBuildPage.getCounterLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " counter links but found: " + actual
        );
    }

    @Then("there should be at least {int} role build links on the champion build page")
    public void thereShouldBeAtLeastRoleBuildLinksOnBuildPage(int minCount) {
        int actual = championBuildPage.getRoleBuildLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " role build links but found: " + actual
        );
    }
}
