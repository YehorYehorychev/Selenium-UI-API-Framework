package com.yehorychev.selenium.data;

/**
 * Cucumber tag constants used in {@code @CucumberOptions(tags=...)} and {@code .feature} files.
 *
 * <p>Usage in runner:
 * <pre>{@code
 *   @CucumberOptions(
 *       tags = Tags.SMOKE + " and " + Tags.UI
 *   )
 * }</pre>
 *
 * <p>Usage in feature files:
 * <pre>{@code
 *   @smoke @ui @critical
 *   Scenario: User logs in successfully
 * }</pre>
 */
public final class Tags {

    // ── Test levels ───────────────────────────────────────────────────────────

    /** Smoke tests — critical path scenarios executed on every commit. */
    public static final String SMOKE = "@smoke";

    /** Regression tests — full suite coverage. */
    public static final String REGRESSION = "@regression";

    // ── Test types ────────────────────────────────────────────────────────────

    /** UI / browser-based tests (Selenium). */
    public static final String UI = "@ui";

    /** API / backend tests (REST / GraphQL). */
    public static final String API = "@api";

    // ── Priority ──────────────────────────────────────────────────────────────

    /** Critical business scenarios — must pass before deployment. */
    public static final String CRITICAL = "@critical";

    // ── Functional areas ──────────────────────────────────────────────────────

    /** Navigation / routing tests. */
    public static final String NAVIGATION = "@navigation";

    /** Authentication / login flows. */
    public static final String AUTH = "@auth";

    /** Tests requiring an authenticated user. */
    public static final String AUTHENTICATED = "@authenticated";

    /** Profile / user settings tests. */
    public static final String PROFILE = "@profile";

    /** Dashboard / home page tests. */
    public static final String DASHBOARD = "@dashboard";

    /** Search functionality tests. */
    public static final String SEARCH = "@search";

    // ── Special flags ─────────────────────────────────────────────────────────

    /** Work in progress — excluded from CI. */
    public static final String WIP = "@wip";

    /** Known bug — tracked but not blocking. */
    public static final String KNOWN_BUG = "@known-bug";

    /** Flaky test — requires stabilization. */
    public static final String FLAKY = "@flaky";

    /** Slow test — may exceed standard timeout. */
    public static final String SLOW = "@slow";

    private Tags() {}
}

