/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.usercatalog;

import ru.axetta.ecafe.processor.core.persistence.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WTUserCatalog {
    private Long idOfUserCatalog;
    private String catalogName;
    private String GUID;
    private Date createDate;
    private Date lastUpdate;
    private Long version;
    private Boolean deleteState;
    private User userCreator;
    private Set<WTUserCatalogItem> items;

    public WTUserCatalog(){
        // For Hibernate
    }

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

    public User getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }

    public Set<WTUserCatalogItem> getItems() {
        if(items == null){
            items = new HashSet<>();
        }
        return items;
    }

    public void setItems(Set<WTUserCatalogItem> items) {
        this.items = items;
    }

    public String getDeleteStateAsString(){
        return deleteState ? "Удален" : "Активен";
    }

    @Override
    public int hashCode(){
        return idOfUserCatalog.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof WTUserCatalog)){
            return false;
        }
        WTUserCatalog catalog = (WTUserCatalog) o;
        return this.idOfUserCatalog.equals(catalog.idOfUserCatalog);
    }
}
