package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;

import java.util.ArrayList;
import java.util.List;

public class PreorderMenuDetailDTO {
    @Schema(description = "Идентификатор предзаказа")
    private Long preorderId;

    @Schema(description = "Список предзаказов на блюда")
    private List<PreorderMenuDetailItemDTO> dishes;

    public PreorderMenuDetailDTO() {

    }

    public static PreorderMenuDetailDTO build(List<PreorderMenuDetail> preorderMenuDetails) {
        PreorderMenuDetailDTO result = new PreorderMenuDetailDTO();

        List<PreorderMenuDetailItemDTO> dishes = new ArrayList<>();
        for (PreorderMenuDetail preorderMenuDetail : preorderMenuDetails) {
            result.setPreorderId(preorderMenuDetail.getPreorderComplex().getIdOfPreorderComplex());
            PreorderMenuDetailItemDTO preorderMenuDetailItemDTO = new PreorderMenuDetailItemDTO();
            preorderMenuDetailItemDTO.setPreorderMenuDetailId(preorderMenuDetail.getIdOfPreorderMenuDetail());
            preorderMenuDetailItemDTO.setAmount(preorderMenuDetail.getAmount());
            dishes.add(preorderMenuDetailItemDTO);
        }
        result.setDishes(dishes);
        return result;
    }

    public Long getPreorderId() {
        return preorderId;
    }

    public void setPreorderId(Long preorderId) {
        this.preorderId = preorderId;
    }

    public List<PreorderMenuDetailItemDTO> getDishes() {
        return dishes;
    }

    public void setDishes(List<PreorderMenuDetailItemDTO> dishes) {
        this.dishes = dishes;
    }
}
