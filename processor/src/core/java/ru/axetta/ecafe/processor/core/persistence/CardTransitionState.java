/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum CardTransitionState {

    /*0*/ OWN(0, "Своя"),
    /*1*/ GIVEN_AWAY_NOT_SYNC(1, "Отдана не синхронизирована"),
    /*2*/ GIVEN_AWAY_SYNC(2, "Отдана синхронизирована"),
    /*3*/ BORROWED(4, "Заимствована");

    private final Integer code;
    private final String description;

    static Map<Integer,CardTransitionState> map = new HashMap<Integer,CardTransitionState>();
    static {
        for (CardTransitionState state : CardTransitionState.values()) {
            map.put(state.getCode(), state);
        }
    }

    private CardTransitionState(int code, String description) {
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

    public static CardTransitionState fromInteger(Integer value){
        return map.get(value);
    }
}