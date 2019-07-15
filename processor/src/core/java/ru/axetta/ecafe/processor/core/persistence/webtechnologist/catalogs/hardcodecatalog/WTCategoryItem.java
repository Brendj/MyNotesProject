/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import javax.persistence.*;

@Entity
@Table(name = "cf_wt_category_items")
public class WTCategoryItem extends AbstractHardCodeCatalogItem {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOfCategoryItem;

    public Long getIdOfCategoryItem() {
        return idOfCategoryItem;
    }

    public void setIdOfCategoryItem(Long idOfCategoryItem) {
        this.idOfCategoryItem = idOfCategoryItem;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        } else if(!(o instanceof WTCategoryItem)){
            return false;
        }
        WTCategoryItem item = (WTCategoryItem) o;

        return this.getGUID().equals(item.getGUID());
    }
}