/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.meshsync.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum EntityType {
    PERSON("info"),
    PERSON_AGENT("agent"),
    PERSON_ADDRESS("address"),
    PERSON_CONTACT("contact"),
    PERSON_DOCUMENT("document"),
    PERSON_EDUCATION("education"),
    PERSON_IDS("ids"),
    CATEGORY("categories"),
    PERSON_PREVENTION("prevention"),
    VALIDATION("validation"),
    CLASS("class");

    EntityType(String apiField){
        this.apiField = apiField;
    }

    @JsonCreator
    public static EntityType of(@JsonProperty("entity_name") String entityName){
        if(entityName == null){
            return null;
        }
        for(EntityType t : EntityType.values()){
            if(t.name().toLowerCase().equals(entityName)){
                return t;
            }
        }
        return null;
    }

    private String apiField;

    public String getApiField() {
        return apiField;
    }
}
