package com.yehorychev.selenium.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralised test configuration — reads from env vars, config.properties, then hard-coded defaults.
 * <p>
 * Resolution order (highest priority wins):
 * 1. Environment variable  (e.g. BASE_URL)
 * 2. config.properties on the classpath  (e.g. base.url)
 * 3. Hard-coded fallback constant in this class
 * <p>
 * Usage:
 * String  url      = TestConfig.BASE_URL;
 * long    timeout  = TestConfig.DEFAULT_TIMEOUT_MS;
 * boolean headless = TestConfig.HEADLESS;
 */
public final class TestConfig {

    // ── Fallback defaults (lowest priority) ────────────────────────────────
    private static final String DEFAULT_BASE_URL = "https://mobalytics.gg";
    private static final String DEFAULT_API_BASE_URL = "https://account.mobalytics.gg";
    private static final String DEFAULT_BROWSER = "chrome";
    private static final String DEFAULT_HEADLESS = "true";
    private static final String DEFAULT_TIMEOUT = "15000";
    private static final String DEFAULT_NAVIGATION_TIMEOUT = "30000";
    private static final String DEFAULT_API_TIMEOUT = "10000";
    private static final String DEFAULT_RETRY_COUNT = "2";
    private static final String DEFAULT_PARALLEL_THREADS = "4";
    private static final String DEFAULT_VIEWPORT_WIDTH = "1920";
    private static final String DEFAULT_VIEWPORT_HEIGHT = "1080";
    private static final String DEFAULT_SCREENSHOT_FAILURE = "true";
    private static final String DEFAULT_ALLURE_RESULTS_DIR = "target/allure-results";
    private static final String DEFAULT_ALLURE_REPORT_DIR = "target/allure-report";

    // ── Resolved public constants ───────────────────────────────────────────

    /**
     * Application base URL — starting point for page navigation.
     */
    public static final String BASE_URL;

    /**
     * API base URL — used for REST and GraphQL requests.
     */
    public static final String API_BASE_URL;

    /**
     * Target browser: chrome | firefox | edge.
     */
    public static final String BROWSER;

    /**
     * Run browser without a UI window when true.
     */
    public static final boolean HEADLESS;

    /**
     * Default element-wait timeout in milliseconds.
     */
    public static final long DEFAULT_TIMEOUT_MS;

    /**
     * Page navigation timeout in milliseconds.
     */
    public static final long NAVIGATION_TIMEOUT_MS;

    /**
     * API call timeout in milliseconds.
     */
    public static final long API_TIMEOUT_MS;

    /**
     * Number of test retries on failure (0 = no retry).
     */
    public static final int RETRY_COUNT;

    /**
     * Number of parallel test threads.
     */
    public static final int PARALLEL_THREADS;

    /**
     * Browser viewport width in pixels.
     */
    public static final int VIEWPORT_WIDTH;

    /**
     * Browser viewport height in pixels.
     */
    public static final int VIEWPORT_HEIGHT;

    /**
     * Attach a screenshot to the Allure report when a test fails.
     */
    public static final boolean SCREENSHOT_ON_FAILURE;

    /**
     * Directory where Allure raw results are written.
     */
    public static final String ALLURE_RESULTS_DIR;

    /**
     * Directory where the generated Allure HTML report is placed.
     */
    public static final String ALLURE_REPORT_DIR;

    /**
     * Test user login — sourced from TEST_USER_LOGIN env var or .env file.
     */
    public static final String USER_LOGIN;

    /**
     * Test user password — sourced from TEST_USER_PASSWORD env var or .env file.
     */
    public static final String USER_PASSWORD;

    /**
     * Admin user login — sourced from ADMIN_USER_LOGIN env var or .env file.
     * May be null if not configured — optional credential.
     */
    public static final String ADMIN_USER_LOGIN;

    /**
     * Admin user password — sourced from ADMIN_USER_PASSWORD env var or .env file.
     * May be null if not configured — optional credential.
     */
    public static final String ADMIN_USER_PASSWORD;

    // ── Dotenv instance — shared across all resolve() calls ─────────────────
    private static final Dotenv DOTENV = loadDotenv();

    // ── Static initializer — runs once when the class is first loaded ───────
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
        ALLURE_RESULTS_DIR = resolve("ALLURE_RESULTS_DIR", "allure.results.dir", DEFAULT_ALLURE_RESULTS_DIR, props);
        ALLURE_REPORT_DIR = resolve("ALLURE_REPORT_DIR", "allure.report.dir", DEFAULT_ALLURE_REPORT_DIR, props);

        // Credentials — no fallback defaults, may be null if not set
        USER_LOGIN         = resolveOptional("TEST_USER_LOGIN");
        USER_PASSWORD      = resolveOptional("TEST_USER_PASSWORD");
        ADMIN_USER_LOGIN   = resolveOptional("ADMIN_USER_LOGIN");
        ADMIN_USER_PASSWORD = resolveOptional("ADMIN_USER_PASSWORD");
    }

    // ── Private utilities ───────────────────────────────────────────────────

    /**
     * Not instantiable — all members are static.
     */
    private TestConfig() {
    }

    /**
     * Resolves a config value from (in priority order):
     * 1. Real OS environment variable
     * 2. .env file (via Dotenv)
     * 3. config.properties on the classpath
     * 4. Hard-coded fallback
     *
     * @param envKey   environment variable / .env key  (e.g. "BASE_URL")
     * @param propKey  key in config.properties          (e.g. "base.url")
     * @param fallback hard-coded default value
     * @param props    loaded Properties instance
     * @return resolved string value (never null)
     */
    private static String resolve(String envKey, String propKey, String fallback, Properties props) {
        // 1. OS environment variable
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) return envValue.trim();

        // 2. .env file
        if (DOTENV != null) {
            String dotenvValue = DOTENV.get(envKey, null);
            if (dotenvValue != null && !dotenvValue.isBlank()) return dotenvValue.trim();
        }

        // 3. config.properties
        String propValue = props.getProperty(propKey);
        if (propValue != null && !propValue.isBlank()) return propValue.trim();

        // 4. Hard-coded fallback
        return fallback;
    }

    /**
     * Like resolve() but returns null instead of a fallback when the key is missing.
     * Used for optional/credential values that have no sensible default.
     */
    private static String resolveOptional(String envKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) return envValue.trim();

        if (DOTENV != null) {
            String dotenvValue = DOTENV.get(envKey, null);
            if (dotenvValue != null && !dotenvValue.isBlank()) return dotenvValue.trim();
        }

        return null; // deliberately no fallback for sensitive credentials
    }

    /**
     * Loads config.properties from the classpath.
     * Returns an empty Properties instance if the file is not found.
     */
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

    /**
     * Loads .env from the project root directory.
     * Returns null if the file is missing — non-fatal, system env vars still work.
     */
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

