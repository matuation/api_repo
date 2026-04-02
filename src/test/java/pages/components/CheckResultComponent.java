package pages.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;

public class CheckResultComponent {

    public void checkReviewResultValues(SelenideElement key, String value) {
        key.shouldHave(text(value));

    }
}
