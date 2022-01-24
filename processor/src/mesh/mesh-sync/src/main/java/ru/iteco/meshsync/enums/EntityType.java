/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.meshsync.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum EntityType {
    PERSON("person"),
    PERSON_AGENT("person_agent"),
    PERSON_ADDRESS("person_address"),
    PERSON_CONTACT("person_contact"),
    PERSON_DOCUMENT("person_document"),
    PERSON_EDUCATION("person_education"),
    CATEGORY("person_category"),
    PERSON_PREVENTION("person_prevention"),
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
