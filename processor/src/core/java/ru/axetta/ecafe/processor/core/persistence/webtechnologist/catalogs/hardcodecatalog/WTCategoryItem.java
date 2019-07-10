/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cf_wt_category_items")
public class WTCategoryItem extends AbstractHardCodeCatalogItem {

    @Id
    @Column
    private Long idOfCategoryItem;

    public Long getIdOfCategoryItem() {
        return idOfCategoryItem;
    }

    public void setIdOfCategoryItem(Long idOfCategoryItem) {
        this.idOfCategoryItem = idOfCategoryItem;
    }
}
