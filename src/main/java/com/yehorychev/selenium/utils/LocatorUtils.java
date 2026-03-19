package com.yehorychev.selenium.utils;

import org.openqa.selenium.By;

import java.util.Arrays;

/**
 * Locator helpers for common case-insensitive XPath patterns.
 */
public final class LocatorUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

    private LocatorUtils() {
    }

    public static By h2ContainsText(String text) {
        String needle = text.toLowerCase();
        String xpath = String.format("//h2[contains(translate(normalize-space(.),'%s','%s'),'%s')]",
                UPPER, LOWER, needle);
        return By.xpath(xpath);
    }

    public static By h2ContainsAny(String... texts) {
        String[] lowered = Arrays.stream(texts).map(String::toLowerCase).toArray(String[]::new);
        String conditions = Arrays.stream(lowered)
                .map(t -> String.format("contains(translate(normalize-space(.),'%s','%s'),'%s')", UPPER, LOWER, t))
                .reduce((a, b) -> a + " or " + b)
                .orElse("false()");
        return By.xpath("//h2[" + conditions + "]");
    }

    public static By buttonSpanEquals(String text) {
        String needle = text.toLowerCase();
        String xpath = String.format(
                "//button[.//span[translate(normalize-space(text()),'%s','%s')='%s']]",
                UPPER, LOWER, needle);
        return By.xpath(xpath);
    }
}

