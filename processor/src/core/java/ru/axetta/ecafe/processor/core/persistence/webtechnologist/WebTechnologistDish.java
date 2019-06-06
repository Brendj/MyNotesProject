/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist;

import ru.axetta.ecafe.processor.core.persistence.User;

import java.util.Date;
import java.util.Set;

public class WebTechnologistDish {
    private Long idOfWebTechnologistDish;
    private String dishName;
    private String componentsOfDish;
    private Integer code;
    private Long price;
    private Date dateOfBeginMenuIncluding;
    private Date dateOfEndMenuIncluding;
    private Date createDate;
    private Date lastUpdate;
    private Long version;
    private Boolean deleteState;
    private User userOwner;
    private String GUID;
    private Set<WebTechnologistCatalogItem> catalogTypes;

    public WebTechnologistDish(){
        // For Hibernate
    }

    public Long getIdOfWebTechnologistDish() {
        return idOfWebTechnologistDish;
    }

    public void setIdOfWebTechnologistDish(Long idOfWebTechnologistDish) {
        this.idOfWebTechnologistDish = idOfWebTechnologistDish;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getComponentsOfDish() {
        return componentsOfDish;
    }

    public void setComponentsOfDish(String componentsOfDish) {
        this.componentsOfDish = componentsOfDish;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Date getDateOfBeginMenuIncluding() {
        return dateOfBeginMenuIncluding;
    }

    public void setDateOfBeginMenuIncluding(Date dateOfBeginMenuIncluding) {
        this.dateOfBeginMenuIncluding = dateOfBeginMenuIncluding;
    }

    public Date getDateOfEndMenuIncluding() {
        return dateOfEndMenuIncluding;
    }

    public void setDateOfEndMenuIncluding(Date dateOfEndMenuIncluding) {
        this.dateOfEndMenuIncluding = dateOfEndMenuIncluding;
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

    public User getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(User userOwner) {
        this.userOwner = userOwner;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public Set<WebTechnologistCatalogItem> getCatalogTypes() {
        return catalogTypes;
    }

    public void setCatalogTypes(Set<WebTechnologistCatalogItem> catalogTypes) {
        this.catalogTypes = catalogTypes;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
