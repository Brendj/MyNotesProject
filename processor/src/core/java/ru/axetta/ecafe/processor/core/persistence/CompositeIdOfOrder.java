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
public class CompositeIdOfOrder implements Serializable {

    private Long idOfOrg;
    private Long idOfOrder;

    CompositeIdOfOrder() {
        // For Hibernate only
    }

    public CompositeIdOfOrder(long idOfOrg, long idOfOrder) {
        this.idOfOrg = idOfOrg;
        this.idOfOrder = idOfOrder;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    private void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    private void setIdOfOrder(Long idOfOrder) {
        // For Hibernate only
        this.idOfOrder = idOfOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfOrder)) {
            return false;
        }
        final CompositeIdOfOrder that = (CompositeIdOfOrder) o;
        return idOfOrder.equals(that.getIdOfOrder()) && idOfOrg.equals(that.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        return idOfOrder.hashCode();
    }

    @Override
    public String toString() {
        return "CompositeIdOfOrder{" + "idOfOrg=" + idOfOrg + ", idOfOrder=" + idOfOrder + '}';
    }
}
