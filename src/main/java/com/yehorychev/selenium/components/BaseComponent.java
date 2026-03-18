package com.yehorychev.selenium.components;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BaseComponent {

    protected final WebDriver driver;
    protected final By rootLocator;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;
    protected final Logger log;

    protected BaseComponent(WebDriver driver, By rootLocator) {
        this.driver = driver;
        this.rootLocator = rootLocator;
        this.wait = new WebDriverWait(driver, Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        this.log = new Logger(this.getClass());
    }

    protected WebElement getRoot() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(rootLocator));
    }

    public boolean isVisible() {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(rootLocator)) != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Component not visible: " + rootLocator + " - " + e.getMessage());
            return false;
        }
    }

    public boolean isPresent() {
        try {
            return getRoot() != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Component not present: " + rootLocator + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds an element scoped to this component's root.
     */
    protected WebElement findElement(By locator) {
        return getRoot().findElement(locator);
    }

    /**
     * Finds all elements scoped to this component's root.
     */
    protected List<WebElement> findElements(By locator) {
        return getRoot().findElements(locator);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOf(findElement(locator)));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(findElement(locator)));
    }

    protected void click(By locator) {
        log.step("Clicking element within component: " + locator);
        waitForClickable(locator).click();
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected String getAttribute(By locator, String attribute) {
        return findElement(locator).getAttribute(attribute);
    }

    /**
     * Finds an element at document level — not scoped to this component's root.
     */
    protected WebElement findElementGlobal(By locator) {
        return driver.findElement(locator);
    }

    /**
     * Waits for a document-level element to be clickable (outside component root).
     */
    protected WebElement waitForClickableGlobal(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}
