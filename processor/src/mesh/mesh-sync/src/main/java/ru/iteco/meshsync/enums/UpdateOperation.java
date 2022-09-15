package ru.iteco.meshsync.enums;

public enum UpdateOperation {
    FULL_UPDATE_OPERATION(1, "Полное обновление клиента"),
    DOCUMENTS_UPDATE_OPERATION(2, "Обновление документов клиента"),
    CONTACTS_UPDATE_OPERATION(3, "Обновление контактов клиента");

    private final Integer code;
    private final String description;

    UpdateOperation(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
