package tests.mobile;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import tests.mobile.drivers.LocalDriver;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;


public class TestBase {
    @BeforeAll
    static void beforeAll() {
        Configuration.browser = LocalDriver.class.getName();
        Configuration.browserSize = null;
    }


    @BeforeEach
    void beforeEach() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(false) // Отключаем автоматические веб-скриншоты
                .savePageSource(false));
        open();
    }

    @AfterEach
    void addAttachments() {
        tests.mobile.helpers.Attach.screenshotAs("Last screenshot");
        tests.mobile.helpers.Attach.pageSource();
        closeWebDriver();
    }
}