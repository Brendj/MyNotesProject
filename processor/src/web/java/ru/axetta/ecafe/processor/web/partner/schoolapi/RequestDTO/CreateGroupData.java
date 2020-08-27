/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

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
        return new StringBuilder(String.format("CreateGroupData: orgId = %o; groupName = %s",
                this.getOrgId(), this.getGroupName())).toString();
    }
}
