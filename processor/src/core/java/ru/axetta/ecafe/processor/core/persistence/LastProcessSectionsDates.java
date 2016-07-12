/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 11:20
 */
public class LastProcessSectionsDates implements Serializable {

    private CompositeIdOfLastProcessSectionsDates compositeIdOfLastProcessSectionsDates;
    private Org org;
    private Date date;

    public LastProcessSectionsDates() {
    }

    public LastProcessSectionsDates(CompositeIdOfLastProcessSectionsDates compositeIdOfLastProcessSectionsDates,
            Date date) {
        this.compositeIdOfLastProcessSectionsDates = compositeIdOfLastProcessSectionsDates;
        this.date = date;

    }

    public CompositeIdOfLastProcessSectionsDates getCompositeIdOfLastProcessSectionsDates() {
        return compositeIdOfLastProcessSectionsDates;
    }

    public void setCompositeIdOfLastProcessSectionsDates(
            CompositeIdOfLastProcessSectionsDates compositeIdOfLastProcessSectionsDates) {
        this.compositeIdOfLastProcessSectionsDates = compositeIdOfLastProcessSectionsDates;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
