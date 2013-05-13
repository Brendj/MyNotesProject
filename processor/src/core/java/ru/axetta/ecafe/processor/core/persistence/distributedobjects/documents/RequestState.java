/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.documents;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 25.04.13
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
public enum RequestState{
    CREATED("Создан"),
    FOLLOW("К исполнению"),
    COMPLETED("Выполнен");

    private String description;

    static HashMap<String, RequestState> map = new HashMap<String, RequestState>();
    static {
        for (RequestState requestState: RequestState.values()){
            map.put(requestState.toString(),requestState);
        }
    }

    private RequestState(String description) {
        this.description = description;
    }

    public static RequestState parse(String s){
        return map.get(s);
    }

    @Override
    public String toString() {
        return description;
    }
}