/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.order.responseDTO;

import java.util.LinkedList;
import java.util.List;

public class OrderResponseDTO {
    private Long time;
    private Long sum;
    private Boolean cancel;
    private Long idOfOrder;
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

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public List<OrderDetailResponseDTO> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailResponseDTO> details) {
        this.details = details;
    }
}
