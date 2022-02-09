package com.footballbettingcore.scrape;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebDriverFactory {
    public final static String GECKODRIVER_PATH = System.getenv("GECKODRIVER_PATH");
    public final static String CHROMEDRIVER_PATH = System.getenv("CHROMEDRIVER_PATH");

    private WebDriverFactory() {}

    public static WebDriver getChromeDriver() {
        System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized"); // open Browser in maximized mode
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems for linux
        options.addArguments("--no-sandbox"); // Bypass OS security model
        return new ChromeDriver(options);
    }

    public static WebDriver getFirefoxDriver() {
        System.setProperty("webdriver.gecko.driver", GECKODRIVER_PATH);
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--start-maximized");
        return new FirefoxDriver(options);
    }

    public static WebDriver getEdgeDriver() {
        System.setProperty("webdriver.edge.driver", "/home/peter/Documents/personalProjects/footballBettingCore/target/msedgedriver");
        EdgeOptions options = new EdgeOptions();
//        options.addArguments("--headless");
//        options.setCapability("UseChromium", false);
//        options.addArguments("--start-maximized");
        return new EdgeDriver(options);
    }
}
