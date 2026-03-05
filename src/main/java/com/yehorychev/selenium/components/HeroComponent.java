package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Hero component — represents the hero section at the top of the home page.
 *
 * Encapsulates:
 *   - Main headline (h1)
 *   - Sub-headline / description
 *   - CTA buttons (Download App, Sign Up, etc.)
 *   - Hero image / background
 *
 * Usage:
 *   HeroComponent hero = new HeroComponent(driver);
 *   String heading = hero.getHeadingText();
 *   hero.clickDownloadButton();
 *   assertTrue(hero.isCtaVisible());
 */
public class HeroComponent extends BaseComponent {

    // ── Selectors (relative to root) ─────────────────────────────────────────

    private static final By HEADING = By.cssSelector("h1");
    private static final By SUBHEADING = By.cssSelector("h2, p.hero-description");
    private static final By DOWNLOAD_BUTTON = By.cssSelector("a[href*='download'], button[data-testid='download']");
    private static final By SIGNUP_BUTTON = By.cssSelector("a[href*='signup'], button[data-testid='signup']");
    private static final By CTA_BUTTONS = By.cssSelector("a.cta-button, button.cta");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a HeroComponent bound to the hero section.
     *
     * @param driver active WebDriver instance
     */
    public HeroComponent(WebDriver driver) {
        super(driver, By.cssSelector("section.hero, [data-testid='hero-section']"));
    }

    // ── Content accessors ────────────────────────────────────────────────────

    /**
     * Returns the main hero heading text (h1).
     *
     * @return heading text, trimmed
     */
    public String getHeadingText() {
        return getText(HEADING);
    }

    /**
     * Returns the sub-heading or description text.
     *
     * @return sub-heading text, trimmed
     */
    public String getSubheadingText() {
        return getText(SUBHEADING);
    }

    /**
     * Returns true if the hero heading contains the expected text.
     *
     * @param expected expected text fragment (case-insensitive)
     * @return whether heading contains the text
     */
    public boolean headingContains(String expected) {
        return getHeadingText().toLowerCase().contains(expected.toLowerCase());
    }

    // ── CTA interactions ─────────────────────────────────────────────────────

    /**
     * Clicks the Download App button.
     */
    public void clickDownloadButton() {
        log.step("Clicking Download button in hero section");
        click(DOWNLOAD_BUTTON);
    }

    /**
     * Clicks the Sign Up button.
     */
    public void clickSignUpButton() {
        log.step("Clicking Sign Up button in hero section");
        click(SIGNUP_BUTTON);
    }

    /**
     * Returns true if CTA buttons are visible in the hero section.
     *
     * @return CTA visibility status
     */
    public boolean isCtaVisible() {
        return !findElements(CTA_BUTTONS).isEmpty();
    }

    /**
     * Returns the number of CTA buttons in the hero section.
     *
     * @return CTA button count
     */
    public int getCtaButtonCount() {
        return findElements(CTA_BUTTONS).size();
    }

    /**
     * Returns the href attribute of the download button.
     *
     * @return download link URL
     */
    public String getDownloadLink() {
        return getAttribute(DOWNLOAD_BUTTON, "href");
    }
}
