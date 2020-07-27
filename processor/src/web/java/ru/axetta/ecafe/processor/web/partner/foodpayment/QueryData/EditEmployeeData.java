/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

public class EditEmployeeData {
    private long orgId;
    private String groupName;
    private long contractId;
    private boolean status;

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName.trim();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public long getContractId() {
        return contractId;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    @Override
    public String toString(){
        return new StringBuilder(String.format("EditEmployeeData:  groupName = %s; contractId = %o; status = %s",
                this.groupName, this.contractId, this.status)).toString();
    }
}
