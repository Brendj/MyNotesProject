/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.EZD;

import java.util.Date;

public class RequestsEzdMenuView {
    private Long id;
    private Long idOforg;
    private String guid;
    private Long ekisid;
    private Long idofcomplex;
    private String complexname;
    private Date menuDate;

    public Long getIdOforg() {
        return idOforg;
    }

    public void setIdOforg(Long idOforg) {
        this.idOforg = idOforg;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getEkisid() {
        return ekisid;
    }

    public void setEkisid(Long ekisid) {
        this.ekisid = ekisid;
    }

    public String getComplexname() {
        return complexname;
    }

    public void setComplexname(String complexname) {
        this.complexname = complexname;
    }

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdofcomplex() {
        return idofcomplex;
    }

    public void setIdofcomplex(Long idofcomplex) {
        this.idofcomplex = idofcomplex;
    }
}
