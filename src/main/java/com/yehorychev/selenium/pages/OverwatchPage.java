package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.components.NavigationComponent;
import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OverwatchPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");

    private static final By STADIUM_BUILDS_SECTION = LocatorUtils.h2ContainsAny("stadium", "build");

    private static final By HERO_LINKS = By.cssSelector("a[href*='/overwatch/']");

    public OverwatchPage(WebDriver driver) {
        super(driver);
        this.navigation = new NavigationComponent(driver);
    }

    private final NavigationComponent navigation;

    public void open() {
        log.step("Opening Overwatch page");
        open(TestConfig.BASE_URL + "/overwatch");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public boolean isStadiumBuildsSectionPresent() {
        return isPresent(STADIUM_BUILDS_SECTION);
    }

    public int getHeroLinkCount() {
        return waitForAll(HERO_LINKS).size();
    }

    public boolean isSignInButtonVisible() {
        return navigation.isLoginButtonVisible();
    }
}
