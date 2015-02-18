/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.feedingandvisit;

import ru.axetta.ecafe.processor.core.report.model.ClientGroupSortByName;

import java.util.LinkedList;
import java.util.List;

/**
 * User: shamil
 * Date: 03.10.14
 * Time: 11:55
 */
public class Data extends ClientGroupSortByName<Data> {

    private List<Days> daysList = new LinkedList<Days>();
    List<Row> reserve = new LinkedList<Row>();
    List<Row> plan = new LinkedList<Row>();
    List<Row> total = new LinkedList<Row>();

    public Data(List<Days> daysList) {
        this.daysList = daysList;
    }

    public Data(String name) {
        setName(name);
    }

    public Data(String name, List<Days> daysList) {
        setName(name);
        this.daysList = daysList;
    }

    public Data(List<Days> daysList, List<Group> groupList) {
        this.daysList = daysList;
    }

    public Data(String name, List<Days> daysList, List<Row> reserve, List<Row> plan, List<Row> total) {
        setName(name);
        this.daysList = daysList;
        this.reserve = reserve;
        this.plan = plan;
        this.total = total;
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
}
