/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog;

import ru.axetta.ecafe.processor.core.persistence.User;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractHardCodeCatalogItem {

    @Column(unique = true, nullable = false)
    private String description;

    @Column(unique = true, nullable = false)
    private String GUID;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    @Type(type = "ru.axetta.ecafe.processor.core.persistence.BoolType")
    private Boolean deleteState;

    @ManyToOne()
    @JoinColumn(name = "idOfUser")
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
