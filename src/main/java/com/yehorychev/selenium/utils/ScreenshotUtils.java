package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.helpers.Logger;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Screenshot helpers — full-page, viewport, and element-level captures via AShot,
 * with automatic Allure report attachment.
 * All methods are static — no instantiation needed.
 * Naming convention:
 * capture* — returns raw byte[] without side effects
 * attach*  — captures and attaches to the Allure report
 * save*    — captures and saves to the file system (no Allure attachment)
 * Usage:
 * ScreenshotUtils.attachViewport(driver, "Login page");
 * ScreenshotUtils.attachFullPage(driver, "Full home page");
 * ScreenshotUtils.attachElement(driver, element, "Submit button");
 * Path file = ScreenshotUtils.saveViewport(driver, "target/screenshots", "checkout");
 * byte[] bytes = ScreenshotUtils.captureFullPage(driver);
 */
public final class ScreenshotUtils {

    private static final Logger log = new Logger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    // Configuration constants (replaces magic numbers)
    private static final int SCREENSHOT_SCROLL_PADDING = 100;
    private static final String FILE_EXTENSION = ".png";
    private static final String ALLURE_MIME_TYPE = "image/png";

    private ScreenshotUtils() {
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Single point of Allure attachment — encapsulates Allure API call.
     * All public attach* methods delegate to this.
     *
     * @param bytes screenshot bytes (PNG format)
     * @param name  attachment label shown in the Allure report
     */
    private static void attachBytesToAllure(byte[] bytes, String name) {
        Allure.addAttachment(name, ALLURE_MIME_TYPE, new ByteArrayInputStream(bytes), FILE_EXTENSION);
    }

    /**
     * Converts BufferedImage to PNG byte array.
     *
     * @param image source image
     * @return PNG byte array
     * @throws IOException if image conversion fails
     */
    private static byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", out);
        return out.toByteArray();
    }

    /**
     * Validates WebDriver parameter.
     *
     * @param driver WebDriver to validate
     * @throws NullPointerException if driver is null
     */
    private static void validateDriver(WebDriver driver) {
        Objects.requireNonNull(driver, "driver cannot be null");
    }

    /**
     * Validates name parameter.
     *
     * @param name name to validate
     * @throws IllegalArgumentException if name is null or blank
     */
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
    }

    /**
     * Validates directory path parameter.
     *
     * @param dir directory path to validate
     * @throws IllegalArgumentException if dir is null or blank
     */
    private static void validateDirectory(String dir) {
        if (dir == null || dir.isBlank()) {
            throw new IllegalArgumentException("directory path cannot be null or empty");
        }
    }

    // ── Capture Methods (Raw Bytes, No Side Effects) ─────────────────────────

    /**
     * Captures a viewport screenshot and returns raw PNG bytes.
     * Does not attach to Allure or save to disk.
     *
     * @param driver active WebDriver
     * @return PNG byte array
     * @throws NullPointerException if driver is null
     */
    public static byte[] captureViewport(WebDriver driver) {
        validateDriver(driver);
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Captures a full-page screenshot (scrolls the entire page via AShot) and returns raw PNG bytes.
     * Falls back to viewport screenshot if full-page capture fails.
     * Does not attach to Allure or save to disk.
     *
     * @param driver active WebDriver
     * @return PNG byte array
     * @throws NullPointerException if driver is null
     */
    public static byte[] captureFullPage(WebDriver driver) {
        validateDriver(driver);
        try {
            Screenshot screenshot = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(SCREENSHOT_SCROLL_PADDING))
                    .takeScreenshot(driver);
            return toBytes(screenshot.getImage());
        } catch (Exception e) {
            log.warn("Full-page screenshot failed, falling back to viewport: " + e.getMessage());
            return captureViewport(driver);
        }
    }

    /**
     * Captures a screenshot of a single WebElement and returns raw PNG bytes.
     * Does not attach to Allure or save to disk.
     *
     * @param driver  active WebDriver
     * @param element element to capture
     * @return PNG byte array
     * @throws NullPointerException if driver or element is null
     */
    public static byte[] captureElement(WebDriver driver, WebElement element) {
        validateDriver(driver);
        Objects.requireNonNull(element, "element cannot be null");
        try {
            Screenshot screenshot = new AShot().takeScreenshot(driver, element);
            return toBytes(screenshot.getImage());
        } catch (Exception e) {
            log.warn("Element screenshot failed: " + e.getMessage());
            throw new RuntimeException("Failed to capture element screenshot", e);
        }
    }

    // ── Attach Methods (Capture + Allure Attachment) ─────────────────────────

    /**
     * Captures a viewport screenshot and attaches it to the Allure report.
     *
     * @param driver active WebDriver
     * @param name   attachment label shown in the Allure report
     * @throws NullPointerException     if driver is null
     * @throws IllegalArgumentException if name is null or blank
     */
    public static void attachViewport(WebDriver driver, String name) {
        validateName(name);
        log.step("Capturing viewport screenshot: " + name);
        byte[] bytes = captureViewport(driver);
        attachBytesToAllure(bytes, name);
    }

    /**
     * Captures a full-page screenshot and attaches it to the Allure report.
     *
     * @param driver active WebDriver
     * @param name   attachment label shown in the Allure report
     * @throws NullPointerException     if driver is null
     * @throws IllegalArgumentException if name is null or blank
     */
    public static void attachFullPage(WebDriver driver, String name) {
        validateName(name);
        log.step("Capturing full-page screenshot: " + name);
        byte[] bytes = captureFullPage(driver);
        attachBytesToAllure(bytes, name);
    }

    /**
     * Captures an element screenshot and attaches it to the Allure report.
     *
     * @param driver  active WebDriver
     * @param element element to capture
     * @param name    attachment label shown in the Allure report
     * @throws NullPointerException     if driver or element is null
     * @throws IllegalArgumentException if name is null or blank
     */
    public static void attachElement(WebDriver driver, WebElement element, String name) {
        validateName(name);
        log.step("Capturing element screenshot: " + name);
        byte[] bytes = captureElement(driver, element);
        attachBytesToAllure(bytes, name);
    }

    // ── Save Methods (Capture + Save to Disk, NO Allure Attachment) ──────────

    /**
     * Captures a viewport screenshot and saves it to disk.
     * Does NOT attach to Allure report (follows Single Responsibility Principle).
     *
     * @param driver active WebDriver
     * @param dir    target directory (created automatically if absent)
     * @param name   base filename (timestamp is appended)
     * @return path of the saved file
     * @throws NullPointerException     if driver is null
     * @throws IllegalArgumentException if dir or name is null/blank
     * @throws RuntimeException         if file save fails
     */
    public static Path saveViewport(WebDriver driver, String dir, String name) {
        validateDirectory(dir);
        validateName(name);
        log.step("Saving viewport screenshot to disk: " + dir + "/" + name);
        byte[] bytes = captureViewport(driver);
        return saveBytesToFile(bytes, dir, name);
    }

    /**
     * Captures a full-page screenshot and saves it to disk.
     * Does NOT attach to Allure report (follows Single Responsibility Principle).
     *
     * @param driver active WebDriver
     * @param dir    target directory (created automatically if absent)
     * @param name   base filename (timestamp is appended)
     * @return path of the saved file
     * @throws NullPointerException     if driver is null
     * @throws IllegalArgumentException if dir or name is null/blank
     * @throws RuntimeException         if file save fails
     */
    public static Path saveFullPage(WebDriver driver, String dir, String name) {
        validateDirectory(dir);
        validateName(name);
        log.step("Saving full-page screenshot to disk: " + dir + "/" + name);
        byte[] bytes = captureFullPage(driver);
        return saveBytesToFile(bytes, dir, name + "_fullpage");
    }

    /**
     * Saves screenshot bytes to a file with timestamp.
     *
     * @param bytes screenshot bytes
     * @param dir   target directory
     * @param name  base filename
     * @return path of the saved file
     * @throws RuntimeException if file save fails
     */
    private static Path saveBytesToFile(byte[] bytes, String dir, String name) {
        try {
            Path directory = Paths.get(dir);
            Files.createDirectories(directory);

            String filename = name + "_" + LocalDateTime.now().format(TIMESTAMP_FMT) + FILE_EXTENSION;
            Path filePath = directory.resolve(filename);

            Files.write(filePath, bytes);
            log.info("Screenshot saved", filePath.toString());
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot to: " + dir, e);
        }
    }


    // ── Cleanup Utilities ─────────────────────────────────────────────────────

    /**
     * Cleans up old screenshot files from the specified directory.
     * Removes PNG files older than the specified number of days.
     *
     * @param dir     directory to clean
     * @param daysOld files older than this will be deleted
     * @throws RuntimeException if cleanup fails
     */
    public static void cleanupOldScreenshots(String dir, int daysOld) {
        validateDirectory(dir);
        if (daysOld < 0) {
            throw new IllegalArgumentException("daysOld must be non-negative");
        }

        log.info("Cleaning up screenshots older than " + daysOld + " days from: " + dir);
        try {
            Path directory = Paths.get(dir);
            if (!Files.exists(directory)) {
                log.debug("Directory does not exist, nothing to clean: " + dir);
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000);
            final long[] deletedCount = {0};

            try (var stream = Files.walk(directory)) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(FILE_EXTENSION))
                        .filter(path -> {
                            try {
                                return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                            } catch (IOException e) {
                                log.warn("Could not check file time: " + path, e);
                                return false;
                            }
                        })
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                                deletedCount[0]++;
                                log.debug("Deleted old screenshot: " + path);
                            } catch (IOException e) {
                                log.warn("Could not delete file: " + path, e);
                            }
                        });
            }

            log.info("Cleanup complete. Deleted " + deletedCount[0] + " old screenshot(s)");
        } catch (IOException e) {
            throw new RuntimeException("Failed to cleanup screenshots in: " + dir, e);
        }
    }
}

