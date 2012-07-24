/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.text.SimpleDateFormat;
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
    private Set products;
    /* Пользователь бэк-оффиса котрый создал изменил конфигурацию */
    private User userCreate;
    private User userEdit;
    /* дата создания объекта */
    private Date createdDate;
    /* дата мзминения объекта */
    private Date lastUpdate;
    private Set<Org> orgInternal = new HashSet<Org>();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public String getHistory(){
        if (this.userCreate==null)
            return "";
        String stringUserCreate = "";
        String stringUserEdit = "";
        if (getUserCreate()!=null){
            stringUserCreate = getUserCreate().getUserName();
        }
        if (getUserEdit()!=null){
            stringUserEdit = getUserEdit().getUserName();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<span  style=\"white-space:nowrap\">");
        sb.append("Создан: ").append(userCreate).append("<br/>")
                .append("Дата создания: ").append(dateFormat.format(this.createdDate));
        if (this.userEdit!=null)
            sb.append("<br/>").append("Изменен: ").append(userEdit)
                    .append("<br/>").append("Дата изменения: ").append(dateFormat.format(this.lastUpdate));
        /*if (this.userDelete!=null)
            sb.append("<br/>").append('\n').append("Удален: ").append(this.userDelete.getUserName())
                    .append("<br/>").append("Дата удаления: ").append(dateFormat.format(this.deleteTime));*/
        sb.append("</span>");
        return sb.toString();
    }

    public List<Org> getOrgs(){
        return new ArrayList<Org>(Collections.unmodifiableSet(getOrgInternal()));
    }

    public Boolean getOrgEmpty(){
        return orgInternal == null || orgInternal.isEmpty();
    }

    public void addOrg(Org org){
        orgInternal.add(org);
    }

    private Set<Org> getOrgInternal() {
        return orgInternal;
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

    public Set getProducts() {
        return products;
    }

    public void setProducts(Set products) {
        this.products = products;
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
}
