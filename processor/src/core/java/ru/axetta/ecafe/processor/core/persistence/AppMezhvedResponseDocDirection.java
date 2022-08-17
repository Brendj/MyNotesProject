package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum AppMezhvedResponseDocDirection {
    STARTING(0, "Начало льготы"),
    ENDING(1, "Окончание льготы");

    private final Integer code;
    private final String description;
    private static Map<Integer, AppMezhvedResponseDocDirection> mapInt = new HashMap<Integer, AppMezhvedResponseDocDirection>();
    private static Map<String, AppMezhvedResponseDocDirection> mapStr = new HashMap<String, AppMezhvedResponseDocDirection>();
    static {
        for (AppMezhvedResponseDocDirection value : AppMezhvedResponseDocDirection.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    AppMezhvedResponseDocDirection(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AppMezhvedResponseDocDirection fromCode(Integer id) {
        return mapInt.get(id);
    }

    public static AppMezhvedResponseDocDirection fromDescription(String description) {
        return mapStr.get(description);
    }
}
