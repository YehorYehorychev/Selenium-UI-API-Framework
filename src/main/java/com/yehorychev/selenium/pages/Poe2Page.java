package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Path of Exile 2 section.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/poe-2:
 *   H1  : "Path of Exile 2 Builds, Guides & More"
 *   H2s : "Featured Builds", "Beginner Guides", "Latest Classes"
 *   Build links   : a[href*="/poe-2/builds/"] — 8 items
 *   Guide links   : a[href*="guide"] — 16 items
 *   "Latest Classes" H2 is the stable proxy for class content (no CSS class-selector widget)
 *   Search input  : input[placeholder*="builds"] (placeholder: "Ask me anything about PoE builds!")
 */
public class Poe2Page extends BasePage {

    private static final By PAGE_HEADING  = By.cssSelector("h1");
    /** Build links — href-based, stable across CSS class renames */
    private static final By BUILD_CARDS   = By.cssSelector("a[href*='/poe-2/builds/']");
    /** Guide links — href-based */
    private static final By GUIDES_SECTION = By.cssSelector("a[href*='guide']");
    /** "Latest Classes" H2 — proxy for character class section (no widget selector exists) */
    private static final By CLASS_SELECTOR = By.xpath("//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'class')]");
    /** Actual search input placeholder: "Ask me anything about PoE builds!" */
    private static final By SEARCH_INPUT  = By.cssSelector(
            "input[placeholder*='builds' i], input[placeholder*='PoE' i], input[type='text']"
    );

    public Poe2Page(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Path of Exile 2 page");
        open(TestConfig.BASE_URL + "/poe-2");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    /** Returns true if the "Latest Classes" H2 section heading is present. */
    public boolean isClassSelectorVisible() {
        return isPresent(CLASS_SELECTOR);
    }

    public void selectClass(String className) {
        log.step("Selecting class: " + className);
        click(By.xpath(String.format(".//a[contains(text(),'%s')]", className)));
    }

    public int getBuildCardCount() {
        return waitForAll(BUILD_CARDS).size();
    }

    public void searchBuilds(String keyword) {
        log.step("Searching for PoE2 builds: " + keyword);
        type(SEARCH_INPUT, keyword);
    }

    /** Returns true if guide links are present on the page. */
    public boolean isGuidesSectionVisible() {
        return isPresent(GUIDES_SECTION);
    }
}

