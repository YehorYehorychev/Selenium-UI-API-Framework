package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Teamfight Tactics section.
 * <p>
 * Selectors confirmed via live DOM inspection on mobalytics.gg/tft:
 * - Champion links : a[href*="/tft/champions/"] or a[href*="/tft/units/"] — 52 links
 * - Tier list      : a[href*="/tft/tier-list"] — present
 * - Team comps     : a[href*="comp"] — 11 links
 * - Search input   : input[placeholder*="Game Name"]
 */
public class TftPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By SEARCH_INPUT = By.cssSelector("input[placeholder*='Game Name'], input[placeholder*='Search']");
    /**
     * TFT champion / unit links — stable URL pattern
     */
    private static final By CHAMPION_CARDS = By.cssSelector("a[href*='/tft/champions/'], a[href*='/tft/units/']");
    /**
     * TFT-specific tier list links
     */
    private static final By TIER_LIST = By.cssSelector("a[href*='/tft/tier-list']");
    /**
     * Team comp links
     */
    private static final By TEAM_COMPS = By.cssSelector("a[href*='comp']");
    /**
     * Items section via links
     */
    private static final By ITEMS_SECTION = By.cssSelector("a[href*='/tft/items'], a[href*='/tft/item']");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a TftPage instance bound to the given driver.
     *
     * @param driver active WebDriver from DriverContext
     */
    public TftPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the TFT page.
     */
    public void open() {
        log.step("Opening Teamfight Tactics page");
        open(TestConfig.BASE_URL + "/tft");
        waitForVisible(PAGE_HEADING);
    }

    /**
     * Returns true if the page is loaded (heading is visible).
     *
     * @return page load status
     */
    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    /**
     * Returns the page heading text.
     *
     * @return heading text
     */
    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Searches for a TFT champion by name.
     *
     * @param championName champion name to search for
     */
    public void searchChampion(String championName) {
        log.step("Searching for TFT champion: " + championName);
        type(SEARCH_INPUT, championName);
    }

    /**
     * Returns the number of champion cards currently displayed.
     *
     * @return champion card count
     */
    public int getChampionCardCount() {
        return waitForAll(CHAMPION_CARDS).size();
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    /**
     * Returns true if the tier list section is visible.
     *
     * @return tier list visibility status
     */
    public boolean isTierListVisible() {
        return isPresent(TIER_LIST);
    }

    /**
     * Returns true if the team comps section is visible.
     *
     * @return team comps visibility status
     */
    public boolean isTeamCompsSectionVisible() {
        return isPresent(TEAM_COMPS);
    }

    /**
     * Returns true if the items section is visible.
     *
     * @return items section visibility status
     */
    public boolean isItemsSectionVisible() {
        return isPresent(ITEMS_SECTION);
    }
}
