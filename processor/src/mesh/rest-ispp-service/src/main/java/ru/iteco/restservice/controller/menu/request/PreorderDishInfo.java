package ru.iteco.restservice.controller.menu.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class PreorderDishInfo {
    @NotNull
    @Schema(description = "Идентификатор блюда", example = "8555")
    private Long dishId;

    @NotNull
    @Schema(description = "Заказываемое количество блюда", example = "1")
    private Integer amount;

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
