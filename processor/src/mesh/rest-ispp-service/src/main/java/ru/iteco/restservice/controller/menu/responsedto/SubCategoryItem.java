package ru.iteco.restservice.controller.menu.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.iteco.restservice.model.wt.WtCategoryItem;

/**
 * Created by nuc on 27.04.2021.
 */
public class SubCategoryItem {
    @Schema(description = "Идентификатор подкатегории")
    private Long subCategoryId;

    @Schema(description = "Название подкатегории")
    private String subCategoryName;

    public SubCategoryItem(WtCategoryItem wtCategoryItem) {
        this.subCategoryId = wtCategoryItem.getIdOfCategoryItem();
        this.subCategoryName = wtCategoryItem.getDescription();
    }

    public Long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}
