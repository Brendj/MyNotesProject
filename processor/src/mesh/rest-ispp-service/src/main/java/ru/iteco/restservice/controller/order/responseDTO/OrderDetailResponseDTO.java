/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.order.responseDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Детали заказа."
        + " Наполнение полей зависит от того, что было продано: комплекс или отдельно блюда."
        + " Для комплекса заполняются поля \"complexName\", \"currentPrice\", \"isDiscountComplex\", \"goodType\"."
        + " Оставшийся поля, помеченные как nullable, заполняются для блюд.")
public class OrderDetailResponseDTO {
    @Schema(description = "ID записи", example = "704602893601087488")
    private String idOfOrderDetail;

    @Schema(description = "Название комплекса", example = "Составной_Платный", nullable = true)
    private String complexName;

    @Schema(description = "Цена комплекса в копейках", example = "11075", nullable = true)
    private Long currentPrice;

    @Schema(description = "Признак льготного комплекса", example = "false", nullable = true)
    private Boolean isDiscountComplex;

    @Schema(description = "Вид рациона", example = "Завтрак", nullable = true)
    private String goodType;

    @Schema(description = "Цена блюда в копейках", example = "0", nullable = true)
    private Long price;

    @Schema(description = "Название продукта", example = "Фруктовый салат", nullable = true)
    private String name;

    @Schema(description = "Калорийность", example = "83", nullable = true)
    private Integer calories;

    @Schema(description = "Вес", example = "100", nullable = true)
    private String output;

    @Schema(description = "Белки", example = "14", nullable = true)
    private Integer protein;

    @Schema(description = "Жиры", example = "1", nullable = true)
    private Integer fat;

    @Schema(description = "Углеводы", example = "54", nullable = true)
    private Integer carbohydrates;

    @Schema(description = "Количество", example = "1")
    private Integer amount;

    @JsonIgnore
    private Long idOfComplex;

    private List<OrderDetailResponseDTO> complexDetail;

    public List<OrderDetailResponseDTO> getComplexDetail() {
        return complexDetail;
    }

    public void setComplexDetail(List<OrderDetailResponseDTO> complexDetail) {
        this.complexDetail = complexDetail;
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

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

    public String getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(String idOfOrderDetail) {
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
