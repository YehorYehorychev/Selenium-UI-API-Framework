package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Deadlock game section on Mobalytics.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/deadlock:
 *   H1            : "Deadlock Hero Guides"
 *   H2s           : "Deadlock Heroes", "Featured Builds"
 *   Build/hero links : a[href*="/deadlock/build"], a[href*="/deadlock/hero"], a[href*="/deadlock/guide"] — 9+
 *   Sign In button: present via XPath text match (same as sub-pages)
 */
public class DeadlockPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * "Deadlock Heroes" section H2.
     */
    private static final By HEROES_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'hero')]"
    );

    /**
     * "Featured Builds" section H2.
     */
    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    /**
     * Content links for builds, hero guides, and general guides.
     */
    private static final By CONTENT_LINKS = By.cssSelector(
            "a[href*='/deadlock/build'], a[href*='/deadlock/hero'], a[href*='/deadlock/guide']"
    );

    /**
     * Sign In button — same XPath pattern used across all Mobalytics sub-pages.
     */
    private static final By SIGN_IN_BUTTON = By.xpath(
            "//button[.//span[translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='sign in']]"
    );

    // ── Constructor ──────────────────────────────────────────────────────────

    public DeadlockPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Deadlock game page.
     */
    public void open() {
        log.step("Opening Deadlock page");
        open(TestConfig.BASE_URL + "/deadlock");
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
     * Returns true if the Heroes section heading is present.
     */
    public boolean isHeroesSectionPresent() {
        return isPresent(HEROES_SECTION);
    }

    /**
     * Returns true if the Builds section heading is present.
     */
    public boolean isBuildsSectionPresent() {
        return isPresent(BUILDS_SECTION);
    }

    /**
     * Returns the number of content links (builds, heroes, guides) present.
     */
    public int getContentLinkCount() {
        return driver.findElements(CONTENT_LINKS).size();
    }

    /**
     * Returns true if the Sign In button is visible on the page.
     */
    public boolean isSignInButtonVisible() {
        return isVisible(SIGN_IN_BUTTON);
    }
}

