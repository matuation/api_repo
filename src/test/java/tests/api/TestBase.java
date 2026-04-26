package tests.api;

import allure.Attachments;
import api.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    public static String browser = System.getProperty("browser", "chrome");
    public static String remoteBaseUsername = System.getProperty("remoteBaseUsername");
    public static String remoteBasePass = System.getProperty("remoteBasePass");
    public static String remoteBaseUrl = System.getProperty("remoteBaseUrl");

    protected static final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUp() {

        RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";
        if (remoteBaseUrl != null) {
            Configuration.remote = "http://185.154.53.106";
        }
        Configuration.baseUrl = "https://book-club.qa.guru";
        Configuration.browser = browser;
        Configuration.browserVersion = System.getProperty("103.0");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.pageLoadStrategy = System.getProperty("pageLoadStrat", "eager");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.<String, Object>of("enableVNC", true, "enableVideo", true));
        Configuration.browserCapabilities = capabilities;
    }

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterEach
    void addAttachments() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Attachments.screenshotAs("Скрин");
            Attachments.pageSource();
            Attachments.browserConsoleLogs();
            Attachments.addVideo();
            closeWebDriver();
        }
    }
}