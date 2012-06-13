/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.06.12
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public abstract class DistributedObject implements Comparable<DistributedObject> {

    /* Идентификатор объекта */
    private long globalId;
    /* версия объекта */
    private long globalVersion;
    /* Идентификатор организации */
    private Long idOfOrg;
    /* дата создания объекта */
    private Date createTime;
    /* дата мзминения объекта */
    private Date editTime;
    /* дата удаления объекта */
    private Date deleteTime;
    /* статус объекта (активен/удален) */
    private boolean status;

    /* меод создания узла элемента */
    public abstract Element toElement(Document document);
    /* метод парсинга элемента */
    public abstract DistributedObject parseXML(Node node);
    /* Метод определения названия элемента */
    public abstract String getNodeName();

    @Override
    public int compareTo(DistributedObject o) {
        return getNodeName().compareTo(o.getNodeName());
    }

    /* Getters and Setters */
    public long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(long globalId) {
        this.globalId = globalId;
    }

    public long getGlobalVersion() {
        return globalVersion;
    }

    public void setGlobalVersion(long globalVersion) {
        this.globalVersion = globalVersion;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
