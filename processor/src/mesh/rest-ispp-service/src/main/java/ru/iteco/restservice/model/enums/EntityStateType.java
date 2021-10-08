package ru.iteco.restservice.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nuc on 06.05.2021.
 */
public enum EntityStateType {
    ACTIVE("Активно"),
    ARCHIVE("Архивировано"),
    DELETED("Удалено");
    private static Map<Integer, EntityStateType> map = new HashMap<>();

    private String name;

    static {
        for (EntityStateType state : EntityStateType.values()) {
            map.put(state.ordinal(), state);
        }
    }

    EntityStateType(String name) {
        this.name = name;
    }

    public static EntityStateType getByInt(Integer id) {
        return map.get(id);
    }

    public String getName() {
        return name;
    }
}
