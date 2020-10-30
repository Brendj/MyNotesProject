/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Parameter {
    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    public Parameter(){
    }

    public Parameter(String name, Object o) {
        this.name = name;
        this.value = String.valueOf(o);
    }

    public Parameter(PropertyField field, Object o) {
        this.name = field.getFieldName();
        this.value = String.valueOf(o);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
