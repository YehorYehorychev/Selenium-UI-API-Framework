package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LolPage extends BasePage {

    private static final By PAGE_HEADING   = By.cssSelector("h1");
    private static final By CHAMPION_CARDS = By.cssSelector("a[href*='/lol/champions/']");
    private static final By SEARCH_INPUT   = By.cssSelector(
            "input[placeholder*='Game Name'], input[placeholder*='Champion']");
    private static final By TIER_LIST_SECTION = By.cssSelector("a[href*='tier-list']");
    private static final By BUILDS_SECTION    = By.cssSelector(
            "a[href*='/lol/champions/'][href*='build'], a[href*='/lol/build']");

    public LolPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening League of Legends page");
        open(TestConfig.BASE_URL + "/lol");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public void searchChampion(String championName) {
        log.step("Searching for champion: " + championName);
        type(SEARCH_INPUT, championName);
    }

    public int getChampionCardCount() {
        return waitForAll(CHAMPION_CARDS).size();
    }

    public boolean isTierListVisible() {
        return isPresent(TIER_LIST_SECTION);
    }

    public boolean isBuildsVisible() {
        return isPresent(BUILDS_SECTION);
    }
}
