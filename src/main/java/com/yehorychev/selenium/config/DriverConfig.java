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
 * WebDriver factory — analogue of the browser-launch logic in {@code playwright.config.ts}.
 *
 * <p>Reads browser type, headless flag, and viewport from {@link TestConfig} and
 * delegates driver binary management to WebDriverManager so that no manual
 * driver downloads are required.
 *
 * <p>Usage:
 * <pre>{@code
 *   WebDriver driver = DriverConfig.createDriver();
 *   // or explicitly:
 *   WebDriver driver = DriverConfig.createDriver("firefox");
 * }</pre>
 *
 * <p>Supported browser values (case-insensitive):
 * <ul>
 *   <li>{@code chrome}  — Google Chrome (default)</li>
 *   <li>{@code firefox} — Mozilla Firefox</li>
 *   <li>{@code edge}    — Microsoft Edge</li>
 * </ul>
 */
public final class DriverConfig {

    /** Not instantiable — all members are static. */
    private DriverConfig() {}

    // ── Public factory methods ──────────────────────────────────────────────

    /**
     * Creates a {@link WebDriver} using the browser defined in {@link TestConfig}.
     *
     * @return configured and ready-to-use {@link WebDriver} instance
     */
    public static WebDriver createDriver() {
        return createDriver(TestConfig.BROWSER);
    }

    /**
     * Creates a {@link WebDriver} for the requested browser, applying headless
     * mode and viewport from {@link TestConfig}.
     *
     * @param browser target browser: {@code chrome}, {@code firefox}, or {@code edge}
     * @return configured {@link WebDriver} instance
     * @throws IllegalArgumentException if the browser name is not supported
     */
    public static WebDriver createDriver(String browser) {
        WebDriver driver = switch (browser.toLowerCase().trim()) {
            case "firefox" -> createFirefoxDriver();
            case "edge"    -> createEdgeDriver();
            case "chrome"  -> createChromeDriver();
            default        -> throw new IllegalArgumentException(
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
     * This is a no-op when {@code --window-size} is already passed via CLI args,
     * but acts as a safety net for drivers that ignore the argument.
     */
    private static void applyViewport(WebDriver driver) {
        driver.manage().window()
                .setSize(new Dimension(TestConfig.VIEWPORT_WIDTH, TestConfig.VIEWPORT_HEIGHT));
    }

    /**
     * Applies implicit wait and script/page-load timeouts.
     *
     * <p><strong>Note:</strong> explicit waits ({@link org.openqa.selenium.support.ui.WebDriverWait})
     * are preferred over implicit waits for individual element interactions, but a
     * small implicit wait here prevents flaky NoSuchElementExceptions on slow pages.
     */
    private static void applyTimeouts(WebDriver driver) {
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofMillis(500))
                .pageLoadTimeout(Duration.ofMillis(TestConfig.NAVIGATION_TIMEOUT_MS))
                .scriptTimeout(Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
    }
}

