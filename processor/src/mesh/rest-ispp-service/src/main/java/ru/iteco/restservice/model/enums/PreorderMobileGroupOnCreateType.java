package ru.iteco.restservice.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum PreorderMobileGroupOnCreateType {
    /*0*/ UNKNOWN("Не определено"),
    /*1*/ PARENT("Родитель"),
    /*2*/ PARENT_EMPLOYEE("Родитель и сотрудник"),
    /*3*/ EMPLOYEE("Сотрудник"),
    /*4*/ STUDENT("Учащийся");

    private final String description;
    static Map<Integer,PreorderMobileGroupOnCreateType> map = new HashMap<Integer,PreorderMobileGroupOnCreateType>();
    static {
        for (PreorderMobileGroupOnCreateType questionaryStatus : PreorderMobileGroupOnCreateType.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }

    PreorderMobileGroupOnCreateType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static PreorderMobileGroupOnCreateType fromInteger(Integer value){
        return map.get(value);
    }
}
