/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.PlanOrderGroupDTO;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.List;

public class ResponsePlanOrders extends Result {
    @JsonProperty("PlanDate")
    private Date planDate;

    @JsonProperty("GroupsList")
    private List<PlanOrderGroupDTO> groupsList;

    public ResponsePlanOrders(Date planDate){
        super(0,"Ok");
        this.planDate = planDate;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public List<PlanOrderGroupDTO> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(List<PlanOrderGroupDTO> groupsList) {
        this.groupsList = groupsList;
    }
}
