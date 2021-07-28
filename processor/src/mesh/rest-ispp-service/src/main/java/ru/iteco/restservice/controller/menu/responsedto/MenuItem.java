package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.ProhibitionMenu;
import ru.iteco.restservice.model.wt.WtCategoryItem;
import ru.iteco.restservice.model.wt.WtDish;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    @Schema(description = "Идентификатор блюда")
    private Long dishId;

    @Schema(description = "Название блюда")
    private String dishName;

    @Schema(description = "Состав блюда")
    private String dishContent;

    @Schema(description = "Список подкатегорий блюда")
    private final List<SubCategoryItem> subcategories;

    @Schema(description = "Цена блюда в копейках")
    private Long price;

    @Schema(description = "Калорийность блюда")
    private Integer calories;

    @Schema(description = "Масса блюда в граммах")
    private String output;

    @Schema(description = "Признак-идентификатора запрета покупки блюда")
    private Long prohibitionId;

    @Schema(description = "Белки")
    private Integer protein;

    @Schema(description = "Жиры")
    private Integer fat;

    @Schema(description = "Углеводы")
    private Integer carbohydrates;

    public MenuItem(WtDish wtDish, List<ProhibitionMenu> prohibitions) {
        this.dishId = wtDish.getIdOfDish();
        this.dishName = wtDish.getDishName();
        this.dishContent = wtDish.getComponentsOfDish();
        this.price = (wtDish.getPrice().multiply(BigDecimal.valueOf(100))).longValue();
        this.calories = wtDish.getCalories();
        this.output = wtDish.getQty() == null ? "" : wtDish.getQty();
        this.protein = wtDish.getProtein();
        this.fat = wtDish.getFat();
        this.carbohydrates = wtDish.getCarbohydrates();
        this.subcategories = new ArrayList<>();
        for (WtCategoryItem wtCategoryItem : wtDish.getCategoryItems()) {
            SubCategoryItem item = new SubCategoryItem(wtCategoryItem);
            item.setProhibitionId(getProhibitionForSubcategory(wtCategoryItem, prohibitions));
            this.subcategories.add(item);
        }
    }

    private Long getProhibitionForSubcategory(WtCategoryItem wtCategoryItem, List<ProhibitionMenu> prohibitions) {
        for (ProhibitionMenu pm : prohibitions) {
            if (pm.getCategoryItem() != null && pm.getCategoryItem().equals(wtCategoryItem)) {
                return pm.getIdOfProhibitions();
            }
        }
        return null;
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

    public List<SubCategoryItem> getSubcategories() {
        return subcategories;
    }

    public Long getProhibitionId() {
        return prohibitionId;
    }

    public void setProhibitionId(Long prohibitionId) {
        this.prohibitionId = prohibitionId;
    }

    public String getDishContent() {
        return dishContent;
    }

    public void setDishContent(String dishContent) {
        this.dishContent = dishContent;
    }
}
