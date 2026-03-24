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

/**
 * Base class for all Page Objects in the framework.
 *
 * <p>Provides two wait tiers — {@link #wait} (15 s, driven by {@code TestConfig.DEFAULT_TIMEOUT_MS})
 * for interactions, and {@link #shortWait} (3 s, driven by {@code TestConfig.SHORT_TIMEOUT_MS})
 * for lightweight visibility/presence checks — matching the same dual-wait contract used by
 * {@link com.yehorychev.selenium.components.BaseComponent}.
 *
 * <h3>Choosing between wait helpers</h3>
 * <ul>
 *   <li>{@link #waitForVisible(By)} — waits up to 15 s for an element to be rendered and
 *       non-zero-size; use before interacting.</li>
 *   <li>{@link #waitForPresent(By)} — waits up to 15 s for an element to exist in the DOM
 *       (may still be hidden); use for elements that are off-screen.</li>
 *   <li>{@link #isVisible(By)} — 3 s probe; returns {@code false} instead of throwing; use in
 *       assertion helpers or conditional branches.</li>
 *   <li>{@link #isPresent(By)} — same as above but checks DOM presence rather than visibility.</li>
 * </ul>
 *
 * <h3>Typed exceptions</h3>
 * Every wait failure is rethrown as a {@link com.yehorychev.selenium.errors.FrameworkException}
 * subclass so Allure {@code categories.json} can classify it correctly. Catch
 * {@link com.yehorychev.selenium.errors.FrameworkException} to handle all framework failures
 * in one place.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * public class MyPage extends BasePage {
 *     private static final By TITLE = By.cssSelector("h1.page-title");
 *
 *     public MyPage(WebDriver driver) { super(driver); }
 *
 *     public String getTitle() { return getText(TITLE); }
 * }
 * }</pre>
 */
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

    /**
     * Navigates the browser to {@code url}.
     * Throws {@link com.yehorychev.selenium.errors.PageLoadException} if the navigation times out.
     */
    @Step("Navigate to {url}")
    public void open(String url) {
        log.step("Navigating to: " + url);
        try {
            driver.get(url);
        } catch (TimeoutException e) {
            throw new PageLoadException(url, TestConfig.NAVIGATION_TIMEOUT_MS, e);
        }
    }

    /** Navigates to {@link com.yehorychev.selenium.config.TestConfig#BASE_URL}. */
    @Step("Navigate to base URL")
    public void openBaseUrl() {
        open(TestConfig.BASE_URL);
    }

    /** Returns the browser tab title. */
    public String getTitle() {
        return driver.getTitle();
    }

    /** Returns the full URL of the current page including query parameters. */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Waits until the current URL contains {@code urlFragment}.
     * Delegates to {@link com.yehorychev.selenium.utils.WaitUtils#waitForUrl}.
     */
    public void waitForUrl(String urlFragment) {
        WaitUtils.waitForUrl(driver, urlFragment);
    }

    /**
     * Waits until the page title contains {@code titleFragment}.
     * Delegates to {@link com.yehorychev.selenium.utils.WaitUtils#waitForTitle}.
     */
    public void waitForTitle(String titleFragment) {
        WaitUtils.waitForTitle(driver, titleFragment);
    }

    /**
     * Waits for {@code locator} to be clickable then clicks it.
     * Throws {@link com.yehorychev.selenium.errors.ElementNotFoundException} on timeout.
     */
    @Step("Click {locator}")
    public void click(By locator) {
        log.step("Clicking element: " + locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Waits for {@code element} to be clickable then clicks it.
     * Prefer {@link #click(By)} — use this overload only when you already hold a
     * {@link WebElement} reference.
     */
    public void click(WebElement element) {
        log.step("Clicking element: " + element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /**
     * Clears {@code locator}, then types {@code text} into it.
     * Waits for visibility before interacting.
     */
    @Step("Type '{text}' into {locator}")
    public void type(By locator, String text) {
        log.step("Typing \"" + text + "\" into: " + locator);
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Clears {@code element}, then types {@code text} into it.
     * Prefer {@link #type(By, String)} — use this overload only when you already hold a reference.
     */
    public void type(WebElement element, String text) {
        log.step("Typing \"" + text + "\" into element");
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Appends {@code text} to {@code locator} without clearing the field first.
     * Useful for tag / chip inputs that process each keystroke.
     */
    @Step("Append '{text}' into {locator}")
    public void typeAppend(By locator, String text) {
        waitForVisible(locator).sendKeys(text);
    }

    /**
     * Returns the trimmed visible text of {@code locator}.
     * Waits for visibility before reading.
     */
    public String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /**
     * Returns the value of {@code attribute} on the element found by {@code locator}.
     * Waits for visibility before reading.
     */
    public String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    /** Sends {@link Keys#ENTER} to {@code locator} after waiting for visibility. */
    @Step("Press ENTER on {locator}")
    public void pressEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    /** Moves the mouse over {@code locator} using {@link org.openqa.selenium.interactions.Actions}. */
    @Step("Hover over {locator}")
    public void hover(By locator) {
        log.step("Hovering over: " + locator);
        actions.moveToElement(waitForVisible(locator)).perform();
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for {@code locator} to be <em>visible</em>
     * (rendered and non-zero-size).
     *
     * <p>Use this before interacting with an element. For a softer DOM-presence check
     * use {@link #waitForPresent(By)}.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException if the element does not
     *         become visible within the timeout
     */
    public WebElement waitForVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for {@code locator} to be present in the DOM
     * (the element may still be hidden from the user).
     *
     * <p>Use for off-screen or zero-opacity elements that cannot pass a visibility check.
     * For interactive elements prefer {@link #waitForVisible(By)}.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public WebElement waitForPresent(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for {@code locator} to disappear from the viewport.
     * Use after triggering a dismiss/close action.
     */
    public void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for at least one element matching {@code locator}
     * to be present in the DOM, then returns the full list.
     *
     * <p>The returned list is backed by a single WebDriver call and is safe to iterate
     * once returned — it is not live. Use {@link #waitForVisibleElements(By)} when you also
     * need visibility guarantees.
     */
    public List<WebElement> waitForAll(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Performs a fast 3-second visibility probe on {@code locator}.
     * Returns {@code false} — rather than throwing — if the element is absent or hidden.
     *
     * <p>Use in assertion helpers, conditional page-state checks, and component-state queries.
     * Do <em>not</em> use as a guard before interactions — prefer {@link #waitForVisible(By)}
     * for that to benefit from the full timeout.
     */
    public boolean isVisible(By locator) {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)) != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Element not visible: " + locator + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Performs a fast 3-second DOM-presence probe on {@code locator}.
     * Returns {@code false} — rather than throwing — if the element is absent.
     *
     * <p>Use when the element can be hidden (e.g., a skeleton loader, a tooltip, an off-screen
     * section) and you only need to know whether the DOM node exists.
     */
    public boolean isPresent(By locator) {
        try {
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Element not present: " + locator + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Executes {@code script} in the current browser context via {@link JavascriptExecutor}.
     * Pass WebElement references as {@code args[0]}, {@code args[1]}, etc.
     */
    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    /**
     * Scrolls {@code locator} into the vertical centre of the viewport using
     * {@code scrollIntoView({block:'center'})}.
     */
    @Step("Scroll {locator} into view")
    public void scrollIntoView(By locator) {
        WebElement el = waitForPresent(locator);
        executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    /**
     * Waits for {@code document.readyState === 'complete'} via
     * {@link com.yehorychev.selenium.utils.WaitUtils#waitForPageLoad(WebDriver)}.
     * Throws {@link com.yehorychev.selenium.errors.PageLoadException} on timeout.
     */
    public void waitForPageReady() {
        try {
            WaitUtils.waitForPageLoad(driver);
        } catch (TimeoutException e) {
            throw new PageLoadException(driver.getCurrentUrl(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for {@code locator} to be clickable
     * (visible + enabled). Returns the element for chaining.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public WebElement waitForClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for all elements matching {@code locator}
     * to be visible, then returns them.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public List<WebElement> waitForVisibleElements(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Returns the trimmed visible text of every element matching {@code locator}.
     * Waits for all elements to be visible before reading.
     */
    public List<String> getTexts(By locator) {
        return waitForVisibleElements(locator).stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} until exactly {@code expectedCount} elements
     * match {@code locator}. Returns the matched list.
     */
    public List<WebElement> waitForNumberOfElements(By locator, int expectedCount) {
        try {
            return wait.until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits until {@code locator}'s {@code attribute} equals {@code value} (case-sensitive).
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public void waitForAttributeEquals(By locator, String attribute, String value) {
        try {
            wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits until {@code locator}'s {@code attribute} contains {@code value}.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public void waitForAttributeContains(By locator, String attribute, String value) {
        try {
            wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits until the visible text of {@code locator} contains {@code text}.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public void waitForText(By locator, String text) {
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits until {@code element} becomes stale (detached from the DOM).
     * Useful after a full-page navigation or a React re-render that replaces a known element.
     */
    public void waitForStaleness(WebElement element) {
        try {
            wait.until(ExpectedConditions.stalenessOf(element));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(element.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits for the iframe matched by {@code locator} to be available and switches focus into it.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    @Step("Switch to frame {locator}")
    public void switchToFrame(By locator) {
        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /** Switches WebDriver focus back to the top-level browsing context. */
    @Step("Switch to default content")
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    /**
     * Clicks {@code locator} via JavaScript {@code arguments[0].click()}.
     * Use when native click is blocked by an overlapping element or an animation.
     */
    @Step("JS click {locator}")
    public void jsClick(By locator) {
        WebElement element = waitForVisible(locator);
        executeScript("arguments[0].click();", element);
    }

    /**
     * Scrolls {@code locator} into view and clicks it; retries up to 3 times on stale/obscured
     * element failures using {@link com.yehorychev.selenium.utils.WaitUtils#retry}.
     */
    @Step("Click {locator} (with retry)")
    public void clickWithRetry(By locator) {
        clickWithRetry(locator, 3);
    }

    /**
     * Same as {@link #clickWithRetry(By)} with a configurable number of {@code attempts}.
     */
    public void clickWithRetry(By locator, int attempts) {
        WaitUtils.retry(attempts, () -> {
            scrollIntoView(locator);
            waitForClickable(locator).click();
        });
    }

    /**
     * Waits until the browser URL equals {@code expectedUrl} exactly.
     *
     * @throws com.yehorychev.selenium.errors.NavigationException if the URL does not match
     *         within {@code DEFAULT_TIMEOUT_MS}
     */
    public void waitForUrlToBe(String expectedUrl) {
        try {
            wait.until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (TimeoutException e) {
            throw new NavigationException(driver.getCurrentUrl(), expectedUrl, e);
        }
    }

    /**
     * Waits until the browser URL matches {@code pattern}.
     *
     * @throws com.yehorychev.selenium.errors.NavigationException on timeout
     */
    public void waitForUrlMatches(Pattern pattern) {
        try {
            wait.until(ExpectedConditions.urlMatches(pattern.pattern()));
        } catch (TimeoutException e) {
            throw new NavigationException(driver.getCurrentUrl(), pattern.pattern(), e);
        }
    }

    /** Scrolls the viewport to the top-left corner of the page. */
    @Step("Scroll to top")
    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
    }

    /** Scrolls the viewport to the bottom of the page. */
    @Step("Scroll to bottom")
    public void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scrolls the viewport by {@code x} pixels horizontally and {@code y} pixels vertically
     * relative to the current scroll position.
     */
    @Step("Scroll by ({x}, {y})")
    public void scrollBy(int x, int y) {
        executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    /**
     * Performs a native HTML drag-and-drop from the element matched by {@code sourceLocator}
     * to the element matched by {@code targetLocator} using the Actions API.
     *
     * <p>For apps that intercept drag events via JavaScript (e.g. React DnD, SortableJS),
     * prefer {@link #dragAndDropHtml5(By, By)}.
     */
    @Step("Drag {sourceLocator} to {targetLocator}")
    public void dragAndDrop(By sourceLocator, By targetLocator) {
        WebElement source = waitForVisible(sourceLocator);
        WebElement target = waitForVisible(targetLocator);
        actions.dragAndDrop(source, target).perform();
    }

    /**
     * Performs a native drag-and-drop between two already-resolved {@link WebElement} instances.
     * Prefer the {@link #dragAndDrop(By, By)} overload unless you already hold references.
     */
    public void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(source, target).perform();
    }

    /**
     * Drags {@code sourceLocator} by {@code xOffset} and {@code yOffset} pixels using
     * the Actions API.
     */
    @Step("Drag {sourceLocator} by offset ({xOffset}, {yOffset})")
    public void dragAndDropBy(By sourceLocator, int xOffset, int yOffset) {
        WebElement source = waitForVisible(sourceLocator);
        actions.dragAndDropBy(source, xOffset, yOffset).perform();
    }

    /**
     * Performs an HTML5 drag-and-drop by dispatching {@code dragstart}, {@code drop}, and
     * {@code dragend} DOM events via JavaScript.
     *
     * <p>Use this for React / Angular components that rely on the {@code DataTransfer} API
     * rather than native mouse events (where the Actions-based drag-and-drop silently fails).
     */
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

    /**
     * Sends the absolute path of {@code file} to the {@code <input type="file">} element
     * matched by {@code locator}. Waits for the input to be visible before sending keys.
     *
     * @param locator the {@code <input type="file">} locator
     * @param file    the absolute path to the file to upload
     */
    @Step("Upload file '{file}' via {locator}")
    public void uploadFile(By locator, Path file) {
        waitForVisible(locator).sendKeys(file.toString());
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for a browser alert/confirm/prompt to appear
     * and returns the {@link Alert} handle.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException if no alert appears
     */
    public Alert waitForAlert() {
        try {
            return wait.until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException e) {
            throw new ElementNotFoundException("alert", TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /** Returns the text message of the currently open browser alert. */
    public String getAlertText() {
        return waitForAlert().getText();
    }

    /** Waits for a browser alert and accepts it (clicks OK). */
    @Step("Accept browser alert")
    public void acceptAlert() {
        waitForAlert().accept();
    }

    /** Waits for a browser alert and dismisses it (clicks Cancel). */
    @Step("Dismiss browser alert")
    public void dismissAlert() {
        waitForAlert().dismiss();
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for {@code locator} to become enabled and
     * clickable. Use after a form validation step that re-enables a submit button.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public void waitForEnabled(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for {@code locator} to become disabled
     * (i.e. no longer clickable). Use to verify that a button is locked during loading.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public void waitForDisabled(By locator) {
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(locator)));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for {@code locator} to be absent from or
     * invisible in the DOM.
     *
     * <p>Symmetric counterpart to {@link #waitForPresent(By)} — use after a close/dismiss
     * action to confirm the element is gone before continuing.
     *
     * @throws com.yehorychev.selenium.errors.ElementNotFoundException on timeout
     */
    public void waitForNotPresent(By locator) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator.toString(), TestConfig.DEFAULT_TIMEOUT_MS, e);
        }
    }

    /**
     * Opens a new blank browser tab via JavaScript and switches focus to it.
     * Use in multi-tab flows (e.g., OAuth redirect, download confirmation).
     */
    @Step("Open new browser tab")
    public void openNewTab() {
        executeScript("window.open('about:blank','_blank');");
        switchToLastTab();
    }

    /**
     * Switches WebDriver focus to the last open tab/window handle.
     * Typically called after {@link #openNewTab()} or after a link opens a new window.
     */
    @Step("Switch to last tab")
    public void switchToLastTab() {
        ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(handles.get(handles.size() - 1));
    }

    /**
     * Switches WebDriver focus to the tab/window at zero-based {@code index}.
     * Throws {@link com.yehorychev.selenium.errors.NavigationException} if {@code index}
     * is out of range.
     */
    @Step("Switch to tab {index}")
    public void switchToTab(int index) {
        ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
        if (index < 0 || index >= handles.size()) {
            throw new NavigationException(driver.getCurrentUrl(), "tab index " + index, null);
        }
        driver.switchTo().window(handles.get(index));
    }

    /**
     * Iterates through all open windows/tabs and switches focus to the one whose title
     * contains {@code titleFragment}.
     *
     * @return {@code true} if a matching window was found and switched to;
     *         {@code false} if no window title contains {@code titleFragment}
     */
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

    /**
     * Closes the currently active tab and switches WebDriver focus back to the first tab.
     * Throws {@link com.yehorychev.selenium.errors.NavigationException} if no tabs remain.
     */
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