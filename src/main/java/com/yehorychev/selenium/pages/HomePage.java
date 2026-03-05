package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Mobalytics home page.
 *
 * All selectors are encapsulated here — steps interact only through public methods,
 * so selector changes require edits in a single place.
 *
 * Usage:
 *   HomePage homePage = new HomePage(driverContext.getDriver());
 *   homePage.open();
 *   assertTrue(homePage.isLoaded());
 *   homePage.clickNavGame("LoL");
 */
public class HomePage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    /**
     * Logo anchor in the site header.
     */
    private static final By LOGO = By.cssSelector("a.base-logo");

    /**
     * Site header container.
     */
    private static final By HEADER = By.cssSelector("header.site-header");

    /**
     * Hero section headline (h1).
     */
    private static final By HERO_HEADING = By.cssSelector("h1");

    /**
     * Hero section root element.
     */
    private static final By HERO_SECTION = By.cssSelector(".hl-hero");

    /**
     * Desktop-app download CTA button in the hero.
     */
    private static final By DOWNLOAD_CTA = By.cssSelector("a.download-btn");

    /**
     * A specific navigation game link by its visible text.
     * Use getNavGameLocator(String) to build the locator dynamically.
     */
    private static final String NAV_LINK_XPATH = "//nav[contains(@class,'site-navigation')]//li[contains(@class,'menu-item')]/a[normalize-space()='%s']";

    /**
     * Social links in the header (Twitter, Facebook, etc.).
     */
    private static final By SOCIAL_LINKS = By.cssSelector("header .soc-link");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a HomePage instance bound to the given driver.
     *
     * @param driver active WebDriver from DriverContext
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates the browser to the application's base URL and waits for the
     * hero section to be visible.
     */
    public void open() {
        log.step("Opening Mobalytics home page: " + TestConfig.BASE_URL);
        openBaseUrl();
        waitForVisible(HERO_SECTION);
    }

    /**
     * Clicks the logo and waits for the page to return to the home URL.
     */
    public void clickLogo() {
        log.step("Clicking site logo");
        click(LOGO);
        waitForUrl("mobalytics.gg");
    }

    /**
     * Clicks the desktop-app download CTA button in the hero section.
     */
    public void clickDownloadCta() {
        log.step("Clicking download CTA button");
        click(DOWNLOAD_CTA);
    }

    /**
     * Clicks a game navigation link by its visible label (e.g. "LoL", "TFT", "Diablo 4").
     *
     * @param gameName the visible text of the nav item
     */
    public void clickNavGame(String gameName) {
        log.step("Clicking nav game link: " + gameName);
        click(getNavGameLocator(gameName));
    }

    // ── Page state / assertions ───────────────────────────────────────────────

    /**
     * Returns true when the hero section is visible — page is fully loaded.
     *
     * @return true if the home page is loaded
     */
    public boolean isLoaded() {
        return isVisible(HERO_SECTION);
    }

    /**
     * Returns true if the site header is visible.
     *
     * @return header visibility
     */
    public boolean isHeaderVisible() {
        return isVisible(HEADER);
    }

    /**
     * Returns the hero heading text (the big h1 copy).
     *
     * @return hero heading text, trimmed
     */
    public String getHeroHeadingText() {
        return getText(HERO_HEADING);
    }

    /**
     * Returns the href of the download CTA button.
     *
     * @return download URL string
     */
    public String getDownloadCtaHref() {
        return getAttribute(DOWNLOAD_CTA, "href");
    }

    /**
     * Returns true if a game nav link with the given label exists in the header.
     *
     * @param gameName the visible text of the nav item
     * @return presence of the nav item
     */
    public boolean isNavGamePresent(String gameName) {
        return isPresent(getNavGameLocator(gameName));
    }

    /**
     * Returns the number of social links visible in the header.
     *
     * @return count of social link elements
     */
    public int getSocialLinkCount() {
        return waitForAll(SOCIAL_LINKS).size();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Builds an XPath locator for a navigation game link by its visible text.
     *
     * @param gameName visible nav item label (e.g. "LoL")
     * @return By XPath locator
     */
    private By getNavGameLocator(String gameName) {
        return By.xpath(String.format(NAV_LINK_XPATH, gameName));
    }
}

