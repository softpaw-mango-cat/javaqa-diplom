package ru.netology.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.OrderPage;

public class CreditUITest {

    private OrderPage page;

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
        SQLHelper.cleanDB();
    }

    /* ПОЗИТИВНЫЕ КЕЙСЫ
    TC-02: Успешная оплата тура в кредит по одобренной карте */
    @Test
    @DisplayName("Should Make Payment With Approved Card")
    void shouldMakePaymentWithApprovedCard() {
        DataHelper.CardInfo cardInfo = DataHelper.generateValidApprovedCard();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifySuccessNotificationText();

        var credit = SQLHelper.getLastCredit();
        var order = SQLHelper.getLastOrder();
        Assertions.assertAll(
                () -> Assertions.assertEquals("APPROVED", credit.getStatus()),
                () -> Assertions.assertNotNull(order.getCredit_id()),
                () -> Assertions.assertNull(order.getPayment_id()),
                () -> Assertions.assertEquals(credit.getBank_id(), order.getCredit_id())
        );
    }

    /* НЕГАТИВНЫЕ КЕЙСЫ
    TC-04: Отклонение оплаты тура в кредит по отклоненной карте */
    @Test
    @DisplayName("Should Not Make Payment With Declined Card")
    void shouldNotMakePaymentWithDeclinedCard() {
        DataHelper.CardInfo cardInfo = DataHelper.generateValidDeclinedCard();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyErrorNotificationText();

        var credit = SQLHelper.getLastCredit();
        var order = SQLHelper.getLastOrder();
        Assertions.assertAll(
                () -> Assertions.assertEquals("DECLINED", credit.getStatus()),
                () -> Assertions.assertNotNull(order.getCredit_id()),
                () -> Assertions.assertNull(order.getPayment_id()),
                () -> Assertions.assertEquals(credit.getBank_id(), order.getCredit_id())
        );
    }

    /* TC-06: Отклонение оплаты тура в кредит по карте не входящей в набор валидных карт */
    @Test
    @DisplayName("Should Not Make Payment With Non Existing Card")
    void shouldNotMakePaymentWithNonExistingCard() {
        String randomNumber = DataHelper.generateRandomCardNumber();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber(randomNumber);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyErrorNotificationText();

        var credits = SQLHelper.getAllCredits();
        var orders = SQLHelper.getAllOrders();
        Assertions.assertAll(
                () -> Assertions.assertTrue(credits.isEmpty()),
                () -> Assertions.assertTrue(orders.isEmpty())
        );
    }

    /* НЕГАТИВНЫЕ КЕЙСЫ - ВАЛИДАЦИЯ
    TC-30: Проверка валидации поля Номер карты при оплате тура в кредит - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Card Field")
    void shouldNotMakePaymentWithEmptyCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithEmptyNumber();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-31: Проверка валидации поля Номер карты при оплате тура в кредит - поле с нулями */
    @Test
    @DisplayName("Should Not Make Payment With Zero Card Field")
    void shouldNotMakePaymentWithZeroCardField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithZeroNumber();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-32: Проверка валидации поля Номер карты при оплате тура в кредит - неверный формат (1 цифра) */
    @Test
    @DisplayName("Should Not Make Payment With One Digit In Card Field")
    void shouldNotMakePaymentWithOneDigitInCardField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber(oneDigit);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-33: Проверка валидации поля Номер карты при оплате тура в кредит - неверный формат (15 цифр) */
    @Test
    @DisplayName("Should Not Make Payment With Less Than 16 In Card Field")
    void shouldNotMakePaymentWithLessThan16InCardField() {
        String cardNumber = DataHelper.generate15DigitCardNumber();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithNumber(cardNumber);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCardFieldNotification("Неверный формат");
    }

    /* TC-34: Проверка валидации поля Месяц при оплате тура в кредит - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Month Field")
    void shouldNotMakePaymentWithEmptyMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithEmptyMonth();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    /* TC-35: Проверка валидации поля Месяц при оплате тура в кредит - поле с нулями */
    @Test
    @DisplayName("Should Not Make Payment With Zero Month Field")
    void shouldNotMakePaymentWithZeroMonthField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithZeroMonth();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    /* TC-36: Проверка валидации поля Месяц при оплате тура в кредит - одна цифра в поле */
    @Test
    @DisplayName("Should Not Make Payment With One Digit Month Field")
    void shouldNotMakePaymentWithOneDigitMonthField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth(oneDigit);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверный формат");
    }

    /* TC-37: Проверка валидации поля Месяц при оплате тура в кредит - несуществующий месяц */
    @Test
    @DisplayName("Should Not Make Payment With Invalid Month Field")
    void shouldNotMakePaymentWithInvalidMonthField() {
        String invalidMonth = DataHelper.generateInvalidMonth();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithMonth(invalidMonth);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyMonthFieldNotification("Неверно указан срок действия карты");
    }

    /* TC-38: Проверка валидации поля Год при оплате тура в кредит - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Year Field")
    void shouldNotMakePaymentWithEmptyYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithEmptyYear();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверный формат");
    }

    /* TC-39: Проверка валидации поля Год при оплате тура в кредит - поле с нулями */
    @Test
    @DisplayName("Should Not Make Payment With Zero Year Field")
    void shouldNotMakePaymentWithZeroYearField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithZeroYear();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Истёк срок действия карты");
    }

    /* TC-40: Проверка валидации поля Год при оплате тура в кредит - одна цифра в поле */
    @Test
    @DisplayName("Should Not Make Payment With One Digit Year Field")
    void shouldNotMakePaymentWithOneDigitYearField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear(oneDigit);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверный формат");
    }

    /* TC-41: Проверка валидации поля Год при оплате тура в кредит - год в прошлом */
    @Test
    @DisplayName("Should Not Make Payment With Past Year Field")
    void shouldNotMakePaymentWithPastYearField() {
        String pastYear = DataHelper.generatePastYear();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear(pastYear);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Истёк срок действия карты");
    }

    /* TC-42: Проверка валидации поля Год при оплате тура в кредит - год в будущем */
    @Test
    @DisplayName("Should Not Make Payment With Invalid Future Year Field")
    void shouldNotMakePaymentWithInvalidFutureYearField() {
        String futureYear = DataHelper.generateFutureYear();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithYear(futureYear);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyYearFieldNotification("Неверно указан срок действия карты");
    }

    /* TC-43: Проверка валидации поля Владелец при оплате тура в кредит - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Owner Field")
    void shouldNotMakePaymentWithEmptyOwnerField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithEmptyOwner();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Поле обязательно для заполнения");
    }

    /* TC-44: Проверка валидации поля Владелец при оплате тура в кредит - кириллица */
    @Test
    @DisplayName("Should Not Make Payment With Owner Field In Cyrillic")
    void shouldNotMakePaymentWithOwnerFieldInCyrillic() {
        String cyrillicName = DataHelper.generateRandomRuName();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(cyrillicName);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-45: Проверка валидации поля Владелец при оплате тура в кредит - слишком короткое */
    @Test
    @DisplayName("Should Not Make Payment With Too Short Owner Field")
    void shouldNotMakePaymentWithTooShortOwnerField() {
        String shortString = DataHelper.generateRandomLetter();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(shortString);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-46: Проверка валидации поля Владелец при оплате тура в кредит - слишком длинное */
    @Test
    @DisplayName("Should Not Make Payment With Too Long Owner Field")
    void shouldNotMakePaymentWithTooLongOwnerField() {
        String longString = DataHelper.generateRandomLongString();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(longString);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-47: Проверка валидации поля Владелец при оплате тура в кредит - спецсимволы */
    @Test
    @DisplayName("Should Not Make Payment With Symbols In Owner Field")
    void shouldNotMakePaymentWithSymbolsInOwnerField() {
        String symbols = DataHelper.generateRandomSymbols();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(symbols);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-48: Проверка валидации поля Владелец при оплате тура в кредит - числа */
    @Test
    @DisplayName("Should Not Make Payment With Digits In Owner Field")
    void shouldNotMakePaymentWithDigitsInOwnerField() {
        String digits = DataHelper.generateRandomDigits();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithOwner(digits);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyOwnerFieldNotification("Неверный формат");
    }

    /* TC-49: Проверка валидации поля Cvc при оплате тура в кредит - пустое поле */
    @Test
    @DisplayName("Should Not Make Payment With Empty Cvc Field")
    void shouldNotMakePaymentWithEmptyCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithEmptyCVC();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    /* TC-50: Проверка валидации поля Cvc при оплате тура в кредит - нули в поле */
    @Test
    @DisplayName("Should Not Make Payment With Zero Cvc Field")
    void shouldNotMakePaymentWithZeroCvcField() {
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithZeroCVC();
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    /* TC-51: Проверка валидации поля Cvc при оплате тура в кредит - одна цифра в поле */
    @Test
    @DisplayName("Should Not Make Payment With One Digit Cvc Field")
    void shouldNotMakePaymentWithOneDigitCvcField() {
        String oneDigit = DataHelper.generateRandomOneDigit();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC(oneDigit);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }

    /* TC-52: Проверка валидации поля Cvc при оплате тура в кредит - две цифры в поле */
    @Test
    @DisplayName("Should Not Make Payment With Two Digits Cvc Field")
    void shouldNotMakePaymentWithTwoDigitsCvcField() {
        String twoDigits = DataHelper.generateRandomTwoDigits();
        DataHelper.CardInfo cardInfo = DataHelper
                .generateCardWithCVC(twoDigits);
        page.payWithCredit();
        page.fillAndSend(cardInfo);
        page.verifyCvcFieldNotification("Неверный формат");
    }
}

