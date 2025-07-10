package com.example.bankfake.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.bankfake.config.DataSourceConfig;
import com.example.bankfake.model.ConnectionProperties;

@Service
public class DatabaseService {

    @Autowired
    private DataSourceConfig dsConfig;

    /**
     * Connects to the "master" database and returns all database names.
     */
    public List<String> listDatabases(ConnectionProperties props) {
        DataSource ds   = dsConfig.createDataSource(props, "master");
        JdbcTemplate jt = new JdbcTemplate(ds);
        return jt.queryForList(
            "SELECT name FROM sys.databases",
            String.class
        );
    }

    /**
     * Connects to the given database and returns all user table names.
     */
    public List<String> listTables(ConnectionProperties props, String dbName) {
        DataSource ds   = dsConfig.createDataSource(props, dbName);
        JdbcTemplate jt = new JdbcTemplate(ds);
        String sql =
            "SELECT TABLE_NAME " +
            "FROM INFORMATION_SCHEMA.TABLES " +
            "WHERE TABLE_TYPE='BASE TABLE'";
        return jt.queryForList(sql, String.class);
    }

    /**
     * Fetches up to `limit` rows from the specified table (no aliases).
     */
    public List<Map<String, Object>> previewTable(
        ConnectionProperties props,
        String dbName,
        String table,
        int limit
    ) {
        DataSource ds   = dsConfig.createDataSource(props, dbName);
        JdbcTemplate jt = new JdbcTemplate(ds);
        String sql = String.format(
            "SELECT TOP %d * FROM %s",
            limit,
            table
        );
        return jt.queryForList(sql);
    }

    /**
     * Counts the total number of rows in the specified table.
     */
    public long countRows(
        ConnectionProperties props,
        String dbName,
        String table
    ) {
        DataSource ds   = dsConfig.createDataSource(props, dbName);
        JdbcTemplate jt = new JdbcTemplate(ds);
        String sql = String.format(
            "SELECT COUNT(*) FROM %s",
            table
        );
        return jt.queryForObject(sql, Long.class);
    }

    /**
     * Returns each raw SQL column name and its SQL data type (including length).
     */
    public List<Map<String,String>> listColumnTypes(
        ConnectionProperties props,
        String dbName,
        String table
    ) {
        DataSource ds   = dsConfig.createDataSource(props, dbName);
        JdbcTemplate jt = new JdbcTemplate(ds);
        String sql =
            "SELECT COLUMN_NAME, " +
            "       DATA_TYPE + " +
            "         COALESCE('('+CAST(CHARACTER_MAXIMUM_LENGTH AS VARCHAR)+')','') " +
            "         AS DATA_TYPE " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = ?";
        return jt.query(
            sql,
            new Object[]{ table },
            (rs, rowNum) -> Map.of(
                "columnName", rs.getString("COLUMN_NAME"),
                "dataType",   rs.getString("DATA_TYPE")
            )
        );
    }

    /**
     * Creates (or recreates) a new table named "<originalTable>Fake" in the same database:
     *   1) Drops it if it already exists,
     *   2) Copies the schema via "SELECT TOP 0 * INTO",
     *   3) Batch-inserts all provided rows.
     */
    public void saveFakeTable(
        ConnectionProperties props,
        String dbName,
        String originalTable,
        List<Map<String, Object>> rows
) {
    DataSource ds   = dsConfig.createDataSource(props, dbName);
    JdbcTemplate jt = new JdbcTemplate(ds);

    // quote the table name too, in case it has special chars
    String fakeTable = "[" + originalTable + "Fake]";

    // 1) Drop existing
    jt.execute(
      "IF OBJECT_ID('" + originalTable + "Fake','U') IS NOT NULL DROP TABLE " + fakeTable
    );

    // 2) Copy schema
    jt.execute(
      "SELECT TOP 0 * INTO " + fakeTable + " FROM [" + originalTable + "]"
    );

    if (rows == null || rows.isEmpty()) return;

    // 3) Build a bracketed, comma-separated column list
    List<String> cols = new ArrayList<>(rows.get(0).keySet());
    List<String> quotedCols = cols.stream()
        .map(c -> "[" + c + "]")
        .collect(Collectors.toList());
    String colList = String.join(", ", quotedCols);

    // 4) placeholders
    String placeholders = quotedCols.stream()
        .map(c -> "?")
        .collect(Collectors.joining(", "));

    String insertSql =
      "INSERT INTO " + fakeTable +
      " (" + colList + ") VALUES (" + placeholders + ")";

    // 5) Batch insert
    List<Object[]> batchArgs = rows.stream()
        .map(row ->
          cols.stream().map(row::get).toArray(Object[]::new)
        )
        .collect(Collectors.toList());

    jt.batchUpdate(insertSql, batchArgs);
}

}
