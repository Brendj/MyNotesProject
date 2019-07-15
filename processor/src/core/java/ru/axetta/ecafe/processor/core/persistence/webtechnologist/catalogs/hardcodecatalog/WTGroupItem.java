/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import javax.persistence.*;

@Entity
@Table(name = "cf_wt_group_items")
public class WTGroupItem extends AbstractHardCodeCatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idOfGroupItem;

    public Long getIdOfGroupItem() {
        return idOfGroupItem;
    }

    public void setIdOfGroupItem(Long idOfGroupItem) {
        this.idOfGroupItem = idOfGroupItem;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        } else if(!(o instanceof WTGroupItem)){
            return false;
        }
        WTGroupItem item = (WTGroupItem) o;

        return this.getGUID().equals(item.getGUID());
    }
}
