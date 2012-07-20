/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.distributionsync;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.07.12
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 * Класс содержащий метож для сортировки элеметов множества
 */
public class DistributedObjectsEnumComparator implements Comparator<DistributedObjectsEnum> {

    private static List<ErrorObject> errorObjectList = new ArrayList<ErrorObject>();

    public static List<ErrorObject> getErrorObjectList() {
        if(errorObjectList==null){
            errorObjectList = new ArrayList<ErrorObject>();
        }
        return errorObjectList;
    }

    public static void setErrorObjectList(List<ErrorObject> errorObjectList) {
        DistributedObjectsEnumComparator.errorObjectList = errorObjectList;
    }

    public static boolean isEmptyOrNull(){
        return DistributedObjectsEnumComparator.getErrorObjectList()==null || DistributedObjectsEnumComparator.getErrorObjectList().isEmpty();
    }

    public static int getErrorObject(ErrorObject errorObject){
        return DistributedObjectsEnumComparator.getErrorObjectList().indexOf(errorObject);
    }

    public static String getTypeByIndex(int index){
        return String.valueOf(DistributedObjectsEnumComparator.getErrorObjectList().get(index).getType());
    }

    @Override
    public int compare(DistributedObjectsEnum o1, DistributedObjectsEnum o2) {
        return o1.getPriority() - o2.getPriority();
    }
}
