package ru.netology.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.config.DatabaseConfig;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import static io.restassured.RestAssured.given;

public class PostgreSQLTest {

    private static Gson gson = new Gson();

    @BeforeEach
    @SneakyThrows
    public void setUp() {
        DatabaseConfig.connectToPostgreSQL();
        SQLHelper.clearAllTables();
    }

    @AfterEach
    @SneakyThrows
    public void tearDown() {
        DatabaseConfig.closeConnection();
    }

    private String getApprovedCard() {
        var card = DataHelper.generateValidApprovedCard();
        JsonObject cardJson = new JsonObject();
        cardJson.addProperty("number", card.getNumber());
        cardJson.addProperty("year", card.getYear());
        cardJson.addProperty("month", card.getMonth());
        cardJson.addProperty("holder", card.getOwner());
        cardJson.addProperty("cvc", card.getCvc());
        return gson.toJson(card);
    }

    private String getDeclinedCard() {
        var card = DataHelper.generateValidDeclinedCard();
        JsonObject cardJson = new JsonObject();
        cardJson.addProperty("number", card.getNumber());
        cardJson.addProperty("year", card.getYear());
        cardJson.addProperty("month", card.getMonth());
        cardJson.addProperty("holder", card.getOwner());
        cardJson.addProperty("cvc", card.getCvc());
        return gson.toJson(card);
    }

    private Response getResponse(String endpoint, String card) {
        return given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.JSON)
                .body(card)
                .when()
                .post(endpoint);
    }

    // ========== ПРОВЕРКА ОТВЕТОВ СЕРВЕРА ==========

    @Test
    public void shouldSendStatusCode200WhenPaymentIsCorrect() {
        var approvedCard = getApprovedCard();
        var response = getResponse("/api/v1/pay", approvedCard);
        int statusCode = response.getStatusCode();

        Assertions.assertEquals(200, statusCode, "Статус  должен быть 200");
    }

    @Test
    public void shouldSendStatusCode200WhenCreditIsCorrect() {
        var approvedCard = getApprovedCard();
        var response = getResponse("/api/v1/credit", approvedCard);
        int statusCode = response.getStatusCode();

        Assertions.assertEquals(200, statusCode, "Статус  должен быть 200");
    }

    @Test
    public void shouldNotSendStatusCode200WhenPaymentIsDeclined() {
        var declinedCard = getDeclinedCard();
        var response = getResponse("/api/v1/pay", declinedCard);
        int statusCode = response.getStatusCode();

        Assertions.assertNotEquals(200, statusCode, "Статус не должен быть 200");
    }

    @Test
    public void shouldNotSendStatusCode200WhenCreditIsDeclined() {
        var declinedCard = getDeclinedCard();
        var response = getResponse("/api/v1/credit", declinedCard);
        int statusCode = response.getStatusCode();

        Assertions.assertNotEquals(200, statusCode, "Статус не должен быть 200");
    }

    // ========== ПОЗИТИВНЫЕ ПРОВЕРКИ ДЛЯ ОПЛАТЫ КАРТОЙ ==========
    // В таблице payment_entity появилась одна запись
    // В таблице payment_entity значение статуса карты - approved
    // В таблице payment_entity есть значение transaction_id, сохраним его
    // В таблице order_entity появилась запись
    // В таблице order_entity значение payment_id равно значению transaction_id из payment_entity
    // В таблице order_entity значение credit_id равно null
    // В таблице credit_request_entity записей нет

    @Test
    @SneakyThrows
    public void shouldSaveDataWithApprovedPaymentCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/pay", approvedCard);
        var payment = SQLHelper.getLastPayment();

        Assertions.assertNotNull(payment,
                "В таблице payment_entity запись о платеже должна существовать");
        Assertions.assertEquals(1, SQLHelper.getPaymentsCount(),
                "В таблице payment_entity должна быть ровно одна запись");}

    @Test
    @SneakyThrows
    public void shouldHaveCorrectStatusWithApprovedPaymentCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/pay", approvedCard);
        var payment = SQLHelper.getLastPayment();

        Assertions.assertEquals("APPROVED", payment.get("status"),
                "Статус карты в таблице payment_entity должен быть APPROVED");}

    @Test
    @SneakyThrows
    public void shouldSaveOrderDataWithApprovedPaymentCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/pay", approvedCard);
        var order = SQLHelper.getLastOrder();

        Assertions.assertNotNull(order,
                "В таблице order_entity запись о заказе должна существовать");
        Assertions.assertEquals(1, SQLHelper.getOrdersCount(),
                "В таблице order_entity должна быть ровно одна запись");}

    @Test
    @SneakyThrows
    public void shouldSaveCorrectTransactionWithApprovedPaymentCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/pay", approvedCard);
        var payment = SQLHelper.getLastPayment();
        var order = SQLHelper.getLastOrder();

        Assertions.assertNotNull(payment.get("transaction_id"),
                "В таблице payment_entity transaction_id не должен быть null");
        Assertions.assertEquals(payment.get("transaction_id"), order.get("payment_id"),
                "order_entity.payment_id должен быть равен payment_entity.transaction_id");}

    @Test
    @SneakyThrows
    public void shouldNotSaveCreditInfoWithApprovedPaymentCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/pay", approvedCard);
        var order = SQLHelper.getLastOrder();

        Assertions.assertNull(order.get("credit_id"),
                "В таблице order_entity для обычной оплаты credit_id должен быть null");
        Assertions.assertEquals(0, SQLHelper.getCreditRequestsCount(),
                "При обычной оплате не должно быть записей в credit_request_entity");}


    // ========== ПОЗИТИВНЫЕ ПРОВЕРКИ ДЛЯ ОПЛАТЫ В КРЕДИТ ==========
    // В таблице credit_request_entity появилась одна запись
    // В таблице credit_request_entity значение статуса карты - approved
    // В таблице credit_request_entity есть значение bank_id, сохраним его
    // В таблице order_entity появилась запись
    // В таблице order_entity значение credit_id равно значению bank_id из credit_request_entity
    // В таблице order_entity значение payment_id равно null
    // В таблице payment_entity записей нет

    @Test
    @SneakyThrows
    public void shouldSaveDataWithApprovedCreditCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/credit", approvedCard);
        var credit = SQLHelper.getLastCreditRequest();

        Assertions.assertNotNull(credit,
                "В таблице credit_request_entity запись о платеже в кредит должна существовать");
        Assertions.assertEquals(1, SQLHelper.getCreditRequestsCount(),
                "В таблице credit_request_entity должна быть ровно одна запись");}

    @Test
    @SneakyThrows
    public void shouldHaveCorrectStatusWithApprovedCreditCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/credit", approvedCard);
        var credit = SQLHelper.getLastCreditRequest();

        Assertions.assertEquals("APPROVED", credit.get("status"),
                "Статус карты в таблице credit_request_entity должен быть APPROVED");}

    @Test
    @SneakyThrows
    public void shouldSaveOrderDataWithApprovedCreditCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/credit", approvedCard);
        var order = SQLHelper.getLastOrder();

        Assertions.assertNotNull(order,
                "В таблице order_entity запись о заказе должна существовать");
        Assertions.assertEquals(1, SQLHelper.getOrdersCount(),
                "В таблице order_entity должна быть ровно одна запись");}

    @Test
    @SneakyThrows
    public void shouldSaveCorrectTransactionWithApprovedCreditCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/credit", approvedCard);
        var credit = SQLHelper.getLastCreditRequest();
        var order = SQLHelper.getLastOrder();

        Assertions.assertNotNull(credit.get("bank_id"),
                "В таблице credit_request_entity bank_id не должен быть null");
        Assertions.assertEquals(order.get("credit_id"), credit.get("bank_id"),
                "order_entity.credit_id должен быть равен credit_request_entity.bank_id");}

    @Test
    @SneakyThrows
    public void shouldNotSavePaymentInfoWithApprovedCreditCard() {
        var approvedCard = getApprovedCard();
        getResponse("/api/v1/credit", approvedCard);
        var order = SQLHelper.getLastOrder();

        Assertions.assertNull(order.get("payment_id"),
                "В таблице order_entity при оплате в кредит payment_id должен быть null");
        Assertions.assertEquals(0, SQLHelper.getPaymentsCount(),
                "При оплате в кредит не должно быть записей в payment_entity");}

    // ========== НЕГАТИВНЫЕ ПРОВЕРКИ ПРИ ОПЛАТЕ КАРТОЙ DECLINED ==========
    // В таблице payment_entity нет записей
    // В таблице order_entity нет записей
    // В таблице credit_request_entity нет записей

    @Test
    @SneakyThrows
    public void shouldNotSavePaymentDataWithDeclinedPaymentCard() {
        var declinedCard = getDeclinedCard();
        getResponse("/api/v1/pay", declinedCard);

        Assertions.assertEquals(0, SQLHelper.getPaymentsCount(),
                "В таблице payment_entity не должно быть записей");}

    @Test
    @SneakyThrows
    public void shouldNotSaveOrderDataWithDeclinedPaymentCard() {
        var declinedCard = getDeclinedCard();
        getResponse("/api/v1/pay", declinedCard);

        Assertions.assertEquals(0, SQLHelper.getOrdersCount(),
                "В таблице order_entity не должно быть записей");}

    @Test
    @SneakyThrows
    public void shouldNotSaveCreditDataWithDeclinedPaymentCard() {
        var declinedCard = getDeclinedCard();
        getResponse("/api/v1/pay", declinedCard);

        Assertions.assertEquals(0, SQLHelper.getCreditRequestsCount(),
                "В таблице credit_request_entity не должно быть записей");}

    // ========== НЕГАТИВНЫЕ ПРОВЕРКИ ПРИ ОПЛАТЕ В КРЕДИТ КАРТОЙ DECLINED ==========
    // В таблице payment_entity нет записей
    // В таблице order_entity нет записей
    // В таблице credit_request_entity нет записей

    @Test
    @SneakyThrows
    public void shouldNotSavePaymentDataWithDeclinedCreditCard() {
        var declinedCard = getDeclinedCard();
        getResponse("/api/v1/credit", declinedCard);

        Assertions.assertEquals(0, SQLHelper.getPaymentsCount(),
                "В таблице payment_entity не должно быть записей");}

    @Test
    @SneakyThrows
    public void shouldNotSaveOrderDataWithDeclinedCreditCard() {
        var declinedCard = getDeclinedCard();
        getResponse("/api/v1/credit", declinedCard);

        Assertions.assertEquals(0, SQLHelper.getOrdersCount(),
                "В таблице order_entity не должно быть записей");}

    @Test
    @SneakyThrows
    public void shouldNotSaveCreditDataWithDeclinedCreditCard() {
        var declinedCard = getDeclinedCard();
        getResponse("/api/v1/credit", declinedCard);

        Assertions.assertEquals(0, SQLHelper.getCreditRequestsCount(),
                "В таблице credit_request_entity не должно быть записей");}
}
