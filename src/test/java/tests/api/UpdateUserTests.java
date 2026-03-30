package tests.api;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.api.TestData.*;


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
        GENERATED_PASSWORD = faker.credentials().password();
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

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));


        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        SuccessfulPutUpdateResponseModel putUpdateResponse =
                api.user.successfulUpdatePut(new PutUpdateBodyModel(NEW_GENERATED_USERNAME, GENERATED_FIRST_NAME,
                        GENERATED_LAST_NAME, GENERATED_EMAIL), accessToken);

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

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse =
                api.user.unsuccessfulUpdatePut(new PutUpdateBodyModel(FORBIDDEN_USERNAME, EXCEEDED_USERNAME,
                        EXCEEDED_USERNAME, FORBIDDEN_EXCEED_EMAIL), accessToken);

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

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse =
                api.user.unsuccessfulUpdatePut(new PutUpdateBodyModel(FORBIDDEN_USERNAME, EXCEEDED_USERNAME,
                        EXCEEDED_USERNAME, FORBIDDEN_EMAIL), accessToken);

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

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse =
                api.user.noDataUpdatePut(new EmptyPutUpdateBodyModel(), accessToken);

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

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        WrongOrNoFieldsPutUpdateResponseModel putUpdateResponse =
                api.user.emptyUpdatePut(new PutUpdateBodyModel(EMPTY_STRING, EMPTY_STRING,
                        EMPTY_STRING, EMPTY_STRING), accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.username().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена данных методом PUT - передан только Username")
    public void onlyUsernameUpdatePutTest() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        OnlyUsernamePutUpdateResponseModel putUpdateResponse =
                api.user.onlyUsernameUpdatePut(new OnlyUsernamePutUpdateBodyModel(GENERATED_USERNAME), accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(putUpdateResponse.firstName().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(putUpdateResponse.lastName().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(putUpdateResponse.email().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Успешная замена всех данных методом PATCH")
    public void successfulAllFieldsUpdatePatchTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        SuccessfulPatchUpdateResponseModel patchUpdateResponse =
                api.user.successfulUpdatePatch(new PatchUpdateBodyModel(NEW_GENERATED_USERNAME, GENERATED_FIRST_NAME,
                        GENERATED_LAST_NAME, GENERATED_EMAIL), accessToken);

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


        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        SuccessfulPatchUpdateResponseModel patchUpdateResponse =
                api.user.successfulUsernameUpdatePatch(new OnlyUsernamePatchUpdateBodyModel(NEW_GENERATED_USERNAME),
                        accessToken);

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

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        SuccessfulPatchUpdateResponseModel patchUpdateResponse =
                api.user.successfulFirstNameUpdatePatch(new OnlyFirstNamePatchUpdateBodyModel(GENERATED_FIRST_NAME),
                        accessToken);

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

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        SuccessfulPatchUpdateResponseModel patchUpdateResponse =
                api.user.successfulLastNameUpdatePatch(new OnlyLastNamePatchUpdateBodyModel(GENERATED_LAST_NAME),
                        accessToken);

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


        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        SuccessfulPatchUpdateResponseModel patchUpdateResponse =
                api.user.successfulEmailNameUpdatePatch(new OnlyEmailPatchUpdateBodyModel(GENERATED_EMAIL),
                        accessToken);

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

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        WrongFieldsPatchUpdateResponseModel patchUpdateResponse =
                api.user.unsuccessfulUpdatePatch(new PatchUpdateBodyModel(FORBIDDEN_EXCEED_USERNAME,
                        FORBIDDEN_EXCEED_USERNAME, FORBIDDEN_EXCEED_USERNAME, FORBIDDEN_EXCEED_EMAIL), accessToken);

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

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        WrongFieldsPatchUpdateResponseModel patchUpdateResponse =
                api.user.unsuccessfulUpdatePatch(new PatchUpdateBodyModel(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING,
                        EMPTY_STRING), accessToken);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(patchUpdateResponse.username().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Неуспешная замена всех полей методом PATCH - не переданы поля")
    public void wrongNoFieldsUpdatePatchTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME,
                GENERATED_PASSWORD));

        SuccessfulPatchUpdateResponseModel patchUpdateResponse =
                api.user.unsuccessfulNoFieldsUpdatePatch(new EmptyPatchUpdateBodyModel(), accessToken);

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

