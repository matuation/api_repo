package specs.review;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ReviewSpec {

    public static ResponseSpecification successfulReviewPostResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/review/successful_get_post_review_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("club", notNullValue())
            .expectBody("user", notNullValue())
            .expectBody("user.id", notNullValue())
            .expectBody("user.username", notNullValue())
            .expectBody("review", notNullValue())
            .expectBody("assessment", notNullValue())
            .expectBody("readPages", notNullValue())
            .expectBody("created", notNullValue())
            .expectBody("modified", nullValue())
            .build();

public static ResponseSpecification successfulReviewGetResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(200)
        .expectBody(matchesJsonSchemaInClasspath(
                "schemas/review/successful_get_post_review_response_schema.json"))
        .expectBody("id", notNullValue())
        .expectBody("club", notNullValue())
        .expectBody("user", notNullValue())
        .expectBody("user.id", notNullValue())
        .expectBody("user.username", notNullValue())
        .expectBody("review", notNullValue())
        .expectBody("assessment", notNullValue())
        .expectBody("readPages", notNullValue())
        .expectBody("created", notNullValue())
        .expectBody("modified", nullValue())
        .build();

    public static ResponseSpecification successfulReviewPutResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/review/successful_put_review_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("club", notNullValue())
            .expectBody("user", notNullValue())
            .expectBody("user.id", notNullValue())
            .expectBody("user.username", notNullValue())
            .expectBody("review", notNullValue())
            .expectBody("assessment", notNullValue())
            .expectBody("readPages", notNullValue())
            .expectBody("created", notNullValue())
            .expectBody("modified", notNullValue())
            .build();

    public static ResponseSpecification successfulReviewDeleteResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();

    public static ResponseSpecification unsuccessfulReviewGetResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/review/unsuccessful_get_not_existing_review_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification unsuccessfulReviewDeleteNoPermissionResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(403)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/review/unsuccessful_get_not_existing_review_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();
}
