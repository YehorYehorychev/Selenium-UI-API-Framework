package com.yehorychev.selenium.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralised test configuration — analogue of {@code test.config.ts}.
 *
 * <p>Resolution order (highest priority wins):
 * <ol>
 *   <li>Environment variable (e.g. {@code BASE_URL})</li>
 *   <li>Value from {@code config.properties} on the classpath</li>
 *   <li>Hard-coded fallback constant defined in this class</li>
 * </ol>
 *
 * <p>Usage:
 * <pre>{@code
 *   String url     = TestConfig.BASE_URL;
 *   long   timeout = TestConfig.DEFAULT_TIMEOUT_MS;
 *   boolean headless = TestConfig.HEADLESS;
 * }</pre>
 */
public final class TestConfig {

    // ── Fallback defaults (lowest priority) ────────────────────────────────
    private static final String DEFAULT_BASE_URL            = "https://mobalytics.gg";
    private static final String DEFAULT_API_BASE_URL        = "https://api.mobalytics.gg";
    private static final String DEFAULT_BROWSER             = "chrome";
    private static final String DEFAULT_HEADLESS            = "true";
    private static final String DEFAULT_TIMEOUT             = "15000";
    private static final String DEFAULT_NAVIGATION_TIMEOUT  = "30000";
    private static final String DEFAULT_API_TIMEOUT         = "10000";
    private static final String DEFAULT_RETRY_COUNT         = "1";
    private static final String DEFAULT_PARALLEL_THREADS    = "4";
    private static final String DEFAULT_VIEWPORT_WIDTH      = "1920";
    private static final String DEFAULT_VIEWPORT_HEIGHT     = "1080";
    private static final String DEFAULT_SCREENSHOT_FAILURE  = "true";
    private static final String DEFAULT_ALLURE_RESULTS_DIR  = "target/allure-results";
    private static final String DEFAULT_ALLURE_REPORT_DIR   = "target/allure-report";

    // ── Resolved public constants ───────────────────────────────────────────

    /** Application base URL — used as the starting point for page navigation. */
    public static final String BASE_URL;

    /** API base URL — used for REST / GraphQL requests. */
    public static final String API_BASE_URL;

    /**
     * Target browser: {@code chrome} | {@code firefox} | {@code edge}.
     * Maps to a concrete WebDriver in {@code DriverConfig}.
     */
    public static final String BROWSER;

    /** Run browser without a UI window when {@code true}. */
    public static final boolean HEADLESS;

    /** Default element-wait timeout in milliseconds. */
    public static final long DEFAULT_TIMEOUT_MS;

    /** Page navigation timeout in milliseconds. */
    public static final long NAVIGATION_TIMEOUT_MS;

    /** API call timeout in milliseconds. */
    public static final long API_TIMEOUT_MS;

    /** Number of test retries on failure (0 = no retry). */
    public static final int RETRY_COUNT;

    /** Number of parallel test threads. */
    public static final int PARALLEL_THREADS;

    /** Browser viewport width in pixels. */
    public static final int VIEWPORT_WIDTH;

    /** Browser viewport height in pixels. */
    public static final int VIEWPORT_HEIGHT;

    /** Attach a screenshot to the Allure report when a test fails. */
    public static final boolean SCREENSHOT_ON_FAILURE;

    /** Directory where Allure raw results are written. */
    public static final String ALLURE_RESULTS_DIR;

    /** Directory where the generated Allure HTML report is placed. */
    public static final String ALLURE_REPORT_DIR;

    // ── Static initializer — runs once when the class is first loaded ───────
    static {
        Properties props = loadProperties();

        BASE_URL           = resolve("BASE_URL",            "base.url",            DEFAULT_BASE_URL,           props);
        API_BASE_URL       = resolve("API_BASE_URL",        "api.base.url",        DEFAULT_API_BASE_URL,       props);
        BROWSER            = resolve("BROWSER",             "browser",             DEFAULT_BROWSER,            props).toLowerCase();
        HEADLESS           = Boolean.parseBoolean(resolve("HEADLESS",             "headless",             DEFAULT_HEADLESS,           props));
        DEFAULT_TIMEOUT_MS = Long.parseLong(resolve("DEFAULT_TIMEOUT",       "timeout.default",      DEFAULT_TIMEOUT,            props));
        NAVIGATION_TIMEOUT_MS = Long.parseLong(resolve("NAVIGATION_TIMEOUT",    "timeout.navigation",   DEFAULT_NAVIGATION_TIMEOUT, props));
        API_TIMEOUT_MS     = Long.parseLong(resolve("API_TIMEOUT",           "timeout.api",          DEFAULT_API_TIMEOUT,        props));
        RETRY_COUNT        = Integer.parseInt(resolve("RETRY_COUNT",           "retry.count",          DEFAULT_RETRY_COUNT,        props));
        PARALLEL_THREADS   = Integer.parseInt(resolve("PARALLEL_THREADS",      "parallel.threads",     DEFAULT_PARALLEL_THREADS,   props));
        VIEWPORT_WIDTH     = Integer.parseInt(resolve("VIEWPORT_WIDTH",        "viewport.width",       DEFAULT_VIEWPORT_WIDTH,     props));
        VIEWPORT_HEIGHT    = Integer.parseInt(resolve("VIEWPORT_HEIGHT",       "viewport.height",      DEFAULT_VIEWPORT_HEIGHT,    props));
        SCREENSHOT_ON_FAILURE = Boolean.parseBoolean(resolve("SCREENSHOT_ON_FAILURE", "screenshot.on.failure", DEFAULT_SCREENSHOT_FAILURE, props));
        ALLURE_RESULTS_DIR = resolve("ALLURE_RESULTS_DIR",  "allure.results.dir",  DEFAULT_ALLURE_RESULTS_DIR, props);
        ALLURE_REPORT_DIR  = resolve("ALLURE_REPORT_DIR",   "allure.report.dir",   DEFAULT_ALLURE_REPORT_DIR,  props);
    }

    // ── Private utilities ───────────────────────────────────────────────────

    /** Not instantiable — all members are static. */
    private TestConfig() {}

    /**
     * Resolves a configuration value using the three-level priority chain:
     * env var → property file → hard-coded default.
     *
     * @param envKey      environment variable name  (e.g. {@code "BASE_URL"})
     * @param propKey     key in {@code config.properties} (e.g. {@code "base.url"})
     * @param fallback    hard-coded default value
     * @param props       loaded {@link Properties} instance
     * @return            the resolved string value (never {@code null})
     */
    private static String resolve(String envKey, String propKey, String fallback, Properties props) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }
        String propValue = props.getProperty(propKey);
        if (propValue != null && !propValue.isBlank()) {
            return propValue.trim();
        }
        return fallback;
    }

    /**
     * Loads {@code config.properties} from the classpath.
     * Returns an empty {@link Properties} instance if the file is not found,
     * so that the fallback defaults are used instead.
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = TestConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // Non-fatal: fallback defaults will be used
            System.err.println("[TestConfig] Could not load config.properties: " + e.getMessage());
        }
        return props;
    }

    /**
     * Loads {@code .env} file from the project root.
     * Returns {@code null} if the file is not found — non-fatal,
     * system env vars or config.properties will be used instead.
     */
    private static Dotenv loadDotenv() {
        try {
            return Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
            // Non-fatal: system env vars and properties will still work
            System.err.println("[TestConfig] Could not load .env file: " + e.getMessage());
            return null;
        }
    }
}

