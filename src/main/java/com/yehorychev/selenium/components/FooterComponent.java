package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class FooterComponent extends BaseComponent {

    private static final By FOOTER_LINKS = By.cssSelector("a");
    private static final By SOCIAL_ICONS = By.cssSelector(
            "a[href*='twitter'], a[href*='x.com'], a[href*='discord'], " +
                    "a[href*='facebook'], a[href*='youtube']"
    );
    private static final By COPYRIGHT = By.cssSelector(".footer-copyright");
    private static final By LEGAL_LINKS = By.cssSelector(
            "a[href*='privacy'], a[href*='terms'], a[href*='legal']"
    );
    private static final String LINK_BY_TEXT_XPATH = ".//a[contains(text(),'%s')]";

    public FooterComponent(WebDriver driver) {
        super(driver, By.cssSelector("div.footer-outer"));
    }

    public void clickLink(String linkText) {
        log.step("Clicking footer link: " + linkText);
        click(By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText)));
    }

    public List<String> getAllLinkTexts() {
        return findElements(FOOTER_LINKS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .toList();
    }

    public boolean hasLink(String linkText) {
        return getAllLinkTexts().stream().anyMatch(text -> text.equalsIgnoreCase(linkText));
    }

    public int getSocialIconCount() {
        return findElements(SOCIAL_ICONS).size();
    }

    public boolean areSocialIconsVisible() {
        return !findElements(SOCIAL_ICONS).isEmpty();
    }

    public void clickSocialIcon(String platform) {
        log.step("Clicking social icon: " + platform);
        click(By.cssSelector("a[href*='" + platform.toLowerCase() + "']"));
    }

    public String getCopyrightText() {
        return getText(COPYRIGHT);
    }

    public boolean copyrightContainsCurrentYear() {
        return getCopyrightText().contains(String.valueOf(java.time.Year.now().getValue()));
    }

    public List<String> getLegalLinkTexts() {
        return findElements(LEGAL_LINKS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .toList();
    }

    public boolean hasLegalLink(String linkText) {
        return getLegalLinkTexts().stream().anyMatch(text -> text.equalsIgnoreCase(linkText));
    }

    public void clickLegalLink(String linkText) {
        log.step("Clicking legal link: " + linkText);
        click(By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText)));
    }
}
