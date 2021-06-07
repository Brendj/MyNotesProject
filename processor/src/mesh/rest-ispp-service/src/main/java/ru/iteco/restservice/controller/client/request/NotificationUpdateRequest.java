/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;


public class NotificationUpdateRequest {
    @Schema(description = "л/с ребенка")
    private Long contractId;

    @Schema(description = "Тип настройки")
    private Long notificationType;

    @Schema(description = "Признак активности")
    private Boolean activity;

    @Schema(description = "Номер телефона опекуна")
    private String guardianMobile;

    public NotificationUpdateRequest() {
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Long notificationType) {
        this.notificationType = notificationType;
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
        return Objects.equals(contractId, that.contractId) && Objects.equals(notificationType, that.notificationType)
                && Objects.equals(activity, that.activity) && Objects.equals(guardianMobile, that.guardianMobile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractId, notificationType, activity, guardianMobile);
    }
}
