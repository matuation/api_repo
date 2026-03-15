package tests;

import io.restassured.path.json.JsonPath;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () ->
                given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        int userId = registrationResponse.getInt("id");
        String remoteAddress = registrationResponse.getString("remoteAddr");

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () ->
                given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(username, firstName, lastName, email);

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () ->
                given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulPutUserUpdateSpec)
                .extract().jsonPath());

        int actualId = putUpdateResponse.getInt("id");
        String actualUsername = putUpdateResponse.getString("username");
        String actualFirstName = putUpdateResponse.getString("firstName");
        String actualLastName = putUpdateResponse.getString("lastName");
        String actualEmail = putUpdateResponse.getString("email");
        String actualAddr = putUpdateResponse.getString("remoteAddr");

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo(firstName);
        assertThat(actualLastName).isEqualTo(lastName);
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualAddr).isEqualTo(remoteAddress);

    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - превышен лимит и некорректный Username")
    public void wrongExceedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () ->
                given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                        .extract().jsonPath());

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () ->
                given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                        .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(forbiddenUsername, exceededLengthUsername,
                exceededLengthUsername, forbiddenExceededEmail);

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () ->
                given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                        .extract().jsonPath());

        String actualUsernameError = putUpdateResponse.getList("username", String.class).get(0);
        String actualFirstNameError = putUpdateResponse.getList("firstName", String.class).get(0);
        String actualLastNameError = putUpdateResponse.getList("lastName",String.class).get(0);
        String actualAmountEmailError = putUpdateResponse.getList("email", String.class).get(0);
        String actualFormatEmailError = putUpdateResponse.getList("email", String.class).get(1);
        String expectedUsernameError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
        String expectedFirstNameError = "Ensure this field has no more than 150 characters.";
        String expectedLastNameError = "Ensure this field has no more than 150 characters.";
        String expectedAmountEmailError = "Ensure this field has no more than 254 characters.";
        String expectedFormatEmailError = "Enter a valid email address.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualFirstNameError).isEqualTo(expectedFirstNameError);
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(actualAmountEmailError).isEqualTo(expectedAmountEmailError);
        assertThat(actualFormatEmailError).isEqualTo(expectedFormatEmailError);

    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - превышен лимит и некорректный Email")
    public void wrongEmailFormatUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () ->
                given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () ->
                given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                        .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(forbiddenUsername, exceededLengthUsername,
                exceededLengthUsername, forbiddenEmail);

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () ->
                given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                        .extract().jsonPath());

        String actualUsernameError = putUpdateResponse.getList("username", String.class).get(0);
        String actualFirstNameError = putUpdateResponse.getList("firstName", String.class).get(0);
        String actualLastNameError = putUpdateResponse.getList("lastName",String.class).get(0);
        String actualFormatEmailError = putUpdateResponse.getList("email", String.class).get(0);
        String expectedUsernameError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
        String expectedFirstNameError = "Ensure this field has no more than 150 characters.";
        String expectedLastNameError = "Ensure this field has no more than 150 characters.";
        String expectedFormatEmailError = "Enter a valid email address.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualFirstNameError).isEqualTo(expectedFirstNameError);
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(actualFormatEmailError).isEqualTo(expectedFormatEmailError);

    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - поля не переданы в тело")
    public void noFieldsProvidedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () ->
                given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                        .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        EmptyPutUpdateBodyModel putUpdateBody = new EmptyPutUpdateBodyModel();

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                .extract().jsonPath());

        String actualUsernameError = putUpdateResponse.getList("username", String.class).get(0);
        String actualFirstNameError = putUpdateResponse.getList("firstName", String.class).get(0);
        String actualLastNameError = putUpdateResponse.getList("lastName",String.class).get(0);
        String actualFormatEmailError = putUpdateResponse.getList("email", String.class).get(0);
        String expectedUsernameError = "This field is required.";
        String expectedFirstNameError = "This field is required.";
        String expectedLastNameError = "This field is required.";
        String expectedFormatEmailError = "This field is required.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualFirstNameError).isEqualTo(expectedFirstNameError);
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(actualFormatEmailError).isEqualTo(expectedFormatEmailError);

    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - пустые поля")
    public void emptyFieldsProvidedUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(emptyUsername, emptyFirstName,
                emptyLastName, emptyEmail);

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(emptyFieldsPutUserUpdateSpec)
                .extract().jsonPath());

        String actualUsernameError = putUpdateResponse.getList("username", String.class).get(0);

        String expectedUsernameError = "This field may not be blank.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);


    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - передан только Username")
    public void onlyUsernameUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () ->
                given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                        .extract().jsonPath());

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        OnlyUsernamePutUpdateBodyModel putUpdateBody = new OnlyUsernamePutUpdateBodyModel(username);

        JsonPath putUpdateResponse = step("Изменение данных пользователя", () -> given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(onlyUsernamePutUserUpdateSpec)
                .extract().jsonPath());

        String actualFirstNameError = putUpdateResponse.getList("firstName", String.class).get(0);
        String actualLastNameError = putUpdateResponse.getList("lastName",String.class).get(0);
        String actualFormatEmailError = putUpdateResponse.getList("email", String.class).get(0);
        String expectedFirstNameError = "This field is required.";
        String expectedLastNameError = "This field is required.";
        String expectedFormatEmailError = "This field is required.";

        assertThat(actualFirstNameError).isEqualTo(expectedFirstNameError);
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(actualFormatEmailError).isEqualTo(expectedFormatEmailError);

    }

    @Test
    @DisplayName("Успешная замена всех данных методом PATCH")
    public void successfulAllFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        int userId = registrationResponse.getInt("id");
        String remoteAddress = registrationResponse.getString("remoteAddr");

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(username, firstName, lastName, email);

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () ->
                given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulPatchUserUpdateSpec)
                        .extract().jsonPath());

        int actualId = patchUpdateResponse.getInt("id");
        String actualUsername = patchUpdateResponse.getString("username");
        String actualFirstName = patchUpdateResponse.getString("firstName");
        String actualLastName = patchUpdateResponse.getString("lastName");
        String actualEmail = patchUpdateResponse.getString("email");
        String actualAddr = patchUpdateResponse.getString("remoteAddr");

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo(firstName);
        assertThat(actualLastName).isEqualTo(lastName);
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualAddr).isEqualTo(remoteAddress);

    }

    @Test
    @DisplayName("Успешная замена только Username методом PATCH")
    public void onlyUsernameUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        int userId = registrationResponse.getInt("id");
        String remoteAddress = registrationResponse.getString("remoteAddr");

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        OnlyUsernamePatchUpdateBodyModel patchUpdateBody = new OnlyUsernamePatchUpdateBodyModel(username);

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () ->
                given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                        .extract().jsonPath());

        int actualId = patchUpdateResponse.getInt("id");
        String actualUsername = patchUpdateResponse.getString("username");
        String actualFirstName = patchUpdateResponse.getString("firstName");
        String actualLastName = patchUpdateResponse.getString("lastName");
        String actualEmail = patchUpdateResponse.getString("email");
        String actualAddr = patchUpdateResponse.getString("remoteAddr");

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo("");
        assertThat(actualLastName).isEqualTo("");
        assertThat(actualEmail).isEqualTo("");
        assertThat(actualAddr).isEqualTo(remoteAddress);

    }

    @Test
    @DisplayName("Успешная замена только FirstName методом PATCH")
    public void onlyFirstNameUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        int userId = registrationResponse.getInt("id");
        String remoteAddress = registrationResponse.getString("remoteAddr");

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        OnlyFirstNamePatchUpdateBodyModel patchUpdateBody = new OnlyFirstNamePatchUpdateBodyModel(firstName);

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                .extract().jsonPath());

        int actualId = patchUpdateResponse.getInt("id");
        String actualUsername = patchUpdateResponse.getString("username");
        String actualFirstName = patchUpdateResponse.getString("firstName");
        String actualLastName = patchUpdateResponse.getString("lastName");
        String actualEmail = patchUpdateResponse.getString("email");
        String actualAddr = patchUpdateResponse.getString("remoteAddr");

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo(firstName);
        assertThat(actualLastName).isEqualTo("");
        assertThat(actualEmail).isEqualTo("");
        assertThat(actualAddr).isEqualTo(remoteAddress);

    }

    @Test
    @DisplayName("Успешная замена только LastName методом PATCH")
    public void onlyLastNameUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        int userId = registrationResponse.getInt("id");
        String remoteAddress = registrationResponse.getString("remoteAddr");

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        OnlyLastNamePatchUpdateBodyModel patchUpdateBody = new OnlyLastNamePatchUpdateBodyModel(lastName);

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                .extract().jsonPath());

        int actualId = patchUpdateResponse.getInt("id");
        String actualUsername = patchUpdateResponse.getString("username");
        String actualFirstName = patchUpdateResponse.getString("firstName");
        String actualLastName = patchUpdateResponse.getString("lastName");
        String actualEmail = patchUpdateResponse.getString("email");
        String actualAddr = patchUpdateResponse.getString("remoteAddr");

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo("");
        assertThat(actualLastName).isEqualTo(lastName);
        assertThat(actualEmail).isEqualTo("");
        assertThat(actualAddr).isEqualTo(remoteAddress);

    }


    @Test
    @DisplayName("Успешная замена только Email методом PATCH")
    public void onlyEmailUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        int userId = registrationResponse.getInt("id");
        String remoteAddress = registrationResponse.getString("remoteAddr");

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        OnlyEmailPatchUpdateBodyModel patchUpdateBody = new OnlyEmailPatchUpdateBodyModel(email);

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                .extract().jsonPath());

        int actualId = patchUpdateResponse.getInt("id");
        String actualUsername = patchUpdateResponse.getString("username");
        String actualFirstName = patchUpdateResponse.getString("firstName");
        String actualLastName = patchUpdateResponse.getString("lastName");
        String actualEmail = patchUpdateResponse.getString("email");
        String actualAddr = patchUpdateResponse.getString("remoteAddr");

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo("");
        assertThat(actualLastName).isEqualTo("");
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualAddr).isEqualTo(remoteAddress);

    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - превышен лимит символов, нарушен формат")
    public void exceedAndWrongFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(forbiddenExceededUsername, forbiddenExceededUsername, forbiddenExceededUsername, forbiddenExceededEmail);

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () ->
                given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(wrongFieldsPatchUserUpdateSpec)
                        .extract().jsonPath());

        String actualLengthUsernameError = patchUpdateResponse.getList("username", String.class).get(0);
        String actualFormatUsernameError = patchUpdateResponse.getList("username", String.class).get(1);
        String actualFirstNameError = patchUpdateResponse.getList("firstName", String.class).get(0);
        String actualLastNameError = patchUpdateResponse.getList("lastName",String.class).get(0);
        String actualAmountEmailError = patchUpdateResponse.getList("email", String.class).get(0);
        String actualFormatEmailError = patchUpdateResponse.getList("email", String.class).get(1);
        String expectedLengthUsernameError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
        String expectedUsernameError = "Ensure this field has no more than 150 characters.";
        String expectedFirstNameError = "Ensure this field has no more than 150 characters.";
        String expectedLastNameError = "Ensure this field has no more than 150 characters.";
        String expectedAmountEmailError = "Ensure this field has no more than 254 characters.";
        String expectedFormatEmailError = "Enter a valid email address.";

        assertThat(actualLengthUsernameError).isEqualTo(expectedLengthUsernameError);
        assertThat(actualFormatUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualFirstNameError).isEqualTo(expectedFirstNameError);
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(actualAmountEmailError).isEqualTo(expectedAmountEmailError);
        assertThat(actualFormatEmailError).isEqualTo(expectedFormatEmailError);

    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - переданы пустые строки")
    public void emptyFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(emptyUsername, emptyFirstName, emptyLastName, emptyEmail);

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () ->
                given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(emptyFieldsPatchUserUpdateSpec)
                        .extract().jsonPath());

        String actualUsernameError = patchUpdateResponse.getList("username", String.class).get(0);
        String expectedUsernameError = "This field may not be blank.";

        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);


    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - не переданы поля")
    public void wrongNoFieldsUpdatePatchTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        JsonPath registrationResponse = step("Регистрация пользователя", () -> given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().jsonPath());

        int userId = registrationResponse.getInt("id");
        String remoteAddress = registrationResponse.getString("remoteAddr");

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        JsonPath loginResponse = step("Авторизация пользователя", () -> given(requestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().jsonPath());

        String accessToken = "Bearer " + loginResponse.getString("access");

        EmptyPatchUpdateBodyModel patchUpdateBody = new EmptyPatchUpdateBodyModel();

        JsonPath patchUpdateResponse = step("Изменение данных пользователя", () -> given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(noFieldsPatchUserUpdateSpec)
                .extract().jsonPath());

        int actualId = patchUpdateResponse.getInt("id");
        String actualUsername = patchUpdateResponse.getString("username");
        String actualFirstName = patchUpdateResponse.getString("firstName");
        String actualLastName = patchUpdateResponse.getString("lastName");
        String actualEmail = patchUpdateResponse.getString("email");
        String actualAddr = patchUpdateResponse.getString("remoteAddr");

        assertThat(actualId).isEqualTo(userId);
        assertThat(actualUsername).isEqualTo(username);
        assertThat(actualFirstName).isEqualTo("");
        assertThat(actualLastName).isEqualTo("");
        assertThat(actualEmail).isEqualTo("");
        assertThat(actualAddr).isEqualTo(remoteAddress);

    }


}

