package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NightReignPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");

    private static final By NIGHTFARERS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'nightfarer')]"
    );

    private static final By NIGHTLORDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'nightlord')]"
    );

    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );

    private static final By GUIDES_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'guide')]"
    );

    private static final By CONTENT_LINKS = By.cssSelector("a[href*='/elden-ring-nightreign/']");

    public NightReignPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Elden Ring Nightreign page");
        open(TestConfig.BASE_URL + "/elden-ring-nightreign");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public boolean isNightfarersSectionPresent() {
        return isPresent(NIGHTFARERS_SECTION);
    }

    public boolean isNightlordsSectionPresent() {
        return isPresent(NIGHTLORDS_SECTION);
    }

    public boolean isBuildsSectionPresent() {
        return isPresent(BUILDS_SECTION);
    }

    public boolean isGuidesSectionPresent() {
        return isPresent(GUIDES_SECTION);
    }

    public int getContentLinkCount() {
        return waitForAll(CONTENT_LINKS).size();
    }
}
