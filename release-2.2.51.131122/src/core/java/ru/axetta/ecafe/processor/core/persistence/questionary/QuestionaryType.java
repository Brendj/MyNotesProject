/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.01.13
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */
public enum QuestionaryType {

    DEFAULT(0,"По умолчанию"),
    MENU(1,"Меню");

    private final Integer value;
    private final String description;
    static Map<Integer,QuestionaryType> map = new HashMap<Integer,QuestionaryType>();
    static {
        for (QuestionaryType questionaryStatus : QuestionaryType.values()) {
            map.put(questionaryStatus.getValue(), questionaryStatus);
        }
    }
    private QuestionaryType (Integer value, String description){
        this.value=value;
        this.description = description;
    }

    public Integer getValue(){
        return value;
    }

    @Override
    public String toString() {
        return description;
    }

    public static QuestionaryType fromInteger(Integer value){
        QuestionaryType questionaryType = map.get(value);
        if(questionaryType==null) questionaryType = QuestionaryType.DEFAULT;
        return questionaryType;
    }

}
