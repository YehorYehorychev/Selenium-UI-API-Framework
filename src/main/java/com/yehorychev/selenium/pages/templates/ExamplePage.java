package com.yehorychev.selenium.pages.templates;

// ─────────────────────────────────────────────────────────────────────────────
// TEMPLATE — ExamplePage.java
//
// PURPOSE  : Shows how to create a new Page Object in this framework.
// LOCATION : src/main/java/.../pages/  (copy this file there and rename it)
// EXCLUDES : This file is in the `templates` sub-package and is never executed
//            as part of any test run. It is purely educational.
//
// HOW TO USE THIS TEMPLATE
// ────────────────────────
//  1. Copy this file to  src/main/java/com/yehorychev/selenium/pages/
//  2. Rename it to match your page — e.g. DashboardPage.java
//  3. Update the package declaration at the top (remove ".templates")
//  4. Replace every locator with real CSS selectors / XPaths for your page
//  5. Rename the class and all its methods to match what the page actually does
//  6. Create your step definition class (see ExampleSteps.java) and feature file
//
// RULE: Every new page MUST extend BasePage and pass the WebDriver to super().
// ─────────────────────────────────────────────────────────────────────────────

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.TestData;
import com.yehorychev.selenium.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Example Page Object that demonstrates every common pattern used in this framework.
 *
 * <p>Rename this class, update the locators, and you have a working page object.
 *
 * <h3>Locator strategy guide</h3>
 * <ul>
 *   <li>Prefer CSS selectors — fast, readable, widely supported.</li>
 *   <li>Use {@code By.id()} when the element has a stable {@code id} attribute.</li>
 *   <li>Fall back to XPath only when CSS cannot express the selector
 *       (e.g. "find a button whose visible text equals X").</li>
 *   <li>Avoid class-name selectors that look machine-generated (hashed) — they break on every deploy.</li>
 *   <li>Prefer {@code data-testid} attributes when the front-end team adds them — they are
 *       explicitly stable and the most robust option.</li>
 * </ul>
 */
public class ExamplePage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // Declare ALL locators as private static final By fields at the top of the class.
    // Keeping them here makes maintenance easy — one place to update when the UI changes.
    // Name them after what they REPRESENT, not how they look in the DOM.

    /** The main visible heading that confirms the page loaded. */
    private static final By PAGE_HEADING = By.cssSelector("h1.page-title");

    /** A search / filter input — identified by a stable data-testid attribute. */
    private static final By SEARCH_INPUT = By.cssSelector("[data-testid='search-input']");

    /** A submit / action button located by its semantic role and visible text. */
    private static final By SUBMIT_BUTTON = By.xpath(
            "//button[@type='submit' and normalize-space(.)='Search']");

    /** A result list that appears after a search completes. */
    private static final By RESULT_ITEMS = By.cssSelector("ul.results-list li");

    /** An error/empty-state message shown when there are no results. */
    private static final By EMPTY_STATE_MESSAGE = By.cssSelector(".empty-state p");

    /**
     * An XPath template for a result item whose text matches a runtime value.
     * Usage: {@code String.format(RESULT_BY_NAME_XPATH, "Ahri")}
     * Produces: {@code //li[contains(@class,'result-item') and normalize-space(.)='Ahri']}
     */
    private static final String RESULT_BY_NAME_XPATH =
            "//li[contains(@class,'result-item') and normalize-space(.)='%s']";

    // ── Constructor ───────────────────────────────────────────────────────────
    // Always accept a WebDriver and pass it to super(). Nothing else goes here.

    public ExamplePage(WebDriver driver) {
        super(driver);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Opens this page by navigating to its URL path.
     * After navigation, waits for the heading to confirm the page loaded.
     *
     * <p>Pattern: always wait for a stable element AFTER calling {@code open()} —
     * this acts as a built-in "page ready" gate so subsequent steps don't race.
     */
    public void open() {
        log.step("Opening Example page");
        // Add your page's path to TestData.UrlPatterns, then reference the constant here.
        // Example — in TestData.UrlPatterns:
        //   public static final String EXAMPLE = "/example";
        // Then use it:
        //   open(TestConfig.BASE_URL + TestData.UrlPatterns.EXAMPLE);
        //
        // For now this references HOME as a working placeholder so the template compiles:
        open(TestConfig.BASE_URL + TestData.UrlPatterns.HOME);
        waitForVisible(PAGE_HEADING);
    }

    // ── State checks ──────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when the page's main heading is visible.
     *
     * <p>{@link #isVisible(By)} uses a SHORT 3-second wait (via {@code shortWait}) and
     * returns {@code false} instead of throwing — safe to use in assertions and
     * conditional branches.
     *
     * <p>Contrast with {@link #waitForVisible(By)}, which uses the full 15-second
     * timeout and throws on failure — use that one before interactions.
     */
    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    /**
     * Returns {@code true} when the empty-state message exists in the DOM.
     *
     * <p>{@link #isPresent(By)} checks DOM presence — the element may be invisible.
     * Use it for hidden/off-screen elements that cannot pass a visibility check.
     */
    public boolean isEmptyStateVisible() {
        return isPresent(EMPTY_STATE_MESSAGE);
    }

    // ── Interactions ──────────────────────────────────────────────────────────

    /**
     * Types {@code query} into the search input.
     *
     * <p>{@link #type(By, String)} waits for visibility, clears the field, then sends keys.
     * Never call {@code sendKeys()} directly — always go through {@code type()}.
     */
    public void enterSearchQuery(String query) {
        log.step("Entering search query: " + query);
        type(SEARCH_INPUT, query);
    }

    /**
     * Clicks the submit button.
     *
     * <p>{@link #click(By)} waits for the element to be clickable before clicking.
     * Never call {@code element.click()} directly.
     */
    public void clickSubmit() {
        log.step("Clicking submit");
        click(SUBMIT_BUTTON);
    }

    /**
     * Convenience method that combines entering a query and clicking submit.
     * Prefer composing existing methods over duplicating logic.
     */
    public void search(String query) {
        enterSearchQuery(query);
        clickSubmit();
    }

    /**
     * Clicks a result item that matches {@code name} exactly (case-sensitive).
     * Uses a dynamic XPath built from the RESULT_BY_NAME_XPATH template.
     */
    public void clickResult(String name) {
        log.step("Clicking result: " + name);
        click(By.xpath(String.format(RESULT_BY_NAME_XPATH, name)));
    }

    // ── Data extraction ───────────────────────────────────────────────────────

    /**
     * Returns the trimmed visible text of the page heading.
     *
     * <p>{@link #getText(By)} waits for visibility and trims whitespace automatically.
     */
    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    /**
     * Returns the number of result items currently displayed.
     *
     * <p>{@link #waitForAll(By)} waits up to 15 s for at least one match, then
     * returns the full list — call {@code .size()} to count.
     */
    public int getResultCount() {
        return waitForAll(RESULT_ITEMS).size();
    }

    /**
     * Returns the text of the empty-state paragraph.
     * Only call this after confirming {@link #isEmptyStateVisible()} returns {@code true}.
     */
    public String getEmptyStateMessage() {
        return getText(EMPTY_STATE_MESSAGE);
    }
}

