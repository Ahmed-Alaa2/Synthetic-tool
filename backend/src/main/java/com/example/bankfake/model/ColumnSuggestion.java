package com.example.bankfake.model;

public class ColumnSuggestion {
    private String columnName;   // will now hold raw name: “col1”, “col2”, …
    private String suggestion;   // type: “CUSTOMER_NAME”, “ADDRESS”, etc.

    public ColumnSuggestion() { }

    public ColumnSuggestion(String columnName, String suggestion) {
        this.columnName = columnName;
        this.suggestion  = suggestion;
    }

    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getSuggestion() {
        return suggestion;
    }
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
