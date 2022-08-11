package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum AppMezhvedRequestType {
    DOCS(0, "Подтверждение паспорта"),
    GUARDIANSHIP(1, "Подтверждение родства");

    private final Integer code;
    private final String description;
    private static Map<Integer, AppMezhvedRequestType> mapInt = new HashMap<Integer, AppMezhvedRequestType>();
    private static Map<String, AppMezhvedRequestType> mapStr = new HashMap<String, AppMezhvedRequestType>();
    static {
        for (AppMezhvedRequestType value : AppMezhvedRequestType.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    AppMezhvedRequestType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AppMezhvedRequestType fromCode(Integer id) {
        return mapInt.get(id);
    }

    public static AppMezhvedRequestType fromDescription(String description) {
        return mapStr.get(description);
    }
}
