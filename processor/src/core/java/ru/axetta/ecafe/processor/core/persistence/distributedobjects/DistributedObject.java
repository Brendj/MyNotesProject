/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
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
    protected SendToAssociatedOrgs sendAll;

    private DateFormat dateOnlyFormat;

    private DateFormat timeFormat;

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
            //if(distributedObjectException.equals(DistributedObjectException.ErrorType.DATA_EXIST_VALUE)){
            //    element.setAttribute("ErrorType", );
            //} else {
            //    String error = String.format("%d : %s", errorType.getValue(), errorType.name());
            //    element.setAttribute("ErrorType", error);
            //}
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
        /* Begin required params */
        String stringGUID = getStringAttributeValue(node,"Guid",36);
        if(stringGUID!=null) setGuid(stringGUID);

        Long version = getLongAttributeValue(node,"V");
        if(version!=null) setGlobalVersion(version);


        Integer status = getIntegerAttributeValue(node,"D");
        setDeletedState(status != null);
        tagName = node.getNodeName();
        /* End required params */
        if(getDeletedState()){
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

    protected void setAttribute(Element element, String name, Object value){
        if(value!=null) {
            if(value instanceof Boolean) {
                element.setAttribute(name, ((Boolean)value?"1":"0"));
            } else {
                element.setAttribute(name,String.valueOf(value));
            }

        }
    }

    protected Date getDateOnlyAttributeValue(Node node, String attributeName)  throws Exception{
        Date result = null;
        try{
            String attributeValue = getAttributeValue(node, attributeName);
            if(!(attributeValue==null || attributeValue.equals(""))) {
                result = dateOnlyFormat.parse(attributeValue);
            }
        } catch (Exception e){
            throw new DistributedObjectException("PARSE_DATE_VALUE");
        }
        return result;
    }

    protected Date getDateTimeAttributeValue(Node node, String attributeName)  throws Exception{
        Date result = null;
        try{
            String attributeValue = getAttributeValue(node, attributeName);
            if(!(attributeValue==null || attributeValue.equals(""))) {
                result = timeFormat.parse(attributeValue);
            }
        } catch (Exception e){
            throw new DistributedObjectException("PARSE_DATE_VALUE");
        }
        return result;
    }

    protected String getStringAttributeValue(Node node, String attributeName,Integer length) throws Exception{
        if(getAttributeValue(node, attributeName)==null) return null;
        String result = getAttributeValue(node, attributeName);
        if(result.length()>length) return result.substring(0, length);
        return result;
    }

    protected Float getFloatAttributeValue(Node node, String attributeName) throws Exception{
        Float result = null;
        try{
            String attributeValue = getAttributeValue(node, attributeName);
            if(!(attributeValue==null || attributeValue.equals(""))) {
                String replacedString = attributeValue.replaceAll(",", ".");
                result = Float.parseFloat(replacedString);
            }
        } catch (Exception e){
            throw new DistributedObjectException("PARSE_FLOAT_VALUE");
        }
        return result;
    }

    protected Boolean getBollAttributeValue(Node node, String attributeName) throws DistributedObjectException{
        Boolean result = null;
        try{
            String attributeValue = getAttributeValue(node, attributeName);
            if(!(attributeValue==null || attributeValue.equals(""))) {
                result = Boolean.parseBoolean(getAttributeValue(node, attributeName));
            }
        } catch (Exception e){
            throw new DistributedObjectException("PARSE_BOOLEAN_VALUE");
        }
        return result;
    }

    protected Long getLongAttributeValue(Node node, String attributeName) throws Exception{
        Long result = null;
        try{
            String attributeValue = getAttributeValue(node, attributeName);
            if(!(attributeValue==null || attributeValue.equals(""))) {
                result = Long.parseLong(getAttributeValue(node, attributeName));
            }
        } catch (Exception e){
            throw new DistributedObjectException("PARSE_LONG_VALUE");
        }
        return result;
    }

    protected Integer getIntegerAttributeValue(Node node, String attributeName) throws Exception{
        Integer result = null;
        try{
            String attributeValue = getAttributeValue(node, attributeName);
            if(!(attributeValue==null || attributeValue.equals(""))) {
                result = Integer.parseInt(attributeValue);
            }
        } catch (Exception e){
            throw new DistributedObjectException("PARSE_INTEGER_VALUE");
        }
        return result;
    }

    protected Character getCharacterAttributeValue(Node node, String attributeName) throws Exception{
        Character result = null;
        try {
            String attributeValue = getAttributeValue(node, attributeName);
            if(!(attributeValue==null || attributeValue.equals(""))) {
                result = getAttributeValue(node, attributeName).charAt(0);
            }
        } catch (Exception e) {
            throw new DistributedObjectException("PARSE_CHAR_VALUE");
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

    //public DistributedObjectException.ErrorType getErrorType() {
    //    return errorType;
    //}
    //
    //public void setErrorType(DistributedObjectException.ErrorType errorType) {
    //    this.errorType = errorType;
    //}

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

    public SendToAssociatedOrgs getSendAll() {
        return sendAll;
    }

    public void setSendAll(SendToAssociatedOrgs sendAll) {
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
                ", className=" + getClass().getSimpleName() +
                '}';
    }
}
