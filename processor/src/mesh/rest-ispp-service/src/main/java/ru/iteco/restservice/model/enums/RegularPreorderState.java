package ru.iteco.restservice.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum RegularPreorderState {
    /*0*/ CHANGE_BY_USER(0, "Изменено пользователем"),
    /*1*/ CHANGE_BY_SERVICE(1, "Изменено сервисом");

    private final Integer code;
    private final String description;

    static Map<Integer,RegularPreorderState> map = new HashMap<Integer,RegularPreorderState>();
    static {
        for (RegularPreorderState status : RegularPreorderState.values()) {
            map.put(status.getCode(), status);
        }
    }

    private RegularPreorderState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static RegularPreorderState fromInteger(Integer value){
        return map.get(value);
    }
}

