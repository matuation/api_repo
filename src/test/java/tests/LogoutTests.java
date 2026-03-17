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

        String refreshToken = step("Авторизация и получение токена", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().path("refresh");
        });

        step("Отправка запроса на разлогин с refershToken", () -> {
            LogoutBodyModel logoutBody = new LogoutBodyModel(refreshToken);
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

        InvalidLogoutTokenModel invalidToken = step("Отправка запроса на разлогин с неправильным refershToken", () -> {
            LogoutBodyModel logoutBody = new LogoutBodyModel(badToken);
            return given(requestSpec)
                    .body(logoutBody)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(invalidLogoutResponseSpec)
                    .extract().as(InvalidLogoutTokenModel.class);
        });

        step("Проверка корректности сообщения об ошибке", () -> {
            assertThat(invalidToken.detail()).isEqualTo("Token is invalid");
            assertThat(invalidToken.code()).isEqualTo("token_not_valid");
        });
    }

    @Test
    @DisplayName("Передан пустой токен")
    public void emptyTokenLogoutTest() {

        EmptyOrNullLogoutResponseModel emptyOrNullToken = step("Отправка запроса на разлогин с пустым refershToken", () -> {
            LogoutBodyModel logoutBody = new LogoutBodyModel(emptyToken);
            return given(requestSpec)
                    .body(logoutBody)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(emptyOrNullLogoutResponseSpec)
                    .extract().as(EmptyOrNullLogoutResponseModel.class);
        });

        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo("This field may not be blank.");
        });
    }

    @Test
    @DisplayName("Передан null токен")
    public void nullTokenLogoutTest() {

        EmptyOrNullLogoutResponseModel emptyOrNullToken = step("Отправка запроса на разлогин с null refershToken", () -> {
            LogoutBodyModel logoutBody = new LogoutBodyModel(nullToken);
            return given(requestSpec)
                    .body(logoutBody)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(emptyOrNullLogoutResponseSpec)
                    .extract().as(EmptyOrNullLogoutResponseModel.class);
        });

        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo("This field may not be null.");
        });
    }

    @Test
    @DisplayName("Не передан токен")
    public void noTokenLogoutTest() {

        EmptyOrNullLogoutResponseModel emptyOrNullToken = step("Отправка запроса на разлогин с без refershToken", () -> {
            NoTokenLogoutBodyModel logoutBody = new NoTokenLogoutBodyModel();
            return given(requestSpec)
                    .body(logoutBody)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(emptyOrNullLogoutResponseSpec)
                    .extract().as(EmptyOrNullLogoutResponseModel.class);
        });

        step("Проверка корректности сообщения об ошибке", () -> {

            assertThat(emptyOrNullToken.refresh().get(0)).isEqualTo("This field is required.");
        });
    }
}