/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.order.responseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponseDTO {
    private Long idOfOrderDetail;
    private String complexName;
    private Long currentPrice;
    private Long price;
    private Boolean isDiscountComplex;
    private String goodType;
    private String name;
    private Integer calories;
    private String output;
    private Integer protein;
    private Integer fat;
    private Integer carbohydrates;
    private Integer amount;

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Boolean getDiscountComplex() {
        return isDiscountComplex;
    }

    public void setDiscountComplex(Boolean discountComplex) {
        isDiscountComplex = discountComplex;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }

    public Integer getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Integer carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderDetailResponseDTO that = (OrderDetailResponseDTO) o;
        return Objects.equals(idOfOrderDetail, that.idOfOrderDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfOrderDetail);
    }
}
