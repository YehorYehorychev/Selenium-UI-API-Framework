package com.yehorychev.selenium.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * WebDriver factory — creates browser instances for Chrome, Firefox, and Edge.
 *
 * Reads browser type, headless flag, and viewport from TestConfig.
 * Delegates driver binary management to WebDriverManager — no manual downloads needed.
 *
 * Usage:
 *   WebDriver driver = DriverConfig.createDriver();           // uses TestConfig.BROWSER
 *   WebDriver driver = DriverConfig.createDriver("firefox");  // explicit override
 *
 * Supported browsers (case-insensitive): chrome | firefox | edge
 */
public final class DriverConfig {

    /** Not instantiable — all members are static. */
    private DriverConfig() {}

    // ── Public factory methods ──────────────────────────────────────────────

    /**
     * Creates a WebDriver using the browser defined in TestConfig.
     *
     * @return configured and ready-to-use WebDriver instance
     */
    public static WebDriver createDriver() {
        return createDriver(TestConfig.BROWSER);
    }

    /**
     * Creates a WebDriver for the requested browser.
     * Applies headless mode and viewport from TestConfig.
     *
     * @param browser target browser: chrome | firefox | edge
     * @return configured WebDriver instance
     * @throws IllegalArgumentException if the browser name is not supported
     */
    public static WebDriver createDriver(String browser) {
        WebDriver driver = switch (browser.toLowerCase().trim()) {
            case "firefox" -> createFirefoxDriver();
            case "edge" -> createEdgeDriver();
            case "chrome" -> createChromeDriver();
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: \"" + browser + "\". Use: chrome | firefox | edge"
            );
        };

        applyViewport(driver);
        applyTimeouts(driver);
        return driver;
    }

    // ── Browser-specific builders ───────────────────────────────────────────

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (TestConfig.HEADLESS) {
            // --headless=new is the modern headless mode (Chrome 112+)
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-extensions",
                "--disable-infobars",
                "--remote-allow-origins=*",
                "--window-size=" + TestConfig.VIEWPORT_WIDTH + "," + TestConfig.VIEWPORT_HEIGHT
        );

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        if (TestConfig.HEADLESS) {
            options.addArguments("-headless");
        }
        options.addArguments(
                "--width=" + TestConfig.VIEWPORT_WIDTH,
                "--height=" + TestConfig.VIEWPORT_HEIGHT
        );

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();

        EdgeOptions options = new EdgeOptions();
        if (TestConfig.HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-extensions",
                "--window-size=" + TestConfig.VIEWPORT_WIDTH + "," + TestConfig.VIEWPORT_HEIGHT
        );

        return new EdgeDriver(options);
    }

    // ── Post-creation configuration ─────────────────────────────────────────

    /**
     * Sets the browser window size to the configured viewport dimensions.
     * Safety net for drivers that ignore the --window-size CLI argument.
     */
    private static void applyViewport(WebDriver driver) {
        driver.manage().window()
                .setSize(new Dimension(TestConfig.VIEWPORT_WIDTH, TestConfig.VIEWPORT_HEIGHT));
    }

    /**
     * Applies page-load and script timeouts.
     * Note: a small implicit wait (500ms) is set here as a safety net for slow pages.
     * Prefer explicit WebDriverWait for individual element interactions.
     */
    private static void applyTimeouts(WebDriver driver) {
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofMillis(500))
                .pageLoadTimeout(Duration.ofMillis(TestConfig.NAVIGATION_TIMEOUT_MS))
                .scriptTimeout(Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
    }
}
