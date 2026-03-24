package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class GameCardsComponent extends BaseComponent {

    private static final By GAME_TILES = By.cssSelector(".games-tiles-grid-wrap a[href]");
    private static final String TILE_BY_HREF_XPATH = ".//a[contains(@href,'/%s')]";

    public GameCardsComponent(WebDriver driver) {
        super(driver, By.cssSelector("section.hl-games-list, section#game-list"));
    }

    public void clickGameTileByHref(String gameSlug) {
        log.step("Clicking game tile for slug: " + gameSlug);
        click(By.xpath(String.format(TILE_BY_HREF_XPATH, gameSlug)));
    }

    public int getCardCount() {
        return waitForGameTiles().size();
    }

    public List<String> getGameHrefs() {
        return waitForGameTiles().stream()
                .map(a -> a.getAttribute("href"))
                .filter(href -> href != null && !href.isBlank())
                .map(href -> href.replaceAll("\\?.*", "")
                        .replace("https://mobalytics.gg", ""))
                .toList();
    }

    public boolean hasTileForHref(String gameSlug) {
        return !findElements(By.xpath(String.format(TILE_BY_HREF_XPATH, gameSlug))).isEmpty();
    }

    public void hoverOverTile(String gameSlug) {
        log.step("Hovering over game tile: " + gameSlug);
        WebElement tile = findElement(By.xpath(String.format(TILE_BY_HREF_XPATH, gameSlug)));
        new Actions(driver).moveToElement(tile).perform();
    }

    private List<WebElement> waitForGameTiles() {
        return wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(GAME_TILES, 0));
    }
}
