/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import ru.axetta.ecafe.processor.web.partner.schoolapi.util.Constants;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.List;

public class SetOrderRequestDTO {
    @JsonProperty("PlanDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_STRING_FORMAT)
    private Date planDate;
    @JsonProperty("ContractIds")
    private List<Long> contractIds;
    @JsonProperty("ComplexName")
    private String complexName;
    @JsonProperty("Order")
    private boolean order;

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

    public boolean isOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }
}
