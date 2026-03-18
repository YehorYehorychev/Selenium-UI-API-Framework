package com.yehorychev.selenium.utils;

import com.yehorychev.selenium.helpers.Logger;
import net.datafaker.Faker;

import java.util.Locale;

/**
 * Faker-based test data generators. All methods are static and return a fresh value each call.
 */
public final class TestDataUtils {

    private static final Logger log = new Logger(TestDataUtils.class);
    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private TestDataUtils() {
    }

    public static String randomEmail() {
        String local = FAKER.internet().username() + "." + FAKER.number().digits(5);
        return local + "@qa-test.com";
    }

    public static String randomUsername() {
        StringBuilder cleaned = new StringBuilder(
                FAKER.internet().username().replaceAll("[^a-zA-Z0-9]", ""));
        while (cleaned.length() < 6) {
            cleaned.append(FAKER.number().digits(3));
        }
        return cleaned.substring(0, Math.min(12, cleaned.length()));
    }

    public static String randomPassword() {
        String upper = FAKER.lorem().characters(3, true, false);
        String digits = FAKER.number().digits(3);
        String special = "!@#$%";
        String lower = FAKER.lorem().characters(5, false, false);
        return upper + digits + special.charAt(FAKER.number().numberBetween(0, special.length())) + lower;
    }

    public static String randomFullName() {
        return FAKER.name().fullName();
    }

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    public static String randomLastName() {
        return FAKER.name().lastName();
    }

    public static String randomPhone() {
        return FAKER.phoneNumber().cellPhone();
    }

    public static String randomSentence() {
        return FAKER.lorem().sentence();
    }

    public static String randomParagraph() {
        return FAKER.lorem().paragraph();
    }

    public static String randomWord() {
        return FAKER.lorem().word();
    }

    public static int randomInt(int min, int max) {
        return FAKER.number().numberBetween(min, max);
    }

    public static String randomDigits(int count) {
        return FAKER.number().digits(count);
    }

    public static String randomGamerTag() {
        String adjective = FAKER.lorem().characters(4, 8, true, false);
        String noun = FAKER.lorem().characters(4, 6, true, false);
        String number = FAKER.number().digits(2);
        return adjective + noun + number;
    }

    public static String randomSummonerName() {
        return FAKER.leagueOfLegends().champion()
                .replaceAll("\\s+", "") + FAKER.number().digits(3);
    }
}
