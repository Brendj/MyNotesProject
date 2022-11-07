package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum AppMezhvedResponseType {
    ERROR(0, "Не подтверждено"),
    OK(1, "Подтверждено");

    private final Integer code;
    private final String description;
    private static Map<Integer, AppMezhvedResponseType> mapInt = new HashMap<Integer, AppMezhvedResponseType>();
    private static Map<String, AppMezhvedResponseType> mapStr = new HashMap<String, AppMezhvedResponseType>();
    static {
        for (AppMezhvedResponseType value : AppMezhvedResponseType.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    AppMezhvedResponseType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AppMezhvedResponseType fromCode(Integer id) {
        return mapInt.get(id);
    }

    public static AppMezhvedResponseType fromDescription(String description) {
        return mapStr.get(description);
    }
}
