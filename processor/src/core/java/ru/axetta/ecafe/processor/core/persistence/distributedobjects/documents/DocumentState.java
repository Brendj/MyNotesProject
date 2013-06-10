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
public enum DocumentState {
    CREATED("Создан"),
    FOLLOW("К исполнению"),
    COMPLETED("Выполнен");

    private String description;

    static HashMap<String, DocumentState> map = new HashMap<String, DocumentState>();
    static {
        for (DocumentState documentState : DocumentState.values()){
            map.put(documentState.toString(), documentState);
        }
    }

    private DocumentState(String description) {
        this.description = description;
    }

    public static DocumentState parse(String s){
        return map.get(s);
    }

    @Override
    public String toString() {
        return description;
    }
}