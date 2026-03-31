package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.components.CheckResultComponent;

import static com.codeborne.selenide.Selenide.*;

public class ClubPage {

    private final SelenideElement addReviewButton = $(".add-review-btn"); //создаем переменные для хранения локаторов
            private final SelenideElement assessmentInput = $("#assessment");
    private final SelenideElement readPagesInput = $("#readPages");
    private final SelenideElement reviewInput = $("#review");
    private final SelenideElement saveButton = $(".save-btn");
    private final SelenideElement reviewerName = $(".reviewer-name");
    private final SelenideElement reviewRating = $(".review-rating");
    private final SelenideElement readPages = $(".read-pages");
    private final SelenideElement reviewContent = $(".review-content");
    private final SelenideElement reviewDate = $(".review-content");
    private final SelenideElement editReviewButton = $(".edit-review-btn");
    private final SelenideElement deleteReviewButton = $(".delete-review-btn");
    private final SelenideElement review = $(".review-card.user-review");

    CheckResultComponent checkResultComponent = new CheckResultComponent();

    @Step("Открыть ресурс и передать авторизацию {value}")
    public ClubPage openPage(String value) {
        open("/favicon.ico");
        localStorage().setItem("book_club_auth", value);
        return this;
    }

    @Step("Открыть форму созданного клуба {value}")
    public ClubPage openClubPage(int value) {
        open("/clubs/" + value);
        return this;
    }

    @Step("Нажатие кнопки 'Написать отзыв'")
    public ClubPage addReview() { //метод для подтверждения
        addReviewButton.click();

        return this;
    }

    @Step("Нажатие кнопки 'Редактировать'")
    public ClubPage editReview() { //метод для подтверждения
        editReviewButton.click();

        return this;
    }

    @Step("Нажатие кнопки 'Удалить'")
    public ClubPage deleteReview() { //метод для подтверждения
        deleteReviewButton.click();

        return this;
    }

    @Step("Ввод оценки {value}")
    public ClubPage setAssessment(String value) { //метод для имени
        assessmentInput.clear();
        assessmentInput.setValue(value);

        return this;
    }

    @Step("Ввод количества страниц {value}")
    public ClubPage setReadPages(String value) { //метод для имени
        readPagesInput.setValue(value);

        return this;
    }

    @Step("Ввод текста обзора {value}")
    public ClubPage setReviewInput(String value) { //метод для имени
        reviewInput.setValue(value);

        return this;
    }

    @Step("Нажатие кнопки 'Опубликовать'")
    public ClubPage saveButton() { //метод для подтверждения
        saveButton.click();

        return this;
    }

    @Step("Проверить, что значение поля {key} содержит ожидаемое {value}")
    public ClubPage checkResult(SelenideElement key, String value) { //метод для провери результирующей таблицы
        checkResultComponent.checkReviewResultValues(key, value);

        return this;
    }


    public SelenideElement getReviewerName() {
        return reviewerName;
    }

    public SelenideElement getReviewRating() {

        return reviewRating;
    }

    public SelenideElement getReadPages() {
        return readPages;
    }

    public SelenideElement getReviewContent() {
        return reviewContent;
    }

    public SelenideElement getReviewDate() {
        return reviewDate;
    }

    public SelenideElement getReview() {
        return review;
    }
}
