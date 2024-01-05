package ru.clevertec.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Утилитарный класс для установления соединения с базой данных PostgreSQL.
 */
@Component
@RequiredArgsConstructor
public class DatabaseConnection {
    private final DataSource dataSource;

    /**
     * Получает соединение с базой данных.
     *
     * @return Объект типа Connection для работы с базой данных.
     * @throws RuntimeException если возникает ошибка при подключении к базе данных.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
