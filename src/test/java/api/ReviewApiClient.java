package api;

import io.qameta.allure.Step;
import models.review.ReviewGetNotExistingResponseBodyModel;
import models.review.ReviewGetResponseBodyModel;
import models.review.ReviewPostRequestBodyModel;
import models.review.ReviewPostResponseBodyModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.requestSpec;
import static specs.review.ReviewSpec.*;

public class ReviewApiClient {

    @Step("Успешное создание обзора на книгу")
    public ReviewPostResponseBodyModel reviewPost(ReviewPostRequestBodyModel reviewData, String accessToken) {
        return given(requestSpec)
                .body(reviewData)
                .header("Authorization", accessToken)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(successfulReviewPostResponseSpec)
                .extract().as(ReviewPostResponseBodyModel.class);
    }

    @Step("Успешный просмотр обзора на книгу")
    public ReviewGetResponseBodyModel reviewGet(int reviewId, String accessToken) {
        return given(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .get("/clubs/reviews/" + reviewId + "/")
                .then()
                .spec(successfulReviewGetResponseSpec)
                .extract().as(ReviewGetResponseBodyModel.class);
    }

    @Step("Успешное полное обновление обзора на книгу")
    public ReviewPostResponseBodyModel reviewPut(int reviewId, ReviewPostRequestBodyModel reviewData, String accessToken) {
        return given(requestSpec)
                .body(reviewData)
                .header("Authorization", accessToken)
                .when()
                .put("/clubs/reviews/" + reviewId + "/")
                .then()
                .spec(successfulReviewPutResponseSpec)
                .extract().as(ReviewPostResponseBodyModel.class);
    }

    @Step("Успешное удаление обзора на книгу")
    public void reviewDelete(int reviewId, String accessToken) {
        given(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .delete("/clubs/reviews/" + reviewId + "/")
                .then()
                .spec(successfulReviewDeleteResponseSpec);
    }

    @Step("Запрос просмотра несуществующего обзора на книгу")
    public ReviewGetNotExistingResponseBodyModel reviewGetUnsuccessful(int reviewId, String accessToken) {
        return given(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .get("/clubs/reviews/" + reviewId + "/")
                .then()
                .spec(unsuccessfulReviewGetResponseSpec)
                .extract().as(ReviewGetNotExistingResponseBodyModel.class);
    }

    @Step("Удаление чужого обзора на книгу")
    public ReviewGetNotExistingResponseBodyModel reviewDeleteUnsuccessfulNoPermission(int reviewId, String accessToken) {
        return given(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .delete("/clubs/reviews/" + reviewId + "/")
                .then()
                .spec(unsuccessfulReviewDeleteNoPermissionResponseSpec)
                .extract().as(ReviewGetNotExistingResponseBodyModel.class);
    }
}
