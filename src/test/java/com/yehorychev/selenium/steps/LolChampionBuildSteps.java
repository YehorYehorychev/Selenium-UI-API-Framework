package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.LolChampionBuildPage;
import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.context.ScenarioSoftAssertions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — League of Legends")
@Story("LoL Champion Build Pages")
public class LolChampionBuildSteps {

    private final LolChampionBuildPage championBuildPage;
    private final ScenarioSoftAssertions soft;

    public LolChampionBuildSteps(DriverContext driverContext, ScenarioSoftAssertions soft) {
        this.championBuildPage = new LolChampionBuildPage(driverContext.getDriver());
        this.soft = soft;
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
        soft.assertThat(actual.toLowerCase().contains(expected.toLowerCase()))
                .as("Expected champion build heading to contain \"%s\" but was: \"%s\"", expected, actual)
                .isTrue();
    }

    @Then("the champion builds section should be present")
    public void theChampionBuildsSectionShouldBePresent() {
        soft.assertThat(championBuildPage.isBuildsSectionPresent())
                .as("Expected the Builds section to be present on the champion build page")
                .isTrue();
    }

    @Then("the champion runes section should be present")
    public void theChampionRunesSectionShouldBePresent() {
        soft.assertThat(championBuildPage.isRunesSectionPresent())
                .as("Expected the Runes section to be present on the champion build page")
                .isTrue();
    }

    @Then("the champion matchups section should be present")
    public void theChampionMatchupsSectionShouldBePresent() {
        soft.assertThat(championBuildPage.isMatchupsSectionPresent())
                .as("Expected the Matchups section to be present on the champion build page")
                .isTrue();
    }

    @Then("there should be at least {int} counter links on the champion build page")
    public void thereShouldBeAtLeastCounterLinksOnBuildPage(int minCount) {
        int actual = championBuildPage.getCounterLinkCount();
        soft.assertThat(actual)
                .as("Expected at least %d counter links but found: %d", minCount, actual)
                .isGreaterThanOrEqualTo(minCount);
    }

    @Then("there should be at least {int} role build links on the champion build page")
    public void thereShouldBeAtLeastRoleBuildLinksOnBuildPage(int minCount) {
        int actual = championBuildPage.getRoleBuildLinkCount();
        soft.assertThat(actual)
                .as("Expected at least %d role build links but found: %d", minCount, actual)
                .isGreaterThanOrEqualTo(minCount);
    }
}
