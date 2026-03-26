package com.yehorychev.selenium.templates;

// ─────────────────────────────────────────────────────────────────────────────
// TEMPLATE — ExampleSteps.java
//
// PURPOSE  : Shows how to write Cucumber step definitions in this framework.
// LOCATION : src/test/java/.../steps/  (copy this file there and rename it)
// EXCLUDES : This file lives in `com.yehorychev.selenium.templates` — outside
//            the `steps` and `hooks` glue packages declared in CucumberRunner,
//            so it is NEVER loaded or executed during test runs.
//
// HOW TO USE THIS TEMPLATE
// ────────────────────────
//  1. Copy this file to  src/test/java/com/yehorychev/selenium/steps/
//  2. Rename it — e.g. DashboardSteps.java
//  3. Update the package declaration (remove ".templates")
//  4. Swap ExamplePage for your real page class
//  5. Replace the @Feature / @Story annotations with your feature area
//  6. Write (or copy from your feature file) the Gherkin step strings
//
// PICOCONTAINER DI RULES — READ BEFORE WRITING A CONSTRUCTOR
// ───────────────────────────────────────────────────────────
//  • NEVER add a no-arg constructor — PicoContainer needs to wire dependencies
//    through the constructor and breaks silently if a no-arg one exists.
//  • NEVER instantiate DriverContext / ApiContext / ScenarioContext with `new`.
//    PicoContainer creates exactly one instance of each per scenario and passes
//    them to every steps class that declares them as constructor parameters.
//  • Add ONLY the context objects you actually need as constructor params.
//    Unused injections add overhead and confuse readers.
//
//  Supported injections (declare any subset):
//    DriverContext          → gives you WebDriver for UI steps
//    ApiContext             → gives you a RestAssured spec for API steps
//    ScenarioContext        → key/value store shared across all step classes
//    ScenarioSoftAssertions → accumulates failures; assertAll() called auto by hook
// ─────────────────────────────────────────────────────────────────────────────

// ── Framework imports you will always need ────────────────────────────────────
import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.context.ScenarioContext;
import com.yehorychev.selenium.context.ScenarioSoftAssertions;

// ── Import your page and/or component classes ─────────────────────────────────
// Replace ExamplePage with your actual page class:
import com.yehorychev.selenium.pages.templates.ExamplePage;

// ── Cucumber step annotations ─────────────────────────────────────────────────
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

// ── Allure reporting annotations ──────────────────────────────────────────────
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

// ── Assertion libraries ───────────────────────────────────────────────────────
// Use `soft.assertThat(...)` for multi-check steps (all run even if one fails).
// Use `assertTrue(...)` / `assertEquals(...)` for single mandatory checks where
// failure should stop the scenario immediately (e.g. page didn't load at all).
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Example step definition class — demonstrates every common pattern in this framework.
 *
 * <p>The {@code @Feature} and {@code @Story} annotations power the Allure report
 * hierarchy. Match them to your test area so the report stays well-organised.
 */
@Feature("UI — Example Page")      // top-level grouping in the Allure report
@Story("Example Feature")          // secondary grouping under the Feature
public class ExampleSteps {

    // ── Fields ────────────────────────────────────────────────────────────────
    // Declare page/component fields as final. They are initialised once in the
    // constructor and then used by multiple step methods.

    private final ExamplePage examplePage;
    private final ScenarioContext scenarioContext;
    private final ScenarioSoftAssertions soft;

    // ── Constructor ───────────────────────────────────────────────────────────
    // Declare ONLY the context objects you actually use as parameters.
    // PicoContainer matches by TYPE — order does not matter.
    //
    // ✅ Need WebDriver?           → add DriverContext driverContext
    // ✅ Need cross-step storage?  → add ScenarioContext scenarioContext
    // ✅ Need soft assertions?     → add ScenarioSoftAssertions soft
    // ✅ Need REST API client?     → add ApiContext api
    //
    // Then initialise page/component objects here using driverContext.getDriver().

    public ExampleSteps(DriverContext driverContext,
                        ScenarioContext scenarioContext,
                        ScenarioSoftAssertions soft) {
        this.examplePage = new ExamplePage(driverContext.getDriver());
        this.scenarioContext = scenarioContext;
        this.soft = soft;
    }

    // =========================================================================
    // GIVEN steps — establish pre-conditions / initial state
    // =========================================================================

    /**
     * Navigates to the example page and waits until it is ready.
     *
     * <p>Gherkin: {@code Given I open the example page}
     *
     * <p>Pattern: every feature file should have a Given step that navigates to
     * the relevant page so scenarios are self-contained and order-independent.
     */
    @Given("I open the example page")
    public void iOpenTheExamplePage() {
        examplePage.open();
    }

    // =========================================================================
    // WHEN steps — actions the user performs
    // =========================================================================

    /**
     * Types a search query into the search field.
     *
     * <p>Gherkin: {@code When I search for "Ahri"}
     *
     * <p>{@code {string}} in the Gherkin expression is a built-in Cucumber
     * parameter type that maps to a Java {@code String}. The quotes in the
     * feature file are stripped — you receive the raw value.
     */
    @When("I search for {string}")
    public void iSearchFor(String query) {
        examplePage.enterSearchQuery(query);
    }

    /** Gherkin: {@code When I submit the search form} */
    @When("I submit the search form")
    public void iSubmitTheSearchForm() {
        examplePage.clickSubmit();
    }

    /**
     * Combined action: type a query and submit in a single step.
     *
     * <p>Gherkin: {@code When I search for "Ahri" and submit}
     *
     * <p>Use compound steps like this only when BOTH actions always happen
     * together. If the feature file ever needs them separately, split them.
     */
    @When("I search for {string} and submit")
    public void iSearchForAndSubmit(String query) {
        examplePage.search(query);
    }

    /**
     * Saves the current result count to ScenarioContext so a later Then step
     * can compare it without repeating the read.
     *
     * <p>Gherkin: {@code And I save the current result count as "initialCount"}
     *
     * <p>Use {@code @And} for "save / store" steps — consistent with
     * {@code CommonSteps.iSaveCurrentUrlAs()} and
     * {@code NavigationSteps.iSaveAvailableNavigationGamesAs()}.
     * Use ScenarioContext for cross-step state; prefer typed constants from
     * ScenarioContextKeys for fixed framework keys, inline strings for
     * feature-file-driven dynamic keys like this one.
     */
    @And("I save the current result count as {string}")
    public void iSaveCurrentResultCountAs(String key) {
        int count = examplePage.getResultCount();
        scenarioContext.set(key, count);
    }

    // =========================================================================
    // THEN steps — assertions about the expected outcome
    // =========================================================================

    /**
     * Hard assertion: the page must be loaded or the scenario stops immediately.
     *
     * <p>Gherkin: {@code Then the example page is loaded}
     *
     * <p>Use {@code assertTrue} / {@code assertEquals} for single, blocking
     * checks — if this fails, there is no point running the rest of the scenario.
     */
    @Then("the example page is loaded")
    public void theExamplePageIsLoaded() {
        assertTrue(examplePage.isLoaded(),
                "Expected the example page heading to be visible");
    }

    /**
     * Soft assertion: heading content check — failure is recorded but the step
     * continues so sibling assertions in the same step also run.
     *
     * <p>Gherkin: {@code Then the page heading should contain "Champion Search"}
     *
     * <p>Use {@code soft.assertThat(...)} when a step contains multiple related
     * checks and you want ALL results reported, not just the first failure.
     * The SoftAssertionsHook calls {@code assertAll()} automatically at scenario end.
     * NEVER call {@code assertAll()} manually.
     */
    @Then("the page heading should contain {string}")
    public void thePageHeadingShouldContain(String expected) {
        String actual = examplePage.getHeadingText();
        soft.assertThat(actual.toLowerCase())
                .as("Page heading should contain \"%s\" but was: \"%s\"", expected, actual)
                .contains(expected.toLowerCase());
    }

    /**
     * Gherkin: {@code Then there should be at least 5 results}
     *
     * <p>{@code {int}} is a built-in Cucumber parameter type for integers.
     * Use it instead of {@code {string}} when the Gherkin value is numeric —
     * no manual parsing required.
     */
    @Then("there should be at least {int} results")
    public void thereShouldBeAtLeastResults(int minimum) {
        int actual = examplePage.getResultCount();
        soft.assertThat(actual)
                .as("Expected at least %d results but found: %d", minimum, actual)
                .isGreaterThanOrEqualTo(minimum);
    }

    /**
     * Gherkin: {@code Then there should be exactly 0 results}
     *
     * <p>Demonstrates a hard equality assertion using TestNG's {@code assertEquals}.
     */
    @Then("there should be exactly {int} results")
    public void thereShouldBeExactlyResults(int expected) {
        int actual = examplePage.getResultCount();
        assertEquals(actual, expected,
                "Expected exactly " + expected + " results but found: " + actual);
    }

    /**
     * Reads a previously saved count from ScenarioContext and compares it.
     *
     * <p>Gherkin: {@code Then the result count for "initialCount" should be {int}}
     *
     * <p>Shows how to retrieve a value stored by an earlier When step.
     */
    @Then("the result count for {string} should be {int}")
    public void theResultCountForShouldBe(String key, int expected) {
        Integer actual = scenarioContext.get(key);
        soft.assertThat(actual)
                .as("Stored count for key \"%s\" should be %d but was: %d", key, expected, actual)
                .isEqualTo(expected);
    }

    /**
     * Gherkin: {@code Then the empty state message should be visible}
     */
    @Then("the empty state message should be visible")
    public void theEmptyStateMessageShouldBeVisible() {
        soft.assertThat(examplePage.isEmptyStateVisible())
                .as("Expected the empty-state message to be visible after a search with no results")
                .isTrue();
    }

    /**
     * Gherkin: {@code And the empty state message should contain "No results found"}
     *
     * <p>Uses the {@code @And} annotation — interchangeable with
     * {@code @Given / @When / @Then} at the Java level; use whichever reads
     * most naturally in context. Cucumber treats them all the same.
     */
    @And("the empty state message should contain {string}")
    public void theEmptyStateMessageShouldContain(String expected) {
        soft.assertThat(examplePage.getEmptyStateMessage())
                .as("Empty-state message should contain \"%s\"", expected)
                .containsIgnoringCase(expected);
    }
}

