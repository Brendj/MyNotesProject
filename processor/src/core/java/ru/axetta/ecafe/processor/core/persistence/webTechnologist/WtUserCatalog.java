/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import java.util.Objects;

public class WtUserCatalog {

    private Long idOfUserCatalog;
    private String catalogName;
    private String guid;
    private Long createDate;
    private Long lastUpdate;
    private Long version;
    private Integer deleteState;

    public Long getIdOfUserCatalog() {
        return idOfUserCatalog;
    }

    public void setIdOfUserCatalog(Long idOfUserCatalog) {
        this.idOfUserCatalog = idOfUserCatalog;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtUserCatalog that = (WtUserCatalog) o;
        return Objects.equals(idOfUserCatalog, that.idOfUserCatalog) && Objects.equals(catalogName, that.catalogName)
                && Objects.equals(guid, that.guid) && Objects.equals(createDate, that.createDate) && Objects
                .equals(lastUpdate, that.lastUpdate) && Objects.equals(version, that.version) && Objects
                .equals(deleteState, that.deleteState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfUserCatalog, catalogName, guid, createDate, lastUpdate, version, deleteState);
    }
}
