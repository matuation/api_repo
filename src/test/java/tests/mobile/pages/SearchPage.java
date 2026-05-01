package tests.mobile.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static io.appium.java_client.AppiumBy.accessibilityId;
import static io.appium.java_client.AppiumBy.className;

public class SearchPage {

    private final String searchQuery = "Appium";

    private final SelenideElement searchArea = $(accessibilityId("Search Wikipedia")),
            searchFiled = $(AppiumBy.id("org.wikipedia.alpha:id/search_src_text")),
            recentSearch = $(AppiumBy.id("org.wikipedia.alpha:id/list_title")),
            recentSearchElement = $(byXpath("//android.widget.TextView[@text=\"Appium\"]")),
            searchResult = $(byXpath("//androidx.compose.ui.platform.ComposeView/android.view.View/android.view.View/android.view.View[1]")),
            searchResultTitle = $(byXpath("(//android.widget.TextView[@text=\"Appium\"])[1]")),
            closeButton = $(AppiumBy.id("org.wikipedia.alpha:id/closeButton")),
            backButton = $(accessibilityId("Navigate up")),
            clearSearchButton = $(accessibilityId("Clear query"));

    private final ElementsCollection searchResultsList = $$(className("android.view.View"));


    @Step("Закрыть начальный экран")
    public SearchPage skipStartScreen() {
        back();
        return this;
    }

    @Step("Нажать на область поиска")
    public SearchPage clickSearchArea() {
        searchArea.click();
        return this;
    }

    @Step("Ввести поисковой запрос 'Appium'")
    public SearchPage enterSearchQuery() {
        searchFiled.sendKeys("Appium");
        return this;
    }

    @Step("Проверить, что по запросу есть результаты")
    public SearchPage verifyResultListIsNotEmpty() {
        searchResultsList.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Нажать на результат поиска")
    public SearchPage clickSearchResult() {
        searchResult.click();
        return this;
    }

    @Step("Нажать на Закрыть")
    public SearchPage clickCloseButton() {
        closeButton.click();
        return this;
    }

    @Step("Нажать Назад")
    public SearchPage clickBackButton() {
        backButton.click();
        return this;

    }

    @Step("Нажать Очистить поиск")
    public SearchPage clickClearSearchButton() {
        clearSearchButton.click();
        return this;
    }

    @Step("Проверить заголовок страницы поискового результата")
    public SearchPage verifySearchResultTitle() {
        searchResultTitle.shouldHave(text(searchQuery));
        return this;
    }

    @Step("Проверить, что поисковая строка пустая")
    public SearchPage verifySearchFieldIsEmpty() {
        searchFiled.shouldHave(text("Search Wikipedia"));
        return this;
    }

    @Step("Проверить, что есть заголовок Недавние запросы")
    public SearchPage verifyRecentSearchTitle() {
        recentSearch.shouldHave(text("Recent searches:"));
        return this;
    }

    @Step("Проверить, что есть недавний запрос")
    public SearchPage verifyRecentSearchElement() {
        recentSearchElement.shouldHave(text(searchQuery));
        return this;
    }
}
