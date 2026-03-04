package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Abstract base class for all Page Objects.
 *
 * <p>Every concrete page class extends {@code BasePage} and gets:
 * <ul>
 *   <li>A pre-configured {@link WebDriverWait} with the default timeout</li>
 *   <li>Reusable wait/interact helpers: {@code click}, {@code type}, {@code getText}, etc.</li>
 *   <li>Visibility / presence checks: {@code isVisible}, {@code isPresent}</li>
 *   <li>Navigation utilities: {@code open}, {@code waitForUrl}, {@code getTitle}</li>
 *   <li>JavaScript executor shortcuts: {@code scrollIntoView}, {@code jsClick}</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 *   public class LoginPage extends BasePage {
 *       private final By emailInput = By.id("email");
 *
 *       public LoginPage(WebDriver driver) {
 *           super(driver);
 *       }
 *
 *       public void login(String email, String password) {
 *           type(emailInput, email);
 *           click(By.id("submit"));
 *       }
 *   }
 * }</pre>
 */
public abstract class BasePage {

    protected final WebDriver     driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;
    protected final Actions       actions;
    protected final Logger        log;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Initialises the page with the default timeout from {@link TestConfig}.
     *
     * @param driver active {@link WebDriver} instance (injected from {@code TestContext} via PicoContainer)
     */
    protected BasePage(WebDriver driver) {
        this.driver    = driver;
        this.wait      = new WebDriverWait(driver, Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        this.actions   = new Actions(driver);
        this.log       = new Logger(this.getClass());
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Opens the given URL in the browser.
     *
     * @param url full URL to navigate to
     */
    public void open(String url) {
        log.step("Navigating to: " + url);
        driver.get(url);
    }

    /**
     * Opens the base URL from {@link TestConfig}.
     */
    public void openBaseUrl() {
        open(TestConfig.BASE_URL);
    }

    /**
     * Returns the current browser page title.
     *
     * @return page title string
     */
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Returns the current browser URL.
     *
     * @return current URL string
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Waits until the current URL contains the given fragment.
     * Delegates to {@link WaitUtils#waitForUrl(WebDriver, String)}.
     *
     * @param urlFragment expected URL fragment
     */
    public void waitForUrl(String urlFragment) {
        WaitUtils.waitForUrl(driver, urlFragment);
    }

    /**
     * Waits until the page title contains the given fragment.
     * Delegates to {@link WaitUtils#waitForTitle(WebDriver, String)}.
     *
     * @param titleFragment expected title fragment
     */
    public void waitForTitle(String titleFragment) {
        WaitUtils.waitForTitle(driver, titleFragment);
    }

    // ── Element interaction ───────────────────────────────────────────────────

    /**
     * Waits for an element to be clickable and clicks it.
     *
     * @param locator element locator
     */
    public void click(By locator) {
        log.step("Clicking element: " + locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Waits for an element to be clickable and clicks it.
     *
     * @param element pre-located {@link WebElement}
     */
    public void click(WebElement element) {
        log.step("Clicking element: " + element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /**
     * Clears the field and types the given text.
     *
     * @param locator element locator
     * @param text    text to enter
     */
    public void type(By locator, String text) {
        log.step("Typing \"" + text + "\" into: " + locator);
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Clears the field and types the given text.
     *
     * @param element pre-located {@link WebElement}
     * @param text    text to enter
     */
    public void type(WebElement element, String text) {
        log.step("Typing \"" + text + "\" into element");
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Types the given text without clearing the field first (appends).
     *
     * @param locator element locator
     * @param text    text to append
     */
    public void typeAppend(By locator, String text) {
        waitForVisible(locator).sendKeys(text);
    }

    /**
     * Retrieves the visible text of an element.
     *
     * @param locator element locator
     * @return trimmed text content
     */
    public String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /**
     * Retrieves the value of an attribute on an element.
     *
     * @param locator   element locator
     * @param attribute attribute name (e.g. {@code "href"}, {@code "value"})
     * @return attribute value string
     */
    public String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    /**
     * Submits a form by pressing {@code ENTER} on the element.
     *
     * @param locator element locator
     */
    public void pressEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    /**
     * Hovers the mouse over an element.
     *
     * @param locator element locator
     */
    public void hover(By locator) {
        log.step("Hovering over: " + locator);
        actions.moveToElement(waitForVisible(locator)).perform();
    }

    // ── Waits ─────────────────────────────────────────────────────────────────

    /**
     * Waits until the element is visible in the DOM.
     *
     * @param locator element locator
     * @return visible {@link WebElement}
     */
    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the element is present in the DOM (not necessarily visible).
     *
     * @param locator element locator
     * @return present {@link WebElement}
     */
    public WebElement waitForPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until the element disappears from the DOM / becomes invisible.
     *
     * @param locator element locator
     */
    public void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits until at least one element matching the locator is present.
     *
     * @param locator element locator
     * @return list of matching {@link WebElement}s
     */
    public List<WebElement> waitForAll(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    // ── Visibility checks ─────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the element is currently visible (no explicit wait).
     *
     * @param locator element locator
     * @return visibility status
     */
    public boolean isVisible(By locator) {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)) != null;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the element is present in the DOM (no explicit wait).
     *
     * @param locator element locator
     * @return presence status
     */
    public boolean isPresent(By locator) {
        try {
            return shortWait.until(ExpectedConditions.presenceOfElementLocated(locator)) != null;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    // ── JavaScript helpers ────────────────────────────────────────────────────

    /**
     * Executes a JavaScript snippet in the context of the current page.
     *
     * @param script JavaScript code
     * @param args   optional arguments passed as {@code arguments[0]}, {@code arguments[1]}, …
     * @return script return value
     */
    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    /**
     * Scrolls the given element into the browser viewport.
     *
     * @param locator element locator
     */
    public void scrollIntoView(By locator) {
        WebElement el = waitForPresent(locator);
        executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    /**
     * Clicks an element via JavaScript — useful when the element is obscured by
     * an overlay or is not in the standard clickable state.
     *
     * @param locator element locator
     */
    public void jsClick(By locator) {
        log.step("JS click on: " + locator);
        WebElement el = waitForPresent(locator);
        executeScript("arguments[0].click();", el);
    }

    /**
     * Scrolls the page to the very top.
     */
    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scrolls the page to the very bottom.
     */
    public void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // ── Screenshot helpers ────────────────────────────────────────────────────

    /**
     * Takes a full-page screenshot and attaches it to the Allure report.
     *
     * @param name screenshot name (displayed in Allure report)
     */
    public void takeScreenshot(String name) {
        log.step("Taking screenshot: " + name);
        byte[] screenshot = com.yehorychev.selenium.utils.ScreenshotUtils.takeFullPageScreenshot(driver);
        io.qameta.allure.Allure.addAttachment(
                name,
                "image/png",
                new java.io.ByteArrayInputStream(screenshot),
                "png"
        );
    }

    // ── Assertion helpers ─────────────────────────────────────────────────────

    /**
     * Verifies that an element's text contains the expected substring.
     * Throws {@link AssertionError} if the text does not contain the expected value.
     *
     * @param locator  element locator
     * @param expected expected text fragment (case-insensitive)
     * @throws AssertionError if text does not contain expected value
     */
    public void verifyTextContains(By locator, String expected) {
        String actual = getText(locator);
        if (!actual.toLowerCase().contains(expected.toLowerCase())) {
            String message = String.format(
                    "Expected element [%s] to contain \"%s\" but was: \"%s\"",
                    locator, expected, actual
            );
            log.error(message);
            throw new AssertionError(message);
        }
        log.debug("Text verification passed: element contains \"" + expected + "\"");
    }

    /**
     * Asserts that the current URL matches the given pattern (substring or regex).
     * Throws {@link com.yehorychev.selenium.errors.NavigationException} if the URL doesn't match.
     *
     * @param urlPattern expected URL pattern (substring or regex)
     * @throws com.yehorychev.selenium.errors.NavigationException if URL doesn't match
     */
    public void assertNavigatesTo(String urlPattern) {
        String currentUrl = getCurrentUrl();
        if (!currentUrl.matches(".*" + urlPattern + ".*")) {
            throw new com.yehorychev.selenium.errors.NavigationException(currentUrl, urlPattern);
        }
        log.debug("Navigation assertion passed: URL matches \"" + urlPattern + "\"");
    }
}


