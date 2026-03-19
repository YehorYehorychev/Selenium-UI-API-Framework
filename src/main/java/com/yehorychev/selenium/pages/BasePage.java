package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.errors.ElementNotFoundException;
import com.yehorychev.selenium.errors.PageLoadException;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;
    protected final Actions actions;
    protected final Logger log;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        this.actions = new Actions(driver);
        this.log = new Logger(this.getClass());
    }

    public void open(String url) {
        log.step("Navigating to: " + url);
        try {
            driver.get(url);
        } catch (TimeoutException e) {
            throw new PageLoadException(url, TestConfig.NAVIGATION_TIMEOUT_MS, e);
        }
    }

    public void openBaseUrl() {
        open(TestConfig.BASE_URL);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void waitForUrl(String urlFragment) {
        WaitUtils.waitForUrl(driver, urlFragment);
    }

    public void waitForTitle(String titleFragment) {
        WaitUtils.waitForTitle(driver, titleFragment);
    }

    public void click(By locator) {
        log.step("Clicking element: " + locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    public void click(WebElement element) {
        log.step("Clicking element: " + element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    public void type(By locator, String text) {
        log.step("Typing \"" + text + "\" into: " + locator);
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    public void type(WebElement element, String text) {
        log.step("Typing \"" + text + "\" into element");
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    public void typeAppend(By locator, String text) {
        waitForVisible(locator).sendKeys(text);
    }

    public String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    public String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    public void pressEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    public void hover(By locator) {
        log.step("Hovering over: " + locator);
        actions.moveToElement(waitForVisible(locator)).perform();
    }

    public WebElement waitForVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public WebElement waitForPresent(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public List<WebElement> waitForAll(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public boolean isVisible(By locator) {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)) != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Element not visible: " + locator + " - " + e.getMessage());
            return false;
        }
    }

    public boolean isPresent(By locator) {
        try {
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Element not present: " + locator + " - " + e.getMessage());
            return false;
        }
    }

    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    public void scrollIntoView(By locator) {
        WebElement el = waitForPresent(locator);
        executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }
}