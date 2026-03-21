package api;

import io.qameta.allure.Step;
import models.club.*;

import static io.restassured.RestAssured.given;
import static specs.club.ClubSpec.*;
import static specs.login.LoginSpec.requestSpec;

public class ClubApiClient {
    @Step("Успешное создание книжного клуба")
    public CreateClubPostResponseBodyModel clubCreate (CreateClubPostRequestBodyModel clubData, String accessToken) {
        return given(requestSpec)
                .body(clubData)
                .header("Authorization", accessToken)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulPostClubCreateSpec)
                .extract().as(CreateClubPostResponseBodyModel.class);
    }

    @Step("Успешный просмотр созданного книжного клуба")
    public GetClubResponseBodyModel clubGet (int clubId, String accessToken) {
        return given(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .get("clubs/" + clubId + "/")
                .then()
                .spec(successfulGetClubSpec)
                .extract().as(GetClubResponseBodyModel.class);
    }

    @Step("Успешное обновление книжного клуба")
    public UpdateClubPutResponseBodyModel clubPutUpdate (int clubId, UpdateClubPutRequestBodyModel clubData, String accessToken) {
        return given(requestSpec)
                .body(clubData)
                .header("Authorization", accessToken)
                .when()
                .put("clubs/" + clubId + "/")
                .then()
                .spec(successfulPutClubUpdateSpec)
                .extract().as(UpdateClubPutResponseBodyModel.class);
    }

    @Step("Успешное удаление книжного клуба")
    public void clubDelete (int clubId, String accessToken) {
        given(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .delete("clubs/" + clubId + "/")
                .then()
                .spec(successfulDeleteSpec);
    }

    @Step("Запрос несуществующего книжного клуба")
    public GetNotExistingClubResponseBodyModel getNotExistingClub (int clubId, String accessToken) {
        return given(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .get("clubs/" + clubId + "/")
                .then()
                .spec(notExistingClubGetSpec)
                .extract().as(GetNotExistingClubResponseBodyModel.class);
    }
}
