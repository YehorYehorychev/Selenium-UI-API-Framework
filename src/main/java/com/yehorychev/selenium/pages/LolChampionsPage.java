package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LolChampionsPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By CHAMPION_LINKS = By.cssSelector("a[href*='/lol/champions/']");
    private static final By SEARCH_INPUT = By.cssSelector(
            "input[placeholder*='Game Name'], input[placeholder*='Champion'], input[placeholder*='champion']"
    );
    private static final String CHAMPION_LINK_XPATH = "//a[contains(@href,'/lol/champions/%s/')]";

    public LolChampionsPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening LoL Champions page");
        open(TestConfig.BASE_URL + "/lol/champions");
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

    public int getChampionCount() {
        return waitForAll(CHAMPION_LINKS).size();
    }

    public boolean isSearchInputVisible() {
        return isVisible(SEARCH_INPUT);
    }

    public void clickChampion(String championSlug) {
        log.step("Clicking champion: " + championSlug);
        click(By.xpath(String.format(CHAMPION_LINK_XPATH, championSlug)));
    }

    public boolean isChampionPresent(String championSlug) {
        return isPresent(By.xpath(String.format(CHAMPION_LINK_XPATH, championSlug)));
    }
}
