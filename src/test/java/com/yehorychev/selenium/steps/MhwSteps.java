package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.MhwPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Monster Hunter Wilds")
@Story("MH Wilds Page")
public class MhwSteps {

    private final MhwPage mhwPage;

    public MhwSteps(DriverContext driverContext) {
        this.mhwPage = new MhwPage(driverContext.getDriver());
    }

    @Given("I open the MH Wilds page")
    public void iOpenTheMhwPage() {
        mhwPage.open();
    }

    @Then("the MH Wilds page is loaded")
    public void theMhwPageIsLoaded() {
        assertTrue(mhwPage.isLoaded(), "Expected the MH Wilds page heading to be visible");
    }

    @Then("the MH Wilds page heading should contain {string}")
    public void theMhwPageHeadingShouldContain(String expected) {
        String actual = mhwPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected MH Wilds heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    @Then("the MH Wilds builds section should be visible")
    public void theMhwBuildsSectionShouldBeVisible() {
        assertTrue(
                mhwPage.isBuildsSectionPresent(),
                "Expected the Builds section to be present on the MH Wilds page"
        );
    }

    @Then("the MH Wilds guides section should be visible")
    public void theMhwGuidesSectionShouldBeVisible() {
        assertTrue(
                mhwPage.isGuidesSectionPresent(),
                "Expected the Guides section to be present on the MH Wilds page"
        );
    }

    @Then("there should be at least {int} MH Wilds content links")
    public void thereShouldBeAtLeastMhwContentLinks(int minCount) {
        int actual = mhwPage.getContentLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " MH Wilds content links but found: " + actual
        );
    }
}
