package com.yehorychev.selenium.components;

import com.yehorychev.selenium.helpers.Logger;
import com.yehorychev.selenium.utils.WaitFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Base class for all reusable page components.
 *
 * <p>Every component is <em>root-scoped</em>: {@link #findElement(By)} and
 * {@link #findElements(By)} search within the DOM subtree rooted at {@link #rootLocator},
 * preventing accidental matches outside the component boundary.
 *
 * <p>Provides the same dual-wait tiers as {@link com.yehorychev.selenium.pages.BasePage}:
 * {@link #wait} (15 s) for interactions and {@link #shortWait} (3 s) for fast
 * visibility/presence probes.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * public class HeaderComponent extends BaseComponent {
 *     private static final By LOGO = By.cssSelector("a.logo");
 *
 *     public HeaderComponent(WebDriver driver) {
 *         super(driver, By.cssSelector("header.site-header"));
 *     }
 *
 *     public void clickLogo() { click(LOGO); }
 * }
 * }</pre>
 *
 * <h3>Global lookups</h3>
 * Use {@link #findElementGlobal(By)} / {@link #waitForClickableGlobal(By)} when an element
 * lives outside the component root (e.g., a React portal rendered at document body level).
 */
public abstract class BaseComponent {

    protected final WebDriver driver;
    protected final By rootLocator;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;
    protected final Logger log;

    protected BaseComponent(WebDriver driver, By rootLocator) {
        this.driver = driver;
        this.rootLocator = rootLocator;
        this.wait = WaitFactory.defaultWait(driver);
        this.shortWait = WaitFactory.shortWait(driver);
        this.log = new Logger(this.getClass());
    }

    /**
     * Waits up to {@code DEFAULT_TIMEOUT_MS} for the component root element to be present
     * in the DOM.
     *
     * @return the root {@link WebElement} — never {@code null} if no exception is thrown
     */
    protected WebElement getRoot() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(rootLocator));
    }

    /**
     * Performs a 3-second visibility probe on the component root.
     * Returns {@code false} instead of throwing when the root is absent or hidden.
     * Use in conditional page-state checks, not as a guard before interactions.
     */
    public boolean isVisible() {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(rootLocator)) != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Component not visible: " + rootLocator + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Performs a 3-second DOM-presence probe on the component root.
     * Returns {@code false} instead of throwing when the root is absent from the DOM.
     */
    public boolean isPresent() {
        try {
            return getRoot() != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Component not present: " + rootLocator + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds an element scoped to this component's root (within the root's DOM subtree).
     * Throws {@link org.openqa.selenium.NoSuchElementException} if not found.
     */
    protected WebElement findElement(By locator) {
        return getRoot().findElement(locator);
    }

    /**
     * Finds all elements scoped to this component's root.
     * Returns an empty list if none match — never {@code null}.
     */
    protected List<WebElement> findElements(By locator) {
        return getRoot().findElements(locator);
    }

    /**
     * Waits for {@code locator} (within the component root) to be visible, then returns it.
     * Uses the full {@link #wait} timeout (15 s).
     */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOf(findElement(locator)));
    }

    /**
     * Waits for {@code locator} (within the component root) to be clickable, then returns it.
     * Uses the full {@link #wait} timeout (15 s).
     */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(findElement(locator)));
    }

    /**
     * Clicks the element matched by {@code locator} within the component root.
     * Waits for the element to be clickable before clicking.
     */
    protected void click(By locator) {
        log.step("Clicking element within component: " + locator);
        waitForClickable(locator).click();
    }

    /**
     * Returns the trimmed visible text of the element matched by {@code locator}
     * within the component root.
     */
    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /**
     * Returns the value of {@code attribute} on the element matched by {@code locator}
     * within the component root.
     */
    protected String getAttribute(By locator, String attribute) {
        return findElement(locator).getAttribute(attribute);
    }

    /**
     * Finds an element at <em>document level</em> — <strong>not</strong> scoped to this
     * component's root. Use for elements rendered outside the component boundary, such as
     * React portals or modal overlays appended to {@code <body>}.
     */
    protected WebElement findElementGlobal(By locator) {
        return driver.findElement(locator);
    }

    /**
     * Waits for a document-level element (outside the component root) to be clickable.
     * Use for global overlays, modals, or navigation elements that span the full page.
     */
    protected WebElement waitForClickableGlobal(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}
