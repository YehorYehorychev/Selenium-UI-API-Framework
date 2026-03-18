package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LolTierListPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By CHAMPION_LINKS = By.cssSelector("a[href*='/lol/champions/']");
    // Text-based fallback — Filters button class is hashed
    private static final By FILTERS_BUTTON = By.xpath(
            "//button[normalize-space(text())='Filters' or normalize-space(.)='Filters']"
    );
    private static final By METHODOLOGY_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'methodology')]"
    );
    private static final By PATCH_INFO = By.xpath(
            "//*[contains(text(),'Patch') or contains(text(),'patch')]"
    );

    public LolTierListPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening LoL Tier List page");
        open(TestConfig.BASE_URL + "/lol/tier-list");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public int getChampionLinkCount() {
        return waitForAll(CHAMPION_LINKS).size();
    }

    public boolean isMethodologySectionPresent() {
        return isPresent(METHODOLOGY_SECTION);
    }

    public boolean isFilterButtonPresent() {
        return isPresent(FILTERS_BUTTON);
    }

    public void clickFilters() {
        log.step("Clicking Filters button on LoL Tier List");
        click(FILTERS_BUTTON);
    }

    public boolean isPatchInfoPresent() {
        return isPresent(PATCH_INFO);
    }
}
