package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;

import java.util.ArrayList;
import java.util.List;

public class PreorderDishDTO {
    @Schema(description = "Идентификатор предзаказа на блюдо")
    private Long preorderMenuDetailId;

    @Schema(description = "Количество блюд в предзаказе")
    private Integer amount;

    public static PreorderDishDTO build(PreorderMenuDetail preorderMenuDetail) {
        PreorderDishDTO result = new PreorderDishDTO();
        if (preorderMenuDetail == null) {
            result.setAmount(0);
        } else {
            result.setPreorderMenuDetailId(preorderMenuDetail.getIdOfPreorderMenuDetail());
            result.setAmount(preorderMenuDetail.getAmount());
        }
        return result;
    }

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
