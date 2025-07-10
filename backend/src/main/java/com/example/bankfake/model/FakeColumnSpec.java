package com.example.bankfake.model;

public class FakeColumnSpec {
    private String type;
    private String dataType;
    private String startsWith;
    private String endsWith;

    private Integer min;     // numeric lower bound
    private Integer max;     // numeric upper bound
    private Integer length;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getStartsWith() {
        return startsWith;
    }
    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }
    public String getEndsWith() {
        return endsWith;
    }
    public void setEndsWith(String endsWith) {
        this.endsWith = endsWith;
    }
    public Integer getMin() {
        return min;
    }
    public void setMin(Integer min) {
        this.min = min;
    }
    public Integer getMax() {
        return max;
    }
    public void setMax(Integer max) {
        this.max = max;
    }
     public Integer getLength() { return length; }
        public void setLength(Integer length) { this.length = length; }
}

