package ru.netology.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.OrderPage;

public class PaymentUITest {

    OrderPage page = new OrderPage();

    @BeforeAll
    public static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        page = Selenide
                .open("http://localhost:8080", OrderPage.class);
    }

    /* ПОЗИТИВНЫЕ КЕЙСЫ
    TC-01: Успешная оплата тура по одобренной карте
    Шаги 1-3 */
    @Test
    @DisplayName("Should Make Payment With Approved Card")
    void shouldMakePaymentWithApprovedCard() {
        DataHelper.CardInfo cardInfo = DataHelper.generateValidApprovedCard();
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifySuccessNotification();
        page.verifySuccessNotificationText();
    }

    /* НЕГАТИВНЫЕ КЕЙСЫ
    TC-03: Отклонение оплаты тура по отклоненной карте
    Шаги 1-3 */
    @Test
    @DisplayName("Should Not Make Payment With Declined Card")
    void shouldNotMakePaymentWithDeclinedCard() {
        DataHelper.CardInfo cardInfo = DataHelper.generateValidDeclinedCard();
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyErrorNotification();
        page.verifyErrorNotificationText();
    }

    /* TC-05: Отклонение оплаты тура по карте не входящей в набор валидных карт
    Шаги 1-3 */
    @Test
    @DisplayName("Should Not Make Payment With Non Existing Card")
    void shouldNotMakePaymentWithNonExistingCard() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("1234567812345678");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyErrorNotification();
        page.verifyErrorNotificationText();
    }

    /* НЕГАТИВНЫЕ КЕЙСЫ - ВАЛИДАЦИЯ
    TC-07: Проверка валидации поля Номер карты при обычной оплате тура - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Card Field")
    void shouldNotMakePaymentWithEmptyCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-08: Проверка валидации поля Номер карты при обычной оплате тура - поле с нулями */
    @Test
    @DisplayName("Should Not Make Payment With Zero Card Field")
    void shouldNotMakePaymentWithZeroCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("0000000000000000");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-09: Проверка валидации поля Номер карты при обычной оплате тура - неверный формат (1 цифра) */
    @Test
    @DisplayName("Should Not Make Payment With One Digit In Card Field")
    void shouldNotMakePaymentWithOneDigitInCardField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber(oneDigit);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-10: Проверка валидации поля Номер карты при обычной оплате тура - неверный формат (15 цифр) */
    @Test
    @DisplayName("Should Not Make Payment With Less Than 16 In Card Field")
    void shouldNotMakePaymentWithLessThan16InCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber("444444444444444");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-11: Проверка валидации поля Месяц при обычной оплате тура - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Month Field")
    void shouldNotMakePaymentWithEmptyMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth("");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    /* TC-12: Проверка валидации поля Месяц при обычной оплате тура - поле с нулями */
    @Test
    @DisplayName("Should Not Make Payment With Zero Month Field")
    void shouldNotMakePaymentWithZeroMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth("00");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    /* TC-13: Проверка валидации поля Месяц при обычной оплате тура - одна цифра в поле */
    @Test
    @DisplayName("Should Not Make Payment With One Digit Month Field")
    void shouldNotMakePaymentWithOneDigitMonthField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth(oneDigit);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    /* TC-14: Проверка валидации поля Месяц при обычной оплате тура - несуществующий месяц */
    @Test
    @DisplayName("Should Not Make Payment With Invalid Month Field")
    void shouldNotMakePaymentWithInvalidMonthField() {
        String invalidMonth = DataHelper.generateInvalidMonth();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth(invalidMonth);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверно указан срок действия карты");
    }

    /* TC-15: Проверка валидации поля Год при обычной оплате тура - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Year Field")
    void shouldNotMakePaymentWithEmptyYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear("");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверный формат");
    }

    /* TC-16: Проверка валидации поля Год при обычной оплате тура - поле с нулями */
    @Test
    @DisplayName("Should Not Make Payment With Zero Year Field")
    void shouldNotMakePaymentWithZeroYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear("00");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Истёк срок действия карты");
    }

    /* TC-17: Проверка валидации поля Год при обычной оплате тура - одна цифра в поле */
    @Test
    @DisplayName("Should Not Make Payment With One Digit Year Field")
    void shouldNotMakePaymentWithOneDigitYearField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear(oneDigit);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверный формат");
    }

    /* TC-18: Проверка валидации поля Год при обычной оплате тура - год в прошлом */
    @Test
    @DisplayName("Should Not Make Payment With Past Year Field")
    void shouldNotMakePaymentWithPastYearField() {
        String pastYear = DataHelper.generatePastYear();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear(pastYear);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Истёк срок действия карты");
    }

    /* TC-19: Проверка валидации поля Год при обычной оплате тура - год в будущем */
    @Test
    @DisplayName("Should Not Make Payment With Invalid Future Year Field")
    void shouldNotMakePaymentWithInvalidFutureYearField() {
        String futureYear = DataHelper.generateFutureYear();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear(futureYear);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверно указан срок действия карты");
    }

     /* TC-20: Проверка валидации поля Владелец при обычной оплате тура - пустое поле */
     @Test
     @DisplayName("Should Not Make Payment With Empty Owner Field")
     void shouldNotMakePaymentWithEmptyOwnerField() {
         DataHelper.CardInfo cardInfo = DataHelper
                 .generateCardWithOwner("");
         page.payWithCard();
         page.fillAndSend(cardInfo);
         page.verifyOwnerFieldNotification("Поле обязательно для заполнения");
     }

    /* TC-21: Проверка валидации поля Владелец при обычной оплате тура - кириллица */
    @Test
    @DisplayName("Should Not Make Payment With Owner Field In Cyrillic")
    void shouldNotMakePaymentWithOwnerFieldInCyrillic() {
        String cyrillicName = DataHelper.generateRandomRuName();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(cyrillicName);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-22: Проверка валидации поля Владелец при обычной оплате тура - слишком короткое */
    @Test
    @DisplayName("Should Not Make Payment With Too Short Owner Field")
    void shouldNotMakePaymentWithTooShortOwnerField() {
        String shortString = DataHelper.generateRandomLetter();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(shortString);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-23: Проверка валидации поля Владелец при обычной оплате тура - слишком длинное */
    @Test
    @DisplayName("Should Not Make Payment With Too Long Owner Field")
    void shouldNotMakePaymentWithTooLongOwnerField() {
        String longString = DataHelper.generateRandomLongString();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(longString);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-24: Проверка валидации поля Владелец при обычной оплате тура - спецсимволы */
    @Test
    @DisplayName("Should Not Make Payment With Symbols In Owner Field")
    void shouldNotMakePaymentWithSymbolsInOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("!@#$%^&*()_+{}:<>?");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-25: Проверка валидации поля Владелец при обычной оплате тура - числа */
    @Test
    @DisplayName("Should Not Make Payment With Digits In Owner Field")
    void shouldNotMakePaymentWithDigitsInOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner("1234567890");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-26: Проверка валидации поля Cvc при обычной оплате тура - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Cvc Field")
    void shouldNotMakePaymentWithEmptyCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC("");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    /* TC-27: Проверка валидации поля Cvc при обычной оплате тура - нули в поле */
    @Test
    @DisplayName("Should Not Make Payment With Zero Cvc Field")
    void shouldNotMakePaymentWithZeroCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC("000");
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    /* TC-28: Проверка валидации поля Cvc при обычной оплате тура - одна цифра в поле */
    @Test
    @DisplayName("Should Not Make Payment With One Digit Cvc Field")
    void shouldNotMakePaymentWithOneDigitCvcField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC(oneDigit);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    /* TC-29: Проверка валидации поля Cvc при обычной оплате тура - две цифры в поле */
    @Test
    @DisplayName("Should Not Make Payment With Two Digits Cvc Field")
    void shouldNotMakePaymentWithTwoDigitsCvcField() {
        String twoDigits = DataHelper.generateRandomTwoDigits();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC(twoDigits);
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }
}
