/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 14.04.16
 * Time: 12:14
 */
public class CompositeIdOfSpecialDate implements Serializable{
    private Long idOfOrg;
    private Date date;

    public CompositeIdOfSpecialDate() {
    }

    public CompositeIdOfSpecialDate(Long idOfOrg, Date date) {
        this.idOfOrg = idOfOrg;
        this.date = date;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfSpecialDate)) {
            return false;
        }
        final CompositeIdOfSpecialDate that = (CompositeIdOfSpecialDate) o;
        return getIdOfOrg().equals(that.getIdOfOrg()) && date.equals(that.getDate());
    }
}
