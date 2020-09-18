package ru.iteco.meshsync;

public enum EntityType {
    person("info"),
    person_agent("agent"),
    person_address("address"),
    person_contact("contact"),
    person_document("document"),
    person_education("education"),
    person_ids("ids"),
    category("categories"),
    person_prevention("prevention"),
    validation("validation");

    EntityType(String apiField){
        this.apiField = apiField;
    }

    private String apiField;

    public String getApiField() {
        return apiField;
    }
}
