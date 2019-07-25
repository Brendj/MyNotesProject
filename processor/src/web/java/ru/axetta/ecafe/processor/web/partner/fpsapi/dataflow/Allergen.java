/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow;

import org.codehaus.jackson.annotate.JsonProperty;

public class Allergen {
    @JsonProperty(value = "allergenid")
    private String allergenId;

    @JsonProperty(value = "allergenname")
    private String allergenName;

    @JsonProperty(value = "allergentypeid")
    private String allergenTypeId;

    @JsonProperty(value = "allergentypename")
    private String allergentTypeName;

    private String active;

    public Allergen(Long allergenId, String allergenName, Integer allergenTypeId, String allergentTypeName,
            Boolean active) {
        this.allergenId = (null == allergenId) ? "" : allergenId.toString();
        this.allergenName = allergenName;
        this.allergenTypeId = (null == allergenTypeId) ? "" : allergenTypeId.toString();
        this.allergentTypeName = allergentTypeName;
        this.active = (null == active || !active) ? "0" : "1";
    }

    public String getAllergenId() {
        return allergenId;
    }

    public void setAllergenId(String allergenId) {
        this.allergenId = allergenId;
    }

    public String getAllergenName() {
        return allergenName;
    }

    public void setAllergenName(String allergenName) {
        this.allergenName = allergenName;
    }

    public String getAllergenTypeId() {
        return allergenTypeId;
    }

    public void setAllergenTypeId(String allergenTypeId) {
        this.allergenTypeId = allergenTypeId;
    }

    public String getAllergentTypeName() {
        return allergentTypeName;
    }

    public void setAllergentTypeName(String allergentTypeName) {
        this.allergentTypeName = allergentTypeName;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
