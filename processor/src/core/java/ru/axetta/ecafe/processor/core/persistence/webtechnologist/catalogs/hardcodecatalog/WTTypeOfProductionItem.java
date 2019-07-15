/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import javax.persistence.*;

@Entity
@Table(name = "cf_wt_typeOfProduction_items")
public class WTTypeOfProductionItem extends AbstractHardCodeCatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOfTypeProductionItem;

    public Long getIdOfTypeProductionItem() {
        return idOfTypeProductionItem;
    }

    public void setIdOfTypeProductionItem(Long idOfTypeProductionItem) {
        this.idOfTypeProductionItem = idOfTypeProductionItem;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        } else if(!(o instanceof WTTypeOfProductionItem)){
            return false;
        }
        WTTypeOfProductionItem item = (WTTypeOfProductionItem) o;

        return this.getGUID().equals(item.getGUID());
    }
}
