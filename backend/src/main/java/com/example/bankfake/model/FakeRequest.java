package com.example.bankfake.model;

import java.util.Map;

public class FakeRequest {
    private ConnectionProperties connection;
    private int rowCount;
    private Map<String, FakeColumnSpec> columns;

    // getters & setters
    public ConnectionProperties getConnection() { return connection; }
    public void setConnection(ConnectionProperties connection) { this.connection = connection; }
    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }
    public Map<String, FakeColumnSpec> getColumns() { return columns; }
    public void setColumns(Map<String, FakeColumnSpec> columns) { this.columns = columns; }
}