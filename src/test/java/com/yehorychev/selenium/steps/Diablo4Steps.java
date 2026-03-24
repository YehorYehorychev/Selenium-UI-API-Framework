package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.Diablo4Page;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Diablo 4")
@Story("Diablo 4 Page")
public class Diablo4Steps {

    private final Diablo4Page diablo4Page;

    public Diablo4Steps(DriverContext driverContext) {
        this.diablo4Page = new Diablo4Page(driverContext.getDriver());
    }


    @Given("I open the Diablo 4 page")
    public void iOpenTheDiablo4Page() {
        diablo4Page.open();
    }

    @Then("the Diablo 4 page is loaded")
    public void theDiablo4PageIsLoaded() {
        assertTrue(diablo4Page.isLoaded(), "Expected the Diablo 4 page heading to be visible");
    }

    @Then("the Diablo 4 page heading should contain {string}")
    public void theDiablo4PageHeadingShouldContain(String expected) {
        String actual = diablo4Page.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected Diablo 4 heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }


    @Then("the Diablo 4 builds section should be visible")
    public void theDiablo4BuildsSectionShouldBeVisible() {
        assertTrue(diablo4Page.isBuildsSectionVisible(), "Expected a 'Builds' heading section to be visible on Diablo 4 page");
    }

    @Then("the Diablo 4 guides section should be visible")
    public void theDiablo4GuidesSectionShouldBeVisible() {
        assertTrue(diablo4Page.isGuidesSectionVisible(), "Expected a 'Guides' heading section to be visible on Diablo 4 page");
    }
}

