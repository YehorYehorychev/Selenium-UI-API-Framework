package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for an individual LoL Champion Build page.
 *
 * Selectors confirmed via live DOM inspection on mobalytics.gg/lol/champions/ahri/build:
 *   H1              : "Ahri\n·Mid Build"  (champion name + role)
 *   H2s present     : "Builds", "Runes", "Expert Video Guide", "<CHAMPION> MATCHUPS OVERVIEW"
 *   Counter links   : a[href*="counter"] — 15 links
 *   Build links     : a[href*="build"] — multiple role variants
 *
 * Usage:
 *   LolChampionBuildPage page = new LolChampionBuildPage(driver);
 *   page.open("ahri");
 *   assertTrue(page.isLoaded());
 *   assertTrue(page.isRunesSectionPresent());
 */
public class LolChampionBuildPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────

    private static final By PAGE_HEADING = By.cssSelector("h1");

    /**
     * "Builds" H2 section — present as a primary section heading.
     */
    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    /**
     * "Runes" H2 section — always present on champion build pages.
     */
    private static final By RUNES_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rune')]"
    );

    /**
     * "Matchups" / counters section — confirmed via "MATCHUPS OVERVIEW" H2.
     */
    private static final By MATCHUPS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'matchup')]"
    );

    /**
     * Counter links — stable href-based selector.
     */
    private static final By COUNTER_LINKS = By.cssSelector("a[href*='counter']");

    /**
     * Role build variant links (e.g. Mid, Top, Support).
     */
    private static final By ROLE_BUILD_LINKS = By.cssSelector("a[href*='/lol/champions/'][href*='build']");

    // ── Constructor ──────────────────────────────────────────────────────────

    public LolChampionBuildPage(WebDriver driver) {
        super(driver);
    }

    // ── Page actions ──────────────────────────────────────────────────────────

    /**
     * Navigates to the build page for the given champion slug.
     *
     * @param championSlug URL slug (e.g. "ahri", "jinx", "yasuo")
     */
    public void open(String championSlug) {
        String url = TestConfig.BASE_URL + "/lol/champions/" + championSlug + "/build";
        log.step("Opening champion build page: " + url);
        super.open(url);
        waitForVisible(PAGE_HEADING);
    }

    /**
     * Returns true if the page heading is visible.
     */
    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    /**
     * Returns the raw heading text (e.g. "Ahri\n·Mid Build").
     */
    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    // ── Content sections ──────────────────────────────────────────────────────

    /**
     * Returns true if the Builds section heading is present.
     */
    public boolean isBuildsSectionPresent() {
        return isPresent(BUILDS_SECTION);
    }

    /**
     * Returns true if the Runes section heading is present.
     */
    public boolean isRunesSectionPresent() {
        return isPresent(RUNES_SECTION);
    }

    /**
     * Returns true if the Matchups/Counters section heading is present.
     */
    public boolean isMatchupsSectionPresent() {
        return isPresent(MATCHUPS_SECTION);
    }

    /**
     * Returns the number of counter/matchup links on the page.
     */
    public int getCounterLinkCount() {
        return driver.findElements(COUNTER_LINKS).size();
    }

    /**
     * Returns the number of role build variant links (e.g. Mid, Top, Support).
     */
    public int getRoleBuildLinkCount() {
        return driver.findElements(ROLE_BUILD_LINKS).size();
    }
}


