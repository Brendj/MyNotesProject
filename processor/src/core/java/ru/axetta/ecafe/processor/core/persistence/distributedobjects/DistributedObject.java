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
    protected Long globalId;
    /* версия объекта */
    protected Long globalVersion;
    /* Идентификатор организации */
    protected Long orgOwner;
     /* дата создания объекта */
    protected Date createdDate;
    /* дата мзминения объекта */
    protected Date lastUpdate;
    /* дата удаления объекта */
    protected Date deleteDate;
    /* статус объекта (активен/удален) */
    protected Boolean deletedState;
    /* полуе локального идентификатора*/
    protected Long localID;
    /* меод создания узла элемента */
    public Element toConfirmElement(Element element){
        element.setAttribute("GID", Long.toString(this.getGlobalId()));
        if(getDeletedState()){
            element.setAttribute("D", "1");
        }
        if(this.getLocalID()!=null) element.setAttribute("LID", Long.toString(this.getLocalID()));
        if(this.getGlobalVersion()!=null) element.setAttribute("V", Long.toString(this.getGlobalVersion()));
        return element;
    }

    protected abstract void appendAttributes(Element element);

    public Element toElement(Element element){
        if(getDeletedState()){
            element.setAttribute("D", "1");
        } else {
            appendAttributes(element);
        }
        element.setAttribute("GID", Long.toString(this.getGlobalId()));
        element.setAttribute("V", Long.toString(this.getGlobalVersion()));
        return element;
    };
    /* метод парсинга элемента */
    public abstract DistributedObject build(Node node);

    /* Метод определения названия элемента */
    protected String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    protected void setAttribute(Element element, String name, Object value){
        if(value!=null) {
            if(value instanceof Boolean) {
                element.setAttribute(name, ((Boolean)value?"1":"0"));
            } else {
                element.setAttribute(name,String.valueOf(value));
            }

        }
    }

    protected String getStringAttributeValue(Node node, String attributeName,Integer length){
        StringBuilder result = new StringBuilder(getAttributeValue(node, attributeName));
        if(result.length()>length) return result.substring(0, length);
        return result.toString();
    }

    protected Long getLongAttributeValue(Node node, String attributeName){
        Long result = null;
        try{
            result = Long.parseLong(getAttributeValue(node, attributeName));
        } catch (Exception e){ result=null;}
        return result;
    }

    protected Integer getIntegerAttributeValue(Node node, String attributeName){
        Integer result = null;
        try{
            result = Integer.parseInt(getAttributeValue(node, attributeName));
        } catch (Exception e){ result=null;}
        return result;
    }

    private String getAttributeValue(Node node, String attributeName){
        if(node.getAttributes().getNamedItem(attributeName)==null) return null;
        return node.getAttributes().getNamedItem(attributeName).getTextContent().trim();
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

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
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

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }
}
