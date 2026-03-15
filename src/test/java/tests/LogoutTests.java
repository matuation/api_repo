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

public class LogoutTests extends TestBase {

    String username = "qaguru";
    String password = "qaguru123";
    String badToken = "1";
    String emptyToken = "";
    String nullToken = null;

    @Test
    @DisplayName("Успешный выход")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, password);

        String refreshToken = step("Авторизация и получение токена", () ->
                given(requestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginResponseSpec)
                        .extract().path("refresh"));

        LogoutBodyModel logoutBody = new LogoutBodyModel(refreshToken);

        step("Отправка запроса на разлогин с refershToken", () -> {
            SuccessfulLogoutResponseModel successfulLogout = given(requestSpec)
                    .body(logoutBody)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec)
                    .extract().as(SuccessfulLogoutResponseModel.class);
                    });
    }

    @Test
    @DisplayName("Передан невалидный токен")
    public void invalidTokenLogoutTest() {
        step("Отправка запроса на разлогин с неправильным refershToken", () -> {
        LogoutBodyModel logoutBody = new LogoutBodyModel(badToken);

        InvalidLogoutTokenModel invalidToken = given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract().as(InvalidLogoutTokenModel.class);

        String expectedDetailError = "Token is invalid";
        String expectedCodeError = "token_not_valid";
        String actualDetailError = invalidToken.detail();
        String actualCodeError = invalidToken.code();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
        assertThat(actualCodeError).isEqualTo(expectedCodeError);

        });
    }

    @Test
    @DisplayName("Передан пустой токен")
    public void emptyTokenLogoutTest() {

        step("Отправка запроса на разлогин с пустым refershToken", () -> {
        LogoutBodyModel logoutBody = new LogoutBodyModel(emptyToken);

        EmptyOrNullLogoutResponseModel emptyOrNullToken = given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyOrNullLogoutResponseSpec)
                .extract().as(EmptyOrNullLogoutResponseModel.class);

        String expectedRefreshError = "This field may not be blank.";
        String actualRefreshError = emptyOrNullToken.refresh().get(0);

        assertThat(actualRefreshError).isEqualTo(expectedRefreshError);
        });
    }

    @Test
    @DisplayName("Передан null токен")
    public void nullTokenLogoutTest() {
        step("Отправка запроса на разлогин с null refershToken", () -> {
        LogoutBodyModel logoutBody = new LogoutBodyModel(nullToken);

        EmptyOrNullLogoutResponseModel emptyOrNullToken = given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyOrNullLogoutResponseSpec)
                .extract().as(EmptyOrNullLogoutResponseModel.class);

        String expectedRefreshError = "This field may not be null.";
        String actualRefreshError = emptyOrNullToken.refresh().get(0);

        assertThat(actualRefreshError).isEqualTo(expectedRefreshError);
        });
    }


    @Test
    @DisplayName("Не передан токен")
    public void noTokenLogoutTest() {
        step("Отправка запроса на разлогин с без refershToken", () -> {
        NoTokenLogoutBodyModel logoutBody = new NoTokenLogoutBodyModel();

        EmptyOrNullLogoutResponseModel emptyOrNullToken = given(requestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyOrNullLogoutResponseSpec)
                .extract().as(EmptyOrNullLogoutResponseModel.class);

        String expectedRefreshError = "This field is required.";
        String actualRefreshError = emptyOrNullToken.refresh().get(0);

        assertThat(actualRefreshError).isEqualTo(expectedRefreshError);
        });
    }
}