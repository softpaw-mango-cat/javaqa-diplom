package ru.netology.data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.config.DatabaseConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SQLHelper {
    private static QueryRunner queryRunner = new QueryRunner();

    // Получить количество платежей
    public static long getPaymentsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM payment_entity";
        return queryRunner.query(DatabaseConfig.getConnection(), sql, new ScalarHandler<>());
    }

    // Получить последний платеж
    public static Map<String, Object> getLastPayment() throws SQLException {
        String sql = "SELECT * FROM payment_entity ORDER BY id DESC LIMIT 1";
        List<Map<String, Object>> results = queryRunner.query(
                DatabaseConfig.getConnection(), sql, new MapListHandler());
        return results.isEmpty() ? null : results.get(0);
    }

    // Получить количество кредитных заявок
    public static long getCreditRequestsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM credit_request_entity";
        return queryRunner.query(DatabaseConfig.getConnection(), sql, new ScalarHandler<>());
    }

    // Получить последнюю кредитную заявку
    public static Map<String, Object> getLastCreditRequest() throws SQLException {
        String sql = "SELECT * FROM credit_request_entity ORDER BY id DESC LIMIT 1";
        List<Map<String, Object>> results = queryRunner.query(
                DatabaseConfig.getConnection(), sql, new MapListHandler());
        return results.isEmpty() ? null : results.get(0);
    }

    // Получить количество заказов
    public static long getOrdersCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM order_entity";
        return queryRunner.query(DatabaseConfig.getConnection(), sql, new ScalarHandler<>());
    }

    // Получить последний заказ
    public static Map<String, Object> getLastOrder() throws SQLException {
        String sql = "SELECT * FROM order_entity ORDER BY id DESC LIMIT 1";
        List<Map<String, Object>> results = queryRunner.query(
                DatabaseConfig.getConnection(), sql, new MapListHandler());
        return results.isEmpty() ? null : results.get(0);
    }

    // ========== ОЧИСТКА ДАННЫХ ==========

    public static void clearAllTables() throws SQLException {
        // Сначала удаляем из order_entity (из-за внешних ключей)
        queryRunner.update(DatabaseConfig.getConnection(), "DELETE FROM order_entity");
        queryRunner.update(DatabaseConfig.getConnection(), "DELETE FROM payment_entity");
        queryRunner.update(DatabaseConfig.getConnection(), "DELETE FROM credit_request_entity");
    }
}
