package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;

public class PreorderMenuDetailDTO {
    @Schema(description = "Идентификатор предзаказа на комплекс")
    private Long preorderId;

    @Schema(description = "Идентификатор предзаказа на блюдо")
    private Long preorderMenuDetailId;

    @Schema(description = "Количество блюд в предзаказе")
    private Integer amount;

    public static PreorderMenuDetailDTO build(PreorderMenuDetail preorderMenuDetail) {
        PreorderMenuDetailDTO result = new PreorderMenuDetailDTO();
        if (preorderMenuDetail == null) {
            result.setAmount(0);
        } else {
            result.setPreorderId(preorderMenuDetail.getPreorderComplex().getIdOfPreorderComplex());
            result.setPreorderMenuDetailId(preorderMenuDetail.getIdOfPreorderMenuDetail());
            result.setAmount(preorderMenuDetail.getAmount());
        }
        return result;
    }

    public Long getPreorderId() {
        return preorderId;
    }

    public void setPreorderId(Long preorderId) {
        this.preorderId = preorderId;
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
