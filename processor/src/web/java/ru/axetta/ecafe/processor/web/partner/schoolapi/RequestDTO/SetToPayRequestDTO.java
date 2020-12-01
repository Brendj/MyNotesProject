/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import ru.axetta.ecafe.processor.web.partner.schoolapi.util.DateHandler;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Date;
import java.util.List;

public class SetToPayRequestDTO {
    @JsonProperty("PlanDate")
    @JsonDeserialize(using = DateHandler.class)
    private Date planDate;
    @JsonProperty("ContractIds")
    private List<Long> contractIds;
    @JsonProperty("ComplexName")
    private String complexName;
    @JsonProperty("ToPay")
    private boolean toPay;

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public List<Long> getContractIds() {
        return contractIds;
    }

    public void setContractIds(List<Long> contractIds) {
        this.contractIds = contractIds;
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
}
