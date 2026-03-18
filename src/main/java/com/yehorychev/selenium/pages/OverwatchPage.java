package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Overwatch section on Mobalytics.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/overwatch:
 *   H1           : "Overwatch\nOverwatch Heroes Guides, Stadium Builds, and Tier Lists"
 *   H2s          : "Latest Stadium Builds"
 *   Hero links   : a[href*="/overwatch/"] — 51+
 */
public class OverwatchPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * "Latest Stadium Builds" H2 section.
     */
    private static final By STADIUM_BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'stadium') or " +
            "contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    /**
     * Hero/guide links — stable href pattern.
     */
    private static final By HERO_LINKS = By.cssSelector("a[href*='/overwatch/']");

    /**
     * Sign In button — present on Overwatch sub-page.
     */
    private static final By SIGN_IN_BUTTON = By.xpath(
            "//button[.//span[translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='sign in']]"
    );

    // ── Constructor ──────────────────────────────────────────────────────────

    public OverwatchPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Overwatch page.
     */
    public void open() {
        log.step("Opening Overwatch page");
        open(TestConfig.BASE_URL + "/overwatch");
        waitForVisible(PAGE_HEADING);
    }

    /**
     * Returns true if the page heading is visible.
     */
    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    /**
     * Returns the page heading text (may be multi-line).
     */
    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    // ── Content checks ────────────────────────────────────────────────────────

    /**
     * Returns true if the Stadium Builds section heading is present.
     */
    public boolean isStadiumBuildsSectionPresent() {
        return isPresent(STADIUM_BUILDS_SECTION);
    }

    /**
     * Returns the number of hero/guide links on the page.
     */
    public int getHeroLinkCount() {
        return driver.findElements(HERO_LINKS).size();
    }

    /**
     * Returns true if the Sign In button is visible.
     */
    public boolean isSignInButtonVisible() {
        return isVisible(SIGN_IN_BUTTON);
    }
}

