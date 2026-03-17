package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Valorant section.
 * <p>
 * Represents the Valorant-specific page with agent tier lists,
 * weapon guides, builds, and performance analytics.
 * <p>
 * Usage:
 * ValorantPage valorantPage = new ValorantPage(driverContext.getDriver());
 * valorantPage.open();
 * assertTrue(valorantPage.isLoaded());
 * assertTrue(valorantPage.isTierListVisible());
 */
public class ValorantPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");
    /**
     * Search input on /valorant/search — the page Mobalytics redirects to from /valorant.
     * Used for player profile lookup; stable identifier on the player search page.
     */
    private static final By SEARCH_INPUT = By.cssSelector(
            "input[type='search'], input[type='text'], input[placeholder*='name' i], input[placeholder*='search' i]"
    );
    /**
     * Agent tier-list cards — only present on /valorant when routed to agent analytics pages.
     * Falls back gracefully: if selector returns empty list, count will be 0.
     */
    private static final By AGENT_CARDS = By.cssSelector(
            "[class*='agent-card'], [class*='AgentCard'], [data-testid='agent-card']"
    );
    private static final By TIER_LIST = By.cssSelector("[class*='tier-list'], [data-testid='tier-list']");
    private static final By WEAPON_SECTION = By.cssSelector(".weapon-section, [data-testid='weapons']");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a ValorantPage instance bound to the given driver.
     *
     * @param driver active WebDriver from DriverContext
     */
    public ValorantPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the Valorant page.
     */
    public void open() {
        log.step("Opening Valorant page");
        open(TestConfig.BASE_URL + "/valorant");
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
     * Searches for an agent or weapon by keyword.
     *
     * @param keyword search keyword
     */
    public void search(String keyword) {
        log.step("Searching in Valorant page: " + keyword);
        type(SEARCH_INPUT, keyword);
    }

    // ── Content ───────────────────────────────────────────────────────────────

    /**
     * Returns the number of agent cards currently displayed.
     *
     * @return agent card count
     */
    public int getAgentCardCount() {
        return waitForAll(AGENT_CARDS).size();
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    /**
     * Returns true if the tier list section is visible.
     *
     * @return tier list visibility status
     */
    public boolean isTierListVisible() {
        return isVisible(TIER_LIST);
    }

    /**
     * Returns true if the weapon section is visible.
     *
     * @return weapon section visibility status
     */
    public boolean isWeaponSectionVisible() {
        return isVisible(WEAPON_SECTION);
    }
}
