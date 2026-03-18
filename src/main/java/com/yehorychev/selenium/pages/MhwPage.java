package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Monster Hunter Wilds section on Mobalytics.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/mhw:
 *   H1           : "MH Wilds: Builds, Weapons, and Monsters"
 *   H2s          : "Discover More Builds", "Latest Guides and News"
 *   Content links: a[href*="/mhw/build"], a[href*="/mhw/guide"], a[href*="/mhw/weapon"] — 15+
 */
public class MhwPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * "Discover More Builds" H2 section.
     */
    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    /**
     * "Latest Guides and News" H2 section.
     */
    private static final By GUIDES_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'guide')]"
    );

    /**
     * All MHW-specific content links (builds, guides, weapons).
     */
    private static final By CONTENT_LINKS = By.cssSelector(
            "a[href*='/mhw/build'], a[href*='/mhw/guide'], a[href*='/mhw/weapon'], a[href*='/mhw/']"
    );

    // ── Constructor ──────────────────────────────────────────────────────────

    public MhwPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Monster Hunter Wilds page.
     */
    public void open() {
        log.step("Opening Monster Hunter Wilds page");
        open(TestConfig.BASE_URL + "/mhw");
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
     * Returns true if the Builds section heading is present.
     */
    public boolean isBuildsSectionPresent() {
        return isPresent(BUILDS_SECTION);
    }

    /**
     * Returns true if the Guides/News section heading is present.
     */
    public boolean isGuidesSectionPresent() {
        return isPresent(GUIDES_SECTION);
    }

    /**
     * Returns the count of MHW-specific content links.
     */
    public int getContentLinkCount() {
        return driver.findElements(CONTENT_LINKS).size();
    }
}

