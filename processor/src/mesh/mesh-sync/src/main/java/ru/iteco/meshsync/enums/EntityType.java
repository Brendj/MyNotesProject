/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.meshsync.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum EntityType {
    PERSON("person", null),
    PERSON_AGENT("person_agent", "agent"),
    PERSON_ADDRESS("person_address", "address"),
    PERSON_CONTACT("person_contact", "contact"),
    PERSON_DOCUMENT("person_document", "document"),
    PERSON_EDUCATION("person_education", "education"),
    CATEGORY("person_category", "categories"),
    PERSON_PREVENTION("person_prevention", "prevention"),
    CLASS("class","class");

    EntityType(String entityName, String apiField){
        this.entityName = entityName;
        this.apiField = apiField;
    }

    @JsonCreator
    public static EntityType of(@JsonProperty("entity_name") String entityName){
        if(entityName == null){
            return null;
        }
        for(EntityType t : EntityType.values()){
            if(t.getEntityName().equals(entityName)){
                return t;
            }
        }
        return null;
    }

    private final String entityName;
    private final String apiField;

    public String getApiField() {
        return apiField;
    }

    public String getEntityName() {
        return entityName;
    }
}
