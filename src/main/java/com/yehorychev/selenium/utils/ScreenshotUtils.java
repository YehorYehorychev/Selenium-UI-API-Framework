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
 * Screenshot helpers — full-page, viewport, and element captures via AShot with Allure attachment.
 * Naming: {@code capture*} returns raw bytes, {@code attach*} attaches to Allure, {@code save*} writes to disk.
 */
public final class ScreenshotUtils {

    private static final Logger log = new Logger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final int SCREENSHOT_SCROLL_PADDING = 100;
    private static final String FILE_EXTENSION = ".png";
    private static final String ALLURE_MIME_TYPE = "image/png";

    private ScreenshotUtils() {
    }

    private static void attachBytesToAllure(byte[] bytes, String name) {
        Allure.addAttachment(name, ALLURE_MIME_TYPE, new ByteArrayInputStream(bytes), FILE_EXTENSION);
    }

    private static byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", out);
        return out.toByteArray();
    }

    private static void validateDriver(WebDriver driver) {
        Objects.requireNonNull(driver, "driver cannot be null");
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name cannot be null or empty");
    }

    private static void validateDirectory(String dir) {
        if (dir == null || dir.isBlank()) throw new IllegalArgumentException("directory path cannot be null or empty");
    }

    public static byte[] captureViewport(WebDriver driver) {
        validateDriver(driver);
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Captures a full-page screenshot; falls back to viewport if full-page fails.
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

    public static void attachViewport(WebDriver driver, String name) {
        validateName(name);
        log.step("Capturing viewport screenshot: " + name);
        attachBytesToAllure(captureViewport(driver), name);
    }

    public static void attachFullPage(WebDriver driver, String name) {
        validateName(name);
        log.step("Capturing full-page screenshot: " + name);
        attachBytesToAllure(captureFullPage(driver), name);
    }

    public static void attachElement(WebDriver driver, WebElement element, String name) {
        validateName(name);
        log.step("Capturing element screenshot: " + name);
        attachBytesToAllure(captureElement(driver, element), name);
    }

    public static Path saveViewport(WebDriver driver, String dir, String name) {
        validateDirectory(dir);
        validateName(name);
        log.step("Saving viewport screenshot to disk: " + dir + "/" + name);
        return saveBytesToFile(captureViewport(driver), dir, name);
    }

    public static Path saveFullPage(WebDriver driver, String dir, String name) {
        validateDirectory(dir);
        validateName(name);
        log.step("Saving full-page screenshot to disk: " + dir + "/" + name);
        return saveBytesToFile(captureFullPage(driver), dir, name + "_fullpage");
    }

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

    public static void cleanupOldScreenshots(String dir, int daysOld) {
        validateDirectory(dir);
        if (daysOld < 0) throw new IllegalArgumentException("daysOld must be non-negative");

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
