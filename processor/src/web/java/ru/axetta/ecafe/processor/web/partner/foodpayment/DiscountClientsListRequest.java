/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class DiscountClientsListRequest {

    @JsonProperty("OrgId")
    private Long orgId;

    @JsonProperty("DiscountId")
    private Long discountId;

    @JsonProperty("Status")
    private Boolean status;

    @JsonProperty("Clients")
    private List<Long> clients;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Long> getClients() {
        return clients;
    }

    public void setClients(List<Long> clients) {
        this.clients = clients;
    }
}
