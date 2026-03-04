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
 * API-based authentication helper for bypassing UI login flows.
 *
 * <p>Authenticates via REST {@code POST /api/auth/login}, extracts the session
 * token or cookies, and optionally injects them into a {@link WebDriver} instance
 * so that subsequent UI navigation is already authenticated.
 *
 * <p>Usage:
 * <pre>{@code
 *   // Authenticate and get token
 *   Map<String, String> auth = AuthHelper.loginViaApi(
 *       TestData.Credentials.LOGIN,
 *       TestData.Credentials.PASSWORD
 *   );
 *   String token = auth.get("token");
 *
 *   // Inject authentication into WebDriver
 *   AuthHelper.injectAuthIntoDriver(driver, auth);
 *
 *   // Now navigate to an authenticated page without manual login
 *   driver.get(TestConfig.BASE_URL + "/dashboard");
 * }</pre>
 */
public final class AuthHelper {

    private static final Logger log = new Logger(AuthHelper.class);

    private AuthHelper() {}

    // ── API login ─────────────────────────────────────────────────────────────

    /**
     * Authenticates a user via the REST API and returns the auth token + cookies.
     *
     * @param email    user email
     * @param password user password
     * @return a {@link Map} containing:
     *         <ul>
     *           <li>{@code "token"} — bearer token or session ID</li>
     *           <li>{@code "cookieName"} — cookie name (if applicable)</li>
     *           <li>{@code "cookieValue"} — cookie value (if applicable)</li>
     *         </ul>
     * @throws AuthenticationException if the login fails (non-2xx status or error payload)
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
     * Authenticates using credentials from {@link TestData.Credentials}.
     *
     * @return auth data map (token + cookies)
     * @throws AuthenticationException if login fails
     */
    public static Map<String, String> loginViaApi() {
        return loginViaApi(
                TestData.Credentials.LOGIN,
                TestData.Credentials.PASSWORD
        );
    }

    // ── WebDriver injection ───────────────────────────────────────────────────

    /**
     * Injects authentication data (token or cookies) into the given {@link WebDriver}.
     *
     * <p>The driver <strong>must</strong> first navigate to a page on the same domain
     * as the cookie (WebDriver restriction). This method handles that by navigating
     * to the base URL if no domain match is present.
     *
     * @param driver   active {@link WebDriver}
     * @param authData map returned by {@link #loginViaApi(String, String)}
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
     * @param driver   active {@link WebDriver}
     * @param email    user email
     * @param password user password
     */
    public static void loginAndInject(WebDriver driver, String email, String password) {
        Map<String, String> authData = loginViaApi(email, password);
        injectAuthIntoDriver(driver, authData);
    }

    /**
     * Logs in via API using default credentials and injects auth into the driver.
     *
     * @param driver active {@link WebDriver}
     */
    public static void loginAndInject(WebDriver driver) {
        loginAndInject(driver, TestData.Credentials.LOGIN, TestData.Credentials.PASSWORD);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    /**
     * Logs out via API (invalidates token server-side).
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

