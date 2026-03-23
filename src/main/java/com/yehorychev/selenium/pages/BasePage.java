package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.errors.ElementNotFoundException;
import com.yehorychev.selenium.errors.NavigationException;
import com.yehorychev.selenium.errors.PageLoadException;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.utils.WaitFactory;
import com.yehorychev.selenium.utils.WaitUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;
    protected final Actions actions;
    protected final Logger log;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = WaitFactory.defaultWait(driver);
        this.shortWait = WaitFactory.shortWait(driver);
        this.actions = new Actions(driver);
        this.log = new Logger(this.getClass());
    }

    @Step("Navigate to {url}")
    public void open(String url) {
        log.step("Navigating to: " + url);
        try {
            driver.get(url);
        } catch (TimeoutException e) {
            throw new PageLoadException(url, TestConfig.NAVIGATION_TIMEOUT_MS, e);
        }
    }

    @Step("Navigate to base URL")
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

    @Step("Click {locator}")
    public void click(By locator) {
        log.step("Clicking element: " + locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    public void click(WebElement element) {
        log.step("Clicking element: " + element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    @Step("Type '{text}' into {locator}")
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

    @Step("Append '{text}' into {locator}")
    public void typeAppend(By locator, String text) {
        waitForVisible(locator).sendKeys(text);
    }

    public String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    public String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    @Step("Press ENTER on {locator}")
    public void pressEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    @Step("Hover over {locator}")
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

    @Step("Scroll {locator} into view")
    public void scrollIntoView(By locator) {
        WebElement el = waitForPresent(locator);
        executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    public void waitForPageReady() {
        try {
            WaitUtils.waitForPageLoad(driver);
        } catch (TimeoutException e) {
            throw new PageLoadException(driver.getCurrentUrl(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public WebElement waitForClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public List<WebElement> waitForVisibleElements(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public List<String> getTexts(By locator) {
        return waitForVisibleElements(locator).stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    public List<WebElement> waitForNumberOfElements(By locator, int expectedCount) {
        try {
            return wait.until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public void waitForAttributeEquals(By locator, String attribute, String value) {
        try {
            wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public void waitForAttributeContains(By locator, String attribute, String value) {
        try {
            wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public void waitForText(By locator, String text) {
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public void waitForStaleness(WebElement element) {
        try {
            wait.until(ExpectedConditions.stalenessOf(element));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(element.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    @Step("Switch to frame {locator}")
    public void switchToFrame(By locator) {
        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    @Step("Switch to default content")
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    @Step("JS click {locator}")
    public void jsClick(By locator) {
        WebElement element = waitForVisible(locator);
        executeScript("arguments[0].click();", element);
    }

    @Step("Click {locator} (with retry)")
    public void clickWithRetry(By locator) {
        clickWithRetry(locator, 3);
    }

    public void clickWithRetry(By locator, int attempts) {
        WaitUtils.retry(attempts, () -> {
            scrollIntoView(locator);
            waitForClickable(locator).click();
        });
    }

    public void waitForUrlToBe(String expectedUrl) {
        try {
            wait.until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (TimeoutException e) {
            throw new NavigationException(driver.getCurrentUrl(), expectedUrl, e);
        }
    }

    public void waitForUrlMatches(Pattern pattern) {
        try {
            wait.until(ExpectedConditions.urlMatches(pattern.pattern()));
        } catch (TimeoutException e) {
            throw new NavigationException(driver.getCurrentUrl(), pattern.pattern(), e);
        }
    }

    @Step("Scroll to top")
    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
    }

    @Step("Scroll to bottom")
    public void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @Step("Scroll by ({x}, {y})")
    public void scrollBy(int x, int y) {
        executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    @Step("Drag {sourceLocator} to {targetLocator}")
    public void dragAndDrop(By sourceLocator, By targetLocator) {
        WebElement source = waitForVisible(sourceLocator);
        WebElement target = waitForVisible(targetLocator);
        actions.dragAndDrop(source, target).perform();
    }

    public void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(source, target).perform();
    }

    @Step("Drag {sourceLocator} by offset ({xOffset}, {yOffset})")
    public void dragAndDropBy(By sourceLocator, int xOffset, int yOffset) {
        WebElement source = waitForVisible(sourceLocator);
        actions.dragAndDropBy(source, xOffset, yOffset).perform();
    }

    @Step("HTML5 drag {sourceLocator} to {targetLocator}")
    public void dragAndDropHtml5(By sourceLocator, By targetLocator) {
        WebElement source = waitForVisible(sourceLocator);
        WebElement target = waitForVisible(targetLocator);
        String script = "const src=arguments[0],tgt=arguments[1];" +
                "const dt=new DataTransfer();" +
                "src.dispatchEvent(new DragEvent('dragstart',{dataTransfer:dt}));" +
                "tgt.dispatchEvent(new DragEvent('drop',{dataTransfer:dt}));" +
                "src.dispatchEvent(new DragEvent('dragend',{dataTransfer:dt}));";
        executeScript(script, source, target);
    }

    @Step("Upload file '{file}' via {locator}")
    public void uploadFile(By locator, Path file) {
        waitForVisible(locator).sendKeys(file.toString());
    }

    public Alert waitForAlert() {
        try {
            return wait.until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException e) {
            throw new ElementNotFoundException("alert", TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public String getAlertText() {
        return waitForAlert().getText();
    }

    @Step("Accept browser alert")
    public void acceptAlert() {
        waitForAlert().accept();
    }

    @Step("Dismiss browser alert")
    public void dismissAlert() {
        waitForAlert().dismiss();
    }

    public void waitForEnabled(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public void waitForDisabled(By locator) {
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(locator)));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    public void waitForNotPresent(By locator) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    @Step("Open new browser tab")
    public void openNewTab() {
        executeScript("window.open('about:blank','_blank');");
        switchToLastTab();
    }

    @Step("Switch to last tab")
    public void switchToLastTab() {
        ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(handles.get(handles.size() - 1));
    }

    @Step("Switch to tab {index}")
    public void switchToTab(int index) {
        ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (index < 0 || index >= handles.size()) {
            throw new NavigationException(driver.getCurrentUrl(), "tab index " + index, null);
        }
        driver.switchTo().window(handles.get(index));
    }

    @Step("Switch to window with title containing '{titleFragment}'")
    public boolean switchToWindowWithTitle(String titleFragment) {
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(titleFragment)) {
                return true;
            }
        }
        return false;
    }

    @Step("Close current tab and return to first")
    public void closeCurrentTabAndSwitchToFirst() {
        String current = driver.getWindowHandle();
        driver.close();
        ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (handles.isEmpty()) {
            throw new NavigationException(current, "no remaining tabs", null);
        }
        driver.switchTo().window(handles.get(0));
    }
}