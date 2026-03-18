package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.ValorantPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Valorant")
@Story("Valorant Page")
public class ValorantSteps {

    private final ValorantPage valorantPage;

    public ValorantSteps(DriverContext driverContext) {
        this.valorantPage = new ValorantPage(driverContext.getDriver());
    }


    @Given("I open the Valorant page")
    public void iOpenTheValorantPage() {
        valorantPage.open();
    }

    @Then("the Valorant page is loaded")
    public void theValorantPageIsLoaded() {
        assertTrue(valorantPage.isLoaded(), "Expected the Valorant page heading to be visible");
    }

    @Then("the Valorant page heading should contain {string}")
    public void theValorantPageHeadingShouldContain(String expected) {
        String actual = valorantPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected Valorant heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }


    @Then("the Valorant tier list section should be visible")
    public void theValorantTierListSectionShouldBeVisible() {
        assertTrue(valorantPage.isTierListVisible(), "Expected the Valorant tier list section to be visible");
    }

    @Then("the Valorant weapon section should be visible")
    public void theValorantWeaponSectionShouldBeVisible() {
        assertTrue(valorantPage.isWeaponSectionVisible(), "Expected the Valorant weapon section to be visible");
    }


    @When("I search in Valorant page for {string}")
    public void iSearchInValorantPageFor(String keyword) {
        valorantPage.search(keyword);
    }

    @Then("there should be at least {int} Valorant agent cards displayed")
    public void thereShouldBeAtLeastValorantAgentCards(int minCount) {
        int actual = valorantPage.getAgentCardCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " Valorant agent cards but found: " + actual
        );
    }
}

