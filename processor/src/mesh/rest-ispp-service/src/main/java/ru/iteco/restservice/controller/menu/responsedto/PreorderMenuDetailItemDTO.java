package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PreorderMenuDetailItemDTO {
    @Schema(description = "Идентификатор предзаказа на блюдо")
    private Long preorderMenuDetailId;

    @Schema(description = "Количество блюд в предзаказе")
    private Integer amount;

    public Long getPreorderMenuDetailId() {
        return preorderMenuDetailId;
    }

    public void setPreorderMenuDetailId(Long preorderMenuDetailId) {
        this.preorderMenuDetailId = preorderMenuDetailId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
