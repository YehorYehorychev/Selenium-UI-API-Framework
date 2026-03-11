package com.yehorychev.selenium.data;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.errors.TestDataException;

/**
 * Central repository for test data constants and environment-backed credentials.
 * <p>
 * Contains nested static classes:
 * - Credentials  — login/password from env vars; use areConfigured() before accessing
 * - UrlPatterns  — page paths and API endpoint constants
 * - UiStrings    — expected text labels and page titles for assertions
 * - Timeouts     — scenario-specific timeouts supplementing TestConfig
 * <p>
 * Usage:
 * String login   = TestData.Credentials.LOGIN;
 * String title   = TestData.UiStrings.HOME_PAGE_TITLE;
 * String apiUrl  = TestData.UrlPatterns.API_LOGIN;
 * String envVal  = TestData.requireEnv("MY_VAR");
 */
public final class TestData {

    private TestData() {
    }

    // ── Credentials ───────────────────────────────────────────────────────────

    /**
     * Test user credentials — sourced from environment variables or .env file.
     * Values are null if the env vars are not set — use areConfigured() to check.
     */
    public static final class Credentials {

        /**
         * Primary test user login / email. Required for AuthHelper.
         */
        public static final String LOGIN = TestConfig.USER_LOGIN;

        /**
         * Primary test user password. Required for AuthHelper.
         */
        public static final String PASSWORD = TestConfig.USER_PASSWORD;

        /**
         * Admin user login (optional) — sourced via TestConfig resolution chain.
         */
        public static final String ADMIN_LOGIN = TestConfig.ADMIN_USER_LOGIN;

        /**
         * Admin user password (optional) — sourced via TestConfig resolution chain.
         */
        public static final String ADMIN_PASSWORD = TestConfig.ADMIN_USER_PASSWORD;

        private Credentials() {
        }

        /**
         * Returns true if both LOGIN and PASSWORD are non-null and non-blank.
         * Use to conditionally skip auth-dependent tests when credentials are absent.
         *
         * @return true if primary credentials are configured
         */
        public static boolean areConfigured() {
            return LOGIN != null && !LOGIN.isBlank()
                    && PASSWORD != null && !PASSWORD.isBlank();
        }
    }

    // ── URL patterns ──────────────────────────────────────────────────────────

    /**
     * URL fragments and endpoint paths used in navigation and assertions.
     */
    public static final class UrlPatterns {
        public static final String HOME = "/";
        public static final String LOGIN = "/login";
        public static final String DASHBOARD = "/dashboard";
        public static final String PROFILE = "/profile";

        // API endpoints
        public static final String API_LOGIN = "/api/auth/login";
        public static final String API_GRAPHQL = "/api/graphql/v1/query";
        public static final String API_LOGOUT = "/api/auth/logout";

        private UrlPatterns() {
        }
    }

    // ── UI strings ────────────────────────────────────────────────────────────

    /**
     * Expected text labels, titles and messages used for assertions.
     */
    public static final class UiStrings {
        public static final String HOME_PAGE_TITLE = "Mobalytics";
        public static final String LOGIN_PAGE_TITLE = "Sign In";
        public static final String DASHBOARD_PAGE_TITLE = "Dashboard";

        public static final String WELCOME_MESSAGE = "Welcome";
        public static final String ERROR_INVALID_LOGIN = "Invalid username or password";
        public static final String ERROR_REQUIRED_FIELD = "This field is required";

        private UiStrings() {
        }
    }

    // ── Timeouts ──────────────────────────────────────────────────────────────

    /**
     * Special timeouts for specific scenarios (supplementing TestConfig).
     */
    public static final class Timeouts {
        /**
         * Timeout for animations / transitions (milliseconds).
         */
        public static final long ANIMATION_MS = 1000;

        /**
         * Timeout for file uploads (milliseconds).
         */
        public static final long FILE_UPLOAD_MS = 30_000;

        /**
         * Timeout for slow GraphQL queries (milliseconds).
         */
        public static final long GRAPHQL_SLOW_MS = 20_000;

        /**
         * Short wait for UI debounce (milliseconds).
         */
        public static final long DEBOUNCE_MS = 500;

        private Timeouts() {
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    /**
     * Retrieves an environment variable without throwing. Returns null if not set.
     * Use this for optional configuration values.
     *
     * @param key environment variable name
     * @return the variable value, or null if not set
     */
    private static String getEnv(String key) {
        return System.getenv(key);
    }

    /**
     * Retrieves an environment variable and throws TestDataException if missing or blank.
     * Use this for required config values that must be present at runtime.
     *
     * @param key environment variable name
     * @return the variable value (guaranteed non-null, non-blank)
     * @throws TestDataException if the variable is absent or empty
     */
    public static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new TestDataException(key);
        }
        return value;
    }
}
