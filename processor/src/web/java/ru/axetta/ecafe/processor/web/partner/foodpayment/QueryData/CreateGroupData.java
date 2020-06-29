/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData;

public class CreateGroupData extends QueryBodyData {
    private String groupName;

    public void setGroupName(String groupName) {
        this.groupName = groupName.trim();
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public String toString(){
        return new StringBuilder(String.format("CreateGroupData: token = %s; userId = %o; orgId = %o; groupName = %s",
                this.getToken(), this.getUserId(), this.getOrgId(), this.getGroupName())).toString();
    }
}
