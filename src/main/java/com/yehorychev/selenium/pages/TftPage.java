package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TftPage extends BasePage {

    private static final By PAGE_HEADING  = By.cssSelector("h1");
    private static final By SEARCH_INPUT  = By.cssSelector(
            "input[placeholder*='Game Name'], input[placeholder*='Search']");
    private static final By CHAMPION_CARDS = By.cssSelector(
            "a[href*='/tft/champions/'], a[href*='/tft/units/']");
    private static final By TIER_LIST     = By.cssSelector("a[href*='/tft/tier-list']");
    private static final By TEAM_COMPS    = By.cssSelector("a[href*='comp']");
    private static final By ITEMS_SECTION = By.cssSelector(
            "a[href*='/tft/items'], a[href*='/tft/item']");

    public TftPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Teamfight Tactics page");
        open(TestConfig.BASE_URL + "/tft");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public void searchChampion(String championName) {
        log.step("Searching for TFT champion: " + championName);
        type(SEARCH_INPUT, championName);
    }

    public int getChampionCardCount() {
        return waitForAll(CHAMPION_CARDS).size();
    }

    public boolean isTierListVisible() {
        return isPresent(TIER_LIST);
    }

    public boolean isTeamCompsSectionVisible() {
        return isPresent(TEAM_COMPS);
    }

    public boolean isItemsSectionVisible() {
        return isPresent(ITEMS_SECTION);
    }
}
