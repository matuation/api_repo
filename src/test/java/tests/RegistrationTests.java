package tests;

import models.registration.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.requestSpec;
import static specs.registration.RegistrationSpec.*;

public class RegistrationTests extends TestBase {

    String username;
    String password;
    String forbiddenUsername;
    String exceededLengthUsername;
    String exceededLengthPassword;
    String emptyUsername = "";
    String emptyPassword = "";
    String nullUsername = null;
    String nullPassword = null;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
        forbiddenUsername = faker.regexify("[\\=]{5}");
        exceededLengthUsername = faker.regexify("[\\w.@+-]{151}");
        exceededLengthPassword = faker.regexify("[\\w.@+-]{129}");
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void successfulRegistrationTest() {


        SuccessfulRegistrationResponseModel registrationResponse = step("Отправка корректных данных для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseModel.class);
        });

        step("Проверка корректности зарегистрированных данных", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
        });
    }

    @Test
    @DisplayName("Регистрация существующего пользователя")

    public void existingUserWrongRegistrationTest() {

        SuccessfulRegistrationResponseModel firstRegistrationResponse = step("Отправка корректных данных для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec)
                    .extract()
                    .as(SuccessfulRegistrationResponseModel.class);
        });

        WrongUserResponseModel secondRegistrationResponse = step("Отправка повторно идентичных корректных данных для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongUsernameRegistrationResponseSpec)
                    .extract()
                    .as(WrongUserResponseModel.class);
        });
        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(firstRegistrationResponse.username()).isEqualTo(username);
            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo("A user with that username already exists.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c username содержащим недопустимый символ")
    public void forbiddenUsernameRegistrationTest() {

        WrongUserResponseModel registrationResponse = step("Отправка Username, не подходящего для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(forbiddenUsername, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongUsernameRegistrationResponseSpec)
                    .extract()
                    .as(WrongUserResponseModel.class);
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c превышенной длинной полей")
    public void exceededUsernameAndPasswordRegistrationTest() {

        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = step("Отправка Username, длиннее подходящего для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(exceededLengthUsername, exceededLengthPassword);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                    .extract()
                    .as(WrongUsernameAndPasswordRegistrationResponseModel.class);
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo("Ensure this field has no more than 150 characters.");
            assertThat(RegistrationResponse.password().get(0)).isEqualTo("Ensure this field has no more than 128 characters.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c пустыми полями")
    public void emptyUsernameAndPasswordRegistrationTest() {

        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = step("Отправка пустых строк для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(emptyUsername, emptyPassword);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                    .extract()
                    .as(WrongUsernameAndPasswordRegistrationResponseModel.class);
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo("This field may not be blank.");
            assertThat(RegistrationResponse.password().get(0)).isEqualTo("This field may not be blank.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c null полями")
    public void nullUsernameAndPasswordRegistrationTest() {

        WrongUsernameAndPasswordRegistrationResponseModel registrationResponse = step("Отправка null строк для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(nullUsername, nullPassword);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                    .extract()
                    .as(WrongUsernameAndPasswordRegistrationResponseModel.class);
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo("This field may not be null.");
            assertThat(registrationResponse.password().get(0)).isEqualTo("This field may not be null.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя без полей")
    public void noUsernameAndPasswordRegistrationTest() {


        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = step("Отправка запроса без строк для регистрации", () -> {
            NoUsernameAndPasswordRegistrationRequestModel registrationData = new NoUsernameAndPasswordRegistrationRequestModel();
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                    .extract()
                    .as(WrongUsernameAndPasswordRegistrationResponseModel.class);
        });

        step("Проверка корректности отображенных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo("This field is required.");
            assertThat(RegistrationResponse.password().get(0)).isEqualTo("This field is required.");
        });
    }
}