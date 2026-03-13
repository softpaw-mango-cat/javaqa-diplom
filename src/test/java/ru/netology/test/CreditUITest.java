package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.OrderPage;

public class CreditUITest {

    OrderPage page = new OrderPage();

    @BeforeEach
    void setup() {
        page = Selenide
                .open("http://localhost:8080", OrderPage.class);
    }

    /*ПОЗИТИНЫЕ КЕЙСЫ
    Покупка тура в кредит с одобренной валидной карты*/
    @Test
    @DisplayName("Should Make Payment With Approved Card")
    void shouldMakePaymentWithApprovedCard() {
        DataHelper.CardInfo cardInfo = DataHelper.generateValidApprovedCard();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifySuccessNotification();
        page.verifySuccessNotificationText();
    }

    //НЕГАТИВНЫЕ КЕЙСЫ

    /*Покупка тура в кредит с отклоненной валидной карты*/
    @Test
    @DisplayName("Should Not Make Payment With Declined Card")
    void shouldNotMakePaymentWithDeclinedCard() {
        DataHelper.CardInfo cardInfo = DataHelper.generateValidDeclinedCard();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyErrorNotification();
        page.verifyErrorNotificationText();
    }

    /*Покупка тура в кредит с несуществующей карты (карты, которой нет в наборе карт)*/
    @Test
    @DisplayName("Should Not Make Payment With Non Existing Card")
    void shouldNotMakePaymentWithNonExistingCard() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("1234567890123456");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyErrorNotification();
        page.verifyErrorNotificationText();
    }

    //НЕГАТИВНЫЕ КЕЙСЫ - ВАЛИДАЦИЯ
    /*Валидация поля номер карты при ПОКУПКЕ В КРЕДИТ:
    отправка формы с пустым значением поля
    отправка формы с нулями
    отправка формы с некорректным номером (меньше чем 16 цифр - 1 и 15)*/

    @Test
    @DisplayName("Should Not Make Payment With Empty Card Field")
    void shouldNotMakePaymentWithEmptyCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Zero Card Field")
    void shouldNotMakePaymentWithZeroCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("0000000000000000");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With One Digit In Card Field")
    void shouldNotMakePaymentWithOneDigitInCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("2");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Less Than 16 In Card Field")
    void shouldNotMakePaymentWithLessThan16InCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("123412341234123");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /*Валидация поля месяц при ПОКУПКЕ В КРЕДИТ:
    отправка формы с пустым значением поля
    отправка формы с нулями
    отправка формы с некорректными значениями - 1 (одно число), 13 (невадидный месяц)*/
    @Test
    @DisplayName("Should Not Make Payment With Empty Month Field")
    void shouldNotMakePaymentWithEmptyMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth("");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Zero Month Field")
    void shouldNotMakePaymentWithZeroMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth("00");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyErrorNotification();
        page.verifyErrorNotificationText();
    }

    @Test
    @DisplayName("Should Not Make Payment With One Digit Month Field")
    void shouldNotMakePaymentWithOneDigitMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth("1");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Invalid Month Field")
    void shouldNotMakePaymentWithInvalidMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth("13");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверно указан срок действия карты");
    }

    /*Валидация поля год при ПОКУПКЕ В КРЕДИТ:
отправка формы с пустым значением поля
отправка формы с нулями
отправка формы с одним значением - 1
отправка формы с невалидным годом (в прошлом) - 15
отправка формы с невалидным годом (в будущем, более 10 лет) - 40*/
    @Test
    @DisplayName("Should Not Make Payment With Empty Year Field")
    void shouldNotMakePaymentWithEmptyYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear("");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Zero Year Field")
    void shouldNotMakePaymentWithZeroYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear("00");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Should Not Make Payment With One Digit Year Field")
    void shouldNotMakePaymentWithOneDigitYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear("1");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Past Year Field")
    void shouldNotMakePaymentWithPastYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear("15");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Should Not Make Payment With Invalid Future Year Field")
    void shouldNotMakePaymentWithInvalidFutureYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear("40");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверно указан срок действия карты");
    }

    /*Валидация поля владелец при ПОКУПКЕ В КРЕДИТ:
        отправка с пустым полем владелец
        имя владельца на кириллице
        слишком короткое имя (1 символ)
        слишком длинное имя (100 символов)
        спецсимволы в поле
        числа в поле*/
    @Test
    @DisplayName("Should Not Make Payment With Empty Owner Field")
    void shouldNotMakePaymentWithEmptyOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Should Not Make Payment With Owner Field In Cyrillic")
    void shouldNotMakePaymentWithOwnerFieldInCyrillic() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("Иван Иванович Иванов");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Too Short Owner Field")
    void shouldNotMakePaymentWithTooShortOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("S");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Too Long Owner Field")
    void shouldNotMakePaymentWithTooLongOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("HgTfRjKlMpQsWnXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqR");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Symbols In Owner Field")
    void shouldNotMakePaymentWithSymbolsInOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("!@#$%^&*()_+{}:<>?");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Digits In Owner Field")
    void shouldNotMakePaymentWithDigitsInOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("1234567890");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /*Валидация поля cvc при ПОКУПКЕ В КРЕДИТ:
    отправка с пустым полем cvc
    нули в поле
    1 цифра в поле
    2 цифры в поле*/
    @Test
    @DisplayName("Should Not Make Payment With Empty Cvc Field")
    void shouldNotMakePaymentWithEmptyCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC("");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Zero Cvc Field")
    void shouldNotMakePaymentWithZeroCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC("000");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With One Digit Cvc Field")
    void shouldNotMakePaymentWithOneDigitCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC("1");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    @Test
    @DisplayName("Should Not Make Payment With Two Digits Cvc Field")
    void shouldNotMakePaymentWithTwoDigitsCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC("12");
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }


}
