package tests.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.club.CreateClubPostRequestBodyModel;
import models.club.CreateClubPostResponseBodyModel;
import models.localStorage.LocalStorageAuthRequestBody;
import models.localStorage.UserData;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.review.ReviewGetNotExistingResponseBodyModel;
import models.review.ReviewGetResponseBodyModel;
import models.review.ReviewPostRequestBodyModel;
import models.review.ReviewPostResponseBodyModel;
import org.junit.jupiter.api.*;
import pages.ClubPage;
import pages.components.DigitsToStarsComponent;

import static com.codeborne.selenide.Condition.visible;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.api.TestData.*;

public class ReviewTests extends TestBase {

    ClubPage clubPage = new ClubPage();
    DigitsToStarsComponent convert = new DigitsToStarsComponent();

    String GENERATED_USERNAME;
    String GENERATED_USERNAME_NOT_MEMBER;
    String GENERATED_PASSWORD;
    String bookTitle;
    String review;
    String newReview;
    String bookAuthors;
    String newBookAuthors;
    int publicationYear;
    int assessment;
    int newAssessment;
    int readPages;
    int newReadPages;
    String description;
    String newDescription;

    @BeforeEach
    public void prepareTestData() {
        GENERATED_USERNAME = faker.name().firstName() + faker.name().maleFirstName();
        GENERATED_USERNAME_NOT_MEMBER = faker.name().firstName() + faker.name().maleFirstName() + "kek";
        GENERATED_PASSWORD = faker.credentials().password();
        bookTitle = faker.book().title() + " " + faker.naruto().character() + " " + faker.battlefield1().weapon();
        review = faker.book().title() + " " + faker.naruto().eye() + " " + faker.battlefield1().map();
        newReview = faker.book().title() + " " + faker.naruto().eye() + " " + faker.battlefield1().map();
        bookAuthors = faker.book().author();
        newBookAuthors = faker.book().author();
        publicationYear = faker.number().numberBetween(1700, 2025);
        assessment = faker.number().numberBetween(1, 5);
        newAssessment = faker.number().numberBetween(1, 5);
        readPages = faker.number().numberBetween(1, 360);
        newReadPages = faker.number().numberBetween(1, 360);
        description = faker.book().genre() + " " + faker.book().publisher();
        newDescription = faker.book().genre() + " " + faker.book().publisher();
    }

    @Test
    @Tag("API")
    @DisplayName("Успешное создание обзора")
    public void successfulReviewCreationTest() {

        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostRequestBodyModel createClub = new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                publicationYear, description, TELEGRAM_LINK);
        CreateClubPostResponseBodyModel createClubBodyModel = api.club.clubCreate(createClub, accessToken);

        ReviewPostRequestBodyModel reviewBody = new ReviewPostRequestBodyModel(createClubBodyModel.id(), review,
                assessment, readPages);
        ReviewPostResponseBodyModel reviewPost = api.review.reviewPost(reviewBody, accessToken);


        step("Проверка значений созданного обзора", () -> {
            assertThat(reviewPost.id()).isNotNull();
            assertThat(reviewPost.club()).isEqualTo(createClubBodyModel.id());
            assertThat(registrationResponse.id()).isEqualTo(reviewPost.user().get("id"));
            assertThat(registrationResponse.username()).isEqualTo(reviewPost.user().get("username"));
            assertThat(reviewPost.review()).isEqualTo(reviewBody.review());
            assertThat(reviewPost.assessment()).isEqualTo(reviewBody.assessment());
            assertThat(reviewPost.readPages()).isEqualTo(reviewBody.readPages());
            assertThat(reviewPost.created()).isNotNull();
            assertThat(reviewPost.modified()).isNull();
        });
    }

    @Test
    @Tag("API")
    @DisplayName("Успешное получение обзора")
    public void successfulReviewGetTest() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostRequestBodyModel createClub = new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                publicationYear, description, TELEGRAM_LINK);
        CreateClubPostResponseBodyModel createClubBodyModel = api.club.clubCreate(createClub, accessToken);

        ReviewPostRequestBodyModel reviewBody = new ReviewPostRequestBodyModel(createClubBodyModel.id(), review,
                assessment, readPages);
        ReviewPostResponseBodyModel reviewPost = api.review.reviewPost(reviewBody, accessToken);

        ReviewGetResponseBodyModel reviewGet = api.review.reviewGet(reviewPost.id(), accessToken);

        step("Проверка значений созданного обзора", () -> {
            assertThat(reviewGet.id()).isEqualTo(reviewPost.id());
            assertThat(reviewGet.club()).isEqualTo(reviewPost.club());
            assertThat(reviewGet.user().get("id")).isEqualTo(reviewPost.user().get("id"));
            assertThat(reviewGet.user().get("username")).isEqualTo(reviewPost.user().get("username"));
            assertThat(reviewGet.review()).isEqualTo(reviewPost.review());
            assertThat(reviewGet.assessment()).isEqualTo(reviewPost.assessment());
            assertThat(reviewGet.readPages()).isEqualTo(reviewPost.readPages());
            assertThat(reviewGet.created()).isEqualTo(reviewPost.created());
            assertThat(reviewGet.modified()).isNull();
        });
    }

    @Test
    @Tag("API")
    @DisplayName("Успешное обновление обзора")
    public void successfulReviewPutUpdateTest() {

        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostRequestBodyModel createClub = new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                publicationYear, description, TELEGRAM_LINK);
        CreateClubPostResponseBodyModel createClubBodyModel = api.club.clubCreate(createClub, accessToken);

        ReviewPostRequestBodyModel reviewBody = new ReviewPostRequestBodyModel(createClubBodyModel.id(), review,
                assessment, readPages);
        ReviewPostResponseBodyModel reviewPost = api.review.reviewPost(reviewBody, accessToken);

        ReviewPostRequestBodyModel newReviewBody = new ReviewPostRequestBodyModel(createClubBodyModel.id(), newReview,
                newAssessment, newReadPages);

        ReviewPostResponseBodyModel newReviewPost = api.review.reviewPut(reviewPost.id(), newReviewBody, accessToken);

        step("Проверка значений полученного обзора", () -> {
            assertThat(newReviewPost.id()).isEqualTo(reviewPost.id());
            assertThat(newReviewPost.club()).isEqualTo(reviewPost.club());
            assertThat(newReviewPost.user().get("id")).isEqualTo(registrationResponse.id());
            assertThat(newReviewPost.user().get("username")).isEqualTo(registrationResponse.username());
            assertThat(newReviewPost.review()).isEqualTo(newReviewBody.review());
            assertThat(newReviewPost.assessment()).isEqualTo(newReviewBody.assessment());
            assertThat(newReviewPost.readPages()).isEqualTo(newReviewBody.readPages());
            assertThat(newReviewPost.created()).isNotNull();
            assertThat(newReviewPost.modified()).isNotNull();
        });
    }


    @Test
    @Tag("API")
    @DisplayName("Неуспешное получение удалённого обзора")
    public void unsuccessfulReviewCreationTest() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        ReviewPostResponseBodyModel reviewPost =
                api.review.reviewPost(new ReviewPostRequestBodyModel(createClubBodyModel.id(), review,
                        assessment, readPages), accessToken);

        api.review.reviewDelete(reviewPost.id(), accessToken);

        ReviewGetNotExistingResponseBodyModel reviewNotExist =
                api.review.reviewGetUnsuccessful(reviewPost.id(), accessToken);

        step("Проверка корректной ошибки", () -> {
            assertThat(reviewNotExist.detail()).isEqualTo(NO_REVIEW_ERROR);
        });
    }

    @Test
    @Tag("API")
    @Disabled("По факту пока не работает логика, задан вопрос в чат, тест не доделан, дописать пост запроса и проверку")
    @DisplayName("Неуспешное создание обзора - пользователь не в клубе")
    public void unsuccessfulReviewCreationNoRulesTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME_NOT_MEMBER, GENERATED_PASSWORD));

        String accessTokenSecond =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME_NOT_MEMBER, GENERATED_PASSWORD));

        ReviewPostResponseBodyModel reviewPost =
                api.review.reviewPost(new ReviewPostRequestBodyModel(createClubBodyModel.id(), review,
                        assessment, readPages), accessTokenSecond);

        step("Проверка ошибки", () -> {
            assertThat(reviewPost.id()).isNotNull();

        });
    }

    @Test
    @Tag("API")
    @DisplayName("Неуспешное удаление обзора - обзор другого пользователя")
    public void unsuccessfulReviewDeleteNoRulesTest() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostRequestBodyModel createClub = new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                publicationYear, description, TELEGRAM_LINK);
        CreateClubPostResponseBodyModel createClubBodyModel = api.club.clubCreate(createClub, accessToken);

        ReviewPostResponseBodyModel reviewPost = api.review.reviewPost(new ReviewPostRequestBodyModel(createClubBodyModel.id(), review,
                assessment, readPages), accessToken);

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME_NOT_MEMBER, GENERATED_PASSWORD));

        String accessTokenSecond = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME_NOT_MEMBER, GENERATED_PASSWORD));

        ReviewGetNotExistingResponseBodyModel reviewNotExist = api.review.reviewDeleteUnsuccessfulNoPermission(reviewPost.id(), accessTokenSecond);

        step("Проверка значений созданного обзора", () -> {
            assertThat(reviewNotExist.detail()).isEqualTo(NO_PERMISSION_ERROR);

        });
    }

    @Test
    @Tag("API+UI")
    @DisplayName("UI + API Успешное создание обзора")
    public void successfulReviewCreationUITest() {

        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        UserData userData = new UserData(registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr());
        LocalStorageAuthRequestBody localStorageAuthBody = new LocalStorageAuthRequestBody
                (userData, loginResponse.access(), loginResponse.refresh(), true);

        String authJson;
        try {
            authJson = new ObjectMapper().writeValueAsString(localStorageAuthBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка конвертации объекта в JSON", e);
        }

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle,
                        bookAuthors,
                        publicationYear,
                        description,
                        TELEGRAM_LINK), accessToken);

        clubPage.openPage(authJson)
                .openClubPage(createClubBodyModel.id())
                .addReview()
                .setAssessment(assessment + "")
                .setReadPages(readPages + "")
                .setReviewInput(review)
                .saveButton()
                .checkResult(clubPage.getReviewerName(), GENERATED_USERNAME)
                .checkResult(clubPage.getReviewRating(), convert.digitsToStars(assessment))
                .checkResult(clubPage.getReadPages(), readPages + " " + "стр.")
                .checkResult(clubPage.getReviewContent(), review);

    }

    @Test
    @Tag("API+UI")
    @DisplayName("UI + API Успешное обновление обзора")
    public void successfulReviewPutUpdateUITest() {

        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        UserData userData = new UserData(registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr());
        LocalStorageAuthRequestBody localStorageAuthBody = new LocalStorageAuthRequestBody
                (userData, loginResponse.access(), loginResponse.refresh(), true);

        String authJson;
        try {
            authJson = new ObjectMapper().writeValueAsString(localStorageAuthBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка конвертации объекта в JSON", e);
        }

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle,
                        bookAuthors,
                publicationYear,
                        description,
                        TELEGRAM_LINK),
                        accessToken);

        api.review.reviewPost(new ReviewPostRequestBodyModel(createClubBodyModel.id(),
                review,
                assessment,
                readPages),
                accessToken);

        clubPage.openPage(authJson)
                .openClubPage(createClubBodyModel.id())
                .editReview()
                .setAssessment(newAssessment + "")
                .setReadPages(newReadPages + "")
                .setReviewInput(newReview)
                .saveButton()
                .checkResult(clubPage.getReviewerName(), GENERATED_USERNAME)
                .checkResult(clubPage.getReviewRating(), convert.digitsToStars(newAssessment))
                .checkResult(clubPage.getReadPages(), newReadPages + " " + "стр.")
                .checkResult(clubPage.getReviewContent(), newReview);

    }

    @Test
    @Tag("API+UI")
    @DisplayName("UI + API Успешное обновление обзора")
    public void successfulReviewDeleteUITest() {

        SuccessfulRegistrationResponseModel registrationResponse = api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        SuccessfulLoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        UserData userData = new UserData(registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr());
        LocalStorageAuthRequestBody localStorageAuthBody = new LocalStorageAuthRequestBody
                (userData, loginResponse.access(), loginResponse.refresh(), true);

        String authJson;
        try {
            authJson = new ObjectMapper().writeValueAsString(localStorageAuthBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка конвертации объекта в JSON", e);
        }

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle,
                                bookAuthors,
                                publicationYear,
                                description,
                                TELEGRAM_LINK),
                        accessToken);

        api.review.reviewPost(new ReviewPostRequestBodyModel(createClubBodyModel.id(),
                        review,
                        assessment,
                        readPages),
                accessToken);

        clubPage.openPage(authJson)
                .openClubPage(createClubBodyModel.id())
                .deleteReview()
                .getReview().shouldNotBe(visible);

    }

}



