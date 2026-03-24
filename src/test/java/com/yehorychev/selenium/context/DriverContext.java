package com.yehorychev.selenium.context;

import com.yehorychev.selenium.driver.DriverManager;
import com.yehorychev.selenium.helpers.Logger;
import org.openqa.selenium.WebDriver;

/**
 * WebDriver lifecycle and access layer — injected via PicoContainer per scenario.
 */
public class DriverContext {

    private static final Logger log = new Logger(DriverContext.class);

    public void setUp() {
        log.step("Setting up DriverContext");
        DriverManager.initDriver();
    }

    public void setUp(String browser) {
        log.step("Setting up DriverContext with browser: " + browser);
        DriverManager.initDriver(browser);
    }

    public void tearDown() {
        log.step("Tearing down DriverContext");
        DriverManager.quitDriver();
    }

    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }


    public boolean isReady() {
        return DriverManager.isDriverInitialised();
    }
}
