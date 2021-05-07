package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.wt.WtDish;

/**
 * Created by nuc on 29.04.2021.
 */
public class DishItem {
    @Schema(description = "Идентификатор блюда")
    private Long dishId;

    @Schema(description = "Название блюда")
    private String dishName;

    @Schema(description = "Состав блюда")
    private String dishContent;

    @Schema(description = "Цена блюда")
    private Long price;

    @Schema(description = "Калорийность блюда")
    private Integer calories;

    @Schema(description = "Масса блюда в граммах")
    private String output;

    @Schema(description = "Белки")
    private Integer protein;

    @Schema(description = "Жиры")
    private Integer fat;

    @Schema(description = "Углеводы")
    private Integer carbohydrates;

    @Schema(description = "Признак ежедневного повтора блюда")
    private Boolean isRegular;

    @Schema(description = "Код блюда")
    private String itemCode;

    public DishItem(WtDish wtDish) {
        this.dishId = wtDish.getIdOfDish();
        this.dishName = wtDish.getDishName();
        this.dishContent = wtDish.getComponentsOfDish();
        this.price = wtDish.getPrice() == null ? 0 : wtDish.getPrice().longValue();
        this.calories = wtDish.getCalories();
        this.output = wtDish.getQty() == null ? "" : wtDish.getQty();
        this.protein = wtDish.getProtein();
        this.fat = wtDish.getFat();
        this.carbohydrates = wtDish.getCarbohydrates();
        this.isRegular = (wtDish.getRepeatableComplex() != null && wtDish.getRepeatableComplex().size() > 0);
        this.itemCode = wtDish.getCode();
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishContent() {
        return dishContent;
    }

    public void setDishContent(String dishContent) {
        this.dishContent = dishContent;
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

    public Boolean getRegular() {
        return isRegular;
    }

    public void setRegular(Boolean regular) {
        isRegular = regular;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}
