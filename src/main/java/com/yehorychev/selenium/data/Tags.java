package com.yehorychev.selenium.data;

/**
 * Cucumber tag constants — use in @CucumberOptions(tags=...) and .feature files.
 * <p>
 * Usage in runner:
 *
 * @CucumberOptions(tags = Tags.SMOKE + " and " + Tags.UI)
 * <p>
 * Usage in feature files:
 * @smoke @ui @critical
 * Scenario: User logs in successfully
 */
public final class Tags {

    // ── Test levels ───────────────────────────────────────────────────────────

    /**
     * Smoke tests — critical path scenarios executed on every commit.
     */
    public static final String SMOKE = "@smoke";

    /**
     * Regression tests — full suite coverage.
     */
    public static final String REGRESSION = "@regression";

    // ── Test types ────────────────────────────────────────────────────────────

    /**
     * UI / browser-based tests (Selenium).
     */
    public static final String UI = "@ui";

    /**
     * API / backend tests (REST / GraphQL).
     */
    public static final String API = "@api";

    // ── Priority ──────────────────────────────────────────────────────────────

    /**
     * Critical business scenarios — must pass before deployment.
     */
    public static final String CRITICAL = "@critical";

    // ── Functional areas ──────────────────────────────────────────────────────

    /**
     * Navigation / routing tests.
     */
    public static final String NAVIGATION = "@navigation";

    /**
     * Authentication / login flows.
     */
    public static final String AUTH = "@auth";

    /**
     * Tests requiring an authenticated user.
     */
    public static final String AUTHENTICATED = "@authenticated";

    /**
     * Profile / user settings tests.
     */
    public static final String PROFILE = "@profile";

    /**
     * Dashboard / home page tests.
     */
    public static final String DASHBOARD = "@dashboard";

    /**
     * Search functionality tests.
     */
    public static final String SEARCH = "@search";

    // ── Special flags ─────────────────────────────────────────────────────────

    /**
     * Work in progress — excluded from CI.
     */
    public static final String WIP = "@wip";

    /**
     * Filter expression for @CucumberOptions — excludes all @wip scenarios.
     * Compile-time constant so it can be used directly in annotation parameters.
     * Example: @CucumberOptions(tags = Tags.NOT_WIP)
     */
    public static final String NOT_WIP = "not " + WIP;

    /**
     * Known bug — tracked but not blocking.
     */
    public static final String KNOWN_BUG = "@known-bug";

    /**
     * Flaky test — requires stabilization.
     */
    public static final String FLAKY = "@flaky";

    /**
     * Slow test — may exceed standard timeout.
     */
    public static final String SLOW = "@slow";

    /**
     * End-to-end multi-step flow tests — span multiple pages and validate complete user journeys.
     */
    public static final String E2E = "@e2e";

    // ── Game-specific tags ────────────────────────────────────────────────────

    /**
     * League of Legends tests.
     */
    public static final String LOL = "@lol";

    /**
     * Teamfight Tactics tests.
     */
    public static final String TFT = "@tft";

    /**
     * Valorant tests.
     */
    public static final String VALORANT = "@valorant";

    /**
     * Diablo 4 tests.
     */
    public static final String DIABLO4 = "@diablo4";

    /**
     * Path of Exile 2 tests.
     */
    public static final String POE2 = "@poe2";

    /**
     * Deadlock tests.
     */
    public static final String DEADLOCK = "@deadlock";

    /**
     * Monster Hunter Wilds tests.
     */
    public static final String MHW = "@mhw";

    /**
     * Borderlands 4 tests.
     */
    public static final String BORDERLANDS4 = "@borderlands4";

    /**
     * Elden Ring Nightreign tests.
     */
    public static final String NIGHTREIGN = "@nightreign";

    /**
     * Overwatch tests.
     */
    public static final String OVERWATCH = "@overwatch";

    private Tags() {
    }
}
