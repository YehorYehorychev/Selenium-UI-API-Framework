package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Elden Ring Nightreign section on Mobalytics.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/elden-ring-nightreign:
 *   H1            : "Elden Ring Nightreign - Builds, Guides, Items & More"
 *   H2s           : "Explore Nightfarers!", "Discover Nightlords", "Recent Builds", "Latest Guides"
 *   Content links : a[href*="/elden-ring-nightreign/"] — 47+
 */
public class NightReignPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * "Explore Nightfarers!" H2 section.
     */
    private static final By NIGHTFARERS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'nightfarer')]"
    );

    /**
     * "Discover Nightlords" H2 section.
     */
    private static final By NIGHTLORDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'nightlord')]"
    );

    /**
     * "Recent Builds" H2 section.
     */
    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    /**
     * "Latest Guides" H2 section.
     */
    private static final By GUIDES_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'guide')]"
    );

    /**
     * All Nightreign content links.
     */
    private static final By CONTENT_LINKS = By.cssSelector("a[href*='/elden-ring-nightreign/']");

    // ── Constructor ──────────────────────────────────────────────────────────

    public NightReignPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Elden Ring Nightreign page.
     */
    public void open() {
        log.step("Opening Elden Ring Nightreign page");
        open(TestConfig.BASE_URL + "/elden-ring-nightreign");
        waitForVisible(PAGE_HEADING);
    }

    /**
     * Returns true if the page heading is visible.
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

    // ── Content checks ────────────────────────────────────────────────────────

    /**
     * Returns true if the Nightfarers section is present.
     */
    public boolean isNightfarersSectionPresent() {
        return isPresent(NIGHTFARERS_SECTION);
    }

    /**
     * Returns true if the Nightlords section is present.
     */
    public boolean isNightlordsSectionPresent() {
        return isPresent(NIGHTLORDS_SECTION);
    }

    /**
     * Returns true if the Recent Builds section is present.
     */
    public boolean isBuildsSectionPresent() {
        return isPresent(BUILDS_SECTION);
    }

    /**
     * Returns true if the Latest Guides section is present.
     */
    public boolean isGuidesSectionPresent() {
        return isPresent(GUIDES_SECTION);
    }

    /**
     * Returns the count of Nightreign content links.
     */
    public int getContentLinkCount() {
        return driver.findElements(CONTENT_LINKS).size();
    }
}

