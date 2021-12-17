/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.menu.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProhibitionRequest {
    @Schema(description = "Номер лицевого счета клиента", example = "13177")
    private Long contractId;

    @Schema(description = "Идентификатор блюда", example = "10001", nullable = true)
    private Long idOfDish;

    @Schema(description = "Идентификатор категории", example = "110002", nullable = true)
    private Long idOfCategory;

    @Schema(description = "Идентификатор подкатегории", example = "220003", nullable = true)
    private Long idOfCategoryItem;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Long getIdOfCategory() {
        return idOfCategory;
    }

    public void setIdOfCategory(Long idOfCategory) {
        this.idOfCategory = idOfCategory;
    }

    public Long getIdOfCategoryItem() {
        return idOfCategoryItem;
    }

    public void setIdOfCategoryItem(Long idOfCategoryItem) {
        this.idOfCategoryItem = idOfCategoryItem;
    }
}
