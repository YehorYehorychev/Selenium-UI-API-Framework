package com.yehorychev.selenium.components.templates;

// ─────────────────────────────────────────────────────────────────────────────
// TEMPLATE — ExampleComponent.java
//
// PURPOSE  : Shows how to create a reusable page component in this framework.
// LOCATION : src/main/java/.../components/  (copy this file there and rename it)
// EXCLUDES : This file is in the `templates` sub-package and is never executed
//            as part of any test run. It is purely educational.
//
// WHEN TO WRITE A COMPONENT vs A PAGE
// ────────────────────────────────────
//  • Use a PAGE  (BasePage)      when the element is an entire, routable screen.
//  • Use a COMPONENT (BaseComponent) when the element is a self-contained UI
//    section that appears on MULTIPLE pages (e.g. nav bar, footer, filter panel,
//    modal dialog). Components are instantiated directly in step classes or
//    inside page objects — not inherited from.
//
// HOW TO USE THIS TEMPLATE
// ────────────────────────
//  1. Copy this file to  src/main/java/com/yehorychev/selenium/components/
//  2. Rename it — e.g. SearchFilterComponent.java
//  3. Update the package declaration (remove ".templates")
//  4. Change the ROOT_LOCATOR to the outermost wrapper element of your section
//  5. Replace the inner locators and methods with ones relevant to your section
//
// KEY RULE: All element lookups via findElement()/findElements() are
// automatically SCOPED to the root element, so selectors cannot accidentally
// match something outside the component boundary.
// ─────────────────────────────────────────────────────────────────────────────

import com.yehorychev.selenium.components.BaseComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Example Component that demonstrates every common pattern for reusable UI sections.
 *
 * <p>This models a hypothetical "filter panel" sidebar — a real-world UI section
 * that might appear on several different game pages.
 */
public class ExampleComponent extends BaseComponent {

    // ── Root locator ──────────────────────────────────────────────────────────
    // The ROOT_LOCATOR targets the outermost wrapper element of this component.
    // Every other locator inside this class is searched WITHIN that root element.
    // Think of it as the component's bounding box in the DOM.
    private static final By ROOT_LOCATOR = By.cssSelector("aside.filter-panel");

    // ── Inner locators ────────────────────────────────────────────────────────
    // These are relative to the root — do NOT include the root selector again.
    // They are passed to findElement() / click() / getText() which scope them
    // automatically.

    /** The "Apply filters" button inside this panel. */
    private static final By APPLY_BUTTON = By.cssSelector("button.apply-filters");

    /** The "Reset" link that clears all active filters. */
    private static final By RESET_LINK = By.cssSelector("a.reset-filters");

    /** All individual filter checkboxes rendered inside the panel. */
    private static final By FILTER_CHECKBOXES = By.cssSelector("input[type='checkbox']");

    /**
     * XPath template to locate a specific filter option by its visible label text.
     * Usage: {@code By.xpath(String.format(FILTER_BY_LABEL_XPATH, "Top Lane"))}
     *
     * Note: this XPath starts with {@code .//} — the leading dot scopes it to the
     * root element when used with {@link org.openqa.selenium.WebElement#findElement}.
     */
    private static final String FILTER_BY_LABEL_XPATH =
            ".//label[normalize-space(.)='%s']";

    /**
     * A counter badge showing how many results match the active filters.
     * This element may not always be present — guard with {@link #isVisible()}.
     */
    private static final By RESULT_COUNT_BADGE = By.cssSelector(".filter-result-count");

    // ── Constructor ───────────────────────────────────────────────────────────
    // Pass the WebDriver AND the root locator to BaseComponent.
    // The root locator tells BaseComponent where to scope all inner lookups.

    public ExampleComponent(WebDriver driver) {
        super(driver, ROOT_LOCATOR);
    }

    // ── State checks ──────────────────────────────────────────────────────────

    // isVisible() is already inherited from BaseComponent — no need to override it.
    // It performs a 3-second shortWait probe on ROOT_LOCATOR and returns false instead of
    // throwing, making it safe to use in conditional branches and assertions.
    //
    // Example usage in a step:
    //   if (filterPanel.isVisible()) { filterPanel.applyFilters(); }

    /**
     * Returns how many filter checkboxes are currently rendered in the panel.
     *
     * <p>{@link #findElements(By)} is scoped to the root — only checkboxes
     * INSIDE this component are counted.
     */
    public int getFilterCount() {
        return findElements(FILTER_CHECKBOXES).size();
    }

    /**
     * Returns the result-count badge text, e.g. {@code "42 results"}.
     *
     * <p>Guard with {@link #isVisible()} before calling if the badge is conditional.
     */
    public String getResultCountText() {
        return getText(RESULT_COUNT_BADGE);
    }

    // ── Interactions ──────────────────────────────────────────────────────────

    /**
     * Clicks the checkbox filter whose visible label matches {@code filterName}.
     *
     * <p>Uses a dynamic XPath built at runtime from the label text.
     * {@link #click(By)} waits for the element to be clickable before clicking.
     */
    public void selectFilter(String filterName) {
        log.step("Selecting filter: " + filterName);
        click(By.xpath(String.format(FILTER_BY_LABEL_XPATH, filterName)));
    }

    /**
     * Clicks the "Apply" button to submit the currently selected filters.
     */
    public void applyFilters() {
        log.step("Applying filters");
        click(APPLY_BUTTON);
    }

    /**
     * Clicks the "Reset" link to clear all active filters.
     */
    public void resetFilters() {
        log.step("Resetting all filters");
        click(RESET_LINK);
    }

    // ── Data extraction ───────────────────────────────────────────────────────

    /**
     * Returns the visible label text of every checkbox filter currently rendered
     * in the panel.
     *
     * <p>{@link #findElements(By)} returns an empty list — never {@code null} —
     * when no elements match, so streaming is always safe.
     */
    public List<String> getAvailableFilterNames() {
        return findElements(FILTER_CHECKBOXES).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .toList();
    }

    // ── When to use findElementGlobal ─────────────────────────────────────────
    // Sometimes a UI action inside a component opens an element OUTSIDE its root
    // (e.g. a React portal, a toast notification, a full-screen modal overlay).
    // In that case use findElementGlobal(By) or waitForClickableGlobal(By) —
    // these bypass the root scope and search the entire page.
    //
    // Example:
    //   protected WebElement getConfirmationModal() {
    //       return findElementGlobal(By.cssSelector(".modal-overlay"));
    //   }
}


