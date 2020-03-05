/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs;

import javax.persistence.*;

@Entity
@Table(name = "cf_wt_agegroup_items")
public class WTAgeGroupItem extends AbstractHardCodeCatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOfAgeGroupItem")
    private Long idOfAgeGroupItem;

    public Long getIdOfAgeGroupItem() {
        return idOfAgeGroupItem;
    }

    public void setIdOfAgeGroupItem(Long idOfAgeGroupItem) {
        this.idOfAgeGroupItem = idOfAgeGroupItem;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        } else if(!(o instanceof WTAgeGroupItem)){
            return false;
        }
        WTAgeGroupItem item = (WTAgeGroupItem) o;

        return this.getGUID().equals(item.getGUID());
    }
}
