package ru.netology.data;

import lombok.SneakyThrows;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(
                System.getProperty("db.url"), "app", "pass");
    }

    @SneakyThrows
    public static PaymentEntity getLastPayment() {
        var sql = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(conn, sql, new BeanHandler<>(PaymentEntity.class));
        }
    }

    @SneakyThrows
    public static OrderEntity getLastOrder() {
        var sql = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(conn, sql, new BeanHandler<>(OrderEntity.class));
        }
    }

    @SneakyThrows
    public static CreditEntity getLastCredit() {
        var sql = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(conn, sql, new BeanHandler<>(CreditEntity.class));
        }
    }

    @SneakyThrows
    public static List<PaymentEntity> getAllPayments() {
        var sql = "SELECT * FROM payment_entity";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(conn, sql, new BeanListHandler<>(PaymentEntity.class));
        }
    }

    @SneakyThrows
    public static List<CreditEntity> getAllCredits() {
        var sql = "SELECT * FROM credit_request_entity";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(conn, sql, new BeanListHandler<>(CreditEntity.class));
        }
    }

    @SneakyThrows
    public static List<OrderEntity> getAllOrders() {
        var sql = "SELECT * FROM order_entity";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(conn, sql, new BeanListHandler<>(OrderEntity.class));
        }
    }

    @SneakyThrows
    public static void cleanDB() {
        try (var conn = connect()) {
            QUERY_RUNNER.update(conn, "DELETE FROM payment_entity");
            QUERY_RUNNER.update(conn, "DELETE FROM credit_request_entity");
            QUERY_RUNNER.update(conn, "DELETE FROM order_entity");
        }
    }
}



