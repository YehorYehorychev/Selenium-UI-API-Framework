package com.yehorychev.selenium.errors;

/**
 * Thrown when required test data or environment variables are missing or empty.
 *
 * Example:
 *   String email = System.getenv("USER_EMAIL");
 *   if (email == null) throw new TestDataException("USER_EMAIL");
 */
public class TestDataException extends FrameworkException {

    /**
     * @param field the missing environment variable or test-data field name
     */
    public TestDataException(String field) {
        super("Required test data is missing or empty: \"" + field + "\". "
                + "Check your .env file or environment variables.");
    }

    /**
     * @param field   the missing field name
     * @param cause   underlying exception
     */
    public TestDataException(String field, Throwable cause) {
        super("Required test data is missing or empty: \"" + field + "\". "
                + "Check your .env file or environment variables.", cause);
    }
}

