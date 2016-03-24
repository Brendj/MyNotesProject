package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.09.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public enum OrganizationType {

    /*0*/ SCHOOL("Общеобразовательное ОУ"),
    /*1*/ KINDERGARTEN("Дошкольное ОУ"),
    /*2*/ SUPPLIER("Поставщик питания"),
    /*3*/ PROFESSIONAL("Профессиональное ОУ");

    private final String description;

    static Map<Integer,OrganizationType> map = new HashMap<Integer,OrganizationType>();
    static {
        for (OrganizationType questionaryStatus : OrganizationType.values()) {
            map.put(questionaryStatus.ordinal(), questionaryStatus);
        }
    }

    private OrganizationType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static OrganizationType fromInteger(Integer value){
        return map.get(value);
    }
}
