package api;

import io.qameta.allure.Step;
import models.registration.*;
import models.update.*;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.requestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.wrongUsernameRegistrationResponseSpec;
import static specs.update.UpdateUserSpec.*;

public class UserApiClient {
    @Step("Успешная регистрация")
    public SuccessfulRegistrationResponseModel userRegistration(RegistrationBodyModel registrationData) {
        return given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Регистрация некорректного пользователя")
    public WrongUserResponseModel incorrectUserRegistration(RegistrationBodyModel registrationData) {
        return given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameRegistrationResponseSpec)
                .extract()
                .as(WrongUserResponseModel.class);
    }

    @Step("Регистрация пользователя с неправильным логином и паролем")
    public WrongUsernameAndPasswordRegistrationResponseModel incorrectUserAndPasswordRegistration(RegistrationBodyModel registrationData) {
        return given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameAndPasswordRegistrationResponseModel.class);
    }

    @Step("Регистрация пользователя без логина и пароля")
    public WrongUsernameAndPasswordRegistrationResponseModel noUserAndPasswordRegistration(NoUsernameAndPasswordRegistrationRequestModel registrationData) {
        return given(requestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongUsernameRegistrationResponseSpec)
                .extract()
                .as(WrongUsernameAndPasswordRegistrationResponseModel.class);
    }

    @Step("Успешное изменение пользователя методом PUT")
    public SuccessfulPutUpdateResponseModel successfulUpdatePut(PutUpdateBodyModel putUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulPutUserUpdateSpec)
                .extract().as(SuccessfulPutUpdateResponseModel.class);
    }

    @Step("Неуспешное изменение пользователя методом PUT")
    public WrongOrNoFieldsPutUpdateResponseModel unsuccessfulUpdatePut(PutUpdateBodyModel putUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);
    }

    @Step("Неуспешное изменение пользователя методом PUT")
    public WrongOrNoFieldsPutUpdateResponseModel noDataUpdatePut(EmptyPutUpdateBodyModel putUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongOrNoFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);
    }

    @Step("Передача пустых полей методом PUT")
    public WrongOrNoFieldsPutUpdateResponseModel emptyUpdatePut(PutUpdateBodyModel putUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(emptyFieldsPutUserUpdateSpec)
                .extract().as(WrongOrNoFieldsPutUpdateResponseModel.class);
    }

    @Step("Передача только username методом PUT")
    public OnlyUsernamePutUpdateResponseModel onlyUsernameUpdatePut(OnlyUsernamePutUpdateBodyModel putUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(putUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(onlyUsernamePutUserUpdateSpec)
                .extract().as(OnlyUsernamePutUpdateResponseModel.class);
    }

    @Step("Успешное изменение пользователя методом PATCH")
    public SuccessfulPatchUpdateResponseModel successfulUpdatePatch(PatchUpdateBodyModel patchUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulPatchUserUpdateSpec)
                .extract().as(SuccessfulPatchUpdateResponseModel.class);
    }

    @Step("Успешное изменение только username методом PATCH")
    public SuccessfulPatchUpdateResponseModel successfulUsernameUpdatePatch(OnlyUsernamePatchUpdateBodyModel patchUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                .extract().as(SuccessfulPatchUpdateResponseModel.class);
    }

    @Step("Успешное изменение только FirstName методом PATCH")
    public SuccessfulPatchUpdateResponseModel successfulFirstNameUpdatePatch(OnlyFirstNamePatchUpdateBodyModel patchUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                .extract().as(SuccessfulPatchUpdateResponseModel.class);
    }

    @Step("Успешное изменение только LastName методом PATCH")
    public SuccessfulPatchUpdateResponseModel successfulLastNameUpdatePatch(OnlyLastNamePatchUpdateBodyModel patchUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                .extract().as(SuccessfulPatchUpdateResponseModel.class);
    }

    @Step("Успешное изменение только Email методом PATCH")
    public SuccessfulPatchUpdateResponseModel successfulEmailNameUpdatePatch(OnlyEmailPatchUpdateBodyModel patchUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneFieldPatchUserUpdateSpec)
                .extract().as(SuccessfulPatchUpdateResponseModel.class);
    }

    @Step("Неуспешное изменение пользователя методом PATCH")
    public WrongFieldsPatchUpdateResponseModel unsuccessfulUpdatePatch(PatchUpdateBodyModel patchUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .put("/users/me/")
                .then()
                .spec(emptyFieldsPatchUserUpdateSpec)
                .extract().as(WrongFieldsPatchUpdateResponseModel.class);
    }

    @Step("Изменение без полей методом PATCH")
    public SuccessfulPatchUpdateResponseModel unsuccessfulNoFieldsUpdatePatch(EmptyPatchUpdateBodyModel patchUpdateBody, String accessToken) {
        return given(requestSpec)
                .body(patchUpdateBody)
                .header("Authorization", accessToken)
                .when()
                .patch("/users/me/")
                .then()
                .spec(noFieldsPatchUserUpdateSpec)
                .extract().as(SuccessfulPatchUpdateResponseModel.class);
    }
}
