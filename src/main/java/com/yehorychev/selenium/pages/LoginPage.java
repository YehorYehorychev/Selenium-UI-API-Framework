package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Mobalytics login / sign-in page.
 *
 * mobalytics.gg/login redirects to account.mobalytics.gg/sign-in — both URLs supported.
 *
 * Selectors confirmed via live DOM inspection on account.mobalytics.gg/sign-in:
 *   Email    : input#email  (type=text, placeholder="Email")
 *   Password : input#password  (type=password, placeholder="Password")
 *   Submit   : button[type='submit'] with text "Sign In"
 *   Error    : div containing "Please, enter" or "Invalid" text — uses XPath
 */
public class LoginPage extends BasePage {

    private static final By EMAIL_INPUT    = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    /** XPath-based — the Sign In button has a hashed class, text is stable */
    private static final By SIGN_IN_BUTTON = By.xpath(
            "//button[@type='submit' and normalize-space(.)='Sign In']"
    );
    /**
     * Error messages — two cases confirmed via live DOM inspection:
     *   Empty form    : div containing "Please, enter email" / "Please, enter password"
     *   Invalid creds : p containing "Our guards didn't recognize you..."
     */
    private static final By ERROR_MESSAGE  = By.xpath(
            "//*[self::div or self::p][" +
            "contains(text(),'Please') or " +
            "contains(text(),\"didn't recognize\") or " +
            "contains(text(),'guards') or " +
            "contains(text(),'Invalid') or " +
            "contains(text(),'incorrect')]"
    );
    private static final By PAGE_HEADING   = By.cssSelector("h1, h2");
    /** Authenticated indicator — present after successful login on mobalytics.gg */
    private static final By LOGGED_IN_INDICATOR = By.cssSelector(
            "[data-testid='user-menu'], .user-avatar, .profile-avatar, [data-testid='profile']"
    );

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.step("Opening login page");
        open(TestConfig.BASE_URL + TestData.UrlPatterns.LOGIN);
        waitForVisible(EMAIL_INPUT);
    }

    public boolean isLoaded() {
        return isVisible(EMAIL_INPUT);
    }

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public void enterEmail(String email) {
        log.step("Entering email");
        type(EMAIL_INPUT, email);
    }

    public void enterPassword(String password) {
        log.step("Entering password");
        type(PASSWORD_INPUT, password);
    }

    public void clickSignIn() {
        log.step("Clicking Sign In button");
        click(SIGN_IN_BUTTON);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    /** Returns the first visible error message text. */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean hasErrorMessage() {
        return isPresent(ERROR_MESSAGE);
    }

    public boolean isLoggedIn() {
        return isVisible(LOGGED_IN_INDICATOR);
    }
}

