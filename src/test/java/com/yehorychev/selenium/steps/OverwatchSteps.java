package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.OverwatchPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Overwatch")
@Story("Overwatch Page")
public class OverwatchSteps {

    private final OverwatchPage overwatchPage;

    public OverwatchSteps(DriverContext driverContext) {
        this.overwatchPage = new OverwatchPage(driverContext.getDriver());
    }

    @Given("I open the Overwatch page")
    public void iOpenTheOverwatchPage() {
        overwatchPage.open();
    }

    @Then("the Overwatch page is loaded")
    public void theOverwatchPageIsLoaded() {
        assertTrue(overwatchPage.isLoaded(), "Expected the Overwatch page heading to be visible");
    }

    @Then("the Overwatch page heading should contain {string}")
    public void theOverwatchPageHeadingShouldContain(String expected) {
        String actual = overwatchPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected Overwatch heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    @Then("the Overwatch stadium builds section should be visible")
    public void theOverwatchStadiumBuildsSectionShouldBeVisible() {
        assertTrue(
                overwatchPage.isStadiumBuildsSectionPresent(),
                "Expected the Stadium Builds section to be present on the Overwatch page"
        );
    }

    @Then("there should be at least {int} Overwatch hero links")
    public void thereShouldBeAtLeastOverwatchHeroLinks(int minCount) {
        int actual = overwatchPage.getHeroLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " Overwatch hero links but found: " + actual
        );
    }

    @Then("the sign in button should be visible on the Overwatch page")
    public void theSignInButtonShouldBeVisibleOnOverwatchPage() {
        assertTrue(
                overwatchPage.isSignInButtonVisible(),
                "Expected the Sign In button to be visible on the Overwatch page"
        );
    }
}
