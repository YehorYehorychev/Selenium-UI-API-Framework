package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HeroComponent extends BaseComponent {

    private static final By HEADING         = By.cssSelector("h1");
    private static final By SUBHEADING      = By.cssSelector("h2, p.hero-description");
    private static final By DOWNLOAD_BUTTON = By.cssSelector("a.download-btn, a[href*='download.overwolf']");
    private static final By SIGNUP_BUTTON   = By.cssSelector("a[href*='signup'], button[data-testid='signup']");
    private static final By CTA_BUTTONS     = By.cssSelector("a[class*='btn']");

    public HeroComponent(WebDriver driver) {
        super(driver, By.cssSelector("section.hl-hero"));
    }

    public String getHeadingText() {
        return getText(HEADING);
    }

    public String getSubheadingText() {
        return getText(SUBHEADING);
    }

    public boolean headingContains(String expected) {
        return getHeadingText().toLowerCase().contains(expected.toLowerCase());
    }

    public void clickDownloadButton() {
        log.step("Clicking Download button in hero section");
        click(DOWNLOAD_BUTTON);
    }

    public void clickSignUpButton() {
        log.step("Clicking Sign Up button in hero section");
        click(SIGNUP_BUTTON);
    }

    public boolean isCtaVisible() {
        return !findElements(CTA_BUTTONS).isEmpty();
    }

    public int getCtaButtonCount() {
        return findElements(CTA_BUTTONS).size();
    }

    public String getDownloadLink() {
        return getAttribute(DOWNLOAD_BUTTON, "href");
    }
}
