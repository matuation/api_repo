package tests;

import models.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class RegistrationTests extends TestBase {

    String GENERATED_USERNAME;
    String GENERATED_PASSWORD;
    String FORBIDDEN_USERNAME;
    String EXCEEDED_USERNAME;
    String EXCEEDED_PASSWORD;

    @BeforeEach
    public void prepareTestData() {
        GENERATED_USERNAME = faker.name().firstName() + faker.name().maleFirstName();
        GENERATED_PASSWORD = faker.name().firstName();
        FORBIDDEN_USERNAME = faker.regexify("[\\=]{5}");
        EXCEEDED_USERNAME = faker.regexify("[\\w.@+-]{151}");
        EXCEEDED_PASSWORD = faker.regexify("[\\w.@+-]{129}");
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void successfulRegistrationTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(registrationData);

        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(GENERATED_USERNAME);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            assertThat(registrationResponse.remoteAddr()).matches(IP_ADR_REGEXP);
        });
    }

    @Test
    @DisplayName("Регистрация существующего пользователя")

    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD);
        SuccessfulRegistrationResponseModel firstRegistrationResponse = api.user.userRegistration(registrationData);
        WrongUserResponseModel secondRegistrationResponse = api.user.incorrectUserRegistration(registrationData);

        step("Проверка корректности отображенных ошибок", () -> {

            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(EXISTING_USER_ERROR);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c username содержащим недопустимый символ")
    public void forbiddenUsernameRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(FORBIDDEN_USERNAME, PASSWORD);
        WrongUserResponseModel registrationResponse = api.user.incorrectUserRegistration(registrationData);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo(INVALID_USERNAME_ERROR);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c превышенной длинной полей")
    public void exceededUsernameAndPasswordRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(EXCEEDED_USERNAME, EXCEEDED_PASSWORD);
        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = api.user.incorrectUserAndPasswordRegistration(registrationData);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo(EXCEEDED_USERNAME_ERROR);
            assertThat(RegistrationResponse.password().get(0)).isEqualTo(EXCEEDED_PASSWORD_ERROR);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c пустыми полями")
    public void emptyUsernameAndPasswordRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(EMPTY_STRING, EMPTY_STRING);
        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = api.user.incorrectUserAndPasswordRegistration(registrationData);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo(BLANK_FIELD_ERROR);
            assertThat(RegistrationResponse.password().get(0)).isEqualTo(BLANK_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c null полями")
    public void nullUsernameAndPasswordRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(NULL_STRING, NULL_STRING);
        WrongUsernameAndPasswordRegistrationResponseModel registrationResponse = api.user.incorrectUserAndPasswordRegistration(registrationData);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo(NULL_FIELD_ERROR);
            assertThat(registrationResponse.password().get(0)).isEqualTo(NULL_FIELD_ERROR);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя без полей")
    public void noUsernameAndPasswordRegistrationTest() {

        NoUsernameAndPasswordRegistrationRequestModel registrationData = new NoUsernameAndPasswordRegistrationRequestModel();
        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = api.user.noUserAndPasswordRegistration(registrationData);

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
            assertThat(RegistrationResponse.password().get(0)).isEqualTo(REQUIRED_FIELD_ERROR);
        });
    }
}