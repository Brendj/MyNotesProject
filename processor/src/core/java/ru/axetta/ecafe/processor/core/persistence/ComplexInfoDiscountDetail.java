/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ComplexInfoDiscountDetail {

    private Long idOfDiscountDetail;
    private Double size;
    private int isAllGroups;
    private int maxCount;
    private ClientGroup clientGroup;
    private Org org;

    protected ComplexInfoDiscountDetail() {

    }

    public ComplexInfoDiscountDetail(Double size, int allGroups) {
        this.size = size;
        isAllGroups = allGroups;
    }

    public Long getIdOfDiscountDetail() {
        return idOfDiscountDetail;
    }

    public void setIdOfDiscountDetail(Long idOfDiscountDetail) {
        this.idOfDiscountDetail = idOfDiscountDetail;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public  int getIsAllGroups() {
        return isAllGroups;
    }

    public void setIsAllGroups( int allGroups) {
        isAllGroups = allGroups;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(ClientGroup clientGroup) {
        this.clientGroup = clientGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComplexInfoDiscountDetail that = (ComplexInfoDiscountDetail) o;

        if (idOfDiscountDetail != that.idOfDiscountDetail) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfDiscountDetail ^ (idOfDiscountDetail >>> 32));
    }

    @Override
    public String toString() {
        return "ComplexInfoDiscountDetail{" +
                "idOfDiscountDetail=" + idOfDiscountDetail +
                ", size=" + size +
                ", isAllGroups=" + isAllGroups +
                ", maxCount=" + maxCount +
                ", clientGroup=" + clientGroup +
                ", org=" + org +
                '}';
    }
}
