package com.yehorychev.selenium.errors;

public class TestDataException extends FrameworkException {

    public TestDataException(String field) {
        super("Required test data is missing or empty: \"" + field + "\". "
                + "Check your .env file or environment variables.");
    }

    public TestDataException(String field, Throwable cause) {
        super("Required test data is missing or empty: \"" + field + "\". "
                + "Check your .env file or environment variables.", cause);
    }
}
