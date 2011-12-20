/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class SochiClient {

    private Long contractId;
    private Date createTime;
    private Date updateTime;
    private String fullName;
    private String address;
    private Set<SochiClientPayment> payments = new HashSet<SochiClientPayment>();

    SochiClient() {
        // For Hibernate only
    }

    public SochiClient(Long contractId, String fullName) {
        this.contractId = contractId;
        this.fullName = fullName;
        Date currentTime = new Date();
        this.createTime = currentTime;
        this.updateTime = currentTime;
    }

    public Long getContractId() {
        return contractId;
    }

    private void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<SochiClientPayment> getPayments() {
        return payments;
    }

    private void setPayments(Set<SochiClientPayment> payments) {
        this.payments = payments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SochiClient)) {
            return false;
        }
        final SochiClient that = (SochiClient) o;
        return contractId.equals(that.getContractId());
    }

    @Override
    public int hashCode() {
        return contractId.hashCode();
    }

    @Override
    public String toString() {
        return "SochiClient{" + "contractId=" + contractId + ", createTime=" + createTime + ", updateTime=" + updateTime
                + ", fullName='" + fullName + '\'' + ", address='" + address + '\'' + '}';
    }
}