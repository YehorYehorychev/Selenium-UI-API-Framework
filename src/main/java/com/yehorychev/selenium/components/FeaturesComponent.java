package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Features component — represents the "How Mobalytics helps you win more" section.
 *
 * Actual DOM (confirmed via mobalytics.gg live inspection):
 *   Root  : section.hl-win-more
 *   Items : div.hl-win-more-item  (3 items visible in current layout)
 *
 * Usage:
 *   FeaturesComponent features = new FeaturesComponent(driver);
 *   int count = features.getFeatureCount();
 *   assertTrue(features.isVisible());
 */
public class FeaturesComponent extends BaseComponent {

    // ── Selectors (relative to root) ─────────────────────────────────────────

    private static final By SECTION_HEADING      = By.cssSelector("h2, .heading");
    /** div.hl-win-more-item — confirmed 3 items via live DOM inspection. */
    private static final By FEATURE_CARDS        = By.cssSelector(".hl-win-more-item");
    private static final By FEATURE_TITLES       = By.cssSelector(".hl-win-more-item h3, .hl-win-more-item .title");
    private static final By FEATURE_DESCRIPTIONS = By.cssSelector(".hl-win-more-item p, .hl-win-more-item .description");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a FeaturesComponent bound to the "win more" section.
     * Root: section.hl-win-more — confirmed via live DOM inspection.
     *
     * @param driver active WebDriver instance
     */
    public FeaturesComponent(WebDriver driver) {
        super(driver, By.cssSelector("section.hl-win-more"));
    }

    // ── Content accessors ────────────────────────────────────────────────────

    /**
     * Returns the features section heading text.
     *
     * @return section heading text
     */
    public String getSectionHeading() {
        return getText(SECTION_HEADING);
    }

    /**
     * Returns the number of feature cards displayed.
     *
     * @return feature count
     */
    public int getFeatureCount() {
        return findElements(FEATURE_CARDS).size();
    }

    /**
     * Returns a list of all feature titles.
     *
     * @return list of feature titles
     */
    public List<String> getFeatureTitles() {
        return findElements(FEATURE_TITLES).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all feature descriptions.
     *
     * @return list of feature descriptions
     */
    public List<String> getFeatureDescriptions() {
        return findElements(FEATURE_DESCRIPTIONS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Returns true if a feature with the given title is present.
     *
     * @param featureTitle feature title to check
     * @return presence status
     */
    public boolean hasFeature(String featureTitle) {
        return getFeatureTitles().stream()
                .anyMatch(title -> title.equalsIgnoreCase(featureTitle));
    }
}
