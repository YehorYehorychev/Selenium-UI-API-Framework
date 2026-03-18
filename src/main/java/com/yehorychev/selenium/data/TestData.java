package com.yehorychev.selenium.data;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.errors.TestDataException;

public final class TestData {

    private TestData() {
    }

    public static final class Credentials {

        public static final String LOGIN = TestConfig.USER_LOGIN;
        public static final String PASSWORD = TestConfig.USER_PASSWORD;
        public static final String ADMIN_LOGIN = TestConfig.ADMIN_USER_LOGIN;
        public static final String ADMIN_PASSWORD = TestConfig.ADMIN_USER_PASSWORD;

        private Credentials() {
        }

        public static boolean areConfigured() {
            return LOGIN != null && !LOGIN.isBlank()
                    && PASSWORD != null && !PASSWORD.isBlank();
        }
    }

    public static final class UrlPatterns {
        public static final String HOME = "/";
        public static final String LOGIN = "/login";
        public static final String DASHBOARD = "/dashboard";
        public static final String PROFILE = "/profile";

        public static final String LOL = "/lol";
        public static final String TFT = "/tft";
        public static final String VALORANT = "/valorant";
        public static final String DIABLO4 = "/diablo-4";
        public static final String POE2 = "/poe-2";

        public static final String DEADLOCK = "/deadlock";
        public static final String MHW = "/mhw";
        public static final String BORDERLANDS4 = "/borderlands-4";
        public static final String NIGHTREIGN = "/elden-ring-nightreign";
        public static final String OVERWATCH = "/overwatch";

        public static final String LOL_TIER_LIST = "/lol/tier-list";
        public static final String LOL_CHAMPIONS = "/lol/champions";
        public static final String LOL_SUMMONER_SEARCH = "/lol/summoner-search";

        public static final String TFT_TIER_LIST = "/tft/tier-list";
        public static final String TFT_TEAM_COMPS = "/tft/team-comps";

        public static final String API_LOGIN = "/api/auth/login";
        public static final String API_GRAPHQL = "/api/graphql/v1/query";
        public static final String API_LOGOUT = "/api/auth/logout";

        private UrlPatterns() {
        }
    }

    public static final class UiStrings {
        public static final String HOME_PAGE_TITLE = "Mobalytics";
        public static final String LOGIN_PAGE_TITLE = "Sign In";
        public static final String DASHBOARD_PAGE_TITLE = "Dashboard";

        public static final String LOL_HEADING = "League";
        public static final String TFT_HEADING = "Teamfight";
        public static final String VALORANT_HEADING = "Valorant";
        public static final String DIABLO4_HEADING = "Diablo";
        public static final String POE2_HEADING = "Exile";

        public static final String DEADLOCK_HEADING = "Deadlock";
        public static final String MHW_HEADING = "Wilds";
        public static final String BORDERLANDS4_HEADING = "Borderlands";
        public static final String NIGHTREIGN_HEADING = "Nightreign";
        public static final String OVERWATCH_HEADING = "Overwatch";

        public static final String LOL_TIER_LIST_HEADING = "Tier List";
        public static final String LOL_CHAMPIONS_HEADING = "Champions";
        public static final String TFT_TIER_LIST_HEADING = "Tier";

        public static final String WELCOME_MESSAGE = "Welcome";
        public static final String ERROR_INVALID_LOGIN = "Invalid username or password";
        public static final String ERROR_REQUIRED_FIELD = "This field is required";

        private UiStrings() {
        }
    }

    public static final class Timeouts {
        public static final long ANIMATION_MS = 1000;
        public static final long FILE_UPLOAD_MS = 30_000;
        public static final long GRAPHQL_SLOW_MS = 20_000;
        public static final long DEBOUNCE_MS = 500;

        private Timeouts() {
        }
    }

    public static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new TestDataException(key);
        }
        return value;
    }
}
