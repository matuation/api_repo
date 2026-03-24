package tests;

import models.login.LoginBodyModel;
import models.logout.EmptyOrNullLogoutResponseModel;
import models.logout.InvalidLogoutTokenModel;
import models.logout.LogoutBodyModel;
import models.logout.NoTokenLogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход")
    public void successfulLogoutTest() {

        String refreshToken = api.auth.loginRefreshToken(new LoginBodyModel(USERNAME, PASSWORD));

        LogoutBodyModel logoutBody = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutBody);
    }

    @Test
    @DisplayName("Передан невалидный токен")
    public void invalidTokenLogoutTest() {

        InvalidLogoutTokenModel invalidToken = api.auth.logoutTokenError(new LogoutBodyModel(BAD_TOKEN));

        step("Проверка корректности сообщения об ошибке", () -> {
            assertThat(invalidToken.detail()).isEqualTo("Token is invalid");
            assertThat(invalidToken.code()).isEqualTo("token_not_valid");
        });
    }

    @Test
    @DisplayName("Передан пустой токен")
    public void emptyTokenLogoutTest() {

        EmptyOrNullLogoutResponseModel emptyOrNullToken = api.auth.logoutEmptyTokenError(new LogoutBodyModel(EMPTY_STRING));
        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Передан null токен")
    public void nullTokenLogoutTest() {

        EmptyOrNullLogoutResponseModel emptyOrNullToken = api.auth.logoutEmptyTokenError(new LogoutBodyModel(NULL_STRING));

        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo(NULL_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Не передан токен")
    public void noTokenLogoutTest() {

        EmptyOrNullLogoutResponseModel emptyOrNullToken = api.auth.logoutNoTokenError(new NoTokenLogoutBodyModel());

        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }
}