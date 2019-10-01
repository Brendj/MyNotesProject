/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.EZD;

import java.util.Date;

public class RequestsEzdSpecialDateView {

    private Long id;
    private Date specDate;
    private String groupname;
    private Long idoforg;

    public Date getSpecDate() {
        return specDate;
    }

    public void setSpecDate(Date specDate) {
        this.specDate = specDate;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(Long idoforg) {
        this.idoforg = idoforg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
