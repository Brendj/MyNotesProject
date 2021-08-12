/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.guardian.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class DeleteRelationRequest {
    @Schema(description = "Номер телефона представителя", example = "79001234567")
    private String guardianMobile;

    @Schema(description = "Л/С клиента", example = "100345233")
    private Long contractId;

    @Schema(description = "Л/С представителя", example = "1005187712")
    private Long repContractId;

    public DeleteRelationRequest() {
    }

    public Long getRepContractId() {
        return repContractId;
    }

    public void setRepContractId(Long repContractId) {
        this.repContractId = repContractId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeleteRelationRequest that = (DeleteRelationRequest) o;
        return Objects.equals(guardianMobile, that.guardianMobile) && Objects.equals(contractId, that.contractId)
                && Objects.equals(repContractId, that.repContractId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guardianMobile, contractId, repContractId);
    }
}
