package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LolChampionBuildPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By BUILDS_SECTION = LocatorUtils.h2ContainsText("build");
    private static final By RUNES_SECTION = LocatorUtils.h2ContainsText("rune");
    private static final By MATCHUPS_SECTION = LocatorUtils.h2ContainsText("matchup");
    private static final By COUNTER_LINKS = By.cssSelector("a[href*='counter']");
    private static final By ROLE_BUILD_LINKS = By.cssSelector("a[href*='/lol/champions/'][href*='build']");

    public LolChampionBuildPage(WebDriver driver) {
        super(driver);
    }

    public void open(String championSlug) {
        String url = TestConfig.BASE_URL + "/lol/champions/" + championSlug + "/build";
        log.step("Opening champion build page: " + url);
        super.open(url);
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

    public boolean isRunesSectionPresent() {
        return isPresent(RUNES_SECTION);
    }

    public boolean isMatchupsSectionPresent() {
        return isPresent(MATCHUPS_SECTION);
    }

    public int getCounterLinkCount() {
        return waitForAll(COUNTER_LINKS).size();
    }

    public int getRoleBuildLinkCount() {
        return waitForAll(ROLE_BUILD_LINKS).size();
    }
}
