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
        step("Ввод правильного логина и правильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);

            SuccessfulLoginResponseModel loginResponse = given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().as(SuccessfulLoginResponseModel.class);

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
        step("Ввод правильного логина и неправильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);

            WrongCredentialsLoginResponseModel loginResponse = given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Неправильный логин")
    public void wrongCredentialsPasswordTest() {
        step("Ввод неправильного логина и правильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(wrongUsername, password);

            WrongCredentialsLoginResponseModel loginResponse = given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Неправильный логин и пароль")
    public void wrongCredentialsNameAndPasswordTest() {
        step("Ввод неправильного логина и неправильного пароля", () -> {
            LoginBodyModel loginData = new LoginBodyModel(wrongUsername, wrongPassword);

            WrongCredentialsLoginResponseModel loginResponse = given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongCredentialsLoginResponseSpec)
                    .extract().as(WrongCredentialsLoginResponseModel.class);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Логин с неверным форматом данных")
    public void wrongCredentialsWrongFormatTest() {
        step("Ввод логина и пароля неверного формата", () -> {
        WrongDataFormatLoginBodyModel loginData = new WrongDataFormatLoginBodyModel(wrongFormat, wrongFormat);

        WrongCredentialsLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);

        String expectedDetailError = "Invalid username or password.";
        String actualDetailError = loginResponse.detail();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Логин с пустыми строками")
    public void wrongCredentialsEmptyStringTest() {
        step("Ввод логина и пароля с пустыми строками", () -> {
        LoginBodyModel loginData = new LoginBodyModel(emptyString, emptyString);

        EmptyCredentialsLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyCredentialsLoginResponseSpec)
                .extract().as(EmptyCredentialsLoginResponseModel.class);

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
        step("Ввод логина и пароля с null строками", () -> {
        LoginBodyModel loginData = new LoginBodyModel(nullString, nullString);

        EmptyCredentialsLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyCredentialsLoginResponseSpec)
                .extract().as(EmptyCredentialsLoginResponseModel.class);

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
        step("Попытка авторизации без логина и пароля", () -> {
        NoCredentialsLoginResponseModel loginData = new NoCredentialsLoginResponseModel();

        EmptyCredentialsLoginResponseModel loginResponse = given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyCredentialsLoginResponseSpec)
                .extract().as(EmptyCredentialsLoginResponseModel.class);

        String expectedCredentialsError = "This field is required.";
        String actualUsernameError = loginResponse.username().get(0);
        String actualPasswordError = loginResponse.password().get(0);

        assertThat(actualUsernameError).isEqualTo(expectedCredentialsError);
        assertThat(actualPasswordError).isEqualTo(expectedCredentialsError);
        });
    }
}