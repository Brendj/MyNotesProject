/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.good.request;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.02.14
 * Time: 13:03
 * To change this template use File | Settings | File Templates.
 */
public enum  DocumentStateFilter {

    CREATED("Создан"),
    FOLLOW("К исполнению"),
    CREATED_AND_FOLLOW("Создан и к исполнению"),
    COMPLETED("Выполнен"),
    FOLLOW_AND_COMPLETED("К исполнению и выполнен"),
    ALL("Все");


    private String description;

    static HashMap<String, DocumentStateFilter> map = new HashMap<String, DocumentStateFilter>();
    static {
        for (DocumentStateFilter documentState : DocumentStateFilter.values()){
            map.put(documentState.toString(), documentState);
        }
    }

    DocumentStateFilter(String description) {
        this.description = description;
    }

    public static DocumentStateFilter parse(String s){
        return map.get(s);
    }

    public static DocumentState[] states(DocumentStateFilter filter){
        DocumentState[] state = null;
        if(filter==null) state = null;
        else {
            if(filter.equals(CREATED)) state = new DocumentState[]{DocumentState.CREATED};
            if(filter.equals(FOLLOW)) state = new DocumentState[]{DocumentState.FOLLOW};
            if(filter.equals(CREATED_AND_FOLLOW)) state = new DocumentState[]{DocumentState.CREATED, DocumentState.FOLLOW};
            if(filter.equals(COMPLETED)) state = new DocumentState[]{DocumentState.COMPLETED};
            if(filter.equals(FOLLOW_AND_COMPLETED)) state = new DocumentState[]{DocumentState.FOLLOW, DocumentState.COMPLETED};
            if(filter.equals(ALL)) state = DocumentState.values();
        }
        return state;
    }

    public static Integer[] convertToOrdinals(DocumentState[] states) {
        Integer[] stateOrdinals = new Integer[states.length];
        for (int i = 0; i < states.length; i++) {
            stateOrdinals[i] = states[i].ordinal();
        }
        return stateOrdinals;
    }

    @Override
    public String toString() {
        return description;
    }
}
