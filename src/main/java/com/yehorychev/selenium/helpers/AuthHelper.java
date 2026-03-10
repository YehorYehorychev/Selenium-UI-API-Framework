package com.yehorychev.selenium.helpers;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.GraphqlQueries;
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
 * Authenticates via the GraphQL signIn mutation, which sets an HTTP-only
 * session cookie on the account.mobalytics.gg domain.
 * The session cookie can then be injected into a WebDriver.
 *
 * Usage:
 *   Map<String, String> auth = AuthHelper.loginViaApi(email, password);
 *   AuthHelper.injectAuthIntoDriver(driver, auth);
 *
 * Or in one call:
 *   AuthHelper.loginAndInject(driver);  // uses TestData.Credentials
 */
public final class AuthHelper {

    private static final Logger log = new Logger(AuthHelper.class);
    // Key used in the returned authData map to indicate sign-in succeeded
    public static final String KEY_SIGNED_IN = "signedIn";

    private AuthHelper() {
    }

    // ── GraphQL login ─────────────────────────────────────────────────────────

    /**
     * Authenticates via the GraphQL signIn mutation and returns auth data.
     *
     * The mobalytics account service uses cookie-based sessions; no token is
     * returned in the response body. Session cookies set by the server are
     * captured in the RestAssured response and stored in the returned map.
     *
     * @param email    user email
     * @param password user password
     * @return Map with: "signedIn"="true", plus any response cookie name/value pairs
     * @throws AuthenticationException if the mutation returns false or HTTP error
     */
    public static Map<String, String> loginViaApi(String email, String password) {
        log.step("Authenticating via GraphQL signIn: " + email);

        Map<String, Object> variables = Map.of("email", email, "password", password, "continueFrom", "");
        Map<String, Object> body = Map.of(
                "query", GraphqlQueries.SIGN_IN,
                "variables", variables
        );

        Response response = RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .config(io.restassured.RestAssured.config()
                        .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", (int) TestConfig.API_TIMEOUT_MS)
                                .setParam("http.socket.timeout", (int) TestConfig.API_TIMEOUT_MS)))
                .body(body)
                .post("/api/graphql/v1/query");

        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            throw new AuthenticationException(
                    "signIn mutation returned HTTP " + status + ": " + response.getBody().asString()
            );
        }

        // signIn returns { data: { signIn: true/false } }
        Boolean signedIn = response.jsonPath().getBoolean("data.signIn");
        if (!Boolean.TRUE.equals(signedIn)) {
            throw new AuthenticationException(
                    "signIn returned false — invalid credentials for: " + email
            );
        }

        Map<String, String> authData = new HashMap<>();
        authData.put(KEY_SIGNED_IN, "true");
        authData.put("email", email);

        // Capture any session cookies set by the server
        authData.putAll(response.getCookies());

        log.info("GraphQL signIn successful for: " + email);
        return authData;
    }

    /**
     * Authenticates using credentials from TestData.Credentials.
     *
     * @return auth data map
     * @throws AuthenticationException if login fails or credentials are not configured
     */
    public static Map<String, String> loginViaApi() {
        if (!TestData.Credentials.areConfigured()) {
            throw new AuthenticationException(
                    "Test credentials are not configured. " +
                            "Set TEST_USER_LOGIN and TEST_USER_PASSWORD in .env or environment variables."
            );
        }
        return loginViaApi(TestData.Credentials.LOGIN, TestData.Credentials.PASSWORD);
    }

    // ── WebDriver injection ───────────────────────────────────────────────────

    /**
     * Injects session cookies from loginViaApi() into a WebDriver.
     * The driver must already be on a page from the account domain.
     *
     * @param driver   active WebDriver
     * @param authData map returned by loginViaApi()
     */
    public static void injectAuthIntoDriver(WebDriver driver, Map<String, String> authData) {
        log.step("Injecting authentication cookies into WebDriver");

        // Navigate to the account domain so cookies can be set
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl == null || (!currentUrl.startsWith(TestConfig.BASE_URL)
                && !currentUrl.startsWith(TestConfig.API_BASE_URL))) {
            driver.get(TestConfig.BASE_URL);
        }

        // Inject all cookies except the metadata keys
        for (Map.Entry<String, String> entry : authData.entrySet()) {
            String key = entry.getKey();
            if (KEY_SIGNED_IN.equals(key) || "email".equals(key)) continue;
            Cookie cookie = new Cookie(key, entry.getValue());
            driver.manage().addCookie(cookie);
            log.debug("Injected cookie: " + key);
        }

        log.info("Authentication injection complete");
    }

    /**
     * Logs in via API and injects auth into the driver in one step.
     */
    public static void loginAndInject(WebDriver driver, String email, String password) {
        injectAuthIntoDriver(driver, loginViaApi(email, password));
    }

    /**
     * Logs in via API using TestData.Credentials and injects auth into the driver.
     */
    public static void loginAndInject(WebDriver driver) {
        loginAndInject(driver, TestData.Credentials.LOGIN, TestData.Credentials.PASSWORD);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    /**
     * Logs out via GraphQL signOut mutation.
     * Session is cookie-based — no token needed.
     */
    public static void logoutViaApi() {
        log.step("Logging out via GraphQL signOut");

        Map<String, Object> body = Map.of("query", GraphqlQueries.SIGN_OUT);

        RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .post("/api/graphql/v1/query");

        log.info("GraphQL signOut complete");
    }
}
