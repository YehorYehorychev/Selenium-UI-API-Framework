package com.yehorychev.selenium.helpers;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.GraphqlQueries;
import com.yehorychev.selenium.data.TestData;
import com.yehorychev.selenium.errors.AuthenticationException;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * API-based authentication helper — bypasses the UI login form via GraphQL signIn.
 * Session is cookie-based; cookies can be injected into a WebDriver after sign-in.
 */
public final class AuthHelper {

    private static final Logger log = new Logger(AuthHelper.class);
    public static final String KEY_SIGNED_IN = "signedIn";

    private AuthHelper() {
    }

    private static RestAssuredConfig buildTimeoutConfig() {
        int timeoutMs = (int) TestConfig.API_TIMEOUT_MS;
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeoutMs)
                .setSocketTimeout(timeoutMs)
                .setConnectionRequestTimeout(timeoutMs)
                .build();
        return RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .httpClientFactory(() -> HttpClientBuilder.create()
                                .setDefaultRequestConfig(requestConfig)
                                .build()));
    }

    public static Map<String, String> loginViaApi(String email, String password) {
        log.step("Authenticating via GraphQL signIn: " + email);

        Map<String, Object> variables = Map.of("email", email, "password", password, "continueFrom", "");
        Map<String, Object> body = Map.of("query", GraphqlQueries.SIGN_IN, "variables", variables);

        Response response = RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .config(buildTimeoutConfig())
                .body(body)
                .post("/api/graphql/v1/query");

        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            throw new AuthenticationException(
                    "signIn mutation returned HTTP " + status + ": " + response.getBody().asString());
        }

        Boolean signedIn = response.jsonPath().getBoolean("data.signIn");
        if (!Boolean.TRUE.equals(signedIn)) {
            throw new AuthenticationException("signIn returned false — invalid credentials for: " + email);
        }

        Map<String, String> authData = new HashMap<>();
        authData.put(KEY_SIGNED_IN, "true");
        authData.put("email", email);
        authData.putAll(response.getCookies());

        log.info("GraphQL signIn successful for: " + email);
        return authData;
    }

    public static Map<String, String> loginViaApi() {
        if (!TestData.Credentials.areConfigured()) {
            throw new AuthenticationException(
                    "Test credentials are not configured. " +
                            "Set TEST_USER_LOGIN and TEST_USER_PASSWORD in .env or environment variables.");
        }
        return loginViaApi(TestData.Credentials.LOGIN, TestData.Credentials.PASSWORD);
    }

    public static void injectAuthIntoDriver(WebDriver driver, Map<String, String> authData) {
        log.step("Injecting authentication cookies into WebDriver");

        // Cookies from signIn are scoped to account.mobalytics.gg — navigate there before setting them
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl == null || !currentUrl.startsWith(TestConfig.API_BASE_URL)) {
            log.debug("Navigating to account domain for cookie injection: " + TestConfig.API_BASE_URL);
            driver.get(TestConfig.API_BASE_URL);
        }

        for (Map.Entry<String, String> entry : authData.entrySet()) {
            String key = entry.getKey();
            if (KEY_SIGNED_IN.equals(key) || "email".equals(key)) continue;
            driver.manage().addCookie(new Cookie(key, entry.getValue()));
            log.debug("Injected cookie: " + key);
        }

        log.debug("Navigating to main application: " + TestConfig.BASE_URL);
        driver.get(TestConfig.BASE_URL);
        log.info("Authentication injection complete — navigated to " + TestConfig.BASE_URL);
    }

    public static void loginAndInject(WebDriver driver, String email, String password) {
        injectAuthIntoDriver(driver, loginViaApi(email, password));
    }

    public static void loginAndInject(WebDriver driver) {
        loginAndInject(driver, TestData.Credentials.LOGIN, TestData.Credentials.PASSWORD);
    }

    public static void logoutViaApi(Map<String, String> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            throw new AuthenticationException("Logout requires session cookies from loginViaApi()");
        }

        log.step("Logging out via GraphQL signOut with session cookies");

        Map<String, Object> body = Map.of("query", GraphqlQueries.SIGN_OUT);

        Response response = RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .config(buildTimeoutConfig())
                .cookies(cookies)
                .body(body)
                .post("/api/graphql/v1/query");

        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            throw new AuthenticationException(
                    "signOut mutation returned HTTP " + status + ": " + response.getBody().asString());
        }

        Boolean signedOut = response.jsonPath().getBoolean("data.signOut");
        if (!Boolean.TRUE.equals(signedOut)) {
            throw new AuthenticationException(
                    "signOut returned false — logout may have failed. Response: " + response.getBody().asString());
        }

        log.info("GraphQL signOut complete");
    }
}
