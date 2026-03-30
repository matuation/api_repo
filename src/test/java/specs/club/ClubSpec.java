package specs.club;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class ClubSpec {
    public static ResponseSpecification successfulPostClubCreateSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/club/successful_club_create_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("bookTitle", notNullValue())
            .expectBody("bookAuthors", notNullValue())
            .expectBody("publicationYear", notNullValue())
            .expectBody("description", notNullValue())
            .expectBody("telegramChatLink", notNullValue())
            .expectBody("owner", notNullValue())
            .expectBody("members", notNullValue())
            .expectBody("reviews", empty())
            .expectBody("created", notNullValue())
            .expectBody("modified", nullValue())
            .build();

    public static ResponseSpecification successfulGetClubSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/club/successful_club_create_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("bookTitle", notNullValue())
            .expectBody("bookAuthors", notNullValue())
            .expectBody("publicationYear", notNullValue())
            .expectBody("description", notNullValue())
            .expectBody("telegramChatLink", notNullValue())
            .expectBody("owner", notNullValue())
            .expectBody("members", notNullValue())
            .expectBody("reviews", empty())
            .expectBody("created", notNullValue())
            .expectBody("modified", nullValue())
            .build();

    public static ResponseSpecification successfulPutClubUpdateSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/club/successful_club_update_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("bookTitle", notNullValue())
            .expectBody("bookAuthors", notNullValue())
            .expectBody("publicationYear", notNullValue())
            .expectBody("description", notNullValue())
            .expectBody("telegramChatLink", notNullValue())
            .expectBody("owner", notNullValue())
            .expectBody("members", notNullValue())
            .expectBody("reviews", empty())
            .expectBody("created", notNullValue())
            .expectBody("modified", notNullValue())
            .build();

    public static ResponseSpecification successfulDeleteSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();

    public static ResponseSpecification notExistingClubGetSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/club/not_exisiting_club_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification successfulClubSignup = with()
            .log()
            .all()
            .expect().statusCode(204);

    public static ResponseSpecification unsuccessfulClubSignupAlreadySigned = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
    .expectBody(matchesJsonSchemaInClasspath(
            "schemas/club/not_exisiting_club_response_schema.json"))
            .expectBody("detail", notNullValue())
            .build();
}
