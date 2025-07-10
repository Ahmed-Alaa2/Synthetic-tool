package com.example.bankfake.model;

import java.util.Map;

public class ClassificationRequest {
    private ConnectionProperties connection;
    private int sampleSize;
    private Map<String, FakeColumnSpec> overrides;

  

    // getters & setters
    public ConnectionProperties getConnection() { return connection; }
    public void setConnection(ConnectionProperties connection) { this.connection = connection; }
    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int sampleSize) { this.sampleSize = sampleSize; }
    public Map<String, FakeColumnSpec> getOverrides() { return overrides; }
    public void setOverrides(Map<String, FakeColumnSpec> overrides) { this.overrides = overrides; }
}