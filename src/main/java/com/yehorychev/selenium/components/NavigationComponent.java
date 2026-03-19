package com.yehorychev.selenium.components;

import com.yehorychev.selenium.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class NavigationComponent extends BaseComponent {

    private static final By LOGO = By.cssSelector("a.base-logo");
    // Excludes logos and social links — only WordPress menu game items
    private static final By GAME_LINKS = By.cssSelector("nav li.menu-item-type-custom > a");
    private static final By SOCIAL_LINKS = By.cssSelector("a[href*='twitter'], a[href*='discord'], a[href*='youtube']");
    // Sign In lives in the React app header on sub-pages; class names are hashed per deploy
    private static final By SIGN_IN_BUTTON_XPATH = LocatorUtils.buttonSpanEquals("sign in");
    private static final String LINK_BY_TEXT_XPATH = ".//nav//a[contains(text(),'%s')]";

    public NavigationComponent(WebDriver driver) {
        super(driver, By.cssSelector("header.site-header, header[role='banner']"));
    }

    public void clickLogo() {
        log.step("Clicking site logo");
        click(LOGO);
    }

    public boolean isLogoVisible() {
        try {
            return findElement(LOGO).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickGameLink(String gameName) {
        log.step("Clicking game link: " + gameName);
        click(By.xpath(String.format(LINK_BY_TEXT_XPATH, gameName)));
    }

    public List<String> getAvailableGames() {
        return findElements(GAME_LINKS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    public boolean hasGameLink(String gameName) {
        return getAvailableGames().stream().anyMatch(name -> name.equalsIgnoreCase(gameName));
    }

    public void clickLink(String linkText) {
        log.step("Clicking navigation link: " + linkText);
        click(By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText)));
    }

    public String getLinkHref(String linkText) {
        return getAttribute(By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText)), "href");
    }

    public int getSocialLinksCount() {
        return findElements(SOCIAL_LINKS).size();
    }

    public boolean areSocialLinksVisible() {
        return !findElements(SOCIAL_LINKS).isEmpty();
    }

    public void clickLogin() {
        log.step("Clicking Sign In button");
        waitForClickableGlobal(SIGN_IN_BUTTON_XPATH).click();
    }

    public boolean isLoginButtonVisible() {
        try {
            return wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                    .visibilityOfElementLocated(SIGN_IN_BUTTON_XPATH)
            ) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
