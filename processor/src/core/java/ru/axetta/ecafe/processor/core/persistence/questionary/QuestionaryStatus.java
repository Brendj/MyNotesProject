/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.questionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.12.12
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public enum QuestionaryStatus {

    INACTIVE(0,"Неактивен"),
    START(1,"Активен"),
    STOP(2,"Завершен");

    private final Integer value;
    private final String description;
    static Map<Integer,QuestionaryStatus> map = new HashMap<Integer,QuestionaryStatus>();
    static {
        for (QuestionaryStatus questionaryStatus : QuestionaryStatus.values()) {
            map.put(questionaryStatus.getValue(), questionaryStatus);
        }
    }
    private QuestionaryStatus (Integer value, String description){
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

    public static QuestionaryStatus fromInteger(Integer value){
        return map.get(value);
    }
}
