package com.yehorychev.selenium.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralised test configuration resolved from env vars → .env → config.properties → defaults.
 */
public final class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://mobalytics.gg";
    private static final String DEFAULT_API_BASE_URL = "https://account.mobalytics.gg";
    private static final String DEFAULT_BROWSER = "chrome";
    private static final String DEFAULT_HEADLESS = "true";
    private static final String DEFAULT_TIMEOUT = "15000";
    private static final String DEFAULT_NAVIGATION_TIMEOUT = "30000";
    private static final String DEFAULT_API_TIMEOUT = "10000";
    private static final String DEFAULT_RETRY_COUNT = "1";
    private static final String DEFAULT_PARALLEL_THREADS = "4";
    private static final String DEFAULT_VIEWPORT_WIDTH = "1920";
    private static final String DEFAULT_VIEWPORT_HEIGHT = "1080";
    private static final String DEFAULT_SCREENSHOT_FAILURE = "true";
    private static final String DEFAULT_SCREENSHOT_DIR = "target/screenshots";
    private static final String DEFAULT_ALLURE_RESULTS_DIR = "target/allure-results";
    private static final String DEFAULT_ALLURE_REPORT_DIR = "target/allure-report";
    private static final String DEFAULT_REMOTE_ENABLED = "false";
    private static final String DEFAULT_REMOTE_URL = "http://localhost:4444/wd/hub";
    private static final String DEFAULT_REMOTE_BROWSER_VERSION = "";
    private static final String DEFAULT_REMOTE_PLATFORM_NAME = "";
    private static final String DEFAULT_REMOTE_ENABLE_VNC = "false";
    private static final String DEFAULT_REMOTE_ENABLE_VIDEO = "false";

    public static final String BASE_URL;
    public static final String API_BASE_URL;
    public static final String BROWSER;
    public static final boolean HEADLESS;
    public static final long DEFAULT_TIMEOUT_MS;
    public static final long NAVIGATION_TIMEOUT_MS;
    public static final long API_TIMEOUT_MS;
    public static final int RETRY_COUNT;
    public static final int PARALLEL_THREADS;
    public static final int VIEWPORT_WIDTH;
    public static final int VIEWPORT_HEIGHT;
    public static final boolean SCREENSHOT_ON_FAILURE;
    public static final String SCREENSHOT_DIR;
    public static final String ALLURE_RESULTS_DIR;
    public static final String ALLURE_REPORT_DIR;
    public static final String USER_LOGIN;
    public static final String USER_PASSWORD;
    public static final String ADMIN_USER_LOGIN;
    public static final String ADMIN_USER_PASSWORD;
    public static final boolean REMOTE_ENABLED;
    public static final String REMOTE_URL;
    public static final String REMOTE_BROWSER_VERSION;
    public static final String REMOTE_PLATFORM_NAME;
    public static final boolean REMOTE_ENABLE_VNC;
    public static final boolean REMOTE_ENABLE_VIDEO;

    private static final Dotenv DOTENV = loadDotenv();

    static {
        Properties props = loadProperties();

        BASE_URL = resolve("BASE_URL", "base.url", DEFAULT_BASE_URL, props);
        API_BASE_URL = resolve("API_BASE_URL", "api.base.url", DEFAULT_API_BASE_URL, props);
        BROWSER = resolve("BROWSER", "browser", DEFAULT_BROWSER, props).toLowerCase();
        HEADLESS = Boolean.parseBoolean(resolve("HEADLESS", "headless", DEFAULT_HEADLESS, props));
        DEFAULT_TIMEOUT_MS = Long.parseLong(resolve("DEFAULT_TIMEOUT", "timeout.default", DEFAULT_TIMEOUT, props));
        NAVIGATION_TIMEOUT_MS = Long.parseLong(resolve("NAVIGATION_TIMEOUT", "timeout.navigation", DEFAULT_NAVIGATION_TIMEOUT, props));
        API_TIMEOUT_MS = Long.parseLong(resolve("API_TIMEOUT", "timeout.api", DEFAULT_API_TIMEOUT, props));
        RETRY_COUNT = Integer.parseInt(resolve("RETRY_COUNT", "retry.count", DEFAULT_RETRY_COUNT, props));
        PARALLEL_THREADS = Integer.parseInt(resolve("PARALLEL_THREADS", "parallel.threads", DEFAULT_PARALLEL_THREADS, props));
        VIEWPORT_WIDTH = Integer.parseInt(resolve("VIEWPORT_WIDTH", "viewport.width", DEFAULT_VIEWPORT_WIDTH, props));
        VIEWPORT_HEIGHT = Integer.parseInt(resolve("VIEWPORT_HEIGHT", "viewport.height", DEFAULT_VIEWPORT_HEIGHT, props));
        SCREENSHOT_ON_FAILURE = Boolean.parseBoolean(resolve("SCREENSHOT_ON_FAILURE", "screenshot.on.failure", DEFAULT_SCREENSHOT_FAILURE, props));
        SCREENSHOT_DIR = resolve("SCREENSHOT_DIR", "screenshot.dir", DEFAULT_SCREENSHOT_DIR, props);
        ALLURE_RESULTS_DIR = resolve("ALLURE_RESULTS_DIR", "allure.results.dir", DEFAULT_ALLURE_RESULTS_DIR, props);
        ALLURE_REPORT_DIR = resolve("ALLURE_REPORT_DIR", "allure.report.dir", DEFAULT_ALLURE_REPORT_DIR, props);

        USER_LOGIN = resolveOptional("TEST_USER_LOGIN");
        USER_PASSWORD = resolveOptional("TEST_USER_PASSWORD");
        ADMIN_USER_LOGIN = resolveOptional("ADMIN_USER_LOGIN");
        ADMIN_USER_PASSWORD = resolveOptional("ADMIN_USER_PASSWORD");

        REMOTE_ENABLED = Boolean.parseBoolean(resolve("REMOTE_ENABLED", "remote.enabled", DEFAULT_REMOTE_ENABLED, props));
        REMOTE_URL = resolve("REMOTE_URL", "remote.url", DEFAULT_REMOTE_URL, props);
        REMOTE_BROWSER_VERSION = resolve("REMOTE_BROWSER_VERSION", "remote.browser.version", DEFAULT_REMOTE_BROWSER_VERSION, props);
        REMOTE_PLATFORM_NAME = resolve("REMOTE_PLATFORM_NAME", "remote.platform.name", DEFAULT_REMOTE_PLATFORM_NAME, props);
        REMOTE_ENABLE_VNC = Boolean.parseBoolean(resolve("REMOTE_ENABLE_VNC", "remote.enable.vnc", DEFAULT_REMOTE_ENABLE_VNC, props));
        REMOTE_ENABLE_VIDEO = Boolean.parseBoolean(resolve("REMOTE_ENABLE_VIDEO", "remote.enable.video", DEFAULT_REMOTE_ENABLE_VIDEO, props));
    }

    private TestConfig() {
    }

    private static String resolve(String envKey, String propKey, String fallback, Properties props) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) return envValue.trim();

        if (DOTENV != null) {
            String dotenvValue = DOTENV.get(envKey, null);
            if (dotenvValue != null && !dotenvValue.isBlank()) return dotenvValue.trim();
        }

        String propValue = props.getProperty(propKey);
        if (propValue != null && !propValue.isBlank()) return propValue.trim();

        return fallback;
    }

    private static String resolveOptional(String envKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) return envValue.trim();

        if (DOTENV != null) {
            String dotenvValue = DOTENV.get(envKey, null);
            if (dotenvValue != null && !dotenvValue.isBlank()) return dotenvValue.trim();
        }

        return null;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = TestConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("[TestConfig] Could not load config.properties: " + e.getMessage());
        }
        return props;
    }

    private static Dotenv loadDotenv() {
        try {
            return Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
            System.err.println("[TestConfig] Could not load .env file: " + e.getMessage());
            return null;
        }
    }
}
