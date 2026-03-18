package com.yehorychev.selenium.data;

/**
 * Cucumber tag constants for use in @CucumberOptions and .feature files.
 */
public final class Tags {

    public static final String SMOKE = "@smoke";
    public static final String REGRESSION = "@regression";

    public static final String UI = "@ui";
    public static final String API = "@api";

    public static final String CRITICAL = "@critical";

    public static final String NAVIGATION = "@navigation";
    public static final String AUTH = "@auth";
    public static final String AUTHENTICATED = "@authenticated";
    public static final String PROFILE = "@profile";
    public static final String DASHBOARD = "@dashboard";
    public static final String SEARCH = "@search";

    public static final String WIP = "@wip";
    /**
     * Compile-time filter expression that excludes @wip scenarios.
     */
    public static final String NOT_WIP = "not " + WIP;
    public static final String KNOWN_BUG = "@known-bug";
    public static final String FLAKY = "@flaky";
    public static final String SLOW = "@slow";
    public static final String E2E = "@e2e";

    public static final String LOL = "@lol";
    public static final String TFT = "@tft";
    public static final String VALORANT = "@valorant";
    public static final String DIABLO4 = "@diablo4";
    public static final String POE2 = "@poe2";
    public static final String DEADLOCK = "@deadlock";
    public static final String MHW = "@mhw";
    public static final String BORDERLANDS4 = "@borderlands4";
    public static final String NIGHTREIGN = "@nightreign";
    public static final String OVERWATCH = "@overwatch";

    private Tags() {
    }
}
