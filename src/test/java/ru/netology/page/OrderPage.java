package ru.netology.page;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class OrderPage {
    // кнопки
    private SelenideElement paymentButton = $$("button")
            .findBy(exactText("Купить"));
    private SelenideElement creditButton = $$("button")
            .findBy(exactText("Купить в кредит"));
    private SelenideElement continueButton = $$("button")
            .findBy(text("Продолжить"));

    // заголовки
    private SelenideElement paymentHeading = $$("h3")
            .findBy(exactText("Оплата по карте"));
    private SelenideElement creditHeading = $$("h3")
            .findBy(exactText("Кредит по данным карты"));

    // форма
    private SelenideElement cardNumber = $$(".input")
            .filter(text("Номер карты"))
            .first()
            .$(".input__control");

    private SelenideElement month = $$(".input")
            .filter(text("Месяц"))
            .first()
            .$(".input__control");

    private SelenideElement year = $$(".input")
            .filter(text("Год"))
            .first()
            .$(".input__control");

    private SelenideElement owner = $$(".input")
            .filter(text("Владелец"))
            .first()
            .$(".input__control");

    private SelenideElement cvcCode = $$(".input")
            .filter(text("CVC/CVV"))
            .first()
            .$(".input__control");

    // нотификации
    private SelenideElement successNotification = $(".notification_status_ok");
    private SelenideElement errorNotification = $(".notification_status_error");

    // подписи полей формы
    private SelenideElement cardNumberSub = $$(".input")
            .filter(text("Номер карты"))
            .first()
            .$(".input__sub");

    private SelenideElement monthSub = $$(".input")
            .filter(text("Месяц"))
            .first()
            .$(".input__sub");

    private SelenideElement yearSub = $$(".input")
            .filter(text("Год"))
            .first()
            .$(".input__sub");

    private SelenideElement ownerSub = $$(".input")
            .filter(text("Владелец"))
            .first()
            .$(".input__sub");

    private SelenideElement cvcCodeSub = $$(".input")
            .filter(text("CVC/CVV"))
            .first()
            .$(".input__sub");

    // Очистка всех полей
    public void clearAllFields() {
        cardNumber.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        month.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        year.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        owner.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        cvcCode.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }

    // Очистка конкретного поля
    public void clearField(SelenideElement field) {
        field.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }

    // Оплатить с карты
    public void payWithCard() {
        paymentButton.click();
        paymentHeading.shouldBe(visible);
    }

    // Оплатить с карты в кредит
    public void payWithCredit() {
        creditButton.click();
        creditHeading.shouldBe(visible);
    }

    // Заполнение формы
    public void fillCardData(String number, String month, String year,
                             String owner, String cvc) {
        cardNumber.setValue(number);
        this.month.setValue(month);
        this.year.setValue(year);
        this.owner.setValue(owner);
        cvcCode.setValue(cvc);
    }

    public void fillCardData(DataHelper.CardInfo cardInfo) {
        cardNumber.setValue(cardInfo.getNumber());
        this.month.setValue(cardInfo.getMonth());
        this.year.setValue(cardInfo.getYear());
        this.owner.setValue(cardInfo.getOwner());
        cvcCode.setValue(cardInfo.getCvc());}

    // Отправка формы
    public void continueClick() {
        continueButton.click();
    }

    // Заполнение и отправка
    public void fillAndSend(DataHelper.CardInfo cardInfo) {
        fillCardData(cardInfo);
        continueClick();
    }

    // Нотификации
    public void verifySuccessNotification() {
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
    }

    public void verifySuccessNotificationText() {
        successNotification
                .$(".notification__title")
                .shouldHave(Condition.exactText("Успешно"));

        successNotification
                .$(".notification__content")
                .shouldHave(Condition.exactText("Операция одобрена Банком."));
    }

    public void verifyErrorNotification() {
        errorNotification.shouldBe(visible, Duration.ofSeconds(20));
    }

    public void verifyErrorNotificationText() {
        errorNotification
                .$(".notification__title")
                .shouldHave(Condition.exactText("Ошибка"));

        errorNotification
                .$(".notification__content")
                .shouldHave(Condition.exactText("Ошибка! Банк отказал в проведении операции."));
    }

    public void verifyCardFieldNotification(String subFieldText) {
        cardNumberSub.shouldBe(visible);
        cardNumberSub.shouldHave(text(subFieldText));
    }

    public void verifyMonthFieldNotification(String subFieldText) {
        monthSub.shouldBe(visible);
        monthSub.shouldHave(text(subFieldText));
    }

    public void verifyYearFieldNotification(String subFieldText) {
        yearSub.shouldBe(visible);
        yearSub.shouldHave(text(subFieldText));
    }

    public void verifyOwnerFieldNotification(String subFieldText) {
        ownerSub.shouldBe(visible);
        ownerSub.shouldHave(text(subFieldText));
    }

    public void verifyCvcFieldNotification(String subFieldText) {
        cvcCodeSub.shouldBe(visible);
        cvcCodeSub.shouldHave(text(subFieldText));
    }
}
