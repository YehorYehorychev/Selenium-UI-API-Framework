package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private static final By LOGO = By.cssSelector("a.base-logo");
    private static final By HEADER = By.cssSelector("header.site-header");
    private static final By HERO_HEADING = By.cssSelector("h1");
    private static final By HERO_SECTION = By.cssSelector(".hl-hero");
    private static final By DOWNLOAD_CTA = By.cssSelector("a.download-btn");
    private static final By SOCIAL_LINKS = By.cssSelector("header .soc-link");
    private static final String NAV_LINK_XPATH =
            "//nav[contains(@class,'site-navigation')]//li[contains(@class,'menu-item')]/a[normalize-space()='%s']";

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening Mobalytics home page: " + TestConfig.BASE_URL);
        openBaseUrl();
        waitForVisible(HERO_SECTION);
    }

    public void clickLogo() {
        log.step("Clicking site logo");
        click(LOGO);
        waitForUrl("mobalytics.gg");
    }

    public void clickDownloadCta() {
        log.step("Clicking download CTA button");
        click(DOWNLOAD_CTA);
    }

    public void clickNavGame(String gameName) {
        log.step("Clicking nav game link: " + gameName);
        click(getNavGameLocator(gameName));
    }

    public boolean isLoaded() {
        return isVisible(HERO_SECTION);
    }

    public boolean isHeaderVisible() {
        return isVisible(HEADER);
    }

    public String getHeroHeadingText() {
        return getText(HERO_HEADING);
    }

    public String getDownloadCtaHref() {
        return getAttribute(DOWNLOAD_CTA, "href");
    }

    public boolean isNavGamePresent(String gameName) {
        return isPresent(getNavGameLocator(gameName));
    }

    public int getSocialLinkCount() {
        return driver.findElements(SOCIAL_LINKS).size();
    }

    private By getNavGameLocator(String gameName) {
        return By.xpath(String.format(NAV_LINK_XPATH, gameName));
    }
}
