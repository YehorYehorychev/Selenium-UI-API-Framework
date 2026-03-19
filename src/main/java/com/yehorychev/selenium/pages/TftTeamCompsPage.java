package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TftTeamCompsPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    // Comp guide entry links — each featured comp points to /tft/comps-guide/{slug}
    private static final By COMP_CARDS = By.cssSelector("a[href*='/tft/comps-guide/']");
    private static final By TIER_LIST_LINK = By.cssSelector("a[href*='/tft/tier-list']");
    private static final By UNIT_LINKS = By.cssSelector(
            "a[href*='/tft/champions/'], a[href*='/tft/units/']"
    );

    public TftTeamCompsPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening TFT Team Comps page");
        open(TestConfig.BASE_URL + "/tft/team-comps");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public int getCompCardCount() {
        return waitForAll(COMP_CARDS).size();
    }

    public boolean hasCompCards() {
        return isPresent(COMP_CARDS);
    }

    public boolean hasTierListLink() {
        return isPresent(TIER_LIST_LINK);
    }

    public boolean hasUnitLinks() {
        return isPresent(UNIT_LINKS);
    }
}
