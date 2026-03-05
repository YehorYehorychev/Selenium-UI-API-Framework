package com.yehorychev.selenium.data;

import com.yehorychev.selenium.errors.TestDataException;

/**
 * Central repository for test data constants and required environment variables.
 *
 * <p>Contains nested static classes grouping credentials, URL patterns, UI strings,
 * and timeouts. Use {@link #requireEnv(String)} to enforce presence of critical
 * environment variables at runtime.
 *
 * <p>Usage:
 * <pre>{@code
 *   String login = TestData.Credentials.LOGIN;
 *   String expectedTitle = TestData.UiStrings.HOME_PAGE_TITLE;
 *   String apiUrl = TestData.UrlPatterns.API_LOGIN;
 * }</pre>
 */
public final class TestData {

    private TestData() {
    }

    // ── Credentials ───────────────────────────────────────────────────────────

    /**
     * Test user credentials — sourced from environment variables or .env file.
     *
     * <p><strong>Note</strong>: Values are resolved lazily. If you access a credential
     * that is not set, {@code null} will be returned (use {@link #areConfigured()} to check).
     */
    public static final class Credentials {
        /**
         * Primary test user login / email.
         * Required for API tests that use {@link com.yehorychev.selenium.helpers.AuthHelper}.
         */
        public static final String LOGIN = System.getenv("TEST_USER_LOGIN");

        /**
         * Primary test user password.
         * Required for API tests that use {@link com.yehorychev.selenium.helpers.AuthHelper}.
         */
        public static final String PASSWORD = System.getenv("TEST_USER_PASSWORD");

        /**
         * Admin user login (optional).
         */
        public static final String ADMIN_LOGIN = System.getenv("ADMIN_USER_LOGIN");

        /**
         * Admin user password (optional).
         */
        public static final String ADMIN_PASSWORD = System.getenv("ADMIN_USER_PASSWORD");

        private Credentials() {
        }

        /**
         * Returns {@code true} if primary test credentials are configured.
         * Use this to conditionally skip API tests if credentials are not available.
         *
         * @return {@code true} if both LOGIN and PASSWORD are non-null and non-blank
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
     * Special timeouts for specific scenarios (supplementing {@link com.yehorychev.selenium.config.TestConfig}).
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
     * Retrieves an environment variable or .env value without throwing an exception.
     * Returns {@code null} if the variable is not set.
     *
     * <p>Use this for optional configuration values.
     *
     * @param key environment variable name
     * @return the environment variable value, or {@code null} if not set
     */
    private static String getEnv(String key) {
        return System.getenv(key);
    }

    /**
     * Retrieves an environment variable and throws {@link TestDataException} if it
     * is missing or blank.
     *
     * <p>Use this for required configuration values that must be present.
     *
     * @param key environment variable name
     * @return the environment variable value (guaranteed non-null, non-blank)
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

