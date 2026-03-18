package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the League of Legends Tier List page.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/lol/tier-list:
 *   H1               : "League of Legends Tier List for Low ELO..."
 *   Champion links   : a[href*="/lol/champions/"] — 220+ links
 *   Filters button   : .m-1sfidpt (text "Filters")
 *   Methodology H2   : h2 containing "Methodology"
 */
public class LolTierListPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * All champion links — stable URL pattern covering every tier row entry.
     */
    private static final By CHAMPION_LINKS = By.cssSelector("a[href*='/lol/champions/']");

    /**
     * Filters button — text-based fallback when CSS class is hashed.
     */
    private static final By FILTERS_BUTTON = By.xpath(
            "//button[normalize-space(text())='Filters' or normalize-space(.)='Filters']"
    );

    /**
     * Methodology section — always present as an H2 on the tier list page.
     */
    private static final By METHODOLOGY_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'methodology')]"
    );

    /**
     * Patch information text — any element referencing the current patch.
     */
    private static final By PATCH_INFO = By.xpath(
            "//*[contains(text(),'Patch') or contains(text(),'patch')]"
    );

    // ── Constructor ──────────────────────────────────────────────────────────

    public LolTierListPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the LoL Tier List page and waits for the heading to appear.
     */
    public void open() {
        log.step("Opening LoL Tier List page");
        open(TestConfig.BASE_URL + "/lol/tier-list");
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
     * Returns the number of champion links displayed on the tier list.
     */
    public int getChampionLinkCount() {
        return waitForAll(CHAMPION_LINKS).size();
    }

    /**
     * Returns true if the Methodology section heading is present.
     */
    public boolean isMethodologySectionPresent() {
        return isPresent(METHODOLOGY_SECTION);
    }

    /**
     * Returns true if a Filters button is present on the page.
     */
    public boolean isFilterButtonPresent() {
        return isPresent(FILTERS_BUTTON);
    }

    /**
     * Clicks the Filters button to open the filter panel.
     */
    public void clickFilters() {
        log.step("Clicking Filters button on LoL Tier List");
        click(FILTERS_BUTTON);
    }

    /**
     * Returns true if any patch-reference text is present on the page.
     */
    public boolean isPatchInfoPresent() {
        return isPresent(PATCH_INFO);
    }
}

