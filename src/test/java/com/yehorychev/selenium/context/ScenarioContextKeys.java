package com.yehorychev.selenium.context;

/**
 * Typed constants for all fixed {@link ScenarioContext} keys used across hooks and steps.
 * Use these instead of raw string literals to prevent typos and enable IDE navigation.
 *
 * <p>Dynamic, feature-file-driven keys (e.g. "save current URL as {string}") are intentionally
 * left as inline strings in their respective step definitions — only framework-level keys live here.
 */
public final class ScenarioContextKeys {

    private ScenarioContextKeys() {
    }

    // ── Authentication ───────────────────────────────────────────────────────
    /** Boolean flag ("true") set by {@code AuthHooks} / {@code AuthSteps} after successful sign-in. */
    public static final String IS_AUTHENTICATED = "isAuthenticated";

    /**
     * Session cookies captured from {@code AuthHelper.loginViaApi()} (meta-keys {@code signedIn}
     * and {@code email} excluded). Passed to {@code AuthHelper.logoutViaApi()} in teardown so the
     * server-side session is properly invalidated, not just browser cookies cleared.
     */
    public static final String AUTH_COOKIES = "auth.cookies";

    // ── API ──────────────────────────────────────────────────────────────────
    /** Stores the last {@code io.restassured.response.Response} from an API step. */
    public static final String LAST_RESPONSE = "lastResponse";

    // ── Retry tracking ───────────────────────────────────────────────────────
    /** {@code Integer} — current attempt number (1-based). */
    public static final String RETRY_ATTEMPT_NUMBER = "retry.attemptNumber";

    /** {@code Integer} — total allowed attempts (RETRY_COUNT + 1). */
    public static final String RETRY_TOTAL_ATTEMPTS = "retry.totalAttempts";

    /** {@code Boolean} — {@code true} if this is a retry (attempt > 1). */
    public static final String RETRY_WAS_RETRIED = "retry.wasRetried";
}

