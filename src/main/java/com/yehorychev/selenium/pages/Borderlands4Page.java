package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Borderlands 4 section on Mobalytics.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/borderlands-4:
 *   H1           : "Borderlands 4 Builds, Guides & More"
 *   H2s          : "Latest Builds"
 *   Content links: a[href*="/borderlands-4/"] — 21+
 */
public class Borderlands4Page extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * "Latest Builds" H2 section.
     */
    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    /**
     * All Borderlands 4 sub-page content links.
     */
    private static final By CONTENT_LINKS = By.cssSelector("a[href*='/borderlands-4/']");

    // ── Constructor ──────────────────────────────────────────────────────────

    public Borderlands4Page(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Borderlands 4 page.
     */
    public void open() {
        log.step("Opening Borderlands 4 page");
        open(TestConfig.BASE_URL + "/borderlands-4");
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
     * Returns the count of Borderlands 4 content links.
     */
    public int getContentLinkCount() {
        return driver.findElements(CONTENT_LINKS).size();
    }
}

