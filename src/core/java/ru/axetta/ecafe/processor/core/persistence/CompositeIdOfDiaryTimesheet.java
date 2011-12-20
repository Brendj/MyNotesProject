/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.07.2009
 * Time: 13:50:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfDiaryTimesheet implements Serializable {

    private Long idOfOrg;
    private Long idOfClientGroup;
    private Date recDate;

    CompositeIdOfDiaryTimesheet() {
        // For Hibernate only
    }

    public CompositeIdOfDiaryTimesheet(Long idOfOrg, Long idOfClientGroup, Date recDate) {
        this.idOfOrg = idOfOrg;
        this.idOfClientGroup = idOfClientGroup;
        this.recDate = recDate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    private void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    private void setIdOfClientGroup(Long idOfClientGroup) {
        // For Hibernate only
        this.idOfClientGroup = idOfClientGroup;
    }

    public Date getRecDate() {
        return recDate;
    }

    private void setRecDate(Date recDate) {
        // For Hibernate only
        this.recDate = recDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfDiaryTimesheet)) {
            return false;
        }
        final CompositeIdOfDiaryTimesheet that = (CompositeIdOfDiaryTimesheet) o;
        return idOfClientGroup.equals(that.getIdOfClientGroup()) && idOfOrg.equals(that.getIdOfOrg()) && recDate
                .equals(that.getRecDate());
    }

    @Override
    public int hashCode() {
        int result = idOfOrg.hashCode();
        result = 31 * result + idOfClientGroup.hashCode();
        result = 31 * result + recDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfDiaryTimesheet{" + "idOfOrg=" + idOfOrg + ", idOfClientGroup=" + idOfClientGroup
                + ", recDate=" + recDate + '}';
    }
}