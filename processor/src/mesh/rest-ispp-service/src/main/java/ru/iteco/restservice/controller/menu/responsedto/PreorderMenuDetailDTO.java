package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;

public class PreorderMenuDetailDTO {
    @Schema(description = "Идентификатор предзаказа на блюдо")
    private Long preorderMenuDetailId;

    @Schema(description = "Количество блюд в предзаказе")
    private Integer amount;

    public PreorderMenuDetailDTO() {
        amount = 0;
    }

    public static PreorderMenuDetailDTO build(PreorderMenuDetail preorderMenuDetail) {
        PreorderMenuDetailDTO result = new PreorderMenuDetailDTO();
        if (preorderMenuDetail == null) {
            result.setAmount(0);
        } else {
            result.setPreorderMenuDetailId(preorderMenuDetail.getIdOfPreorderMenuDetail());
            result.setAmount(preorderMenuDetail.getAmount());
        }
        return result;
    }

    public static PreorderMenuDetailDTO buildDeleted(PreorderMenuDetail preorderMenuDetail) {
        PreorderMenuDetailDTO result = new PreorderMenuDetailDTO();
        PreorderComplex pc = preorderMenuDetail.getPreorderComplex();
        result.setPreorderMenuDetailId(null);
        result.setAmount(0);
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
