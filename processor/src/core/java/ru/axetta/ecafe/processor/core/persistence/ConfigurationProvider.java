/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 14.05.12
 * Time: 23:17
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationProvider {
    private Long idOfConfigurationProvider;
    private String name;
    private Integer menuSyncCountDays;
    private Integer menuSyncCountDaysInPast;
    private Long version;
    //private Set products;
    /* Пользователь бэк-оффиса котрый создал изменил конфигурацию */
    private User userCreate;
    private User userEdit;
    /* дата создания объекта */
    private Date createdDate;
    /* дата мзминения объекта */
    private Date lastUpdate;
    private Set<Org> orgInternal = new HashSet<Org>();

    public List<Org> getOrgs(){
        return new ArrayList<Org>(Collections.unmodifiableSet(getOrgInternal()));
    }

    public Boolean getOrgEmpty(){
        return orgInternal == null || orgInternal.isEmpty();
    }

    public void addOrg(Org org){
        orgInternal.add(org);
    }

    public void addOrg(Collection<Org> org){
        orgInternal.addAll(org);
    }

    public Set<Org> getOrgInternal() {
        return orgInternal;
    }

    public void clearOrg(){
        orgInternal.clear();
    }

    private void setOrgInternal(Set<Org> orgInternal) {
        this.orgInternal = orgInternal;
    }

    public Long getIdOfConfigurationProvider() {
        return idOfConfigurationProvider;
    }

    public void setIdOfConfigurationProvider(Long idOfConfigurationProvider) {
        this.idOfConfigurationProvider = idOfConfigurationProvider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public User getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(User userCreate) {
        this.userCreate = userCreate;
    }

    public User getUserEdit() {
        return userEdit;
    }

    public void setUserEdit(User userEdit) {
        this.userEdit = userEdit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigurationProvider that = (ConfigurationProvider) o;

        if (!idOfConfigurationProvider.equals(that.idOfConfigurationProvider)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfConfigurationProvider.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public Integer getMenuSyncCountDays() {
        return menuSyncCountDays;
    }

    public void setMenuSyncCountDays(Integer menuSyncCountDays) {
        this.menuSyncCountDays = menuSyncCountDays;
    }

    public Integer getMenuSyncCountDaysInPast() {
        return menuSyncCountDaysInPast;
    }

    public void setMenuSyncCountDaysInPast(Integer menuSyncCountDaysInPast) {
        this.menuSyncCountDaysInPast = menuSyncCountDaysInPast;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
