package com.yehorychev.selenium.components;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Abstract base class for all page components (sections, widgets, modals).
 *
 * Components represent reusable sections of a page (e.g. header, footer, modal).
 * Each component is scoped to a root element — all lookups happen within that root,
 * providing better encapsulation and test stability.
 *
 * Usage:
 *   public class NavigationComponent extends BaseComponent {
 *       private static final By NAV_LINKS = By.cssSelector("nav a");
 *
 *       public NavigationComponent(WebDriver driver) {
 *           super(driver, By.cssSelector("header.site-header"));
 *       }
 *
 *       public void clickLink(String linkText) {
 *           findElements(NAV_LINKS).stream()
 *               .filter(el -> el.getText().equals(linkText))
 *               .findFirst()
 *               .ifPresent(WebElement::click);
 *       }
 *   }
 */
public abstract class BaseComponent {

    protected final WebDriver driver;
    protected final By rootLocator;
    protected final WebDriverWait wait;
    protected final Logger log;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Initializes the component with its root element locator.
     *
     * @param driver      active WebDriver instance
     * @param rootLocator By locator for the component's root element
     */
    protected BaseComponent(WebDriver driver, By rootLocator) {
        this.driver = driver;
        this.rootLocator = rootLocator;
        this.wait = new WebDriverWait(driver, Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
        this.log = new Logger(this.getClass());
    }

    // ── Root element access ──────────────────────────────────────────────────

    /**
     * Returns the component's root {@link WebElement}, waiting for it to be present in the DOM.
     *
     * @return root element
     * @throws TimeoutException if the root is not found within the timeout
     */
    protected WebElement getRoot() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(rootLocator));
    }

    /**
     * Returns {@code true} if the component's root element is currently visible.
     *
     * @return visibility status
     */
    public boolean isVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(rootLocator)) != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Component not visible: " + rootLocator + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns {@code true} if the component's root element is present in the DOM
     * (not necessarily visible).
     *
     * @return presence status
     */
    public boolean isPresent() {
        try {
            return getRoot() != null;
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("Component not present: " + rootLocator + " - " + e.getMessage());
            return false;
        }
    }

    // ── Element lookups (scoped to component root) ───────────────────────────

    /**
     * Finds a single element within this component using the given locator.
     * The search is scoped to the component's root element.
     *
     * @param locator relative {@link By} locator
     * @return matching {@link WebElement}
     * @throws NoSuchElementException if the element is not found
     */
    protected WebElement findElement(By locator) {
        return getRoot().findElement(locator);
    }

    /**
     * Finds all elements within this component using the given locator.
     * The search is scoped to the component's root element.
     *
     * @param locator relative {@link By} locator
     * @return list of matching {@link WebElement}s (may be empty)
     */
    protected List<WebElement> findElements(By locator) {
        return getRoot().findElements(locator);
    }

    /**
     * Waits until an element within this component is visible.
     *
     * @param locator relative {@link By} locator
     * @return visible {@link WebElement}
     */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOf(findElement(locator)));
    }

    /**
     * Waits until an element within this component is clickable.
     *
     * @param locator relative {@link By} locator
     * @return clickable {@link WebElement}
     */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(findElement(locator)));
    }

    // ── Interaction helpers ──────────────────────────────────────────────────

    /**
     * Clicks an element within this component, waiting for it to be clickable.
     *
     * @param locator relative {@link By} locator
     */
    protected void click(By locator) {
        log.step("Clicking element within component: " + locator);
        waitForClickable(locator).click();
    }

    /**
     * Retrieves the visible text of an element within this component.
     *
     * @param locator relative {@link By} locator
     * @return trimmed text content
     */
    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /**
     * Retrieves an attribute value from an element within this component.
     *
     * @param locator   relative {@link By} locator
     * @param attribute attribute name (e.g. {@code "href"}, {@code "value"})
     * @return attribute value string
     */
    protected String getAttribute(By locator, String attribute) {
        return findElement(locator).getAttribute(attribute);
    }
}
