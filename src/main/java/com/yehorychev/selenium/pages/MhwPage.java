package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MhwPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");

    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    private static final By GUIDES_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'guide')]"
    );

    private static final By CONTENT_LINKS = By.cssSelector(
            "a[href*='/mhw/build'], a[href*='/mhw/guide'], a[href*='/mhw/weapon'], a[href*='/mhw/']"
    );

    public MhwPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Monster Hunter Wilds page");
        open(TestConfig.BASE_URL + "/mhw");
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

    public boolean isGuidesSectionPresent() {
        return isPresent(GUIDES_SECTION);
    }

    public int getContentLinkCount() {
        return driver.findElements(CONTENT_LINKS).size();
    }
}
