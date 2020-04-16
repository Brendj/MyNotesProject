package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * User: shamil
 * Date: 27.04.15
 * Time: 16:37
 */
public enum CardState {
    UNKNOWN(100,"Неизвестная карта"),//Карта еще не присутствует в системе
    FREE(5,"Свободна к выдаче"),//Карту можно выдать клиенту системы или посетителю
    ISSUED(0,"Выдана (активна)"),//Карта выдана клиенту или посетителю
    TEMPISSUED(4,"Выдана временно(временно активна)"),//Клиенту выдан «дубликат»  его основной карты, на небольшое время (не превышающее время работы основной карты)
    TEMPBLOCKED(1,"Временно заблокирована"),//Карта запрещена к обслуживанию, запрещены все операции кроме 7 и 8
    BLOCKED(6,"Заблокирована"); //Карта запрещена к обслуживанию, запрещены все операции кроме 8

    private final int value;
    private final String description;

    static Map<Integer,CardState> map = new HashMap<Integer,CardState>();
    static {
        for (CardState state : CardState.values()) {
            map.put(state.getValue(), state);
        }
    }

    private CardState(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static String fromInteger(Integer value) {
        return map.get(value).getDescription();
    }
}
