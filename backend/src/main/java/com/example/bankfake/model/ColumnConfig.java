package com.example.bankfake.model;

/**
 * DTO for the Configure step: a raw SQL column name,
 * its SQL data type, and an initial suggested business‐type.
 */
public class ColumnConfig {
    private String columnName;
    private String dataType;
    private String suggestion;

    public ColumnConfig() {}

    public ColumnConfig(String columnName, String dataType, String suggestion) {
        this.columnName = columnName;
        this.dataType   = dataType;
        this.suggestion = suggestion;
    }

    public String getColumnName() { return columnName; }
    public void setColumnName(String c) { this.columnName = c; }

    public String getDataType() { return dataType; }
    public void setDataType(String d) { this.dataType = d; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String s) { this.suggestion = s; }
}
