package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.components.NavigationComponent;
import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeadlockPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By HEROES_SECTION = LocatorUtils.h2ContainsText("hero");
    private static final By BUILDS_SECTION = LocatorUtils.h2ContainsText("build");
    private static final By CONTENT_LINKS = By.cssSelector(
            "a[href*='/deadlock/build'], a[href*='/deadlock/hero'], a[href*='/deadlock/guide']"
    );

    public DeadlockPage(WebDriver driver) {
        super(driver);
        this.navigation = new NavigationComponent(driver);
    }

    private final NavigationComponent navigation;

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
        return waitForAll(CONTENT_LINKS).size();
    }

    public boolean isSignInButtonVisible() {
        return navigation.isLoginButtonVisible();
    }
}
