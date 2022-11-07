package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum ApplicationForFoodMezhvedState {
    NO_INFO(0, "Не подтверждено в мэш.межвед"),
    REQUEST_SENT(1, "Отправлен запрос"),
    CONFIRMED(2, "Подтверждено, или не требует подтверждения");

    private final Integer code;
    private final String description;
    private static Map<Integer, ApplicationForFoodMezhvedState> mapInt = new HashMap<Integer, ApplicationForFoodMezhvedState>();
    private static Map<String, ApplicationForFoodMezhvedState> mapStr = new HashMap<String, ApplicationForFoodMezhvedState>();
    static {
        for (ApplicationForFoodMezhvedState value : ApplicationForFoodMezhvedState.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    ApplicationForFoodMezhvedState(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ApplicationForFoodMezhvedState fromCode(Integer id) {
        return mapInt.get(id);
    }

    public static ApplicationForFoodMezhvedState fromDescription(String description) {
        return mapStr.get(description);
    }
}
