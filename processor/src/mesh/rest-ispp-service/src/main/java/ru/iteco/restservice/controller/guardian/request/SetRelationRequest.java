/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.guardian.request;

import java.util.Objects;

public class SetRelationRequest {
    private String guardianMobile;
    private Long contractId;
    private Integer relation;
    private Integer isLegalRepresent;

    public SetRelationRequest() {
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

    public Integer getIsLegalRepresent() {
        return isLegalRepresent;
    }

    public void setIsLegalRepresent(Integer isLegalRepresent) {
        this.isLegalRepresent = isLegalRepresent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SetRelationRequest that = (SetRelationRequest) o;
        return Objects.equals(guardianMobile, that.guardianMobile) && Objects.equals(contractId, that.contractId)
                && Objects.equals(relation, that.relation) && Objects.equals(isLegalRepresent, that.isLegalRepresent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guardianMobile, contractId, relation, isLegalRepresent);
    }
}
