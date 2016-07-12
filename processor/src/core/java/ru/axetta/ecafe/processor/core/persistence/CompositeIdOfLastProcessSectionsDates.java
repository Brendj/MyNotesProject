/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 11:23
 */

public class CompositeIdOfLastProcessSectionsDates implements Serializable {
    private Long idOfOrg;
    private Integer type ;

    public CompositeIdOfLastProcessSectionsDates() {
    }

    public CompositeIdOfLastProcessSectionsDates(Long idOfOrg, Integer type) {
        this.idOfOrg = idOfOrg;
        this.type = type;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeIdOfLastProcessSectionsDates that = (CompositeIdOfLastProcessSectionsDates) o;

        if (!getIdOfOrg().equals(that.getIdOfOrg())) {
            return false;
        }
        return getType().equals(that.getType());

    }

    @Override
    public int hashCode() {
        int result = getIdOfOrg().hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }
}
