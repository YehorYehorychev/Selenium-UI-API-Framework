package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class FeaturesComponent extends BaseComponent {

    private static final By SECTION_HEADING      = By.cssSelector("h2, .heading");
    private static final By FEATURE_CARDS        = By.cssSelector(".hl-win-more-item");
    private static final By FEATURE_TITLES       = By.cssSelector(".hl-win-more-item h3, .hl-win-more-item .title");
    private static final By FEATURE_DESCRIPTIONS = By.cssSelector(".hl-win-more-item p, .hl-win-more-item .description");

    public FeaturesComponent(WebDriver driver) {
        super(driver, By.cssSelector("section.hl-win-more"));
    }

    public String getSectionHeading() {
        return getText(SECTION_HEADING);
    }

    public int getFeatureCount() {
        return findElements(FEATURE_CARDS).size();
    }

    public List<String> getFeatureTitles() {
        return findElements(FEATURE_TITLES).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    public List<String> getFeatureDescriptions() {
        return findElements(FEATURE_DESCRIPTIONS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    public boolean hasFeature(String featureTitle) {
        return getFeatureTitles().stream().anyMatch(title -> title.equalsIgnoreCase(featureTitle));
    }
}
