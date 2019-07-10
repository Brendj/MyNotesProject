/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cf_wt_group_items")
public class WTGroupItem extends AbstractHardCodeCatalogItem {

    @Id
    @Column
    private Long idOfGroupItem;

    public Long getIdOfGroupItem() {
        return idOfGroupItem;
    }

    public void setIdOfGroupItem(Long idOfGroupItem) {
        this.idOfGroupItem = idOfGroupItem;
    }
}
