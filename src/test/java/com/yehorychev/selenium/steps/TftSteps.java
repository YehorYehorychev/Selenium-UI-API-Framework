package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.TftPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Teamfight Tactics")
@Story("TFT Page")
public class TftSteps {

    private final TftPage tftPage;

    public TftSteps(DriverContext driverContext) {
        this.tftPage = new TftPage(driverContext.getDriver());
    }

    @Given("I open the TFT page")
    public void iOpenTheTftPage() {
        tftPage.open();
    }

    @Then("the TFT page is loaded")
    public void theTftPageIsLoaded() {
        assertTrue(tftPage.isLoaded(), "Expected the TFT page heading to be visible");
    }

    @Then("the TFT page heading should contain {string}")
    public void theTftPageHeadingShouldContain(String expected) {
        String actual = tftPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected TFT heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    @When("I search for TFT champion {string}")
    public void iSearchForTftChampion(String championName) {
        tftPage.searchChampion(championName);
    }

    @Then("there should be at least {int} TFT champion cards displayed")
    public void thereShouldBeAtLeastTftChampionCards(int minCount) {
        int actual = tftPage.getChampionCardCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " TFT champion cards but found: " + actual
        );
    }

    @Then("the TFT tier list section should be visible")
    public void theTftTierListSectionShouldBeVisible() {
        assertTrue(tftPage.isTierListVisible(), "Expected the TFT tier list section to be visible");
    }

    @Then("the TFT team comps section should be visible")
    public void theTftTeamCompsSectionShouldBeVisible() {
        assertTrue(tftPage.isTeamCompsSectionVisible(), "Expected the TFT team comps section to be visible");
    }

    @Then("the TFT items section should be visible")
    public void theTftItemsSectionShouldBeVisible() {
        assertTrue(tftPage.isItemsSectionVisible(), "Expected the TFT items section to be visible");
    }
}
