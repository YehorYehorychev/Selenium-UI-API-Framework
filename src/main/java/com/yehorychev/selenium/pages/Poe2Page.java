package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Poe2Page extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By BUILD_CARDS = By.cssSelector("a[href*='/poe-2/builds/']");
    private static final By GUIDES_SECTION = By.cssSelector("a[href*='guide']");
    // "Latest Classes" H2 — proxy for character class section (no widget selector exists)
    private static final By CLASS_SELECTOR = LocatorUtils.h2ContainsText("class");
    private static final By SEARCH_INPUT = By.cssSelector(
            "input[placeholder*='builds' i], input[placeholder*='PoE' i], input[type='text']"
    );

    public Poe2Page(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Path of Exile 2 page");
        open(TestConfig.BASE_URL + "/poe-2");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public boolean isClassSelectorVisible() {
        return isPresent(CLASS_SELECTOR);
    }

    public void selectClass(String className) {
        log.step("Selecting class: " + className);
        click(By.xpath(String.format(".//a[contains(text(),'%s')]", className)));
    }

    public int getBuildCardCount() {
        return waitForAll(BUILD_CARDS).size();
    }

    public void searchBuilds(String keyword) {
        log.step("Searching for PoE2 builds: " + keyword);
        type(SEARCH_INPUT, keyword);
    }

    public boolean isGuidesSectionVisible() {
        return isPresent(GUIDES_SECTION);
    }
}
