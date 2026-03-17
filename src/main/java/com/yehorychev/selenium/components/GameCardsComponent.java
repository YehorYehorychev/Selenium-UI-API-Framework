package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Game Cards component — represents the game tiles grid on the home page.
 *
 * Actual DOM (confirmed via mobalytics.gg live inspection):
 *   Root  : section.hl-games-list  (id="game-list")
 *   Tiles : direct <a> children of .games-tiles-grid-wrap — no CSS class on the tile itself.
 *   Each tile href follows the pattern: /lol, /tft, /poe-2, /diablo-4, etc.
 *
 * Usage:
 *   GameCardsComponent gameCards = new GameCardsComponent(driver);
 *   gameCards.clickGameTileByHref("lol");
 *   int count = gameCards.getCardCount();
 *   assertTrue(gameCards.hasTileForHref("tft"));
 */
public class GameCardsComponent extends BaseComponent {

    // ── Selectors (relative to root) ─────────────────────────────────────────

    /** All game tile <a> links inside the grid wrapper. */
    private static final By GAME_TILES  = By.cssSelector(".games-tiles-grid-wrap > a, a[href*='/lol'], a");
    /** Simplified: any direct-child <a> inside the section root. */
    private static final By TILE_LINKS  = By.cssSelector("a");

    // XPath: find tile <a> whose href contains the given game slug (e.g. "lol", "tft")
    private static final String TILE_BY_HREF_XPATH = ".//a[contains(@href,'/%s')]";

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a GameCardsComponent bound to the game tiles section.
     * Root: section.hl-games-list — confirmed via live DOM inspection.
     *
     * @param driver active WebDriver instance
     */
    public GameCardsComponent(WebDriver driver) {
        super(driver, By.cssSelector("section.hl-games-list, section#game-list"));
    }

    // ── Card interactions ────────────────────────────────────────────────────

    /**
     * Clicks a game tile by its URL slug (e.g. "lol", "tft", "diablo-4").
     * The tile <a> href matches /{slug}?int_source=...
     *
     * @param gameSlug URL slug for the game (e.g. "lol", "tft", "diablo-4", "poe-2")
     */
    public void clickGameTileByHref(String gameSlug) {
        log.step("Clicking game tile for slug: " + gameSlug);
        By locator = By.xpath(String.format(TILE_BY_HREF_XPATH, gameSlug));
        click(locator);
    }

    /**
     * Returns the total number of game tiles displayed.
     *
     * @return game tile count
     */
    public int getCardCount() {
        return findElements(TILE_LINKS).size();
    }

    /**
     * Returns a list of href paths from all game tile links (e.g. ["/lol", "/tft", ...]).
     *
     * @return list of href path fragments
     */
    public List<String> getGameHrefs() {
        return findElements(TILE_LINKS).stream()
                .map(a -> a.getAttribute("href"))
                .filter(href -> href != null && !href.isBlank())
                .map(href -> href.replaceAll("\\?.*", "")  // strip query params
                                 .replace("https://mobalytics.gg", ""))
                .collect(Collectors.toList());
    }

    /**
     * Returns true if a tile with the given URL slug is present.
     *
     * @param gameSlug URL slug to check (e.g. "lol", "tft", "diablo-4")
     * @return presence status
     */
    public boolean hasTileForHref(String gameSlug) {
        return !findElements(By.xpath(String.format(TILE_BY_HREF_XPATH, gameSlug))).isEmpty();
    }

    /**
     * Hovers over the game tile with the given URL slug.
     *
     * @param gameSlug URL slug for the game
     */
    public void hoverOverTile(String gameSlug) {
        log.step("Hovering over game tile: " + gameSlug);
        By locator = By.xpath(String.format(TILE_BY_HREF_XPATH, gameSlug));
        WebElement tile = findElement(locator);
        new Actions(driver).moveToElement(tile).perform();
    }
}
