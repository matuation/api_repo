package tests.api;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.api.TestData.*;

public class LoginTests extends TestBase {


    @Test
    @DisplayName("Успешный логин")
    public void successfulLoginTest() {

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(USERNAME, PASSWORD));

        step("Проверка корректности выданных токенов", () -> {

            assertThat(loginResponse.access()).startsWith(EXPECTED_TOKEN_PATH);
            assertThat(loginResponse.refresh()).startsWith(EXPECTED_TOKEN_PATH);
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });
    }

    @Test
    @DisplayName("Неправильный пароль")
    public void wrongCredentialsLoginTest() {

        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongCredentials(new LoginBodyModel(USERNAME, WRONG_PASSWORD));

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Неправильный логин")
    public void wrongCredentialsPasswordTest() {

        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongCredentials(new LoginBodyModel(USERNAME, WRONG_PASSWORD));

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Неправильный логин и пароль")
    public void wrongCredentialsNameAndPasswordTest() {

        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongCredentials(new LoginBodyModel(WRONG_USERNAME, WRONG_PASSWORD));

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Логин с неверным форматом данных")
    public void wrongCredentialsWrongFormatTest() {

        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongFormatCredentials(new WrongDataFormatLoginBodyModel(WRONG_FORMAT, WRONG_FORMAT));

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Логин с пустыми строками")
    public void wrongCredentialsEmptyStringTest() {

        EmptyCredentialsLoginResponseModel loginResponse = api.auth.emptyCredentials(new LoginBodyModel(EMPTY_STRING, EMPTY_STRING));

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.username().get(0)).isEqualTo(BLANK_FIELD_ERROR);
            assertThat(loginResponse.password().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }


    @Test
    @DisplayName("Логин с null параметрами")
    public void wrongCredentialsNullTest() {

        EmptyCredentialsLoginResponseModel loginResponse = api.auth.emptyCredentials(new LoginBodyModel(NULL_STRING, NULL_STRING));

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.username().get(0)).isEqualTo(NULL_FIELD_ERROR);
            assertThat(loginResponse.password().get(0)).isEqualTo(NULL_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Логин без параметров")
    public void noCredentialsNullTest() {

        EmptyCredentialsLoginResponseModel loginResponse = api.auth.noCredentials(new NoCredentialsLoginResponseModel());

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.username().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(loginResponse.password().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }
}