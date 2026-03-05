package com.yehorychev.selenium.helpers;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.TestData;
import com.yehorychev.selenium.errors.AuthenticationException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * API-based authentication helper — bypasses the UI login form.
 *
 * Authenticates via POST /api/auth/login, extracts the session token/cookies,
 * and optionally injects them into a WebDriver so subsequent navigation is
 * already authenticated.
 *
 * Usage:
 *   Map<String, String> auth = AuthHelper.loginViaApi(email, password);
 *   AuthHelper.injectAuthIntoDriver(driver, auth);
 *   driver.get(TestConfig.BASE_URL + "/dashboard");
 *
 * Or in one call:
 *   AuthHelper.loginAndInject(driver);  // uses TestData.Credentials
 */
public final class AuthHelper {

    private static final Logger log = new Logger(AuthHelper.class);

    private AuthHelper() {
    }

    // ── API login ─────────────────────────────────────────────────────────────

    /**
     * Authenticates via the REST API and returns auth data (token + cookies).
     *
     * @param email    user email
     * @param password user password
     * @return Map containing: "token", "cookieName", "cookieValue" (where applicable)
     * @throws AuthenticationException if login returns non-2xx or no token in response
     */
    public static Map<String, String> loginViaApi(String email, String password) {
        log.step("Authenticating via API: " + email);

        Response response = RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", password
                ))
                .post(TestData.UrlPatterns.API_LOGIN);

        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            String body = response.getBody().asString();
            throw new AuthenticationException(
                    "loginViaApi returned HTTP " + status + ": " + body
            );
        }

        // Extract token from JSON response
        String token = response.jsonPath().getString("token");
        if (token == null || token.isBlank()) {
            throw new AuthenticationException(
                    "loginViaApi returned success=true but no token in response: "
                            + response.getBody().asString()
            );
        }

        Map<String, String> authData = new HashMap<>();
        authData.put("token", token);

        // Extract session cookie if present
        io.restassured.http.Cookie sessionCookie = response.getDetailedCookie("session");
        if (sessionCookie != null) {
            authData.put("cookieName", sessionCookie.getName());
            authData.put("cookieValue", sessionCookie.getValue());
        }

        log.info("API login successful for: " + email);
        return authData;
    }

    /**
     * Authenticates using credentials from TestData.Credentials.
     *
     * @return auth data map (token + cookies)
     * @throws AuthenticationException if login fails or credentials are not configured
     */
    public static Map<String, String> loginViaApi() {
        if (!TestData.Credentials.areConfigured()) {
            throw new AuthenticationException(
                    "Test credentials are not configured. " +
                            "Please set TEST_USER_LOGIN and TEST_USER_PASSWORD in .env file or environment variables."
            );
        }
        return loginViaApi(
                TestData.Credentials.LOGIN,
                TestData.Credentials.PASSWORD
        );
    }

    // ── WebDriver injection ───────────────────────────────────────────────────

    /**
     * Injects auth token and cookies into a WebDriver session.
     * The driver must already be on a page from the same domain before cookies are set.
     *
     * @param driver   active WebDriver
     * @param authData map returned by loginViaApi()
     */
    public static void injectAuthIntoDriver(WebDriver driver, Map<String, String> authData) {
        log.step("Injecting authentication into WebDriver");

        // Navigate to base URL to set cookies on the correct domain
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl == null || !currentUrl.startsWith(TestConfig.BASE_URL)) {
            log.debug("Navigating to base URL to enable cookie injection");
            driver.get(TestConfig.BASE_URL);
        }

        // Inject session cookie if present
        String cookieName = authData.get("cookieName");
        String cookieValue = authData.get("cookieValue");
        if (cookieName != null && cookieValue != null) {
            Cookie cookie = new Cookie(cookieName, cookieValue);
            driver.manage().addCookie(cookie);
            log.debug("Injected cookie: " + cookieName);
        }

        // Store token in localStorage (alternative auth pattern)
        String token = authData.get("token");
        if (token != null) {
            driver.navigate().refresh(); // ensure domain is set
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("window.localStorage.setItem('authToken', arguments[0]);", token);
            log.debug("Injected token into localStorage");
        }

        log.info("Authentication injection complete");
    }

    /**
     * Logs in via API and injects auth into the driver in one step.
     *
     * @param driver   active WebDriver
     * @param email    user email
     * @param password user password
     */
    public static void loginAndInject(WebDriver driver, String email, String password) {
        Map<String, String> authData = loginViaApi(email, password);
        injectAuthIntoDriver(driver, authData);
    }

    /**
     * Logs in via API using TestData.Credentials and injects auth into the driver.
     *
     * @param driver active WebDriver
     */
    public static void loginAndInject(WebDriver driver) {
        loginAndInject(driver, TestData.Credentials.LOGIN, TestData.Credentials.PASSWORD);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    /**
     * Logs out via API, invalidating the token server-side.
     *
     * @param token bearer token to invalidate
     */
    public static void logoutViaApi(String token) {
        log.step("Logging out via API");

        RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .header("Authorization", "Bearer " + token)
                .post(TestData.UrlPatterns.API_LOGOUT);

        log.info("API logout complete");
    }
}
