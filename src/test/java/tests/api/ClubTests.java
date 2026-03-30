package tests.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.club.*;
import models.localStorage.LocalStorageAuthRequestBody;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.localStorage.UserData;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.api.TestData.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ClubTests extends TestBase {

    String GENERATED_USERNAME;
    String GENERATED_USERNAME_SECOND;
    String GENERATED_PASSWORD;
    String bookTitle;
    String newBookTitle;
    String bookAuthors;
    String newBookAuthors;
    int publicationYear;
    int newPublicationYear;
    String description;
    String newDescription;

    @BeforeEach
    public void prepareTestData() {
        GENERATED_USERNAME = faker.name().firstName() + faker.name().maleFirstName();
        GENERATED_USERNAME_SECOND = faker.name().firstName() + faker.name().maleFirstName() + "i";
        GENERATED_PASSWORD = faker.credentials().password();
        bookTitle = faker.book().title() + " " + faker.naruto().character() + " " + faker.battlefield1().weapon();
        newBookTitle = faker.book().title() + " " + faker.naruto().eye() + " " + faker.battlefield1().map();
        bookAuthors = faker.book().author();
        newBookAuthors = faker.book().author();
        publicationYear = faker.number().numberBetween(1700, 2025);
        newPublicationYear = faker.number().numberBetween(1700, 2025);
        description = faker.book().genre() + " " + faker.book().publisher();
        newDescription = faker.book().genre() + " " + faker.book().publisher();

    }

    @Test
    @DisplayName("Успешное создание клуба")
    public void successfulClubCreationTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken = "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostRequestBodyModel createClub = new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                publicationYear, description, TELEGRAM_LINK);
        CreateClubPostResponseBodyModel createClubBodyModel = api.club.clubCreate(createClub, accessToken);

        step("Проверка значений созданного клуба", () -> {
            assertThat(createClubBodyModel.id()).isNotNull();
            assertThat(createClubBodyModel.bookTitle()).isEqualTo(createClub.bookTitle());
            assertThat(createClubBodyModel.bookAuthors()).isEqualTo(createClub.bookAuthors());
            assertThat(createClubBodyModel.publicationYear()).isEqualTo(createClub.publicationYear());
            assertThat(createClubBodyModel.description()).isEqualTo(createClub.description());
            assertThat(createClubBodyModel.telegramChatLink()).isEqualTo(createClub.telegramChatLink());
            assertThat(createClubBodyModel.owner()).isEqualTo(registrationResponse.id());
            assertThat(createClubBodyModel.members().get(0).intValue()).isEqualTo(registrationResponse.id());
            assertThat(createClubBodyModel.reviews().isEmpty());
            assertThat(createClubBodyModel.created()).isNotNull();
            assertThat(createClubBodyModel.modified()).isNull();
        });
    }

    @Test
    @DisplayName("Успешный просмотр созданного клуба")
    public void successfulClubGetTest() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        GetClubResponseBodyModel getClubResponse =
                api.club.clubGet(createClubBodyModel.id(), accessToken);

        step("Проверка значений полученного клуба", () -> {
            assertThat(getClubResponse.id()).isEqualTo(createClubBodyModel.id());
            assertThat(getClubResponse.bookTitle()).isEqualTo(createClubBodyModel.bookTitle());
            assertThat(getClubResponse.bookAuthors()).isEqualTo(createClubBodyModel.bookAuthors());
            assertThat(getClubResponse.publicationYear()).isEqualTo(createClubBodyModel.publicationYear());
            assertThat(getClubResponse.description()).isEqualTo(createClubBodyModel.description());
            assertThat(getClubResponse.telegramChatLink()).isEqualTo(createClubBodyModel.telegramChatLink());
            assertThat(getClubResponse.owner()).isEqualTo(createClubBodyModel.owner());
            assertThat(getClubResponse.members().get(0).intValue()).isEqualTo(createClubBodyModel.members().get(0).intValue());
            assertThat(getClubResponse.reviews().isEmpty());
            assertThat(getClubResponse.created()).isEqualTo(createClubBodyModel.created());
            assertThat(getClubResponse.modified()).isNull();
        });
    }

    @Test
    @DisplayName("Успешное обновление книжного клуба")
    public void successfulClubPutUpdateTest() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        UpdateClubPutRequestBodyModel updateClub = new UpdateClubPutRequestBodyModel(newBookTitle, newBookAuthors,
                newPublicationYear, newDescription, NEW_TELEGRAM_LINK);
        UpdateClubPutResponseBodyModel updateClubBodyModel =
                api.club.clubPutUpdate(createClubBodyModel.id(), updateClub, accessToken);

        step("Проверка значений полученного клуба", () -> {
            assertThat(updateClubBodyModel.id()).isEqualTo(createClubBodyModel.id());
            assertThat(updateClubBodyModel.bookTitle()).isEqualTo(updateClub.bookTitle());
            assertThat(updateClubBodyModel.bookAuthors()).isEqualTo(updateClub.bookAuthors());
            assertThat(updateClubBodyModel.publicationYear()).isEqualTo(updateClub.publicationYear());
            assertThat(updateClubBodyModel.description()).isEqualTo(updateClub.description());
            assertThat(updateClubBodyModel.telegramChatLink()).isEqualTo(updateClub.telegramChatLink());
            assertThat(updateClubBodyModel.owner()).isEqualTo(createClubBodyModel.owner());
            assertThat(updateClubBodyModel.members().get(0).intValue()).isEqualTo(createClubBodyModel.members().get(0).intValue());
            assertThat(updateClubBodyModel.reviews().isEmpty());
            assertThat(updateClubBodyModel.created()).isEqualTo(createClubBodyModel.created());
            assertThat(updateClubBodyModel.modified()).isNotNull();
        });
    }

    @Test
    @DisplayName("Успешное удаление клуба")
    public void successfulClubDeleteTest() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        api.club.clubDelete(createClubBodyModel.id(), accessToken);
        GetNotExistingClubResponseBodyModel getLostClub =
                api.club.getNotExistingClub(createClubBodyModel.id(), accessToken);

        step("Проверка корректной ошибки", () -> {
            assertThat(getLostClub.detail()).isEqualTo(NO_CLUB_ERROR);

        });
    }

    @Test
    @DisplayName("Вствупить в клуб")
    public void successfulClubSignUp() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME_SECOND, GENERATED_PASSWORD));

        String accessTokenSecond =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME_SECOND, GENERATED_PASSWORD));

        api.club.signupToClub(createClubBodyModel.id(), accessTokenSecond);
        
    }

    @Test
    @DisplayName("Пользователь уже в клубе")
    public void unsuccessfulClubSignUp() {

        api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        CreateClubPostResponseBodyModel createClubBodyModel =
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        SignUpClubAlreadySignedPostResponseBodyModel responseIfSignedAlready = api.club.signupToSignedClub(createClubBodyModel.id(), accessToken);

        assertThat(responseIfSignedAlready.detail()).isEqualTo(ALREADY_SIGNED_ERROR);
    }

    @Test
    @DisplayName("UI + API Пользователь не может покинуть клуб, если он его владелец")
    public void cantLeaveClubAsOwnerTest(){

        SuccessfulRegistrationResponseModel registrationResponse =
                api.user.userRegistration(new RegistrationBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

        String accessToken =
                "Bearer " + api.auth.loginAccessToken(new LoginBodyModel(GENERATED_USERNAME, GENERATED_PASSWORD));

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
                api.club.clubCreate(new CreateClubPostRequestBodyModel(bookTitle, bookAuthors,
                        publicationYear, description, TELEGRAM_LINK), accessToken);

        open("/favicon.ico");
        localStorage().setItem("book_club_auth", authJson);
        open("/clubs/" + createClubBodyModel.id());

        // cant leave club as owner
        $(".club-content").shouldBe(visible);
        $(".leave-btn").click();
        confirm();
        $(".error").shouldHave(text("Не удалось покинуть клуб"));
    }

}
