package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Diablo4Page extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By SEARCH_INPUT = By.cssSelector("input[type='search'], input[placeholder*='Search']");
    // Text-based locator — game sub-pages use hashed CSS classes
    private static final By BUILDS_SECTION = LocatorUtils.h2ContainsText("build");
    private static final By GUIDES_SECTION = LocatorUtils.h2ContainsText("guide");
    private static final By BUILD_CARDS = By.cssSelector("[class*='build-card'], [data-testid='build-card'], article");

    public Diablo4Page(WebDriver driver) {
        super(driver);
    }

    // NOTE: canonical URL is /diablo-4 (not /d4)
    public void open() {
        log.step("Opening Diablo 4 page");
        open(TestConfig.BASE_URL + "/diablo-4");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public boolean isBuildsSectionVisible() {
        return isPresent(BUILDS_SECTION);
    }

    public boolean isGuidesSectionVisible() {
        return isPresent(GUIDES_SECTION);
    }

    public void searchBuilds(String keyword) {
        log.step("Searching for Diablo 4 builds: " + keyword);
        type(SEARCH_INPUT, keyword);
    }

    public int getBuildCardCount() {
        return waitForAll(BUILD_CARDS).size();
    }
}
