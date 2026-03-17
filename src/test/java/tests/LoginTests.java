package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;

public class LoginTests extends TestBase {

    String username = "qaguru";
    String wrongUsername = "qaruru";
    String password = "qaguru123";
    String wrongPassword = "qaguru1234";
    String emptyString = "";
    String nullString = null;
    double wrongFormat = 0.0;

    @Test
    @DisplayName("Успешный логин")
    public void successfulLoginTest() {

        SuccessfulLoginResponseModel loginResponse = step("Ввод правильного логина и правильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().as(SuccessfulLoginResponseModel.class);
        });

        step("Проверка корректности выданных токенов", () -> {
            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Неправильный пароль")
    public void wrongCredentialsLoginTest() {

        WrongCredentialsLoginResponseModel loginResponse = step("Ввод правильного логина и неправильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);
        });

        step("Проверка отображения ожидаемой ошибки", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Неправильный логин")
    public void wrongCredentialsPasswordTest() {

        WrongCredentialsLoginResponseModel loginResponse = step("Ввод неправильного логина и правильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(wrongUsername, password);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);
        });

        step("Проверка отображения ожидаемой ошибки", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Неправильный логин и пароль")
    public void wrongCredentialsNameAndPasswordTest() {

        WrongCredentialsLoginResponseModel loginResponse = step("Ввод неправильного логина и неправильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(wrongUsername, wrongPassword);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);
        });

        step("Проверка отображения ожидаемой ошибки", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Логин с неверным форматом данных")
    public void wrongCredentialsWrongFormatTest() {

        WrongCredentialsLoginResponseModel loginResponse = step("Ввод логина и пароля неверного формата", () -> {
            WrongDataFormatLoginBodyModel loginData = new WrongDataFormatLoginBodyModel(wrongFormat, wrongFormat);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);
        });

        step("Проверка отображения ожидаемой ошибки", () -> {
            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Логин с пустыми строками")
    public void wrongCredentialsEmptyStringTest() {

        EmptyCredentialsLoginResponseModel loginResponse = step("Ввод логина и пароля с пустыми строками", () -> {
            LoginBodyModel loginData = new LoginBodyModel(emptyString, emptyString);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyCredentialsLoginResponseSpec)
                    .extract().as(EmptyCredentialsLoginResponseModel.class);
        });

        step("Проверка отображения ожидаемой ошибки", () -> {
            String expectedCredentialsError = "This field may not be blank.";
            String actualUsernameError = loginResponse.username().get(0);
            String actualPasswordError = loginResponse.password().get(0);

            assertThat(actualUsernameError).isEqualTo(expectedCredentialsError);
            assertThat(actualPasswordError).isEqualTo(expectedCredentialsError);
        });
    }


    @Test
    @DisplayName("Логин с null параметрами")
    public void wrongCredentialsNullTest() {

        EmptyCredentialsLoginResponseModel loginResponse = step("Ввод логина и пароля с null строками", () -> {
            LoginBodyModel loginData = new LoginBodyModel(nullString, nullString);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyCredentialsLoginResponseSpec)
                    .extract().as(EmptyCredentialsLoginResponseModel.class);
        });

        step("Проверка отображения ожидаемой ошибки", () -> {
            String expectedCredentialsError = "This field may not be null.";
            String actualUsernameError = loginResponse.username().get(0);
            String actualPasswordError = loginResponse.password().get(0);

            assertThat(actualUsernameError).isEqualTo(expectedCredentialsError);
            assertThat(actualPasswordError).isEqualTo(expectedCredentialsError);
        });
    }

    @Test
    @DisplayName("Логин без параметров")
    public void noCredentialsNullTest() {

        EmptyCredentialsLoginResponseModel loginResponse = step("Попытка авторизации без логина и пароля", () -> {
            NoCredentialsLoginResponseModel loginData = new NoCredentialsLoginResponseModel();
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyCredentialsLoginResponseSpec)
                    .extract().as(EmptyCredentialsLoginResponseModel.class);
        });

        step("Проверка отображения ожидаемой ошибки", () -> {
            String expectedCredentialsError = "This field is required.";
            String actualUsernameError = loginResponse.username().get(0);
            String actualPasswordError = loginResponse.password().get(0);

            assertThat(actualUsernameError).isEqualTo(expectedCredentialsError);
            assertThat(actualPasswordError).isEqualTo(expectedCredentialsError);
        });
    }
}