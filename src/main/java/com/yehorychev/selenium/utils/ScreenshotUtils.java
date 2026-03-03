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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot helpers — full-page, viewport and element-level captures via AShot,
 * with automatic Allure report attachment.
 *
 * <p>All methods are static — no instantiation needed.
 *
 * <p>Usage:
 * <pre>{@code
 *   // Attach a viewport screenshot to Allure
 *   ScreenshotUtils.attachToAllure(driver, "Login page");
 *
 *   // Capture a full-page screenshot (scrolling) and attach to Allure
 *   ScreenshotUtils.attachFullPageToAllure(driver, "Full home page");
 *
 *   // Capture only a specific element
 *   ScreenshotUtils.attachElementToAllure(driver, element, "Submit button");
 *
 *   // Save to disk (e.g. for CI artefacts)
 *   Path file = ScreenshotUtils.saveToFile(driver, "target/screenshots", "checkout");
 * }</pre>
 */
public final class ScreenshotUtils {

    private static final Logger log = new Logger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {}

    // ── Allure attachments ────────────────────────────────────────────────────

    /**
     * Takes a viewport screenshot and attaches it to the current Allure step.
     *
     * @param driver active WebDriver
     * @param name   attachment label shown in the Allure report
     */
    public static void attachToAllure(WebDriver driver, String name) {
        log.step("Capturing screenshot: " + name);
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
    }

    /**
     * Takes a full-page screenshot (scrolls the entire page via AShot) and
     * attaches it to the current Allure step.
     *
     * @param driver active WebDriver
     * @param name   attachment label shown in the Allure report
     */
    public static void attachFullPageToAllure(WebDriver driver, String name) {
        log.step("Capturing full-page screenshot: " + name);
        try {
            Screenshot screenshot = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(driver);
            byte[] bytes = toBytes(screenshot.getImage());
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            log.warn("Full-page screenshot failed, falling back to viewport: " + e.getMessage());
            attachToAllure(driver, name + " (viewport fallback)");
        }
    }

    /**
     * Takes a screenshot of a single {@link WebElement} and attaches it to the
     * current Allure step.
     *
     * @param driver  active WebDriver
     * @param element element to capture
     * @param name    attachment label shown in the Allure report
     */
    public static void attachElementToAllure(WebDriver driver, WebElement element, String name) {
        log.step("Capturing element screenshot: " + name);
        try {
            Screenshot screenshot = new AShot()
                    .takeScreenshot(driver, element);
            byte[] bytes = toBytes(screenshot.getImage());
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            log.warn("Element screenshot failed: " + e.getMessage());
        }
    }

    // ── Disk saves ────────────────────────────────────────────────────────────

    /**
     * Saves a viewport screenshot to {@code <dir>/<name>_<timestamp>.png} and
     * also attaches it to the current Allure step.
     *
     * @param driver active WebDriver
     * @param dir    target directory (created automatically if absent)
     * @param name   base filename (timestamp is appended)
     * @return path of the saved file
     */
    public static Path saveToFile(WebDriver driver, String dir, String name) {
        log.step("Saving screenshot to disk: " + dir + "/" + name);
        try {
            Path directory = Paths.get(dir);
            Files.createDirectories(directory);

            String filename = name + "_" + LocalDateTime.now().format(TIMESTAMP_FMT) + ".png";
            Path filePath   = directory.resolve(filename);

            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(filePath, bytes);

            // Also attach to Allure
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
            log.info("Screenshot saved", filePath.toString());
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot to: " + dir, e);
        }
    }

    /**
     * Saves a full-page AShot screenshot to disk.
     *
     * @param driver active WebDriver
     * @param dir    target directory
     * @param name   base filename
     * @return path of the saved file
     */
    public static Path saveFullPageToFile(WebDriver driver, String dir, String name) {
        log.step("Saving full-page screenshot to disk: " + dir + "/" + name);
        try {
            Path directory = Paths.get(dir);
            Files.createDirectories(directory);

            String filename = name + "_fullpage_" + LocalDateTime.now().format(TIMESTAMP_FMT) + ".png";
            Path filePath   = directory.resolve(filename);

            Screenshot screenshot = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(driver);

            ImageIO.write(screenshot.getImage(), "PNG", filePath.toFile());

            // Also attach to Allure
            byte[] bytes = toBytes(screenshot.getImage());
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
            log.info("Full-page screenshot saved", filePath.toString());
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save full-page screenshot to: " + dir, e);
        }
    }

    // ── Raw bytes helper ──────────────────────────────────────────────────────

    /**
     * Returns the raw PNG bytes of a viewport screenshot without saving or attaching.
     *
     * @param driver active WebDriver
     * @return PNG byte array
     */
    public static byte[] takeBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Returns an {@link InputStream} wrapping the PNG bytes of a viewport screenshot.
     *
     * @param driver active WebDriver
     * @return PNG input stream
     */
    public static InputStream takeAsStream(WebDriver driver) {
        return new ByteArrayInputStream(takeBytes(driver));
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private static byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", out);
        return out.toByteArray();
    }
}

