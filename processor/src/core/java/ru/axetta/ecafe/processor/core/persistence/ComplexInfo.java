/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Set;

public class ComplexInfo {

    private Long idOfComplexInfo;
    private int idOfComplex;
    private Org org;
    private int modeFree;
    private int modeGrant;
    private int modeOfAdd;
    private String complexName;
    private Date menuDate;

    public Date getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(Date menuDate) {
        this.menuDate = menuDate;
    }

    protected ComplexInfo() {

    }

    public ComplexInfo(int idOfComplex, Org org, Date menuDate, int modeFree, int modeGrant, int modeOfAdd,
            String complexName) {
        this.idOfComplex = idOfComplex;
        this.org = org;
        this.menuDate = menuDate;
        this.modeFree = modeFree;
        this.modeGrant = modeGrant;
        this.modeOfAdd = modeOfAdd;
        this.complexName = complexName;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public int getModeOfAdd() {
        return modeOfAdd;
    }

    public void setModeOfAdd(int modeOfAdd) {
        this.modeOfAdd = modeOfAdd;
    }

    public int getModeGrant() {
        return modeGrant;
    }

    public void setModeGrant(int modeGrant) {
        this.modeGrant = modeGrant;
    }

    public int getModeFree() {
        return modeFree;
    }

    public void setModeFree(int modeFree) {
        this.modeFree = modeFree;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public int getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(int idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Long getIdOfComplexInfo() {
        return idOfComplexInfo;
    }

    public void setIdOfComplexInfo(Long idOfComplexInfo) {
        this.idOfComplexInfo = idOfComplexInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComplexInfo that = (ComplexInfo) o;

        if (idOfComplexInfo != that.idOfComplexInfo) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfComplexInfo ^ (idOfComplexInfo >>> 32));
    }

    @Override
    public String toString() {
        return "ComplexInfo{" + "idOfComplexInfo=" + idOfComplexInfo + ", idOfComplex=" + idOfComplex + ", org=" + org
                + ", modeFree=" + modeFree + ", modeGrant=" + modeGrant + ", modeOfAdd=" + modeOfAdd +'}';
    }
}
