/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.usercatalog;

import java.util.Date;

public class WTUserCatalogItem {
    private Long idOfUserCatalogItem;
    private WTUserCatalog catalog;
    private String description;
    private String GUID;
    private Date createDate;
    private Date lastUpdate;
    private Long version;
    private Boolean deleteState;

    public WTUserCatalogItem(){
        // For Hibernate
    }

    public Long getIdOfUserCatalogItem() {
        return idOfUserCatalogItem;
    }

    public void setIdOfUserCatalogItem(Long idOfUserCatalogItem) {
        this.idOfUserCatalogItem = idOfUserCatalogItem;
    }

    public WTUserCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(WTUserCatalog catalog) {
        this.catalog = catalog;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Boolean deleteState) {
        this.deleteState = deleteState;
    }

    public String getDeleteStateAsString(){
        return deleteState ? "Удален" : "Активен";
    }

    @Override
    public int hashCode(){
        return idOfUserCatalogItem.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WTUserCatalogItem)) {
            return false;
        }
        WTUserCatalogItem catalogItem = (WTUserCatalogItem) o;
        return this.idOfUserCatalogItem.equals(catalogItem.idOfUserCatalogItem);
    }
}
