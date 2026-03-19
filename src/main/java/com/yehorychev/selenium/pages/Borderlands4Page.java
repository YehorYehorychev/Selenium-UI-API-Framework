package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Borderlands4Page extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");

    private static final By BUILDS_SECTION = LocatorUtils.h2ContainsText("build");

    private static final By CONTENT_LINKS = By.cssSelector("a[href*='/borderlands-4/']");

    public Borderlands4Page(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Borderlands 4 page");
        open(TestConfig.BASE_URL + "/borderlands-4");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public boolean isBuildsSectionPresent() {
        return isPresent(BUILDS_SECTION);
    }

    public int getContentLinkCount() {
        return waitForAll(CONTENT_LINKS).size();
    }
}
