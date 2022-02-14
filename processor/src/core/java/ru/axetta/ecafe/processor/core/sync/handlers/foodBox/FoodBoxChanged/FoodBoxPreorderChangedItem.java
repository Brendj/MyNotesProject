/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxChanged;

import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxStateTypeEnum;

public class FoodBoxPreorderChangedItem {

    private Long id;
    private String error;
    private Long idOfFoodBox;
    private Integer cellNumber;
    private FoodBoxStateTypeEnum state;
    private Long idOfOrder;
    private Integer cancelReason;

    public FoodBoxPreorderChangedItem() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getIdOfFoodBox() {
        return idOfFoodBox;
    }

    public void setIdOfFoodBox(Long idOfFoodBox) {
        this.idOfFoodBox = idOfFoodBox;
    }

    public Integer getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(Integer cellNumber) {
        this.cellNumber = cellNumber;
    }

    public FoodBoxStateTypeEnum getState() {
        return state;
    }

    public void setState(FoodBoxStateTypeEnum state) {
        this.state = state;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Integer getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(Integer cancelReason) {
        this.cancelReason = cancelReason;
    }
}
