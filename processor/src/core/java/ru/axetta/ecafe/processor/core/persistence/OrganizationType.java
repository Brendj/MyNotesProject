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

    /*0*/ SCHOOL(0,"Общеобразовательное ОУ"),
    /*1*/ KINDERGARTEN(1,"Дошкольное ОУ"),
    /*2*/ SUPPLIER(2,"Поставщик питания"),
    /*3*/ PROFESSIONAL(3,"Профессиональное ОУ");

    private final Integer code;
    private final String description;

    static Map<Integer,OrganizationType> map = new HashMap<Integer,OrganizationType>();
    static {
        for (OrganizationType questionaryStatus : OrganizationType.values()) {
            map.put(questionaryStatus.getCode(), questionaryStatus);
        }
    }

    private OrganizationType(int code, String description) {
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

    public static OrganizationType fromInteger(Integer value){
        return map.get(value);
    }

    public String getShortType(){
        if(SCHOOL.code.equals(this.code)){
            return "СОШ";
        } else if(KINDERGARTEN.code.equals(this.code)){
            return "ДОУ";
        } else if(PROFESSIONAL.code.equals(this.code)){
            return "СПО";
        } else {
            return toString();
        }
    }

    public static String getShortTypeByCode(Integer code){
        if(SCHOOL.code.equals(code)){
            return "СОШ";
        } else if(KINDERGARTEN.code.equals(code)){
            return "ДОУ";
        } else if(PROFESSIONAL.code.equals(code)){
            return "СПО";
        } else {
            return fromInteger(code).toString();
        }
    }
}
