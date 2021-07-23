/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.order.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.LinkedList;
import java.util.List;

@Schema(name = "GuardianResponseDTO", description = "Данные по покупкам")
public class OrderResponseDTO {
    @Schema(description = "Время пробития заказа в timestamp", example = "1617224400000")
    private Long time;

    @Schema(description = "Свмма покупки в копейках", example = "11075")
    private Long sum;

    @Schema(description = "Признак сторнирования", example = "false")
    private Boolean cancel;

    @Schema(description = "ID записи", example = "704602893596893184")
    private String idOfOrder;

    @Schema(description = "Детали заказа")
    private List<OrderDetailResponseDTO> details = new LinkedList<>();

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Boolean getCancel() {
        return cancel;
    }

    public void setCancel(Boolean cancel) {
        this.cancel = cancel;
    }

    public String getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(String idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public List<OrderDetailResponseDTO> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailResponseDTO> details) {
        this.details = details;
    }
}
