package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TftTierListPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By UNIT_LINKS = By.cssSelector(
            "a[href*='/tft/champions/'], a[href*='/tft/units/']"
    );
    // /tft/tier-list redirects to /tft/tier-list/team-comps; sub-nav links confirm the redirect landed
    private static final By TEAM_COMP_LINKS = By.cssSelector("a[href*='/tft/tier-list/']");

    public TftTierListPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening TFT Tier List page");
        open(TestConfig.BASE_URL + "/tft/tier-list");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public int getUnitLinkCount() {
        return waitForAll(UNIT_LINKS).size();
    }

    public boolean hasUnitLinks() {
        return isPresent(UNIT_LINKS);
    }

    public boolean hasTeamCompLinks() {
        return isPresent(TEAM_COMP_LINKS);
    }
}
