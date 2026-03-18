package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.NightReignPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Elden Ring Nightreign")
@Story("Nightreign Page")
public class NightReignSteps {

    private final NightReignPage nightReignPage;

    public NightReignSteps(DriverContext driverContext) {
        this.nightReignPage = new NightReignPage(driverContext.getDriver());
    }


    @Given("I open the Nightreign page")
    public void iOpenTheNightReignPage() {
        nightReignPage.open();
    }

    @Then("the Nightreign page is loaded")
    public void theNightReignPageIsLoaded() {
        assertTrue(nightReignPage.isLoaded(), "Expected the Nightreign page heading to be visible");
    }

    @Then("the Nightreign page heading should contain {string}")
    public void theNightReignPageHeadingShouldContain(String expected) {
        String actual = nightReignPage.getHeadingText();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected Nightreign heading to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }


    @Then("the Nightreign Nightfarers section should be visible")
    public void theNightReignNightfarersSectionShouldBeVisible() {
        assertTrue(
                nightReignPage.isNightfarersSectionPresent(),
                "Expected the Nightfarers section to be present on the Nightreign page"
        );
    }

    @Then("the Nightreign Nightlords section should be visible")
    public void theNightReignNightlordsSectionShouldBeVisible() {
        assertTrue(
                nightReignPage.isNightlordsSectionPresent(),
                "Expected the Nightlords section to be present on the Nightreign page"
        );
    }

    @Then("the Nightreign builds section should be visible")
    public void theNightReignBuildsSectionShouldBeVisible() {
        assertTrue(
                nightReignPage.isBuildsSectionPresent(),
                "Expected the Builds section to be present on the Nightreign page"
        );
    }

    @Then("the Nightreign guides section should be visible")
    public void theNightReignGuidesSectionShouldBeVisible() {
        assertTrue(
                nightReignPage.isGuidesSectionPresent(),
                "Expected the Guides section to be present on the Nightreign page"
        );
    }

    @Then("there should be at least {int} Nightreign content links")
    public void thereShouldBeAtLeastNightReignContentLinks(int minCount) {
        int actual = nightReignPage.getContentLinkCount();
        assertTrue(
                actual >= minCount,
                "Expected at least " + minCount + " Nightreign content links but found: " + actual
        );
    }
}

