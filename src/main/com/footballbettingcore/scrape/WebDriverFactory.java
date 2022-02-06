package com.footballbettingcore.scrape;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebDriverFactory {
    public final static String GECKODRIVER_PATH = System.getenv("GECKODRIVER_PATH");

    private WebDriverFactory() {}

    public static WebDriver getFirefoxDriver() {
        System.setProperty("webdriver.gecko.driver", GECKODRIVER_PATH);
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--start-maximized");
        return new FirefoxDriver(options);
    }
}
