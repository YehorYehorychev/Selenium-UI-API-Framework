package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the TFT Team Compositions page.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/tft/team-comps:
 *   H1            : "TFT Meta Comps in Set 16"
 *   Comp elements : [class*="comp"], [class*="Comp"] — 4+ found
 *   Tier list link: a[href*="/tft/tier-list"]
 */
public class TftTeamCompsPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * Comp guide entry links — each featured comp points to /tft/comps-guide/{slug}.
     * Confirmed via live DOM inspection: team-comps page renders /tft/comps-guide/ hrefs.
     */
    private static final By COMP_CARDS = By.cssSelector("a[href*='/tft/comps-guide/']");

    /**
     * Link back to the TFT Tier List — present in page navigation.
     */
    private static final By TIER_LIST_LINK = By.cssSelector("a[href*='/tft/tier-list']");

    /**
     * Champion/unit links that make up the comps.
     */
    private static final By UNIT_LINKS = By.cssSelector(
            "a[href*='/tft/champions/'], a[href*='/tft/units/']"
    );

    // ── Constructor ──────────────────────────────────────────────────────────

    public TftTeamCompsPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the TFT Team Comps page.
     */
    public void open() {
        log.step("Opening TFT Team Comps page");
        open(TestConfig.BASE_URL + "/tft/team-comps");
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
     * Returns the number of comp cards or comp-related elements visible.
     */
    public int getCompCardCount() {
        return driver.findElements(COMP_CARDS).size();
    }

    /**
     * Returns true if at least one comp card/row is present.
     */
    public boolean hasCompCards() {
        return isPresent(COMP_CARDS);
    }

    /**
     * Returns true if the link to the TFT Tier List is present.
     */
    public boolean hasTierListLink() {
        return isPresent(TIER_LIST_LINK);
    }

    /**
     * Returns true if unit links are present inside the team comps.
     */
    public boolean hasUnitLinks() {
        return isPresent(UNIT_LINKS);
    }
}


