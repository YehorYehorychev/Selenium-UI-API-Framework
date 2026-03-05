package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Path of Exile 2 section.
 *
 * Represents the PoE2-specific page with builds, guides, and game information.
 *
 * Usage:
 *   Poe2Page poe2Page = new Poe2Page(driverContext.getDriver());
 *   poe2Page.open();
 *   assertTrue(poe2Page.isLoaded());
 *   poe2Page.selectClass("Sorcerer");
 */
public class Poe2Page extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By CLASS_SELECTOR = By.cssSelector(".class-selector, [data-testid='class-selector']");
    private static final By BUILD_CARDS = By.cssSelector(".build-card, [data-testid='build-card']");
    private static final By GUIDES_SECTION = By.cssSelector(".guides-section, [data-testid='guides']");
    private static final By SEARCH_INPUT = By.cssSelector("input[type='search'], input[placeholder*='Search']");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a Poe2Page instance bound to the given driver.
     *
     * @param driver active WebDriver from DriverContext
     */
    public Poe2Page(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Path of Exile 2 page.
     */
    public void open() {
        log.step("Opening Path of Exile 2 page");
        open(TestConfig.BASE_URL + "/poe-2");
        waitForVisible(PAGE_HEADING);
    }

    /**
     * Returns true if the page is loaded (heading is visible).
     *
     * @return page load status
     */
    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    // ── Class selection ───────────────────────────────────────────────────────

    /**
     * Selects a character class from the class selector.
     *
     * @param className class name (e.g. "Sorcerer", "Ranger")
     */
    public void selectClass(String className) {
        log.step("Selecting class: " + className);
        By classLocator = By.xpath(String.format(".//button[contains(text(),'%s')]", className));
        click(classLocator);
    }

    /**
     * Returns true if the class selector is visible.
     *
     * @return class selector visibility status
     */
    public boolean isClassSelectorVisible() {
        return isVisible(CLASS_SELECTOR);
    }

    // ── Builds ────────────────────────────────────────────────────────────────

    /**
     * Returns the number of build cards displayed on the page.
     *
     * @return build card count
     */
    public int getBuildCardCount() {
        return waitForAll(BUILD_CARDS).size();
    }

    /**
     * Searches for builds by keyword.
     *
     * @param keyword search keyword
     */
    public void searchBuilds(String keyword) {
        log.step("Searching for builds: " + keyword);
        type(SEARCH_INPUT, keyword);
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    /**
     * Returns true if the guides section is visible.
     *
     * @return guides section visibility status
     */
    public boolean isGuidesSectionVisible() {
        return isVisible(GUIDES_SECTION);
    }

    /**
     * Returns the page heading text.
     *
     * @return heading text
     */
    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }
}

