/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto;

import java.io.Serializable;

public class GroupClientsUpdateRequest implements Serializable {
    private Long bindingOrgId;
    private Boolean disableFromPlanLP;

    public Long getBindingOrgId() {
        return bindingOrgId;
    }

    public void setBindingOrgId(Long bindingOrgId) {
        this.bindingOrgId = bindingOrgId;
    }
}
