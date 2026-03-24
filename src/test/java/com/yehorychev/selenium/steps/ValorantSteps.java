package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.ValorantPage;
import com.yehorychev.selenium.context.DriverContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
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
}
