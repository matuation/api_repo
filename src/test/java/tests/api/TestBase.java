package tests.api;

import allure.Attachments;
import api.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.WebConfig;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    private static final WebConfig config = ConfigFactory.create(WebConfig.class, System.getProperties());
    
    public static String browser = System.getProperty("browser", "chrome");
    public static String remoteBaseUsername = System.getProperty("remoteBaseUsername");
    public static String remoteBasePass = System.getProperty("remoteBasePass");


    protected static final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUp() {
        String remoteBaseUrl = System.getProperty("remoteBaseUrl");
        RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";
        if (config.isRemote()) {
            Configuration.remote = "https://" + System.getProperty("remoteBaseUsername") + ":" + System.getProperty("remoteBasePass") + "@" + config.remoteUrl();
        }
        Configuration.baseUrl = "https://book-club.qa.guru";
        Configuration.browser = browser;
        Configuration.browserVersion = System.getProperty("browserVersion", "113.0");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.pageLoadStrategy = System.getProperty("pageLoadStrat", "eager");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.<String, Object>of("enableVNC", true, "enableVideo", true, "videoName", "test_video.mp4"));
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
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            closeWebDriver();
        }
    }
}