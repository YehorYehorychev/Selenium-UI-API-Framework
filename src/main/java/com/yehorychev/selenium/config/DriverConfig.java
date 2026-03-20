package com.yehorychev.selenium.config;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
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
        WebDriver driver = TestConfig.REMOTE_ENABLED
                ? createRemoteDriver(browser)
                : createLocalDriver(browser);

        applyViewport(driver);
        applyTimeouts(driver);
        return driver;
    }

    private static WebDriver createLocalDriver(String browser) {
        return switch (browser.toLowerCase().trim()) {
            case "firefox" -> new FirefoxDriver(buildFirefoxOptions());
            case "edge" -> new EdgeDriver(buildEdgeOptions());
            case "chrome" -> new ChromeDriver(buildChromeOptions());
            default -> throw unsupportedBrowser(browser);
        };
    }

    private static WebDriver createRemoteDriver(String browser) {
        try {
            return switch (browser.toLowerCase().trim()) {
                case "firefox" -> new RemoteWebDriver(buildRemoteUrl(), buildFirefoxOptions());
                case "edge" -> new RemoteWebDriver(buildRemoteUrl(), buildEdgeOptions());
                case "chrome" -> new RemoteWebDriver(buildRemoteUrl(), buildChromeOptions());
                default -> throw unsupportedBrowser(browser);
            };
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid REMOTE_URL: " + TestConfig.REMOTE_URL, e);
        }
    }

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (TestConfig.HEADLESS) {
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
        applyRemoteCapabilities(options);
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (TestConfig.HEADLESS) {
            options.addArguments("-headless");
        }
        options.addArguments(
                "--width=" + TestConfig.VIEWPORT_WIDTH,
                "--height=" + TestConfig.VIEWPORT_HEIGHT
        );
        applyRemoteCapabilities(options);
        return options;
    }

    private static EdgeOptions buildEdgeOptions() {
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
        applyRemoteCapabilities(options);
        return options;
    }

    private static void applyRemoteCapabilities(MutableCapabilities options) {
        if (TestConfig.REMOTE_BROWSER_VERSION != null && !TestConfig.REMOTE_BROWSER_VERSION.isBlank()) {
            options.setCapability("browserVersion", TestConfig.REMOTE_BROWSER_VERSION);
        }
        if (TestConfig.REMOTE_PLATFORM_NAME != null && !TestConfig.REMOTE_PLATFORM_NAME.isBlank()) {
            options.setCapability("platformName", TestConfig.REMOTE_PLATFORM_NAME);
        }
        if (TestConfig.REMOTE_ENABLE_VNC) {
            options.setCapability("enableVNC", true);
        }
        if (TestConfig.REMOTE_ENABLE_VIDEO) {
            options.setCapability("enableVideo", true);
        }
    }

    private static URL buildRemoteUrl() throws MalformedURLException {
        return new URL(TestConfig.REMOTE_URL);
    }

    private static IllegalArgumentException unsupportedBrowser(String browser) {
        return new IllegalArgumentException(
                "Unsupported browser: \"" + browser + "\". Use: chrome | firefox | edge"
        );
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