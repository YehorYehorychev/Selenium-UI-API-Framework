package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Diablo 4 section.
 * <p>
 * Represents the Diablo 4-specific page with character classes, builds,
 * season content, and item guides.
 * <p>
 * Usage:
 * Diablo4Page d4Page = new Diablo4Page(driverContext.getDriver());
 * d4Page.open();
 * assertTrue(d4Page.isLoaded());
 * d4Page.searchBuilds("Necromancer");
 */
public class Diablo4Page extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By SEARCH_INPUT = By.cssSelector("input[type='search'], input[placeholder*='Search']");
    /**
     * Stable text-based locator — game sub-pages use hashed CSS classes.
     */
    private static final By BUILDS_SECTION = By.xpath("//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]");
    private static final By GUIDES_SECTION = By.xpath("//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'guide')]");
    private static final By BUILD_CARDS = By.cssSelector("[class*='build-card'], [data-testid='build-card'], article");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a Diablo4Page instance bound to the given driver.
     *
     * @param driver active WebDriver from DriverContext
     */
    public Diablo4Page(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Diablo 4 page.
     * NOTE: the canonical URL is /diablo-4 (not /d4).
     */
    public void open() {
        log.step("Opening Diablo 4 page");
        open(TestConfig.BASE_URL + "/diablo-4");
        waitForVisible(PAGE_HEADING);
    }

    /**
     * Returns true if the page is loaded (heading is visible).
     */
    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    /**
     * Returns the page heading text.
     */
    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    /**
     * Returns true if a "Builds" heading section is present.
     * Diablo 4 page uses hashed CSS classes; we match by h2 text content.
     */
    public boolean isBuildsSectionVisible() {
        return isPresent(BUILDS_SECTION);
    }

    /**
     * Returns true if a "Guides" heading section is present.
     */
    public boolean isGuidesSectionVisible() {
        return isPresent(GUIDES_SECTION);
    }

    // ── Builds ────────────────────────────────────────────────────────────────

    /**
     * Searches for builds by keyword.
     */
    public void searchBuilds(String keyword) {
        log.step("Searching for Diablo 4 builds: " + keyword);
        type(SEARCH_INPUT, keyword);
    }

    /**
     * Returns the number of build cards currently displayed.
     */
    public int getBuildCardCount() {
        return waitForAll(BUILD_CARDS).size();
    }
}

