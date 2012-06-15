/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.06.12
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */

public abstract class DistributedObject{

    /* Идентификатор объекта */
    private Long globalId;
    /* версия объекта */
    private Long globalVersion;
    /* Идентификатор организации */
    private Long idOfOrg;
    /* дата создания объекта */
    private Date createTime;
    /* дата мзминения объекта */
    private Date editTime;
    /* дата удаления объекта */
    private Date deleteTime;
    /* статус объекта (активен/удален) */
    private Boolean status;
    /* полуе локального идентификатора*/
    private Long localID;
    /* меод создания узла элемента */
    public abstract Element toElement(Document document,String action);
    /* метод парсинга элемента */
    public abstract String parseXML(Node node);
    /* Метод определения названия элемента */
    public abstract String getNodeName();

    public String getAttributeValue(Node node, String attributeName){
        return (node.getAttributes().getNamedItem(attributeName)!=null?node.getAttributes().getNamedItem(
                attributeName).getTextContent():null);
    }

    /* Getters and Setters */
    public Long getLocalID() {
        return localID;
    }

    public void setLocalID(Long localID) {
        this.localID = localID;
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public Long getGlobalVersion() {
        return globalVersion;
    }

    public void setGlobalVersion(Long globalVersion) {
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

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
