/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cf_wt_typeOfProduction_items")
public class WTTypeOfProductionItem extends AbstractHardCodeCatalogItem {

    @Id
    @Column
    private Long idOfTypeProductionItem;

    public Long getIdOfTypeProductionItem() {
        return idOfTypeProductionItem;
    }

    public void setIdOfTypeProductionItem(Long idOfTypeProductionItem) {
        this.idOfTypeProductionItem = idOfTypeProductionItem;
    }
}
