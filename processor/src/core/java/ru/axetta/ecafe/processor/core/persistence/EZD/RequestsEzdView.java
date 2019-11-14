/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.EZD;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RequestsEzdView {

    private Long id;
    private Long idoforg;
    private String orgguid;
    private Integer idofcomplex;
    private String complexname;
    private String groupname;
    private Date menudate;


    public Long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(Long idoforg) {
        this.idoforg = idoforg;
    }

    public Integer getIdofcomplex() {
        return idofcomplex;
    }

    public void setIdofcomplex(Integer idofcomplex) {
        this.idofcomplex = idofcomplex;
    }

    public String getComplexname() {
        return complexname;
    }

    public void setComplexname(String complexname) {
        this.complexname = complexname;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Date getMenudate() {
        return menudate;
    }

    public void setMenudate(Date menudate) {
        this.menudate = menudate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgguid() {
        return orgguid;
    }

    public void setOrgguid(String orgguid) {
        this.orgguid = orgguid;
    }
}
