/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class DiscountClientsListRequest {

    @JsonProperty("DiscountId")
    private Long discountId;

    @JsonProperty("Status")
    private Boolean status;

    @JsonProperty("Clients")
    private List<Long> clients;

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
