package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Features component — represents the features/benefits section on the landing page.
 *
 * <p>This component encapsulates the features section including:
 * <ul>
 *   <li>Feature cards with icons and descriptions</li>
 *   <li>Section heading and subheading</li>
 *   <li>Feature highlights</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 *   FeaturesComponent features = new FeaturesComponent(driver);
 *   int count = features.getFeatureCount();
 *   List<String> titles = features.getFeatureTitles();
 *   assertTrue(features.hasFeature("Real-time Analytics"));
 * }</pre>
 */
public class FeaturesComponent extends BaseComponent {

    // ── Selectors (relative to root) ─────────────────────────────────────────

    private static final By SECTION_HEADING = By.cssSelector("h2");
    private static final By FEATURE_CARDS = By.cssSelector(".feature-card, [data-testid='feature-card']");
    private static final By FEATURE_TITLES = By.cssSelector(".feature-card__title, h3");
    private static final By FEATURE_DESCRIPTIONS = By.cssSelector(".feature-card__description, p");
    private static final By FEATURE_ICONS = By.cssSelector(".feature-card__icon, img, svg");

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a {@code FeaturesComponent} bound to the features section.
     *
     * @param driver active {@link WebDriver} instance
     */
    public FeaturesComponent(WebDriver driver) {
        super(driver, By.cssSelector(".features, [data-testid='features-section']"));
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
     * Returns {@code true} if a feature with the given title is present.
     *
     * @param featureTitle feature title to check
     * @return presence status
     */
    public boolean hasFeature(String featureTitle) {
        return getFeatureTitles().stream()
                .anyMatch(title -> title.equalsIgnoreCase(featureTitle));
    }

    /**
     * Returns {@code true} if feature icons are visible.
     *
     * @return icon visibility status
     */
    public boolean areIconsVisible() {
        return !findElements(FEATURE_ICONS).isEmpty();
    }
}

