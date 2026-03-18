package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeadlockPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By HEROES_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'hero')]"
    );
    private static final By BUILDS_SECTION = By.xpath(
            "//h2[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'build')]"
    );
    private static final By CONTENT_LINKS = By.cssSelector(
            "a[href*='/deadlock/build'], a[href*='/deadlock/hero'], a[href*='/deadlock/guide']"
    );
    // XPath text match — Sign In button class is hashed
    private static final By SIGN_IN_BUTTON = By.xpath(
            "//button[.//span[translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='sign in']]"
    );

    public DeadlockPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Deadlock page");
        open(TestConfig.BASE_URL + "/deadlock");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public boolean isHeroesSectionPresent() {
        return isPresent(HEROES_SECTION);
    }

    public boolean isBuildsSectionPresent() {
        return isPresent(BUILDS_SECTION);
    }

    public int getContentLinkCount() {
        return driver.findElements(CONTENT_LINKS).size();
    }

    public boolean isSignInButtonVisible() {
        return isVisible(SIGN_IN_BUTTON);
    }
}
