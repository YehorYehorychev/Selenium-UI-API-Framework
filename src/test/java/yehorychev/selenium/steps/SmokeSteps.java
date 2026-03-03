package yehorychev.selenium.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import yehorychev.selenium.context.TestContext;

import static org.testng.Assert.assertTrue;

/**
 * Step definitions for smoke-level navigation scenarios.
 *
 * <p>Covers the steps defined in {@code smoke.feature}:
 * <ul>
 *   <li>Opening the home page</li>
 *   <li>Asserting page title contains an expected fragment</li>
 *   <li>Asserting the current URL contains an expected fragment</li>
 * </ul>
 *
 * <p>PicoContainer injects the shared {@link TestContext} — no static state,
 * parallel-safe.
 */
public class SmokeSteps {

    private final TestContext context;

    /**
     * PicoContainer constructor injection.
     *
     * @param context shared test context provided by {@link yehorychev.selenium.hooks.Hooks}
     */
    public SmokeSteps(TestContext context) {
        this.context = context;
    }

    // ── Step definitions ─────────────────────────────────────────────────────

    /**
     * Navigates the browser to the application's base URL.
     *
     * <p>Feature step: {@code Given the browser is open on the home page}
     */
    @Given("the browser is open on the home page")
    public void theBrowserIsOpenOnTheHomePage() {
        context.openBaseUrl();
    }

    /**
     * Asserts that the current page title contains the expected text (case-insensitive).
     *
     * <p>Feature step: {@code Then the page title should contain {string}}
     *
     * @param expected fragment that must appear in the title
     */
    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expected) {
        String actual = context.getPageTitle();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected page title to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }

    /**
     * Asserts that the current browser URL contains the expected text (case-insensitive).
     *
     * <p>Feature step: {@code Then the current URL should contain {string}}
     *
     * @param expected fragment that must appear in the URL
     */
    @Then("the current URL should contain {string}")
    public void theCurrentUrlShouldContain(String expected) {
        String actual = context.getCurrentUrl();
        assertTrue(
                actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected URL to contain \"" + expected + "\" but was: \"" + actual + "\""
        );
    }
}

