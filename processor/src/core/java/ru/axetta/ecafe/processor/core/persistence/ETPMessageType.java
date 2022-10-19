package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum ETPMessageType {
    ZLP(0, "ЗЛП"),
    PROACTIVE(1, "Проактив");

    private final Integer code;
    private final String description;

    static Map<Integer, ETPMessageType> map = new HashMap<Integer, ETPMessageType>();
    static {
        for (ETPMessageType questionaryStatus : ETPMessageType.values()) {
            map.put(questionaryStatus.getCode(), questionaryStatus);
        }
    }

    private ETPMessageType(int code, String description) {
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

    public static ETPMessageType fromInteger(Integer value){
        return map.get(value);
    }

}
