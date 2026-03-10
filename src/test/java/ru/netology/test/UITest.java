package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.OrderPage;

public class UITest {

    OrderPage page = new OrderPage();

    @BeforeEach
    void setup() {
        page = Selenide.open("http://localhost:8080", OrderPage.class);
    }

    // позитивные проверки
    @Test
    @DisplayName("Should Make Payment With Approved Card")
    void shouldMakePaymentWithApprovedCard() {
        DataHelper.CardInfo cardInfo = DataHelper.generateValidApprovedCard();
        page.payWithCard();
        page.fillAndSend(cardInfo);
        page.verifySuccessNotification();
        page.verifySuccessNotificationText();
    }
}
