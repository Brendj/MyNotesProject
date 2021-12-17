package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.preorder.PreorderComplex;

/**
 * Created by nuc on 08.06.2021.
 */
public class PreorderComplexDTO {
    @Schema(description = "Идентификатор предзаказа на комплекс")
    private Long preorderId;

    @Schema(description = "Количество комплексов в предзаказе")
    private Integer amount;

    public static PreorderComplexDTO build(PreorderComplex preorderComplex) {
        PreorderComplexDTO result = new PreorderComplexDTO();
        if (preorderComplex == null) {
            result.setAmount(0);
        } else {
            result.setPreorderId(preorderComplex.getIdOfPreorderComplex());
            result.setAmount(preorderComplex.getAmount());
        }
        return result;
    }

    public static PreorderComplexDTO buildDeleted(PreorderComplex preorderComplex) {
        PreorderComplexDTO result = new PreorderComplexDTO();
        result.setPreorderId(null);
        result.setAmount(0);
        return result;
    }

    public Long getPreorderId() {
        return preorderId;
    }

    public void setPreorderId(Long preorderId) {
        this.preorderId = preorderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
