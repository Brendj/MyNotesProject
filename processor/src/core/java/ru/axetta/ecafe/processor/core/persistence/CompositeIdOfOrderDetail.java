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
public class CompositeIdOfOrderDetail implements Serializable {

    private Long idOfOrg;
    private Long idOfOrderDetail;

    CompositeIdOfOrderDetail() {
        // For Hibernate only
    }

    public CompositeIdOfOrderDetail(long idOfOrg, long idOfOrderDetail) {
        this.idOfOrg = idOfOrg;
        this.idOfOrderDetail = idOfOrderDetail;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    private void setIdOfOrg(Long idOfOrg) {
        // For Hibernate only
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    private void setIdOfOrderDetail(Long idOfOrderDetail) {
        // For Hibernate only
        this.idOfOrderDetail = idOfOrderDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeIdOfOrderDetail)) {
            return false;
        }
        final CompositeIdOfOrderDetail that = (CompositeIdOfOrderDetail) o;
        return idOfOrderDetail.equals(that.getIdOfOrderDetail()) && idOfOrg.equals(that.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        return idOfOrderDetail.hashCode();
    }

    @Override
    public String toString() {
        return "CompositeIdOfOrderDetail{" + "idOfOrg=" + idOfOrg + ", idOfOrderDetail=" + idOfOrderDetail + '}';
    }
}