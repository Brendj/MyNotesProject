package ru.iteco.restservice.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum PreorderState {
    /*0*/ OK(0, "ОК"),
    /*1*/ DELETED(1, "Удалено поставщиком"),
    /*2*/ CHANGED_PRICE(2, "Изменение цены у поставщика"),
    /*3*/ NOT_ENOUGH_BALANCE(3, "Недостаточно средств на балансе л/с"),
    /*4*/ CHANGE_ORG(4, "Перевод в другую ОО"),
    /*5*/ CHANGED_CALENDAR(5, "Изменение календаря учебных дней"),
    /*6*/ PREORDER_OFF(6, "Функционал «предзаказ» выключен в ОО");

    private final Integer code;
    private final String description;

    static Map<Integer,PreorderState> map = new HashMap<Integer,PreorderState>();
    static {
        for (PreorderState status : PreorderState.values()) {
            map.put(status.getCode(), status);
        }
    }

    private PreorderState(int code, String description) {
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

    public static PreorderState fromInteger(Integer value){
        return map.get(value);
    }
}
