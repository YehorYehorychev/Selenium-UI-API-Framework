package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Footer component — represents the site footer with links, social icons, and legal info.
 *
 * <p>This component encapsulates the footer section including:
 * <ul>
 *   <li>Footer navigation links (About, Careers, Support, etc.)</li>
 *   <li>Social media icons</li>
 *   <li>Copyright notice</li>
 *   <li>Legal links (Privacy Policy, Terms of Service)</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 *   FooterComponent footer = new FooterComponent(driver);
 *   footer.clickLink("About Us");
 *   String copyright = footer.getCopyrightText();
 *   assertTrue(footer.hasLegalLink("Privacy Policy"));
 * }</pre>
 */
public class FooterComponent extends BaseComponent {

    // ── Selectors (relative to root) ─────────────────────────────────────────

    private static final By FOOTER_LINKS = By.cssSelector("a");
    private static final By SOCIAL_ICONS = By.cssSelector("a[href*='twitter'], a[href*='discord'], a[href*='facebook'], a[href*='youtube']");
    private static final By COPYRIGHT = By.cssSelector("p[class*='copyright'], small");
    private static final By LEGAL_LINKS = By.cssSelector("a[href*='privacy'], a[href*='terms'], a[href*='legal']");

    // XPath template for finding links by text
    private static final String LINK_BY_TEXT_XPATH = ".//a[contains(text(),'%s')]";

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a {@code FooterComponent} bound to the site footer.
     *
     * @param driver active {@link WebDriver} instance
     */
    public FooterComponent(WebDriver driver) {
        super(driver, By.cssSelector("footer, [role='contentinfo']"));
    }

    // ── Link interactions ────────────────────────────────────────────────────

    /**
     * Clicks a footer link by its visible text.
     *
     * @param linkText visible text of the link (e.g. {@code "About Us"}, {@code "Careers"})
     */
    public void clickLink(String linkText) {
        log.step("Clicking footer link: " + linkText);
        By locator = By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText));
        click(locator);
    }

    /**
     * Returns a list of all footer link texts.
     *
     * @return list of link texts
     */
    public List<String> getAllLinkTexts() {
        return findElements(FOOTER_LINKS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Returns {@code true} if a link with the given text is present in the footer.
     *
     * @param linkText link text to check
     * @return presence status
     */
    public boolean hasLink(String linkText) {
        return getAllLinkTexts().stream()
                .anyMatch(text -> text.equalsIgnoreCase(linkText));
    }

    // ── Social icons ─────────────────────────────────────────────────────────

    /**
     * Returns the count of social media icons in the footer.
     *
     * @return number of social icons
     */
    public int getSocialIconCount() {
        return findElements(SOCIAL_ICONS).size();
    }

    /**
     * Returns {@code true} if social media icons are visible in the footer.
     *
     * @return social icons visibility status
     */
    public boolean areSocialIconsVisible() {
        return !findElements(SOCIAL_ICONS).isEmpty();
    }

    /**
     * Clicks a social media icon by platform name (twitter, discord, facebook, youtube).
     *
     * @param platform social platform name (case-insensitive)
     */
    public void clickSocialIcon(String platform) {
        log.step("Clicking social icon: " + platform);
        By locator = By.cssSelector("a[href*='" + platform.toLowerCase() + "']");
        click(locator);
    }

    // ── Copyright & legal ────────────────────────────────────────────────────

    /**
     * Returns the copyright text from the footer.
     *
     * @return copyright text, trimmed
     */
    public String getCopyrightText() {
        return getText(COPYRIGHT);
    }

    /**
     * Returns {@code true} if the copyright text contains the current year.
     *
     * @return whether copyright contains current year
     */
    public boolean copyrightContainsCurrentYear() {
        int currentYear = java.time.Year.now().getValue();
        return getCopyrightText().contains(String.valueOf(currentYear));
    }

    /**
     * Returns a list of legal link texts (Privacy Policy, Terms of Service, etc.).
     *
     * @return list of legal link texts
     */
    public List<String> getLegalLinkTexts() {
        return findElements(LEGAL_LINKS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Returns {@code true} if a legal link with the given text is present.
     *
     * @param linkText legal link text to check (e.g. {@code "Privacy Policy"})
     * @return presence status
     */
    public boolean hasLegalLink(String linkText) {
        return getLegalLinkTexts().stream()
                .anyMatch(text -> text.equalsIgnoreCase(linkText));
    }

    /**
     * Clicks a legal link by its text.
     *
     * @param linkText visible text of the legal link
     */
    public void clickLegalLink(String linkText) {
        log.step("Clicking legal link: " + linkText);
        By locator = By.xpath(String.format(LINK_BY_TEXT_XPATH, linkText));
        click(locator);
    }
}

