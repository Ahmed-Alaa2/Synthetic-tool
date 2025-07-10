// src/main/java/com/example/bankfake/config/DataSourceConfig.java latestt
package com.example.bankfake.config;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.example.bankfake.model.ConnectionProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class DataSourceConfig {

    /**
     * Build a brand-new HikariCP DataSource for the given connection + database.
     * Appends encrypt=true;trustServerCertificate=true to avoid SSL errors.
     */
    public DataSource createDataSource(ConnectionProperties props, String dbName) {
        // Build the URL
        String url = String.format(
            "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=true;trustServerCertificate=true",
            props.getHost(),
            props.getPort(),
            dbName
        );

        // Configure HikariCP
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(props.getUsername());
        cfg.setPassword(props.getPassword());
        // (Optional tuning:)
        // cfg.setMaximumPoolSize(10);
        // cfg.setConnectionTimeout(30000);

        return new HikariDataSource(cfg);
    }
}
