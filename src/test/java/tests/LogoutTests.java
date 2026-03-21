package tests;

import models.login.LoginBodyModel;
import models.logout.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.requestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogOutSpec.*;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);
        String refreshToken = api.auth.loginRefreshToken(loginData);

            LogoutBodyModel logoutBody = new LogoutBodyModel(refreshToken);
            api.auth.logout(logoutBody);
    }

    @Test
    @DisplayName("Передан невалидный токен")
    public void invalidTokenLogoutTest() {
        LogoutBodyModel logoutBody = new LogoutBodyModel(BAD_TOKEN);
        InvalidLogoutTokenModel invalidToken = api.auth.logoutTokenError(logoutBody);

        step("Проверка корректности сообщения об ошибке", () -> {
            assertThat(invalidToken.detail()).isEqualTo("Token is invalid");
            assertThat(invalidToken.code()).isEqualTo("token_not_valid");
        });
    }

    @Test
    @DisplayName("Передан пустой токен")
    public void emptyTokenLogoutTest() {
        LogoutBodyModel logoutBody = new LogoutBodyModel(EMPTY_STRING);
        EmptyOrNullLogoutResponseModel emptyOrNullToken = api.auth.logoutEmptyTokenError(logoutBody);
        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Передан null токен")
    public void nullTokenLogoutTest() {

        LogoutBodyModel logoutBody = new LogoutBodyModel(NULL_STRING);
        EmptyOrNullLogoutResponseModel emptyOrNullToken = api.auth.logoutEmptyTokenError(logoutBody);

        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo(NULL_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Не передан токен")
    public void noTokenLogoutTest() {
        NoTokenLogoutBodyModel logoutBody = new NoTokenLogoutBodyModel();
        EmptyOrNullLogoutResponseModel emptyOrNullToken = api.auth.logoutNoTokenError(logoutBody);

        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }
}