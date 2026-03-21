package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTests extends TestBase {


    @Test
    @DisplayName("Успешный логин")
    public void successfulLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        step("Проверка корректности выданных токенов", () -> {

            assertThat(loginResponse.access()).startsWith(EXPECTED_TOKEN_PATH);
            assertThat(loginResponse.refresh()).startsWith(EXPECTED_TOKEN_PATH);
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });
    }

    @Test
    @DisplayName("Неправильный пароль")
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(USERNAME, WRONG_PASSWORD);
        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongCredentials(loginData);

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Неправильный логин")
    public void wrongCredentialsPasswordTest() {
        LoginBodyModel loginData = new LoginBodyModel(USERNAME, WRONG_PASSWORD);
        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongCredentials(loginData);

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Неправильный логин и пароль")
    public void wrongCredentialsNameAndPasswordTest() {
        LoginBodyModel loginData = new LoginBodyModel(WRONG_USERNAME, WRONG_PASSWORD);
        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongCredentials(loginData);

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Логин с неверным форматом данных")
    public void wrongCredentialsWrongFormatTest() {
        WrongDataFormatLoginBodyModel loginData = new WrongDataFormatLoginBodyModel(WRONG_FORMAT, WRONG_FORMAT);
        WrongCredentialsLoginResponseModel loginResponse = api.auth.wrongFormatCredentials(loginData);

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.detail()).isEqualTo(WRONG_CREDENTIALS_ERROR);
        });
    }

    @Test
    @DisplayName("Логин с пустыми строками")
    public void wrongCredentialsEmptyStringTest() {
        LoginBodyModel loginData = new LoginBodyModel(EMPTY_STRING, EMPTY_STRING);
        EmptyCredentialsLoginResponseModel loginResponse = api.auth.emptyCredentials(loginData);

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.username().get(0)).isEqualTo(BLANK_FIELD_ERROR);
            assertThat(loginResponse.password().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }


    @Test
    @DisplayName("Логин с null параметрами")
    public void wrongCredentialsNullTest() {
        LoginBodyModel loginData = new LoginBodyModel(NULL_STRING, NULL_STRING);
        EmptyCredentialsLoginResponseModel loginResponse = api.auth.emptyCredentials(loginData);

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.username().get(0)).isEqualTo(NULL_FIELD_ERROR);
            assertThat(loginResponse.password().get(0)).isEqualTo(NULL_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Логин без параметров")
    public void noCredentialsNullTest() {
        NoCredentialsLoginResponseModel loginData = new NoCredentialsLoginResponseModel();
        EmptyCredentialsLoginResponseModel loginResponse = api.auth.noCredentials(loginData);

        step("Проверка отображения ожидаемой ошибки", () -> {

            assertThat(loginResponse.username().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(loginResponse.password().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }
}