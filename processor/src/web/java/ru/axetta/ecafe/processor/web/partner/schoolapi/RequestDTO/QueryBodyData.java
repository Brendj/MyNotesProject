/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

public class QueryBodyData {
    private long orgId;

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getOrgId() {
        return orgId;
    }
}
