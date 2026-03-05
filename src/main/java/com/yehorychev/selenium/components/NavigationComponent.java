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
    private static final By NAV_LINKS = By.cssSelector("nav a");
    private static final By GAME_LINKS = By.cssSelector("nav a[href*='/lol'], nav a[href*='/tft'], nav a[href*='/valorant']");
    private static final By SOCIAL_LINKS = By.cssSelector("a[href*='twitter'], a[href*='discord'], a[href*='youtube']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-testid='login'], a[href*='/login']");

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
     * Clicks the login button in the header.
     */
    public void clickLogin() {
        log.step("Clicking login button");
        click(LOGIN_BUTTON);
    }

    /**
     * Returns true if the login button is visible (user not authenticated).
     *
     * @return login button visibility status
     */
    public boolean isLoginButtonVisible() {
        try {
            return findElement(LOGIN_BUTTON).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
