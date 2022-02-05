package com.footballbettingcore.scrape;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverFactory {
    public final static String CHROMEDRIVER_PATH = System.getenv("CHROMEDRIVER_PATH");

    private ChromeDriverFactory() {}

    public static WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        return new ChromeDriver(options);
    }
}
