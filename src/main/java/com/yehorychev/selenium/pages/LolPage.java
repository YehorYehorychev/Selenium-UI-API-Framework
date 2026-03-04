package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the League of Legends section.
 *
 * <p>Represents the LoL-specific page with champion builds, tier lists, and game analytics.
 *
 * <p>Usage:
 * <pre>{@code
 *   LolPage lolPage = new LolPage(context.getDriver());
 *   lolPage.open();
 *   assertTrue(lolPage.isLoaded());
 *   lolPage.searchChampion("Ahri");
 * }</pre>
 */
public class LolPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By SEARCH_INPUT = By.cssSelector("input[type='search'], input[placeholder*='Search']");
    private static final By CHAMPION_CARDS = By.cssSelector(".champion-card, [data-testid='champion-card']");
    private static final By TIER_LIST_SECTION = By.cssSelector(".tier-list, [data-testid='tier-list']");
    private static final By BUILDS_SECTION = By.cssSelector(".builds-section, [data-testid='builds']");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a {@code LolPage} instance bound to the given driver.
     *
     * @param driver active {@link WebDriver} from {@code TestContext}
     */
    public LolPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the LoL page.
     */
    public void open() {
        log.step("Opening League of Legends page");
        open(TestConfig.BASE_URL + "/lol");
        waitForVisible(PAGE_HEADING);
    }

    /**
     * Returns {@code true} if the page is loaded (heading is visible).
     *
     * @return page load status
     */
    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Searches for a champion by name.
     *
     * @param championName champion name to search for
     */
    public void searchChampion(String championName) {
        log.step("Searching for champion: " + championName);
        type(SEARCH_INPUT, championName);
    }

    /**
     * Returns the number of champion cards displayed on the page.
     *
     * @return champion card count
     */
    public int getChampionCardCount() {
        return waitForAll(CHAMPION_CARDS).size();
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the tier list section is visible.
     *
     * @return tier list visibility status
     */
    public boolean isTierListVisible() {
        return isVisible(TIER_LIST_SECTION);
    }

    /**
     * Returns {@code true} if the builds section is visible.
     *
     * @return builds section visibility status
     */
    public boolean isBuildsVisible() {
        return isVisible(BUILDS_SECTION);
    }

    /**
     * Returns the page heading text.
     *
     * @return heading text
     */
    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }
}

