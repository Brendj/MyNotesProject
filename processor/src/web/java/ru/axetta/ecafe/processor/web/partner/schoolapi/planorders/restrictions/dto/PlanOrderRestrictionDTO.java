/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.dto;

import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestriction;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanOrderRestrictionDTO {
    private Long id;
    private Long idOfClient;
    private Long idOfOrg;
    private Integer planType;
    private Long complexId;
    private Integer resolution;

    public static List<PlanOrderRestrictionDTO> fromList(List<PlanOrdersRestriction> updatedItems) {
        List<PlanOrderRestrictionDTO> result = new ArrayList<>();
        for (PlanOrdersRestriction item : updatedItems) {
            PlanOrderRestrictionDTO dto = new PlanOrderRestrictionDTO();
            dto.setId(item.getIdOfPlanOrdersRestriction());
            dto.setComplexId(Long.valueOf(item.getArmComplexId()));
            dto.setPlanType(item.getPlanOrdersRestrictionType().ordinal());
            dto.setIdOfClient(item.getIdOfClient());
            dto.setResolution(item.getResol());
            dto.setIdOfOrg(item.getIdOfOrgOnCreate());
            result.add(dto);
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

}
