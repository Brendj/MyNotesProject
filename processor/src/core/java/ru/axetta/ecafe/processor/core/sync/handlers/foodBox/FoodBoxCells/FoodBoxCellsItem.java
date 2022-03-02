/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxCells;

public class FoodBoxCellsItem {

    private Long idFoodBox;
    private Long totalCellsCount;

    public FoodBoxCellsItem() {

    }

    public Long getIdFoodBox() {
        return idFoodBox;
    }

    public void setIdFoodBox(Long idFoodBox) {
        this.idFoodBox = idFoodBox;
    }

    public Long getTotalCellsCount() {
        return totalCellsCount;
    }

    public void setTotalCellsCount(Long totalCellsCount) {
        this.totalCellsCount = totalCellsCount;
    }
}
