/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment.DTO;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

public class ClientGroupDTO {
    private Long orgId;
    private ClientGroup clientGroup;

    public ClientGroupDTO(Long orgId, ClientGroup clientGroup){
        this.orgId = orgId;
        this.clientGroup = clientGroup;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(ClientGroup clientGroup) {
        this.clientGroup = clientGroup;
    }
}
