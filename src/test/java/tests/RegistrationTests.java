package tests;

import models.registration.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.username()).isEqualTo(username);
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");

        String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
    }

    @Test
    @DisplayName("Регистрация существующего пользователя")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel firstRegistrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);

        assertThat(firstRegistrationResponse.username()).isEqualTo(username);

        WrongUserResponseModel secondRegistrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameRegistrationResponseSpec)
                .extract()
                .as(WrongUserResponseModel.class);

        String expectedError = "A user with that username already exists.";
        String actualError = secondRegistrationResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    @DisplayName("Регистрация пользователя c username содержащим недопустимый символ")
    public void forbiddenUsernameRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(forbiddenUsername, password);

        WrongUserResponseModel registrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameRegistrationResponseSpec)
                .extract()
                .as(WrongUserResponseModel.class);

        String expectedError = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
        String actualError = registrationResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    @DisplayName("Регистрация пользователя c превышенной длинной полей")
    public void exceededUsernameAndPasswordRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(exceededLengthUsername, exceededLengthPassword);

        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameAndPasswordRegistrationResponseModel.class);

        String expectedUsernameError = "Ensure this field has no more than 150 characters.";
        String expectedPasswordError = "Ensure this field has no more than 128 characters.";
        String actualUsernameError = RegistrationResponse.username().get(0);
        String actualPasswordError = RegistrationResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }

    @Test
    @DisplayName("Регистрация пользователя c пустыми полями")
    public void emptyUsernameAndPasswordRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(emptyUsername, emptyPassword);

        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameAndPasswordRegistrationResponseModel.class);

        String expectedUsernameError = "This field may not be blank.";
        String expectedPasswordError = "This field may not be blank.";
        String actualUsernameError = RegistrationResponse.username().get(0);
        String actualPasswordError = RegistrationResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }

    @Test
    @DisplayName("Регистрация пользователя c null полями")
    public void nullUsernameAndPasswordRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(nullUsername, nullPassword);

        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameAndPasswordRegistrationResponseModel.class);

        String expectedUsernameError = "This field may not be null.";
        String expectedPasswordError = "This field may not be null.";
        String actualUsernameError = RegistrationResponse.username().get(0);
        String actualPasswordError = RegistrationResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }

    @Test
    @DisplayName("Регистрация пользователя без полей")
    public void noUsernameAndPasswordRegistrationTest() {
        NoUsernameAndPasswordRegistrationRequestModel registrationData = new NoUsernameAndPasswordRegistrationRequestModel();

        WrongUsernameAndPasswordRegistrationResponseModel RegistrationResponse = given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameAndPasswordRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameAndPasswordRegistrationResponseModel.class);

        String expectedUsernameError = "This field is required.";
        String expectedPasswordError = "This field is required.";
        String actualUsernameError = RegistrationResponse.username().get(0);
        String actualPasswordError = RegistrationResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }
}