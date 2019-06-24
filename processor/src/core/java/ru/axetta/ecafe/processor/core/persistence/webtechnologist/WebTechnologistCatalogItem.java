/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist;

import java.util.Date;

public class WebTechnologistCatalogItem {
    private Long idOfWebTechnologistCatalogItem;
    private WebTechnologistCatalog catalog;
    private String description;
    private String GUID;
    private Date createDate;
    private Date lastUpdate;
    private Long version;
    private Boolean deleteState;

    public WebTechnologistCatalogItem(){
        // For Hibernate
    }

    public Long getIdOfWebTechnologistCatalogItem() {
        return idOfWebTechnologistCatalogItem;
    }

    public void setIdOfWebTechnologistCatalogItem(Long idOfWebTechnologistCatalogItem) {
        this.idOfWebTechnologistCatalogItem = idOfWebTechnologistCatalogItem;
    }

    public WebTechnologistCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(WebTechnologistCatalog catalog) {
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
        return idOfWebTechnologistCatalogItem.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebTechnologistCatalogItem)) {
            return false;
        }
        WebTechnologistCatalogItem catalogItem = (WebTechnologistCatalogItem) o;
        return this.idOfWebTechnologistCatalogItem.equals(catalogItem.idOfWebTechnologistCatalogItem);
    }
}
