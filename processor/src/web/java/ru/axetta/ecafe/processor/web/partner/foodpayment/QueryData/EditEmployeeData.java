/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

public class EditEmployeeData extends QueryBodyData {
    private String groupName;
    private long contractId;
    private boolean status;

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
        return new StringBuilder(String.format("EditEmployeeData: token = %s; userId = %o; orgId = %o; groupName = %s; contractId = %o; status = %s",
                this.getToken(), this.getUserId(), this.getOrgId(), this.groupName, this.contractId, this.status)).toString();
    }
}
