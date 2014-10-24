/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.feedingandvisit;

import java.util.LinkedList;
import java.util.List;

/**
 * User: shamil
 * Date: 03.10.14
 * Time: 11:55
 */
public class Data  implements Comparable<Data> {
    private String name ;
    private List<Days> daysList = new LinkedList<Days>();
    List<Row> reserve = new LinkedList<Row>();
    List<Row> plan = new LinkedList<Row>();
    List<Row> total = new LinkedList<Row>();

    public Data(List<Days> daysList) {
        this.daysList = daysList;
    }

    public Data(String name) {
        this.name = name;
    }

    public Data(String name, List<Days> daysList) {
        this.name = name;
        this.daysList = daysList;
    }

    public Data(List<Days> daysList, List<Group> groupList) {
        this.daysList = daysList;
    }

    public Data(String name, List<Days> daysList, List<Row> reserve, List<Row> plan, List<Row> total) {
        this.name = name;
        this.daysList = daysList;
        this.reserve = reserve;
        this.plan = plan;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Days> getDaysList() {
        return daysList;
    }

    public void setDaysList(List<Days> daysList) {
        this.daysList = daysList;
    }
    public List<Row> getReserve() {
        return reserve;
    }

    public void setReserve(List<Row> reserve) {
        this.reserve = reserve;
    }

    public List<Row> getPlan() {
        return plan;
    }

    public void setPlan(List<Row> plan) {
        this.plan = plan;
    }

    public List<Row> getTotal() {
        return total;
    }

    public void setTotal(List<Row> total) {
        this.total = total;
    }

    @Override
    public int compareTo(Data o) {
        String numThisString = getName().replaceAll("[^\\d]", "");
        String numOString = o.getName().replaceAll("[^\\d]", "");
        if(numThisString.length() < 1) return -1;
        if(numOString.length() < 1) return 1;


        Integer numThis = Integer.valueOf(numThisString);
        Integer numO = Integer.valueOf(numOString);
        if(numThis.equals(numO)){
            String sThis = getName().replaceAll("[^\\D]", "").toUpperCase();
            String sO = o.getName().replaceAll("[^\\D]", "").toUpperCase();
            Integer letterThis = Character.getNumericValue(sThis.charAt(0));
            Integer letterO = Character.getNumericValue(sO.charAt(0));
            return  letterThis.compareTo(letterO);
        }else{
            return numThis.compareTo(numO);
        }
    }
}
