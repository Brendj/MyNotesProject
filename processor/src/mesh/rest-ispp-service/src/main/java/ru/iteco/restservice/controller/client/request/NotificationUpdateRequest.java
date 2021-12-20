/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;


public class NotificationUpdateRequest {
    @Schema(description = "л/с ребенка")
    private Long contractId;

    @Schema(description = "Номер телефона опекуна")
    private String guardianMobile;

    @Schema(description = "Массив типов установленных уведомлений")
    private List<Long> typeOfNotification;

    @Schema(description = "Признак активности")
    private Boolean activity;

    public NotificationUpdateRequest() {
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Boolean getActivity() {
        return activity;
    }

    public void setActivity(Boolean activity) {
        this.activity = activity;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationUpdateRequest that = (NotificationUpdateRequest) o;
        return Objects.equals(contractId, that.contractId) && Objects.equals(typeOfNotification, that.typeOfNotification)
                && Objects.equals(activity, that.activity) && Objects.equals(guardianMobile, that.guardianMobile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractId, typeOfNotification, activity, guardianMobile);
    }

    public void setTypeOfNotification(List<Long> typeOfNotification) {
        this.typeOfNotification = typeOfNotification;
    }

    public List<Long> getTypeOfNotification() {
        return this.typeOfNotification;
    }
}
