/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;

import java.util.Date;

public class FoodBoxPreorderDish {
    private Long idFoodBoxPreorderDish;
    private FoodBoxPreorder foodBoxPreorder;
    private Long idOfDish;
    private Integer price;
    private Integer qty;
    private Date createDate;
    private Date updateDate;

    public FoodBoxPreorderDish()
    {
        updateDate = new Date();
    }

    public Long getIdFoodBoxPreorderDish() {
        return idFoodBoxPreorderDish;
    }

    public void setIdFoodBoxPreorderDish(Long idFoodBoxPreorderDish) {
        this.idFoodBoxPreorderDish = idFoodBoxPreorderDish;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public FoodBoxPreorder getFoodBoxPreorder() {
        return foodBoxPreorder;
    }

    public void setFoodBoxPreorder(FoodBoxPreorder foodBoxPreorder) {
        this.foodBoxPreorder = foodBoxPreorder;
    }
}
