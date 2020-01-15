/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.webTechnologist;

import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WtOrgGroup {

    private Long idOfOrgGroup;
    private String nameOfOrgGroup;
    private Date createDate;
    private Date lastUpdate;
    private Integer deleteState;
    private Long version;
    private Set<Org> orgs = new HashSet<>();

    public Long getIdOfOrgGroup() {
        return idOfOrgGroup;
    }

    public void setIdOfOrgGroup(Long idOfOrgGroup) {
        this.idOfOrgGroup = idOfOrgGroup;
    }

    public String getNameOfOrgGroup() {
        return nameOfOrgGroup;
    }

    public void setNameOfOrgGroup(String nameOfOrgGroup) {
        this.nameOfOrgGroup = nameOfOrgGroup;
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

    public Integer getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Integer deleteState) {
        this.deleteState = deleteState;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WtOrgGroup that = (WtOrgGroup) o;
        return Objects.equals(idOfOrgGroup, that.idOfOrgGroup) && Objects.equals(nameOfOrgGroup, that.nameOfOrgGroup)
                && Objects.equals(createDate, that.createDate) && Objects.equals(lastUpdate, that.lastUpdate) && Objects
                .equals(deleteState, that.deleteState) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfOrgGroup, nameOfOrgGroup, createDate, lastUpdate, deleteState, version);
    }
}
