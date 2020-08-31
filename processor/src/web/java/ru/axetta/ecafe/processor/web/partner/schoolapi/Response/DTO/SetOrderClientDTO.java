/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.PlanOrder;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SetOrderClientDTO {
    @JsonProperty("ContractId")
    private Long contractId = null;
    @JsonProperty("ComplexName")
    private String complexName = null;
    @JsonProperty("Order")
    private boolean order = false;

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

    public boolean getOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }

    public static List<SetOrderClientDTO> convertFromPlanOrders(List<PlanOrder> planOrders){
        List<SetOrderClientDTO> clients = new ArrayList<>();
        for(PlanOrder planOrder: planOrders){
            SetOrderClientDTO client = new SetOrderClientDTO();
            if(planOrder.getClient()!= null){
                client.setContractId(planOrder.getClient().getContractId());
            }
            client.setComplexName(planOrder.getComplexName());
            if(planOrder.getOrder() != null){
                if(planOrder.getOrder().getState() == Order.STATE_COMMITED)
                    client.setOrder(true);
                else if(planOrder.getOrder().getState() == Order.STATE_CANCELED)
                    client.setOrder(false);
            }
            clients.add(client);
        }
        return clients;
    }
}
