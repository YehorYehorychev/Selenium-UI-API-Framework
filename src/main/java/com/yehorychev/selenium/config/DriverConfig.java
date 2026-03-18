package com.yehorychev.selenium.config;

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
 * Driver binaries are managed automatically by Selenium Manager (Selenium 4.6+).
 */
public final class DriverConfig {

    private DriverConfig() {
    }

    public static WebDriver createDriver() {
        return createDriver(TestConfig.BROWSER);
    }

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

    private static WebDriver createChromeDriver() {
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

    private static void applyViewport(WebDriver driver) {
        driver.manage().window()
                .setSize(new Dimension(TestConfig.VIEWPORT_WIDTH, TestConfig.VIEWPORT_HEIGHT));
    }

    private static void applyTimeouts(WebDriver driver) {
        // Implicit wait is intentionally NOT set — mixing implicit + explicit waits
        // causes unpredictable behavior (e.g. doubled effective timeouts).
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofMillis(TestConfig.NAVIGATION_TIMEOUT_MS))
                .scriptTimeout(Duration.ofMillis(TestConfig.DEFAULT_TIMEOUT_MS));
    }
}