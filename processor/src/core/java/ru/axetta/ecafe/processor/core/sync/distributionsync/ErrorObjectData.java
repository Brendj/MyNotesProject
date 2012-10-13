/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.distributionsync;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.08.12
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class ErrorObjectData {

    private List<ErrorObject> errorObjectList;

    public ErrorObjectData() {
        this.errorObjectList = new ArrayList<ErrorObject>();
    }

    public List<ErrorObject> getErrorObjectList() {
        return errorObjectList;
    }

    public boolean isEmptyOrNull(){
        return getErrorObjectList()==null || getErrorObjectList().isEmpty();
    }

    public int getErrorObject(ErrorObject errorObject){
        return getErrorObjectList().indexOf(errorObject);
    }

    public String getTypeByIndex(int index){
        return String.valueOf(getErrorObjectList().get(index).getType());
    }
    
}
