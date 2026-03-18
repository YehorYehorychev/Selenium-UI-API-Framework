package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the League of Legends Champions list page.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/lol/champions:
 *   H1              : "ALL LEAGUE OF LEGENDS CHAMPIONS, BUILDS AND STATS AT YOUR FINGERTIPS"
 *   Champion links  : a[href*="/lol/champions/"] — 173+ links
 *   Search input    : input[placeholder*="Game Name"]
 */
public class LolChampionsPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * All champion card links — stable href-based selector.
     */
    private static final By CHAMPION_LINKS = By.cssSelector("a[href*='/lol/champions/']");

    /**
     * Search input — confirmed placeholder text on the champions list page.
     */
    private static final By SEARCH_INPUT = By.cssSelector(
            "input[placeholder*='Game Name'], input[placeholder*='Champion'], input[placeholder*='champion']"
    );

    /**
     * Individual champion card anchor — e.g. /lol/champions/ahri/build.
     */
    private static final String CHAMPION_LINK_XPATH = "//a[contains(@href,'/lol/champions/%s/')]";

    // ── Constructor ──────────────────────────────────────────────────────────

    public LolChampionsPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the LoL Champions list page.
     */
    public void open() {
        log.step("Opening LoL Champions page");
        open(TestConfig.BASE_URL + "/lol/champions");
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

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Types a champion name into the search input.
     *
     * @param championName champion name to search
     */
    public void searchChampion(String championName) {
        log.step("Searching for champion: " + championName);
        type(SEARCH_INPUT, championName);
    }

    // ── Content checks ────────────────────────────────────────────────────────

    /**
     * Returns the total number of champion card links visible.
     */
    public int getChampionCount() {
        return waitForAll(CHAMPION_LINKS).size();
    }

    /**
     * Returns true if the search input field is present on the page.
     */
    public boolean isSearchInputVisible() {
        return isVisible(SEARCH_INPUT);
    }

    /**
     * Clicks a specific champion card by champion name slug (e.g. "ahri").
     *
     * @param championSlug URL slug of the champion (lowercase name)
     */
    public void clickChampion(String championSlug) {
        log.step("Clicking champion: " + championSlug);
        click(By.xpath(String.format(CHAMPION_LINK_XPATH, championSlug)));
    }

    /**
     * Returns true if a champion link for the given slug is present.
     *
     * @param championSlug URL slug to check (e.g. "ahri", "jinx")
     */
    public boolean isChampionPresent(String championSlug) {
        return isPresent(By.xpath(String.format(CHAMPION_LINK_XPATH, championSlug)));
    }
}

