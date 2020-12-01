/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import ru.axetta.ecafe.processor.core.persistence.PlanOrder;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SetToPayClientDTO {
    @JsonProperty("ContractId")
    private Long contractId = null;
    @JsonProperty("ComplexName")
    private String complexName = null;
    @JsonProperty("ToPay")
    private boolean toPay = false;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public boolean isToPay() {
        return toPay;
    }

    public void setToPay(boolean toPay) {
        this.toPay = toPay;
    }

    public static List<SetToPayClientDTO> convertFromPlanOrders(List<PlanOrder> planOrders){
        List<SetToPayClientDTO> clients = new ArrayList<>();
        for(PlanOrder planOrder: planOrders){
            SetToPayClientDTO client = new SetToPayClientDTO();
            if(planOrder.getClient()!= null){
                client.setContractId(planOrder.getClient().getContractId());
            }
            client.setComplexName(planOrder.getComplexName());
            if(planOrder.getToPay() != null)
                client.setToPay(planOrder.getToPay());
            clients.add(client);
        }
        return clients;
    }
}
