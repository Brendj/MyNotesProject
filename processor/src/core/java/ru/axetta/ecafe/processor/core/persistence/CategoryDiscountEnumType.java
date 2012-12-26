/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.12.12
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public enum CategoryDiscountEnumType {

    CATEGORY_WITH_DISCOUNT(0,"Категория со скидкой"),
    FEE_CATEGORY(1,"Платная");

    private final Integer value;
    private final String description;
    static Map<Integer,CategoryDiscountEnumType> map = new HashMap<Integer,CategoryDiscountEnumType>();
    static {
        for (CategoryDiscountEnumType questionaryStatus : CategoryDiscountEnumType.values()) {
            map.put(questionaryStatus.getValue(), questionaryStatus);
        }
    }
    private CategoryDiscountEnumType (Integer value, String description){
        this.value=value;
        this.description = description;
    }

    public Integer getValue(){
        return value;
    }

    @Override
    public String toString() {
        return String.format("(%d) - %s",value,description);
    }

    public static CategoryDiscountEnumType fromInteger(Integer value){
        return map.get(value);
    }

}
