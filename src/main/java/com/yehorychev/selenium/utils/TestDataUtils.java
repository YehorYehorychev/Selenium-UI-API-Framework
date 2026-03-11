package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.helpers.Logger;
import net.datafaker.Faker;

import java.util.Locale;

/**
 * Faker-based test data generators.
 * All methods are static — no instantiation needed.
 * Each call returns a freshly generated value so parallel tests never share the same data.
 * Usage:
 * String email    = TestDataUtils.randomEmail();
 * String username = TestDataUtils.randomUsername();
 * String password = TestDataUtils.randomPassword();
 * String name     = TestDataUtils.randomFullName();
 * String phone    = TestDataUtils.randomPhone();
 */
public final class TestDataUtils {

    private static final Logger log = new Logger(TestDataUtils.class);
    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private TestDataUtils() {
    }

    // ── Identity ──────────────────────────────────────────────────────────────

    /**
     * Generates a unique e-mail address suitable for registration forms.
     * Format: firstname.lastname.<random>@qa-test.com
     *
     * @return e-mail string
     */
    public static String randomEmail() {
        String local = FAKER.internet().username() + "." + FAKER.number().digits(5);
        return local + "@qa-test.com";
    }

    /**
     * Generates a username that satisfies common validation rules:
     * letters + digits, 8–16 characters, no special characters.
     *
     * @return username string
     */
    public static String randomUsername() {
        StringBuilder cleaned = new StringBuilder(
                FAKER.internet().username().replaceAll("[^a-zA-Z0-9]", ""));
        // Ensure minimum length of 6 by appending random digits if needed
        while (cleaned.length() < 6) {
            cleaned.append(FAKER.number().digits(3));
        }
        return cleaned.substring(0, Math.min(12, cleaned.length()));
    }

    /**
     * Generates a strong password satisfying most registration requirements:
     * - At least 12 characters
     * - Upper and lower case letters
     * - Digits and special characters
     *
     * @return password string
     */
    public static String randomPassword() {
        String upper = FAKER.lorem().characters(3, true, false);
        String digits = FAKER.number().digits(3);
        String special = "!@#$%";
        String lower = FAKER.lorem().characters(5, false, false);
        return upper + digits + special.charAt(FAKER.number().numberBetween(0, special.length())) + lower;
    }

    /**
     * Generates a full name (first + last).
     *
     * @return full name string
     */
    public static String randomFullName() {
        return FAKER.name().fullName();
    }

    /**
     * Generates a first name.
     *
     * @return first name string
     */
    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    /**
     * Generates a last name.
     *
     * @return last name string
     */
    public static String randomLastName() {
        return FAKER.name().lastName();
    }

    // ── Contact ───────────────────────────────────────────────────────────────

    /**
     * Generates a US-format phone number.
     *
     * @return phone number string
     */
    public static String randomPhone() {
        return FAKER.phoneNumber().cellPhone();
    }

    // ── Text ──────────────────────────────────────────────────────────────────

    /**
     * Generates a random sentence.
     *
     * @return sentence string
     */
    public static String randomSentence() {
        return FAKER.lorem().sentence();
    }

    /**
     * Generates a random paragraph.
     *
     * @return paragraph string
     */
    public static String randomParagraph() {
        return FAKER.lorem().paragraph();
    }

    /**
     * Generates a random word.
     *
     * @return word string
     */
    public static String randomWord() {
        return FAKER.lorem().word();
    }

    // ── Numbers ───────────────────────────────────────────────────────────────

    /**
     * Generates a random integer between min (inclusive) and max (exclusive).
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (exclusive)
     * @return random integer
     */
    public static int randomInt(int min, int max) {
        return FAKER.number().numberBetween(min, max);
    }

    /**
     * Generates a string of count random digits.
     *
     * @param count number of digits
     * @return digit string
     */
    public static String randomDigits(int count) {
        return FAKER.number().digits(count);
    }

    // ── Gaming-domain helpers ─────────────────────────────────────────────────

    /**
     * Generates a gamer-style username (adjective + noun + 2-digit number).
     * Example: SwiftDragon42
     *
     * @return gamer tag string
     */
    public static String randomGamerTag() {
        String adjective = FAKER.lorem().characters(4, 8, true, false);
        String noun = FAKER.lorem().characters(4, 6, true, false);
        String number = FAKER.number().digits(2);
        return adjective + noun + number;
    }

    /**
     * Generates a fake in-game summoner / player name.
     *
     * @return summoner name string
     */
    public static String randomSummonerName() {
        return FAKER.leagueOfLegends().champion()
                .replaceAll("\\s+", "") + FAKER.number().digits(3);
    }
}

