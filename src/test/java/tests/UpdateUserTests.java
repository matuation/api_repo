package tests;

import io.restassured.path.json.JsonPath;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.update.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.requestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.update.UpdateUserSpec.*;

public class UpdateUserTests extends TestBase {

    String username;
    String password;
    String forbiddenUsername;
    String forbiddenExceededUsername;
    String exceededLengthUsername;
    String forbiddenExceededEmail;
    String firstName;
    String lastName;
    String email;
    String forbiddenEmail;
    String emptyUsername = "";
    String emptyFirstName = "";
    String emptyLastName = "";
    String emptyEmail = "";


    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
        forbiddenUsername = faker.regexify("[\\!=]{5}");
        forbiddenExceededUsername = faker.regexify("[\\!.=]{151}");
        exceededLengthUsername = faker.regexify("[\\w.@+-]{151}");
        forbiddenExceededEmail = faker.regexify("[\\w.@+-]{255}");
        forbiddenEmail = faker.regexify("[\\!.=+-]{5}");

    }

    @Test
    @DisplayName("Успешная замена данных методом PUT")
    public void successfulUpdatePutTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> {
            PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(username, firstName, lastName, email);
            return given(requestSpec)
                    .body(putUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(successfulPutUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности обновления данных", () -> {
            assertThat(putUpdateResponse.getInt("id")).isEqualTo(registrationResponse.getInt("id"));
            assertThat(putUpdateResponse.getString("username")).isEqualTo(username);
            assertThat(putUpdateResponse.getString("firstName")).isEqualTo(firstName);
            assertThat(putUpdateResponse.getString("lastName")).isEqualTo(lastName);
            assertThat(putUpdateResponse.getString("email")).isEqualTo(email);
            assertThat(putUpdateResponse.getString("remoteAddr")).isEqualTo(registrationResponse.getString("remoteAddr"));
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - превышен лимит и некорректный Username")
    public void wrongExceedUpdatePutTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> {
            PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(forbiddenUsername, exceededLengthUsername,
                    exceededLengthUsername, forbiddenExceededEmail);
            return given(requestSpec)
                    .body(putUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(wrongOrNoFieldsPutUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.getList("username", String.class).get(0)).isEqualTo("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.");
            assertThat(putUpdateResponse.getList("firstName", String.class).get(0)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(putUpdateResponse.getList("lastName", String.class).get(0)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(putUpdateResponse.getList("email", String.class).get(0)).isEqualTo("Ensure this field has no more than 254 characters.");
            assertThat(putUpdateResponse.getList("email", String.class).get(1)).isEqualTo("Enter a valid email address.");
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - превышен лимит и некорректный Email")
    public void wrongEmailFormatUpdatePutTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> {
            PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(forbiddenUsername, exceededLengthUsername,
                    exceededLengthUsername, forbiddenEmail);
            return given(requestSpec)
                    .body(putUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(wrongOrNoFieldsPutUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.getList("username", String.class).get(0)).isEqualTo("Enter a valid username. " +
                    "This value may contain only letters, numbers, and @/./+/-/_ characters.");
            assertThat(putUpdateResponse.getList("firstName", String.class).get(0)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(putUpdateResponse.getList("lastName", String.class).get(0)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(putUpdateResponse.getList("email", String.class).get(0)).isEqualTo("Enter a valid email address.");
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - поля не переданы в тело")
    public void noFieldsProvidedUpdatePutTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);

            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> {
            EmptyPutUpdateBodyModel putUpdateBody = new EmptyPutUpdateBodyModel();
            return given(requestSpec)
                    .body(putUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(wrongOrNoFieldsPutUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.getList("username", String.class).get(0)).isEqualTo("This field is required.");
            assertThat(putUpdateResponse.getList("firstName", String.class).get(0)).isEqualTo("This field is required.");
            assertThat(putUpdateResponse.getList("lastName", String.class).get(0)).isEqualTo("This field is required.");
            assertThat(putUpdateResponse.getList("email", String.class).get(0)).isEqualTo("This field is required.");
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - пустые поля")
    public void emptyFieldsProvidedUpdatePutTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> {
            PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(emptyUsername, emptyFirstName,
                    emptyLastName, emptyEmail);
            return given(requestSpec)
                    .body(putUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(emptyFieldsPutUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.getList("username", String.class).get(0)).isEqualTo("This field may not be blank.");
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - передан только Username")
    public void onlyUsernameUpdatePutTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> {
            OnlyUsernamePutUpdateBodyModel putUpdateBody = new OnlyUsernamePutUpdateBodyModel(username);
            return given(requestSpec)
                    .body(putUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(onlyUsernamePutUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.getList("firstName", String.class).get(0)).isEqualTo("This field is required.");
            assertThat(putUpdateResponse.getList("lastName", String.class).get(0)).isEqualTo("This field is required.");
            assertThat(putUpdateResponse.getList("email", String.class).get(0)).isEqualTo("This field is required.");
        });
    }

    @Test
    @DisplayName("Успешная замена всех данных методом PATCH")
    public void successfulAllFieldsUpdatePatchTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(username, firstName, lastName, email);
            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulPatchUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.getInt("id")).isEqualTo(registrationResponse.getInt("id"));
            assertThat(patchUpdateResponse.getString("username")).isEqualTo(username);
            assertThat(patchUpdateResponse.getString("firstName")).isEqualTo(firstName);
            assertThat(patchUpdateResponse.getString("lastName")).isEqualTo(lastName);
            assertThat(patchUpdateResponse.getString("email")).isEqualTo(email);
            assertThat(patchUpdateResponse.getString("remoteAddr")).isEqualTo(registrationResponse.getString("remoteAddr"));
        });
    }

    @Test
    @DisplayName("Успешная замена только Username методом PATCH")
    public void onlyUsernameUpdatePatchTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            OnlyUsernamePatchUpdateBodyModel patchUpdateBody = new OnlyUsernamePatchUpdateBodyModel(username);

            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulOneFieldPatchUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.getInt("id")).isEqualTo(registrationResponse.getInt("id"));
            assertThat(patchUpdateResponse.getString("username")).isEqualTo(username);
            assertThat(patchUpdateResponse.getString("firstName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("lastName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("email")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("remoteAddr")).isEqualTo(registrationResponse.getString("remoteAddr"));
        });
    }

    @Test
    @DisplayName("Успешная замена только FirstName методом PATCH")
    public void onlyFirstNameUpdatePatchTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });


        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            OnlyFirstNamePatchUpdateBodyModel patchUpdateBody = new OnlyFirstNamePatchUpdateBodyModel(firstName);
            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulOneFieldPatchUserUpdateSpec)
                    .extract().jsonPath();
        });
        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.getInt("id")).isEqualTo(registrationResponse.getInt("id"));
            assertThat(patchUpdateResponse.getString("username")).isEqualTo(username);
            assertThat(patchUpdateResponse.getString("firstName")).isEqualTo(firstName);
            assertThat(patchUpdateResponse.getString("lastName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("email")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("remoteAddr")).isEqualTo(registrationResponse.getString("remoteAddr"));
        });
    }

    @Test
    @DisplayName("Успешная замена только LastName методом PATCH")
    public void onlyLastNameUpdatePatchTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            OnlyLastNamePatchUpdateBodyModel patchUpdateBody = new OnlyLastNamePatchUpdateBodyModel(lastName);
            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulOneFieldPatchUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.getInt("id")).isEqualTo(registrationResponse.getInt("id"));
            assertThat(patchUpdateResponse.getString("username")).isEqualTo(username);
            assertThat(patchUpdateResponse.getString("firstName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("lastName")).isEqualTo(lastName);
            assertThat(patchUpdateResponse.getString("email")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("remoteAddr")).isEqualTo(registrationResponse.getString("remoteAddr"));
        });
    }

    @Test
    @DisplayName("Успешная замена только Email методом PATCH")
    public void onlyEmailUpdatePatchTest() {


        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            OnlyEmailPatchUpdateBodyModel patchUpdateBody = new OnlyEmailPatchUpdateBodyModel(email);
            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(successfulOneFieldPatchUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.getInt("id")).isEqualTo(registrationResponse.getInt("id"));
            assertThat(patchUpdateResponse.getString("username")).isEqualTo(username);
            assertThat(patchUpdateResponse.getString("firstName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("lastName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("email")).isEqualTo(email);
            assertThat(patchUpdateResponse.getString("remoteAddr")).isEqualTo(registrationResponse.getString("remoteAddr"));
        });
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - превышен лимит символов, нарушен формат")
    public void exceedAndWrongFieldsUpdatePatchTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(forbiddenExceededUsername, forbiddenExceededUsername,
                    forbiddenExceededUsername, forbiddenExceededEmail);
            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(wrongFieldsPatchUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(patchUpdateResponse.getList("username", String.class).get(0)).isEqualTo("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.");
            assertThat(patchUpdateResponse.getList("username", String.class).get(1)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(patchUpdateResponse.getList("firstName", String.class).get(0)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(patchUpdateResponse.getList("lastName", String.class).get(0)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(patchUpdateResponse.getList("email", String.class).get(0)).isEqualTo("Ensure this field has no more than 254 characters.");
            assertThat(patchUpdateResponse.getList("email", String.class).get(1)).isEqualTo("Enter a valid email address.");
        });
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - переданы пустые строки")
    public void emptyFieldsUpdatePatchTest() {
        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(emptyUsername, emptyFirstName, emptyLastName, emptyEmail);
            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(emptyFieldsPatchUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(patchUpdateResponse.getList("username", String.class).get(0)).isEqualTo("This field may not be blank.");
        });
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - не переданы поля")
    public void wrongNoFieldsUpdatePatchTest() {

        JsonPath registrationResponse = step("Регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract().jsonPath();
        });

        String accessToken = step("Авторизация пользователя", () -> {
            LoginBodyModel loginData = new LoginBodyModel(username, password);
            return "Bearer " + given(requestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().jsonPath().getString("access");
        });

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> {
            EmptyPatchUpdateBodyModel patchUpdateBody = new EmptyPatchUpdateBodyModel();
            return given(requestSpec)
                    .body(patchUpdateBody)
                    .header("Authorization", accessToken)
                    .when()
                    .patch("/users/me/")
                    .then()
                    .spec(noFieldsPatchUserUpdateSpec)
                    .extract().jsonPath();
        });

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.getInt("id")).isEqualTo(registrationResponse.getInt("id"));
            assertThat(patchUpdateResponse.getString("username")).isEqualTo(username);
            assertThat(patchUpdateResponse.getString("firstName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("lastName")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("email")).isEqualTo("");
            assertThat(patchUpdateResponse.getString("remoteAddr")).isEqualTo(registrationResponse.getString("remoteAddr"));
        });
    }


}

