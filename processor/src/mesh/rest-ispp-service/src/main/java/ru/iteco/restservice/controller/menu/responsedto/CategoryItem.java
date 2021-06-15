package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.wt.WtCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryItem {
    @Schema(description = "Идентификатор категории")
    private Long categoryId;

    @Schema(description = "Название категории")
    private String categoryName;

    @Schema(description = "Признак-идентификатора запрета покупки категории")
    private Long prohibitionId;

    @Schema(description = "Список блюд категории")
    private final List<MenuItem> menuItems;

    public CategoryItem(WtCategory wtCategory) {
        this.categoryId = wtCategory.getIdOfCategory();
        this.categoryName = wtCategory.getDescription();
        this.menuItems = new ArrayList<>();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getProhibitionId() {
        return prohibitionId;
    }

    public void setProhibitionId(Long prohibitionId) {
        this.prohibitionId = prohibitionId;
    }
}
