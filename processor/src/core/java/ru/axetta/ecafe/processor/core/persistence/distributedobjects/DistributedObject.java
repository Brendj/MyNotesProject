/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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
    protected Boolean sendAll;

    private DateFormat dateOnlyFormat;

    private DateFormat timeFormat;

    private DistributedObjectException.ErrorType errorType;

    /* Node для последующего разбора */
    private Node node;

    /* метод добавления атрибутов в узел в тег подтверждения*/
    public Element toConfirmElement(Element element){
        element.setAttribute("Guid", getGuid());
        if(getDeletedState()){
            element.setAttribute("D", "1");
        }
        if(this.getGlobalVersion()!=null) element.setAttribute("V", Long.toString(this.getGlobalVersion()));
        if(this.errorType != null){
            String error = String.format("%d : %s", errorType.getValue(), errorType.name());
            element.setAttribute("ErrorType", error);
        }
        return element;
    }

    /* меод добавления свойств наследника атрибутов в узел */
    protected abstract void appendAttributes(Element element);

    /* метод добавления общих атрибутов в узел */
    public Element toElement(Element element){
        if(getDeletedState()){
            element.setAttribute("D", "1");
        }
        appendAttributes(element);
        element.setAttribute("Guid", getGuid());
        element.setAttribute("V", String.valueOf(getGlobalVersion()));
        //element.setAttribute("SendAll", String.valueOf(getSendAll()));
        return element;
        /* Метод определения названия элемента */
    }

    /* метод парсинга элемента */
    public DistributedObject build(Node node) throws ParseException, IOException {
        /* Begin required params */
        String stringGUID = getStringAttributeValue(node,"Guid",36);
        if(stringGUID!=null) setGuid(stringGUID);

        Long version = getLongAttributeValue(node,"V");
        if(version!=null) setGlobalVersion(version);


        Integer status = getIntegerAttributeValue(node,"D");
        setDeletedState(status != null);
        tagName = node.getNodeName();
        /* End required params */
        if(deletedState){
            return this;
        }
        Boolean boolSendAll = getBollAttributeValue(node,"SendAll");
        if(boolSendAll!=null) setSendAll(boolSendAll);
        return parseAttributes(node);
    }
    public void preProcess() throws DistributedObjectException,
            ru.axetta.ecafe.processor.core.sync.distributionsync.DistributedObjectException {}

    protected abstract DistributedObject parseAttributes(Node node) throws ParseException, IOException;

    public abstract void fill(DistributedObject distributedObject);

    public List<DistributedObject> getDistributedObjectChildren(HashMap<String, Long> currentMaxVersions) throws Exception
    { return null;}

    protected void setAttribute(Element element, String name, Object value){
        if(value!=null) {
            if(value instanceof Boolean) {
                element.setAttribute(name, ((Boolean)value?"1":"0"));
            } else {
                element.setAttribute(name,String.valueOf(value));
            }

        }
    }

    protected Date getDateAttributeValue(Node node, String attributeName)  throws ParseException{
        if(getAttributeValue(node, attributeName)==null) return null;
        TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat timeFormat1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        timeFormat1.setTimeZone(localTimeZone);
        return timeFormat1.parse(getAttributeValue(node,attributeName));
    }

    protected String getStringAttributeValue(Node node, String attributeName,Integer length){
        if(getAttributeValue(node, attributeName)==null) return null;
        String result = getAttributeValue(node, attributeName);
        if(result.length()>length) return result.substring(0, length);
        return result;
    }

    protected Float getFloatAttributeValue(Node node, String attributeName){
        Float result = null;
        try{
            String calString = getAttributeValue(node, attributeName);
            if (calString==null || calString.equals("")) {
                return null;
            }
            String replacedString = calString.replaceAll(",", ".");
            result = Float.parseFloat(replacedString);
        } catch (Exception e){ result=null;}
        return result;
    }

    protected Boolean getBollAttributeValue(Node node, String attributeName){
        Boolean result = null;
        try{
            result = Boolean.parseBoolean(getAttributeValue(node, attributeName));
        } catch (Exception e){ result=null;}
        return result;
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

    protected Character getCharacterAttributeValue(Node node, String attributeName) {
        Character result = null;
        try {
            result = getAttributeValue(node, attributeName).charAt(0);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    private String getAttributeValue(Node node, String attributeName){
        if(node.getAttributes().getNamedItem(attributeName)==null) return null;
        return node.getAttributes().getNamedItem(attributeName).getTextContent().trim();
    }

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

    public DateFormat getDateOnlyFormat() {
        return dateOnlyFormat;
    }

    public void setDateOnlyFormat(DateFormat dateOnlyFormat) {
        this.dateOnlyFormat = dateOnlyFormat;
    }

    public DateFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(DateFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    public DistributedObjectException.ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(DistributedObjectException.ErrorType errorType) {
        this.errorType = errorType;
    }

    public DateFormat getDateFormat() {
        return timeFormat;
    }

    //public DateFormat getDateFormat() {
    //    TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
    //    DateFormat timeFormat1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    //    timeFormat1.setTimeZone(localTimeZone);
    //    return timeFormat1;
    //}
    //
    //public DateFormat getTimeFormat() {
    //    TimeZone localTimeZone = TimeZone.getTimeZone("Europe/Moscow");
    //    DateFormat timeFormat1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    //    timeFormat1.setTimeZone(localTimeZone);
    //    return timeFormat1;
    //}

    public Boolean getSendAll() {
        return sendAll;
    }

    public void setSendAll(Boolean sendAll) {
        this.sendAll = sendAll;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DistributedObject)) {
            return false;
        }

        DistributedObject that = (DistributedObject) o;

        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }

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
                ", errorType=" + errorType +
                ", className=" + getClass().getSimpleName() +
                '}';
    }
}
