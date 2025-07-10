package com.example.bankfake.model;

import java.util.List;
import java.util.Map;

public class UploadRequest {
    private ConnectionProperties connection;
    private List<Map<String, Object>> rows;

    public ConnectionProperties getConnection() {
        return connection;
    }
    public void setConnection(ConnectionProperties connection) {
        this.connection = connection;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }
    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }
}
