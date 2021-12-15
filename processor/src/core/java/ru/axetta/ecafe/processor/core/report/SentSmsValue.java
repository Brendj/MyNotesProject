package ru.axetta.ecafe.processor.core.report;

public class SentSmsValue {
    private String date;
    private String value;

    public SentSmsValue(String date, String value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
