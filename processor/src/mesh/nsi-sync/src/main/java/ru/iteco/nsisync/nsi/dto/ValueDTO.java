package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueDTO implements Serializable {
    private String value;
    private String id; //TODO уточнить тип

    public ValueDTO(String value, String id) {
        this.value = value;
        this.id = id;
    }

    public ValueDTO() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
