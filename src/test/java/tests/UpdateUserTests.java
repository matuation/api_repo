package tests;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;


public class UpdateUserTests extends TestBase {

    String GENERATED_USERNAME;
    String NEW_GENERATED_USERNAME;
    String GENERATED_PASSWORD;
    String GENERATED_FIRST_NAME;
    String GENERATED_LAST_NAME;
    String GENERATED_EMAIL;
    String FORBIDDEN_USERNAME;
    String FORBIDDEN_EXCEED_USERNAME;
    String FORBIDDEN_EXCEED_EMAIL;
    String FORBIDDEN_EMAIL;
    String EXCEEDED_USERNAME;
    String EXCEEDED_PASSWORD;

    @BeforeEach
    public void prepareTestData() {
        GENERATED_USERNAME = faker.name().firstName() + faker.name().maleFirstName();
        NEW_GENERATED_USERNAME = faker.name().firstName() + faker.name().maleFirstName();
        GENERATED_PASSWORD  = faker.name().firstName();
        GENERATED_FIRST_NAME = faker.name().firstName();
        GENERATED_LAST_NAME = faker.name().lastName();
        GENERATED_EMAIL = faker.internet().emailAddress();
        FORBIDDEN_USERNAME = faker.regexify("[\\=]{5}");
        FORBIDDEN_EXCEED_USERNAME = faker.regexify("[\\!.=]{151}");
        FORBIDDEN_EXCEED_EMAIL = faker.regexify("[\\w.@+-]{255}");
        FORBIDDEN_EMAIL = faker.regexify("[\\!.=+-]{5}");
        EXCEEDED_USERNAME = faker.regexify("[\\w.@+-]{151}");
        EXCEEDED_PASSWORD = faker.regexify("[\\w.@+-]{129}");
    }

    @Test
    @DisplayName("Успешная замена данных методом PUT")
    public void successfulUpdatePutTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(NEW_GENERATED_USERNAME, GENERATED_FIRST_NAME,
                GENERATED_LAST_NAME, GENERATED_EMAIL);
        SuccessfulPutUpdateResponseModel putUpdateResponse = api.user.successfulUpdatePut(putUpdateBody, accessToken);

        step("Проверка корректности обновления данных", () -> {
            assertThat(putUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(putUpdateResponse.username()).isEqualTo(NEW_GENERATED_USERNAME);
            assertThat(putUpdateResponse.firstName()).isEqualTo(GENERATED_FIRST_NAME);
            assertThat(putUpdateResponse.lastName()).isEqualTo(GENERATED_LAST_NAME);
            assertThat(putUpdateResponse.email()).isEqualTo(GENERATED_EMAIL);
            assertThat(putUpdateResponse.remoteAddr()).isEqualTo(registrationResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - превышен лимит и некорректный Username")
    public void wrongExceedUpdatePutTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(FORBIDDEN_USERNAME, EXCEEDED_USERNAME,
                EXCEEDED_USERNAME, FORBIDDEN_EXCEED_EMAIL);
        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = api.user.unsuccessfulUpdatePut(putUpdateBody, accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.username().get(0)).isEqualTo(INVALID_USERNAME_ERROR);
            assertThat(putUpdateResponse.firstName().get(0)).isEqualTo(EXCEEDED_NAME_ERROR);
            assertThat(putUpdateResponse.lastName().get(0)).isEqualTo(EXCEEDED_NAME_ERROR);
            assertThat(putUpdateResponse.email().get(0)).isEqualTo(EXCEEDED_EMAIL_ERROR);
            assertThat(putUpdateResponse.email().get(1)).isEqualTo(INVALID_EMAIL_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - превышен лимит и некорректный Email")
    public void wrongEmailFormatUpdatePutTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(FORBIDDEN_USERNAME, EXCEEDED_USERNAME,
                EXCEEDED_USERNAME, FORBIDDEN_EMAIL);
        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = api.user.unsuccessfulUpdatePut(putUpdateBody, accessToken);


        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.username().get(0)).isEqualTo(INVALID_USERNAME_ERROR);
            assertThat(putUpdateResponse.firstName().get(0)).isEqualTo(EXCEEDED_NAME_ERROR);
            assertThat(putUpdateResponse.lastName().get(0)).isEqualTo(EXCEEDED_NAME_ERROR);
            assertThat(putUpdateResponse.email().get(0)).isEqualTo(INVALID_EMAIL_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - поля не переданы в тело")
    public void noFieldsProvidedUpdatePutTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        EmptyPutUpdateBodyModel putUpdateBody = new EmptyPutUpdateBodyModel();
        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = api.user.noDataUpdatePut(putUpdateBody, accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.username().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(putUpdateResponse.firstName().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(putUpdateResponse.lastName().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(putUpdateResponse.email().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - пустые поля")
    public void emptyFieldsProvidedUpdatePutTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        PutUpdateBodyModel putUpdateBody = new PutUpdateBodyModel(EMPTY_STRING, EMPTY_STRING,
                EMPTY_STRING, EMPTY_STRING);
        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse = api.user.emptyUpdatePut(putUpdateBody, accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.username().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - передан только Username")
    public void onlyUsernameUpdatePutTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        OnlyUsernamePutUpdateBodyModel putUpdateBody = new OnlyUsernamePutUpdateBodyModel(GENERATED_USERNAME);
        OnlyUsernamePutUpdateResponseModel putUpdateResponse = api.user.onlyUsernameUpdatePut(putUpdateBody, accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.firstName().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(putUpdateResponse.lastName().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(putUpdateResponse.email().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Успешная замена всех данных методом PATCH")
    public void successfulAllFieldsUpdatePatchTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(NEW_GENERATED_USERNAME, GENERATED_FIRST_NAME,
                GENERATED_LAST_NAME, GENERATED_EMAIL);
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.user.successfulUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(patchUpdateResponse.username()).isEqualTo(NEW_GENERATED_USERNAME);
            assertThat(patchUpdateResponse.firstName()).isEqualTo(GENERATED_FIRST_NAME);
            assertThat(patchUpdateResponse.lastName()).isEqualTo(GENERATED_LAST_NAME);
            assertThat(patchUpdateResponse.email()).isEqualTo(GENERATED_EMAIL);
            assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(registrationResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Успешная замена только Username методом PATCH")
    public void onlyUsernameUpdatePatchTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        OnlyUsernamePatchUpdateBodyModel patchUpdateBody = new OnlyUsernamePatchUpdateBodyModel(NEW_GENERATED_USERNAME);
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.user.successfulUsernameUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(patchUpdateResponse.username()).isEqualTo(NEW_GENERATED_USERNAME);
            assertThat(patchUpdateResponse.firstName()).isEqualTo("");
            assertThat(patchUpdateResponse.lastName()).isEqualTo("");
            assertThat(patchUpdateResponse.email()).isEqualTo("");
            assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(registrationResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Успешная замена только FirstName методом PATCH")
    public void onlyFirstNameUpdatePatchTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        OnlyFirstNamePatchUpdateBodyModel patchUpdateBody = new OnlyFirstNamePatchUpdateBodyModel(GENERATED_FIRST_NAME);
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.user.successfulFirstNameUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(patchUpdateResponse.username()).isEqualTo(GENERATED_USERNAME);
            assertThat(patchUpdateResponse.firstName()).isEqualTo(GENERATED_FIRST_NAME);
            assertThat(patchUpdateResponse.lastName()).isEqualTo("");
            assertThat(patchUpdateResponse.email()).isEqualTo("");
            assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(registrationResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Успешная замена только LastName методом PATCH")
    public void onlyLastNameUpdatePatchTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        OnlyLastNamePatchUpdateBodyModel patchUpdateBody = new OnlyLastNamePatchUpdateBodyModel(GENERATED_LAST_NAME);
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.user.successfulLastNameUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(patchUpdateResponse.username()).isEqualTo(GENERATED_USERNAME);
            assertThat(patchUpdateResponse.firstName()).isEqualTo("");
            assertThat(patchUpdateResponse.lastName()).isEqualTo(GENERATED_LAST_NAME);
            assertThat(patchUpdateResponse.email()).isEqualTo("");
            assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(registrationResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Успешная замена только Email методом PATCH")
    public void onlyEmailUpdatePatchTest() {


        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        OnlyEmailPatchUpdateBodyModel patchUpdateBody = new OnlyEmailPatchUpdateBodyModel(GENERATED_EMAIL);
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.user.successfulEmailNameUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(patchUpdateResponse.username()).isEqualTo(GENERATED_USERNAME);
            assertThat(patchUpdateResponse.firstName()).isEqualTo("");
            assertThat(patchUpdateResponse.lastName()).isEqualTo("");
            assertThat(patchUpdateResponse.email()).isEqualTo(GENERATED_EMAIL);
            assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(registrationResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - превышен лимит символов, нарушен формат")
    public void exceedAndWrongFieldsUpdatePatchTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(FORBIDDEN_EXCEED_USERNAME, FORBIDDEN_EXCEED_USERNAME,
                FORBIDDEN_EXCEED_USERNAME, FORBIDDEN_EXCEED_EMAIL);
        WrongFieldsPatchUpdateResponseModel patchUpdateResponse = api.user.unsuccessfulUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(patchUpdateResponse.username().get(0)).isEqualTo(INVALID_USERNAME_ERROR);
            assertThat(patchUpdateResponse.username().get(1)).isEqualTo(EXCEEDED_NAME_ERROR);
            assertThat(patchUpdateResponse.firstName().get(0)).isEqualTo(EXCEEDED_NAME_ERROR);
            assertThat(patchUpdateResponse.lastName().get(0)).isEqualTo(EXCEEDED_NAME_ERROR);
            assertThat(patchUpdateResponse.email().get(0)).isEqualTo(EXCEEDED_EMAIL_ERROR);
            assertThat(patchUpdateResponse.email().get(1)).isEqualTo(INVALID_EMAIL_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - переданы пустые строки")
    public void emptyFieldsUpdatePatchTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        PatchUpdateBodyModel patchUpdateBody = new PatchUpdateBodyModel(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
        WrongFieldsPatchUpdateResponseModel patchUpdateResponse = api.user.unsuccessfulUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(patchUpdateResponse.username().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - не переданы поля")
    public void wrongNoFieldsUpdatePatchTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        String accessToken = "Bearer " + api.auth.loginAccessToken(loginData);

        EmptyPatchUpdateBodyModel patchUpdateBody = new EmptyPatchUpdateBodyModel();
        SuccessfulPatchUpdateResponseModel patchUpdateResponse = api.user.unsuccessfulNoFieldsUpdatePatch(patchUpdateBody,accessToken);

        step("Проверка корректности обновления данных", () -> {
            assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(patchUpdateResponse.username()).isEqualTo(GENERATED_USERNAME);
            assertThat(patchUpdateResponse.firstName()).isEqualTo("");
            assertThat(patchUpdateResponse.lastName()).isEqualTo("");
            assertThat(patchUpdateResponse.email()).isEqualTo("");
            assertThat(patchUpdateResponse.remoteAddr()).isEqualTo(registrationResponse.remoteAddr());
        });
    }


}

