package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the TFT Tier List page.
 *
 * Selectors confirmed via live DOM inspection:
 *   Navigating to /tft/tier-list redirects to /tft/tier-list/team-comps.
 *   H1           : "TFT Comps Tier List"
 *   Unit links   : a[href*="/tft/champions/"] or a[href*="/tft/units/"] — 43+ links
 */
public class TftTierListPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * Unit/champion links — always present in a tier list page.
     */
    private static final By UNIT_LINKS = By.cssSelector(
            "a[href*='/tft/champions/'], a[href*='/tft/units/']"
    );

    /**
     * Tier list sub-tab links — after redirect, tabs like /tft/tier-list/champions,
     * /tft/tier-list/items, /tft/tier-list/traits are present in the page nav.
     * Confirmed via live DOM: URL is /tft/tier-list/team-comps with sub-nav links.
     */
    private static final By TEAM_COMP_LINKS = By.cssSelector("a[href*='/tft/tier-list/']");

    // ── Constructor ──────────────────────────────────────────────────────────

    public TftTierListPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the TFT Tier List page (redirects to /tft/tier-list/team-comps).
     */
    public void open() {
        log.step("Opening TFT Tier List page");
        open(TestConfig.BASE_URL + "/tft/tier-list");
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

    // ── Content checks ────────────────────────────────────────────────────────

    /**
     * Returns the number of unit/champion links present in the tier list.
     */
    public int getUnitLinkCount() {
        return waitForAll(UNIT_LINKS).size();
    }

    /**
     * Returns true if unit/champion links are visible on the page.
     */
    public boolean hasUnitLinks() {
        return isPresent(UNIT_LINKS);
    }

    /**
     * Returns true if team comp links are visible on the page.
     */
    public boolean hasTeamCompLinks() {
        return isPresent(TEAM_COMP_LINKS);
    }
}


