package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Game Cards component — represents the grid of game cards on the home page.
 *
 * Encapsulates:
 *   - Individual game cards (LoL, TFT, Valorant, etc.)
 *   - Card images and titles
 *   - Hover effects and CTAs
 *
 * Usage:
 *   GameCardsComponent gameCards = new GameCardsComponent(driver);
 *   gameCards.clickGameCard("League of Legends");
 *   int count = gameCards.getCardCount();
 *   assertTrue(gameCards.hasCard("TFT"));
 */
public class GameCardsComponent extends BaseComponent {

    // ── Selectors (relative to root) ─────────────────────────────────────────

    private static final By GAME_CARDS = By.cssSelector(".game-card, [data-testid='game-card']");
    private static final By CARD_TITLES = By.cssSelector(".game-card__title, h3");
    private static final By CARD_IMAGES = By.cssSelector(".game-card__image, img");

    // XPath template for finding cards by title
    private static final String CARD_BY_TITLE_XPATH = ".//div[contains(@class,'game-card') and .//text()[contains(.,'%s')]]";

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a GameCardsComponent bound to the game cards section.
     *
     * @param driver active WebDriver instance
     */
    public GameCardsComponent(WebDriver driver) {
        super(driver, By.cssSelector(".game-cards, [data-testid='game-cards-section']"));
    }

    // ── Card interactions ────────────────────────────────────────────────────

    /**
     * Clicks a game card by its title.
     *
     * @param gameTitle game title (e.g. "League of Legends", "TFT")
     */
    public void clickGameCard(String gameTitle) {
        log.step("Clicking game card: " + gameTitle);
        By locator = By.xpath(String.format(CARD_BY_TITLE_XPATH, gameTitle));
        click(locator);
    }

    /**
     * Returns the number of game cards displayed.
     *
     * @return game card count
     */
    public int getCardCount() {
        return findElements(GAME_CARDS).size();
    }

    /**
     * Returns a list of all game titles from the cards.
     *
     * @return list of game titles
     */
    public List<String> getGameTitles() {
        return findElements(CARD_TITLES).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Returns true if a card with the given title is present.
     *
     * @param gameTitle game title to check
     * @return presence status
     */
    public boolean hasCard(String gameTitle) {
        return getGameTitles().stream()
                .anyMatch(title -> title.equalsIgnoreCase(gameTitle));
    }

    /**
     * Hovers over a game card by its title.
     *
     * @param gameTitle game title
     */
    public void hoverOverCard(String gameTitle) {
        log.step("Hovering over game card: " + gameTitle);
        By locator = By.xpath(String.format(CARD_BY_TITLE_XPATH, gameTitle));
        WebElement card = findElement(locator);
        WebElement body = driver.findElement(By.tagName("body"));
        Actions actions = new Actions(driver);
        // Move mouse to a neutral position first so the hover-off state is reliably triggered
        actions.moveToElement(body).perform();
        // Then hover the actual card
        actions.moveToElement(card).perform();
    }
}
