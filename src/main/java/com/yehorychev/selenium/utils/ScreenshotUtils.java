package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.errors.FrameworkException;
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
import java.util.stream.Stream;

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
            throw new FrameworkException("Failed to capture element screenshot", e);
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
            throw new FrameworkException("Failed to save screenshot to: " + dir, e);
        }
    }

    /**
     * Deletes {@code .png} files in {@code dir} that are older than {@code retentionDays} days.
     * Silently skips if the directory does not exist yet.
     *
     * @param dir           path to the screenshot directory (e.g. {@code "target/screenshots"})
     * @param retentionDays files older than this many days are removed
     */
    public static void cleanupOldScreenshots(String dir, int retentionDays) {
        Path directory = Paths.get(dir);
        if (!Files.exists(directory)) {
            log.debug("Screenshot directory does not exist yet — skipping cleanup: " + dir);
            return;
        }
        long cutoffMillis = System.currentTimeMillis() - (long) retentionDays * 24 * 60 * 60 * 1000;
        try (Stream<Path> files = Files.list(directory)) {
            files.filter(p -> p.toString().endsWith(FILE_EXTENSION))
                 .filter(p -> {
                     try { return Files.getLastModifiedTime(p).toMillis() < cutoffMillis; }
                     catch (IOException e) { return false; }
                 })
                 .forEach(p -> {
                     try {
                         Files.delete(p);
                         log.debug("Deleted old screenshot: " + p.getFileName());
                     } catch (IOException e) {
                         log.warn("Could not delete screenshot: " + p + " — " + e.getMessage());
                     }
                 });
        } catch (IOException e) {
            log.warn("Failed to list screenshot directory for cleanup: " + dir + " — " + e.getMessage());
        }
    }
}
