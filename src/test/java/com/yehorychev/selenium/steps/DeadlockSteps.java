package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.DeadlockPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Deadlock")
@Story("Deadlock Page")
public class DeadlockSteps {

    private final DeadlockPage deadlockPage;

    public DeadlockSteps(DriverContext driverContext) {
        this.deadlockPage = new DeadlockPage(driverContext.getDriver());
    }


    @Given("I open the Deadlock page")
    public void iOpenTheDeadlockPage() {
        deadlockPage.open();
    }

    @Then("the Deadlock page is loaded")
    public void theDeadlockPageIsLoaded() {
        assertTrue(deadlockPage.isLoaded(), "Expected the Deadlock page heading to be visible");
    }

    @Then("the Deadlock page heading should contain {string}")
    public void theDeadlockPageHeadingShouldContain(String expected) {
        String actual = deadlockPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected Deadlock heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }


    @Then("the Deadlock heroes section should be visible")
    public void theDeadlockHeroesSectionShouldBeVisible() {
        assertTrue(
                deadlockPage.isHeroesSectionPresent(),
                "Expected the Heroes section heading to be present on the Deadlock page"
        );
    }

    @Then("the Deadlock builds section should be visible")
    public void theDeadlockBuildsSectionShouldBeVisible() {
        assertTrue(
                deadlockPage.isBuildsSectionPresent(),
                "Expected the Builds section heading to be present on the Deadlock page"
        );
    }

    @Then("there should be at least {int} Deadlock content links")
    public void thereShouldBeAtLeastDeadlockContentLinks(int minCount) {
        int actual = deadlockPage.getContentLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " Deadlock content links but found: " + actual
        );
    }

    @Then("the sign in button should be visible on the Deadlock page")
    public void theSignInButtonShouldBeVisibleOnDeadlockPage() {
        assertTrue(
                deadlockPage.isSignInButtonVisible(),
                "Expected the Sign In button to be visible on the Deadlock page"
        );
    }
}

