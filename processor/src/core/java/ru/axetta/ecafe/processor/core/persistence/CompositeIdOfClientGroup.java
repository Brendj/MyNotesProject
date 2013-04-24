/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.07.2009
 * Time: 13:50:22
 * To change this template use File | Settings | File Templates.
 */
public class CompositeIdOfClientGroup implements Serializable {

    private Long idOfOrg;
    private Long idOfClientGroup;

    protected CompositeIdOfClientGroup() {
        // For Hibernate only
    }

    public CompositeIdOfClientGroup(long idOfOrg, long idOfClientGroup) {
        this.idOfOrg = idOfOrg;
        this.idOfClientGroup = idOfClientGroup;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfClientGroup)) {
            return false;
        }
        final CompositeIdOfClientGroup that = (CompositeIdOfClientGroup) o;
        return idOfClientGroup.equals(that.getIdOfClientGroup()) && idOfOrg.equals(that.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        int result = idOfOrg.hashCode();
        result = 31 * result + idOfClientGroup.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompositeIdOfClientGroup{" + "idOfOrg=" + idOfOrg + ", idOfClientGroup=" + idOfClientGroup + '}';
    }
}