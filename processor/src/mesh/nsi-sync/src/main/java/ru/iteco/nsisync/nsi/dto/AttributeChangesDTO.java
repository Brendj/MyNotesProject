package ru.iteco.nsisync.nsi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.iteco.nsisync.nsi.AttributeType;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeChangesDTO implements Serializable {
    private String name;
    private Integer fieldId;
    private AttributeType type;
    private ValueChangesDTO values;

    public AttributeChangesDTO(String name, Integer fieldId, AttributeType type, ValueChangesDTO values) {
        this.name = name;
        this.fieldId = fieldId;
        this.type = type;
        this.values = values;
    }

    public AttributeChangesDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public ValueChangesDTO getValues() {
        return values;
    }

    public void setValues(ValueChangesDTO values) {
        this.values = values;
    }
}
