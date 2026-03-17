package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the League of Legends section.
 * <p>
 * Selectors confirmed via live DOM inspection on mobalytics.gg/lol:
 * - Champion cards : a[href*="/lol/champions/"] — 19 links (stable URL pattern)
 * - Search input   : input[placeholder*="Game Name"] (type=text, not type=search)
 * - Tier list      : a[href*="tier-list"] links — multiple present
 * - Builds         : a[href*="/lol/build"] links
 */
public class LolPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");
    /**
     * Stable URL-based selector — champion page links are always /lol/champions/{name}/...
     */
    private static final By CHAMPION_CARDS = By.cssSelector("a[href*='/lol/champions/']");
    /**
     * Actual placeholder confirmed: "Game Name #Tag or Champion"
     */
    private static final By SEARCH_INPUT = By.cssSelector("input[placeholder*='Game Name'], input[placeholder*='Champion']");
    /**
     * Tier list links — present in page nav and in-page section
     */
    private static final By TIER_LIST_SECTION = By.cssSelector("a[href*='tier-list']");
    /**
     * Champion build links — present as featured content
     */
    private static final By BUILDS_SECTION = By.cssSelector("a[href*='/lol/champions/'][href*='build'], a[href*='/lol/build']");

    // ── Constructor ──────────────────────────────────────────────────────────

    public LolPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    public void open() {
        log.step("Opening League of Legends page");
        open(TestConfig.BASE_URL + "/lol");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    public void searchChampion(String championName) {
        log.step("Searching for champion: " + championName);
        type(SEARCH_INPUT, championName);
    }

    public int getChampionCardCount() {
        return waitForAll(CHAMPION_CARDS).size();
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    public boolean isTierListVisible() {
        return isPresent(TIER_LIST_SECTION);
    }

    public boolean isBuildsVisible() {
        return isPresent(BUILDS_SECTION);
    }
}
