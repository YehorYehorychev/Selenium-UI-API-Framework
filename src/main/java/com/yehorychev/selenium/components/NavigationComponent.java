package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Navigation component — represents the site header with logo and game navigation links.
 *
 * Encapsulates:
 *   - Logo link
 *   - Game navigation links (LoL, TFT, Valorant, etc.)
 *   - Social media links
 *   - Authentication buttons (login / sign up)
 *
 * Usage:
 *   NavigationComponent nav = new NavigationComponent(driver);
 *   nav.clickGameLink("LoL");
 *   assertTrue(nav.isLogoVisible());
 *   List<String> games = nav.getAvailableGames();
 */
public class NavigationComponent extends BaseComponent {

    // ── Selectors (relative to root) ─────────────────────────────────────────

    private static final By LOGO = By.cssSelector("a.base-logo");
    // All anchor tags inside the nav — includes LoL, TFT, PoE2, Diablo 4, etc.
    private static final By GAME_LINKS = By.cssSelector("nav a");
    private static final By SOCIAL_LINKS = By.cssSelector("a[href*='twitter'], a[href*='discord'], a[href*='youtube']");

    // Sign In button — lives in the React app header on sub-pages (LoL, PoE2, etc.),
    // NOT on the marketing homepage. Uses XPath text match since the class names are
    // atomic/hashed and change on every deploy.
    private static final By SIGN_IN_BUTTON_XPATH =
            By.xpath("//button[.//span[translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='sign in']]");

    // XPath template for finding links by visible text
    private static final String LINK_BY_TEXT_XPATH = ".//nav//a[contains(text(),'%s')]";

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a NavigationComponent bound to the site header.
     *
     * @param driver active WebDriver instance
     */
    public NavigationComponent(WebDriver driver) {
        super(driver, By.cssSelector("header.site-header, header[role='banner']"));
    }

    // ── Logo interactions ─────────────────────────────────────────────────────

    /**
     * Clicks the site logo (typically navigates to home page).
     */
    public void clickLogo() {
        log.step("Clicking site logo");
        click(LOGO);
    }

    /**
     * Returns true if the logo is visible.
     *
     * @return logo visibility status
     */
    public boolean isLogoVisible() {
        try {
            return findElement(LOGO).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Game navigation ──────────────────────────────────────────────────────

    /**
     * Clicks a game navigation link by its visible text.
     *
     * @param gameName visible game name (e.g. "LoL", "TFT", "Valorant")
     */
    public void clickGameLink(String gameName) {
        log.step("Clicking game link: " + gameName);
        By locator = By.xpath(String.format(LINK_BY_TEXT_XPATH, gameName));
        click(locator);
    }

    /**
     * Returns a list of all available game names in the navigation.
     *
     * @return list of game names
     */
    public List<String> getAvailableGames() {
        return findElements(GAME_LINKS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Returns true if a game link with the given name is present.
     *
     * @param gameName game name to check
     * @return presence status
     */
    public boolean hasGameLink(String gameName) {
        return getAvailableGames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(gameName));
    }

    // ── General navigation links ─────────────────────────────────────────────

    /**
     * Clicks any navigation link by its visible text.
     *
     * @param linkText visible text of the link
     */
    public void clickLink(String linkText) {
        log.step("Clicking navigation link: " + linkText);
        By locator = By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText));
        click(locator);
    }

    /**
     * Returns the href attribute of a navigation link by its text.
     *
     * @param linkText visible text of the link
     * @return href URL
     */
    public String getLinkHref(String linkText) {
        By locator = By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText));
        return getAttribute(locator, "href");
    }

    // ── Social links ─────────────────────────────────────────────────────────

    /**
     * Returns the count of social media links in the header.
     *
     * @return number of social links
     */
    public int getSocialLinksCount() {
        return findElements(SOCIAL_LINKS).size();
    }

    /**
     * Returns true if social links are visible.
     *
     * @return social links visibility status
     */
    public boolean areSocialLinksVisible() {
        return !findElements(SOCIAL_LINKS).isEmpty();
    }

    // ── Authentication ───────────────────────────────────────────────────────

    /**
     * Clicks the Sign In button in the React app header (sub-pages only).
     * The Sign In button does not exist on the marketing homepage.
     */
    public void clickLogin() {
        log.step("Clicking Sign In button");
        waitForClickableGlobal(SIGN_IN_BUTTON_XPATH).click();
    }

    /**
     * Returns true if the Sign In button is visible.
     * The button only appears on sub-pages (LoL, PoE2, etc.), not on the homepage.
     *
     * @return sign in button visibility status
     */
    public boolean isLoginButtonVisible() {
        try {
            return wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                    .visibilityOfElementLocated(SIGN_IN_BUTTON_XPATH)
            ) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
