/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NSIRequestParam {
    private String field;
    private String operator;
    private String value;
    @JsonProperty("negate-operator")
    private Boolean negateOperator;

    public NSIRequestParam() {

    }

    public NSIRequestParam(String field, String operator, String value, Boolean negateOperator) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.negateOperator = negateOperator;
    }

    public NSIRequestParam(String field, String operator, Boolean negateOperator) {
        this.field = field;
        this.operator = operator;
        this.value = null;
        this.negateOperator = negateOperator;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getNegateOperator() {
        return negateOperator;
    }

    public void setNegateOperator(Boolean negateOperator) {
        this.negateOperator = negateOperator;
    }
}
