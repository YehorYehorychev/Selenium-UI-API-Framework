package yehorychev.selenium.steps;

import com.yehorychev.selenium.components.NavigationComponent;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.pages.LolPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import yehorychev.selenium.context.DriverContext;
import yehorychev.selenium.context.ScenarioContext;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for NavigationComponent-based scenarios.
 *
 * Exercises: logo, game nav links, social links, login button visibility.
 * PicoContainer injects DriverContext and ScenarioContext per-scenario.
 */
public class NavigationSteps {

    private static final Logger log = new Logger(NavigationSteps.class);

    private final DriverContext driverContext;
    private final NavigationComponent nav;
    private final ScenarioContext scenarioContext;

    public NavigationSteps(DriverContext driverContext, ScenarioContext scenarioContext) {
        this.driverContext = driverContext;
        this.nav = new NavigationComponent(driverContext.getDriver());
        this.scenarioContext = scenarioContext;
    }

    // ── Page navigation ───────────────────────────────────────────────────────

    @When("I navigate to the LoL page")
    public void iNavigateToTheLolPage() {
        log.step("Navigating to LoL page");
        new LolPage(driverContext.getDriver()).open();
    }

    // ── Logo ──────────────────────────────────────────────────────────────────

    @Then("the site logo should be visible")
    public void theSiteLogoShouldBeVisible() {
        assertTrue(nav.isLogoVisible(), "Expected the site logo to be visible");
    }

    @When("I click the site logo")
    public void iClickTheSiteLogo() {
        nav.clickLogo();
    }

    // ── Game navigation links ─────────────────────────────────────────────────

    @Then("the navigation should contain game link {string}")
    public void theNavigationShouldContainGameLink(String gameName) {
        assertTrue(
                nav.hasGameLink(gameName),
                "Expected nav to contain game link \"" + gameName + "\""
        );
    }

    @When("I click the navigation game link {string}")
    public void iClickTheNavigationGameLink(String gameName) {
        log.step("Clicking navigation game link: " + gameName);
        nav.clickGameLink(gameName);
    }

    @And("I save available navigation games as {string}")
    public void iSaveAvailableNavigationGamesAs(String key) {
        scenarioContext.set(key, nav.getAvailableGames());
        log.debug("Saved available games list as: " + key);
    }

    // ── Login button ──────────────────────────────────────────────────────────

    @Then("the login button should be visible in the navigation")
    public void theLoginButtonShouldBeVisibleInNavigation() {
        assertTrue(nav.isLoginButtonVisible(), "Expected login button to be visible in navigation");
    }
}


