package api;

import io.qameta.allure.Step;
import models.login.*;
import models.logout.*;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.*;
import static specs.logout.LogOutSpec.*;

public class AuthApiClient {

    @Step("Ввод правильного логина и правильного пароля")
    public SuccessfulLoginResponseModel login (LoginBodyModel loginData) {
        return given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);
    }

    @Step("Ввод правильного логина и правильного пароля и получение refresh токена")
    public String loginRefreshToken (LoginBodyModel loginData) {
        return given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("refresh");
    }
    @Step("Ввод правильного логина и правильного пароля и получение access токена")
    public String loginAccessToken (LoginBodyModel loginData) {
        return given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("access");
    }

    @Step("Ввод неправильных авторизационных данных")
    public WrongCredentialsLoginResponseModel wrongCredentials(LoginBodyModel loginData) {
        return given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Ввод авторизационных данных неправильного формата")
    public WrongCredentialsLoginResponseModel wrongFormatCredentials (WrongDataFormatLoginBodyModel loginData) {
        return given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Ввод пустых авторизационных данных")
    public EmptyCredentialsLoginResponseModel emptyCredentials (LoginBodyModel loginData) {
        return given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyCredentialsLoginResponseSpec)
                .extract().as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Авторизация без параметров")
    public EmptyCredentialsLoginResponseModel noCredentials (NoCredentialsLoginResponseModel loginData) {
        return given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyCredentialsLoginResponseSpec)
                .extract().as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Разлогин пользователя")
    public void logout (LogoutBodyModel logoutBody) {
        given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);

    }

    @Step("Разлогин пользователя с неправильным токеном")
    public InvalidLogoutTokenModel logoutTokenError (LogoutBodyModel logoutBody) {
        return given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract().as(InvalidLogoutTokenModel.class);
    }

    @Step("Разлогин пользователя с пустым токеном")
    public EmptyOrNullLogoutResponseModel logoutEmptyTokenError (LogoutBodyModel logoutBody) {
        return given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyOrNullLogoutResponseSpec)
                .extract().as(EmptyOrNullLogoutResponseModel.class);
    }

    @Step("Отправка запроса без токена")
    public EmptyOrNullLogoutResponseModel logoutNoTokenError (NoTokenLogoutBodyModel logoutBody) {
        return given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyOrNullLogoutResponseSpec)
                .extract().as(EmptyOrNullLogoutResponseModel.class);
    }
}
