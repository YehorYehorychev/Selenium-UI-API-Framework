package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ValorantPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By SEARCH_INPUT = By.cssSelector(
            "input[type='search'], input[type='text'], input[placeholder*='name' i], input[placeholder*='search' i]"
    );
    private static final By AGENT_CARDS = By.cssSelector(
            "[class*='agent-card'], [class*='AgentCard'], [data-testid='agent-card']"
    );
    private static final By TIER_LIST = By.cssSelector("[class*='tier-list'], [data-testid='tier-list']");
    private static final By WEAPON_SECTION = By.cssSelector(".weapon-section, [data-testid='weapons']");

    public ValorantPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Valorant page");
        open(TestConfig.BASE_URL + "/valorant");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public void search(String keyword) {
        log.step("Searching in Valorant page: " + keyword);
        type(SEARCH_INPUT, keyword);
    }

    public int getAgentCardCount() {
        return waitForAll(AGENT_CARDS).size();
    }

    public boolean isTierListVisible() {
        return isVisible(TIER_LIST);
    }

    public boolean isWeaponSectionVisible() {
        return isVisible(WEAPON_SECTION);
    }
}
