package ru.netology.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.config.DatabaseConfig;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Map;

public class MySQLTest {

    private static Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws SQLException {
        DatabaseConfig.connectToMySQL();
        SQLHelper.clearAllTables();
    }

    @AfterEach
    public void tearDown() throws SQLException {
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

        assertEquals(200, statusCode, "Статус  должен быть 200");
    }

    @Test
    public void shouldSendStatusCode200WhenCreditIsCorrect() {
        var approvedCard = getApprovedCard();
        var response = getResponse("/api/v1/credit", approvedCard);
        int statusCode = response.getStatusCode();

        assertEquals(200, statusCode, "Статус  должен быть 200");
    }

    @Test
    public void shouldNotSendStatusCode200WhenPaymentIsDeclined() {
        var declinedCard = getDeclinedCard();
        var response = getResponse("/api/v1/pay", declinedCard);
        int statusCode = response.getStatusCode();

        assertNotEquals(200, statusCode, "Статус не должен быть 200");
    }

    @Test
    public void shouldNotSendStatusCode200WhenCreditIsDeclined() {
        var declinedCard = getDeclinedCard();
        var response = getResponse("/api/v1/credit", declinedCard);
        int statusCode = response.getStatusCode();

        assertNotEquals(200, statusCode, "Статус не должен быть 200");
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
    public void shouldSavePaymentDataWhenCardIsApproved() throws SQLException {
        var approvedCard = getApprovedCard();
        // Отправляем POST-запрос на оплату
        getResponse("/api/v1/pay", approvedCard);

        var payment = SQLHelper.getLastPayment();
        var order = SQLHelper.getLastOrder();

        assertAll("Проверка успешной оплаты картой со статусом Approved",
                () -> assertNotNull(payment, "В таблице payment_entity запись о платеже должна существовать"),
                () -> assertEquals(1, SQLHelper.getPaymentsCount(), "В таблице payment_entity должна быть ровно одна запись"),
                () -> assertEquals("APPROVED", payment.get("status"), "Статус карты в таблице payment_entity должен быть APPROVED"),
                () -> assertNotNull(payment.get("transaction_id"), "В таблице payment_entity transaction_id не должен быть null"),
                () -> assertEquals(1, SQLHelper.getOrdersCount(), "В таблице order_entity должна быть ровно одна запись"),
                () -> assertNotNull(order, "В таблице order_entity запись о заказе должна существовать"),
                () -> assertEquals(payment.get("transaction_id"), order.get("payment_id"), "order_entity.payment_id должен быть равен payment_entity.transaction_id"),
                () -> assertNull(order.get("credit_id"), "В таблице order_entity для обычной оплаты credit_id должен быть null"),
                () -> assertEquals(0, SQLHelper.getCreditRequestsCount(), "При обычной оплате не должно быть записей в credit_request_entity")
        );
    }

    // ========== ПОЗИТИВНЫЕ ПРОВЕРКИ ДЛЯ ОПЛАТЫ В КРЕДИТ ==========
    // В таблице credit_request_entity появилась одна запись
    // В таблице credit_request_entity значение статуса карты - approved
    // В таблице credit_request_entity есть значение bank_id, сохраним его
    // В таблице order_entity появилась запись
    // В таблице order_entity значение credit_id равно значению bank_id из credit_request_entity
    // В таблице order_entity значение payment_id равно null
    // В таблице payment_entity записей нет

    @Test
    public void shouldSaveCreditDataWhenCardIsApproved() throws SQLException {
        var approvedCard = getApprovedCard();
        // Отправляем POST-запрос на оплату в кредит
        getResponse("/api/v1/credit", approvedCard);

        var credit = SQLHelper.getLastCreditRequest();
        var order = SQLHelper.getLastOrder();

        assertAll("Проверка успешной оплаты в кредит со статусом Approved",
                () -> assertNotNull(credit, "В таблице credit_request_entity запись о платеже в кредит должна существовать"),
                () -> assertEquals(1, SQLHelper.getCreditRequestsCount(), "В таблице credit_request_entity должна быть ровно одна запись"),
                () -> assertEquals("APPROVED", credit.get("status"), "Статус карты в таблице credit_request_entity должен быть APPROVED"),
                () -> assertNotNull(credit.get("bank_id"), "В таблице credit_request_entity bank_id не должен быть null"),
                () -> assertEquals(1, SQLHelper.getOrdersCount(), "В таблице order_entity должна быть ровно одна запись"),
                () -> assertNotNull(order, "В таблице order_entity запись о заказе должна существовать"),
                () -> assertEquals(order.get("credit_id"), credit.get("bank_id"), "order_entity.credit_id должен быть равен credit_request_entity.bank_id"),
                () -> assertNull(order.get("payment_id"), "В таблице order_entity при оплате в кредит payment_id должен быть null"),
                () -> assertEquals(0, SQLHelper.getPaymentsCount(), "При оплате в кредит не должно быть записей в payment_entity")
        );
    }

    // ========== НЕГАТИВНЫЕ ПРОВЕРКИ ПРИ ОПЛАТЕ КАРТОЙ DECLINED ==========
    // В таблице payment_entity нет записей
    // В таблице order_entity нет записей
    // В таблице credit_request_entity нет записей

    @Test
    public void shouldNotSavePaymentDataWhenCardIsDeclined() throws SQLException {
        var declinedCard = getDeclinedCard();
        // Отправляем POST-запрос на оплату
        getResponse("/api/v1/pay", declinedCard);

        assertAll("Проверка отсутствия сохранения данных при оплате картой со статусом Declined",
                () -> assertEquals(0, SQLHelper.getPaymentsCount(), "В таблице payment_entity не должно быть записей"),
                () -> assertEquals(0, SQLHelper.getOrdersCount(), "В таблице order_entity не должно быть записей"),
                () -> assertEquals(0, SQLHelper.getCreditRequestsCount(), "В таблице credit_request_entity не должно быть записей")
        );
    }

    // ========== НЕГАТИВНЫЕ ПРОВЕРКИ ПРИ ОПЛАТЕ В КРЕДИТ КАРТОЙ DECLINED ==========
    // В таблице payment_entity нет записей
    // В таблице order_entity нет записей
    // В таблице credit_request_entity нет записей
    @Test
    public void shouldNotSaveCreditDataWhenCardIsDeclined() throws SQLException {
        var declinedCard = getDeclinedCard();
        // Отправляем POST-запрос на оплату
        getResponse("/api/v1/credit", declinedCard);
        long paymentsCount = SQLHelper.getPaymentsCount();
        long ordersCount = SQLHelper.getOrdersCount();
        long creditCount = SQLHelper.getCreditRequestsCount();

        assertAll("Проверка отсутствия сохранения данных при оплате в кредит картой со статусом Declined",
                () -> assertEquals(0, paymentsCount, "В таблице payment_entity не должно быть записей"),
                () -> assertEquals(0, ordersCount, "В таблице order_entity не должно быть записей"),
                () -> assertEquals(0, creditCount, "В таблице credit_request_entity не должно быть записей")
        );
    }
    }