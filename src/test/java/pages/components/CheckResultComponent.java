package pages.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class CheckResultComponent {

    private final SelenideElement review =
            $(".review-card user-review");


    public void checkReviewResultValues(SelenideElement key, String value) {
        key.shouldHave(text(value));

    }
}
