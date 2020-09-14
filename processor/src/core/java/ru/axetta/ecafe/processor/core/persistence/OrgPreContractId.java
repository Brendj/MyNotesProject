/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class OrgPreContractId {
    private Long idOfOrgPreContractId;
    private long version;
    private Long idOfOrg;
    private Long contractId;
    private boolean used;
    private Date createdDate;
    private Date usedDate;

    public OrgPreContractId() {

    }

    public OrgPreContractId(Long idOfOrg, Long contractId) {
        this.idOfOrg = idOfOrg;
        this.contractId = contractId;
        this.used = false;
        this.createdDate = new Date();
    }

    public Long getIdOfOrgPreContractId() {
        return idOfOrgPreContractId;
    }

    public void setIdOfOrgPreContractId(Long idOfOrgPreContractId) {
        this.idOfOrgPreContractId = idOfOrgPreContractId;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(Date usedDate) {
        this.usedDate = usedDate;
    }
}
