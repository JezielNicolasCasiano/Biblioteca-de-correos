package com.library.emaillibrary.util;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseConnection {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:oracle:thin:@//129.153.216.4:1521/FREEPDB1");
            config.setUsername("biblioteca_app");
            config.setPassword("oracle1");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);

            dataSource = new HikariDataSource(config);
        } catch (RuntimeException e) {
            System.err.println("ERROR CRÍTICO AL INICIAR EL POOL:");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private DataBaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}


