/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.hibernate.Session;
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

public abstract class DistributedObject{

    /* Идентификатор объекта */
    protected Long globalId;
    /* версия объекта */
    protected Long globalVersion;
    /* версия объекта на момент его создания */
    protected Long globalVersionOnCreate;
     /* дата создания объекта */
    protected Date createdDate;
    /* дата мзминения объекта */
    protected Date lastUpdate;
    /* дата удаления объекта */
    protected Date deleteDate;
    /* статус объекта (активен/удален) */
    protected Boolean deletedState;
    /* имя узла элемента */
    protected String tagName;
    /* GUID объекта */
    protected String guid;
    /* Идентификатор организации */
    protected Long orgOwner;
    protected SendToAssociatedOrgs sendAll;

   // private DistributedObjectException.ErrorType errorType;
    private DistributedObjectException distributedObjectException;

    public DistributedObjectException getDistributedObjectException() {
        return distributedObjectException;
    }

    public void setDistributedObjectException(DistributedObjectException distributedObjectException) {
        this.distributedObjectException = distributedObjectException;
    }

    /* метод добавления атрибутов в узел в тег подтверждения*/
    public Element toConfirmElement(Element element){
        element.setAttribute("Guid", getGuid());
        if(getDeletedState()){
            element.setAttribute("D", "1");
        }
        if(this.getGlobalVersion()!=null) element.setAttribute("V", Long.toString(this.getGlobalVersion()));
        if(this.distributedObjectException != null){
            element.setAttribute("ErrorType", distributedObjectException.getMessage());
            if(distributedObjectException.getData()!=null){
                element.setAttribute("ErrorData", distributedObjectException.getData());
            }
        }
        return element;
    }

    /* меод добавления свойств наследника атрибутов в узел */
    protected abstract void appendAttributes(Element element);

    /* метод добавления общих атрибутов в узел */
    public Element toElement(Element element){
        element.setAttribute("Guid", getGuid());
        element.setAttribute("V", String.valueOf(getGlobalVersion()));
        if(getDeletedState()){
            element.setAttribute("D", "1");
        }
        appendAttributes(element);
        return element;
        /* Метод определения названия элемента */
    }

    /* метод парсинга элемента */
    public DistributedObject build(Node node) throws Exception {
        tagName = node.getNodeName();
        /* Begin required params */
        String stringGUID = XMLUtils.getStringAttributeValue(node, "Guid", 36);
        if (stringGUID != null)
            setGuid(stringGUID);
        Long version = XMLUtils.getLongAttributeValue(node, "V");
        if (version != null)
            setGlobalVersion(version);
        Integer status = XMLUtils.getIntegerAttributeValue(node, "D");
        setDeletedState(status != null);
        /* End required params */
        if (getDeletedState()) {
            return this;
        }
        //Boolean boolSendAll = getBollAttributeValue(node,"SendAll");
        //if(boolSendAll!=null) setSendAll(boolSendAll);
        return parseAttributes(node);
    }

    /* TODO: вставить входным параметром session от hibernate*/
    public void preProcess(Session session) throws DistributedObjectException{}

    protected abstract DistributedObject parseAttributes(Node node) throws Exception;

    public abstract void fill(DistributedObject distributedObject);

    /* Getters and Setters */
    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public Long getGlobalVersionOnCreate() {
        return globalVersionOnCreate;
    }

    public void setGlobalVersionOnCreate(Long globalVersionOnCreate) {
        this.globalVersionOnCreate = globalVersionOnCreate;
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

    public SendToAssociatedOrgs getSendAll() {
        return sendAll;
    }

    public void setSendAll(SendToAssociatedOrgs sendAll) {
        this.sendAll = sendAll;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DistributedObject))
            return false;
        DistributedObject that = (DistributedObject) o;
        if (guid != null ? !guid.equals(that.guid) : that.guid != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return guid != null ? guid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DistributedObject{" +
                "globalId=" + globalId +
                ", globalVersion=" + globalVersion +
                ", guid='" + guid + '\'' +
                ", orgOwner=" + orgOwner +
                ", className=" + getClass().getSimpleName() +
                '}';
    }

    /* идентификатор синхронизируемой организации*/
    private Long idOfSyncOrg;

    public Long getIdOfSyncOrg() {
        return idOfSyncOrg;
    }

    public void setIdOfSyncOrg(Long idOfSyncOrg) {
        this.idOfSyncOrg = idOfSyncOrg;
    }
}
