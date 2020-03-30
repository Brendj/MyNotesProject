/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.preorder;

import java.util.Date;

/**
 * Created by nuc on 27.03.2020.
 */
public class PreorderStatsReportItem {
    private Date date;
    private Integer parents;
    private Integer employee;
    private Integer students;
    private Integer others;

    public PreorderStatsReportItem(Date date, Integer parents, Integer employee, Integer students, Integer others) {
        this.date = date;
        this.parents = parents;
        this.employee = employee;
        this.students = students;
        this.others = others;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getParents() {
        return parents;
    }

    public void setParents(Integer parents) {
        this.parents = parents;
    }

    public Integer getEmployee() {
        return employee;
    }

    public void setEmployee(Integer employee) {
        this.employee = employee;
    }

    public Integer getStudents() {
        return students;
    }

    public void setStudents(Integer students) {
        this.students = students;
    }

    public Integer getOthers() {
        return others;
    }

    public void setOthers(Integer others) {
        this.others = others;
    }
}
