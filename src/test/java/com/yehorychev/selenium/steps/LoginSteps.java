package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.pages.LoginPage;
import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.data.TestData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — Authentication")
@Story("Login Page")
public class LoginSteps {

    private final LoginPage loginPage;

    public LoginSteps(DriverContext driverContext) {
        this.loginPage = new LoginPage(driverContext.getDriver());
    }


    @Given("I open the login page")
    public void iOpenTheLoginPage() {
        loginPage.open();
    }

    @Then("the login page is loaded")
    public void theLoginPageIsLoaded() {
        assertTrue(loginPage.isLoaded(), "Expected the login page email input to be visible");
    }


    @When("I enter email {string} and password {string}")
    public void iEnterEmailAndPassword(String email, String password) {
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
    }

    @When("I click the sign in button")
    public void iClickTheSignInButton() {
        loginPage.clickSignIn();
    }

    @When("I log in with valid credentials")
    public void iLogInWithValidCredentials() {
        loginPage.login(TestData.Credentials.LOGIN, TestData.Credentials.PASSWORD);
    }


    @Then("I should be logged in")
    public void iShouldBeLoggedIn() {
        assertTrue(loginPage.isLoggedIn(), "Expected user to be logged in after sign-in");
    }

    @Then("I should see a login error message")
    public void iShouldSeeALoginErrorMessage() {
        assertTrue(loginPage.hasErrorMessage(), "Expected a login error message to be displayed");
    }

    @Then("the login error message should contain {string}")
    public void theLoginErrorMessageShouldContain(String expected) {
        String actual = loginPage.getErrorMessage();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected login error to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }
}

