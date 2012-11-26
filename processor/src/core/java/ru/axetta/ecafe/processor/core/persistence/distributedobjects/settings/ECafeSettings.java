/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.11.12
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class ECafeSettings extends DistributedObject{

    @Override
    public void preProcess(Session session) throws DistributedObjectException {}

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Value", settingValue);
        setAttribute(element,"Id", identificator);
    }

    @Override
    protected ECafeSettings parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringValue = getStringAttributeValue(node, "Value", 128);
        if(stringValue!=null) setSettingValue(stringValue);
        Long longId = getLongAttributeValue(node, "Id");
        if(longId!=null) setIdentificator(longId);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ECafeSettings) distributedObject).getOrgOwner());
        setSettingValue(((ECafeSettings) distributedObject).getSettingValue());
        setIdentificator(((ECafeSettings) distributedObject).getIdentificator());
    }

    private Long idOfECafeSetting;
    private Long globalVersion;
    private String guid;
    private Date lastUpdate;
    private Date deleteDate;
    private Date createdDate;
    private SendToAssociatedOrgs sendAll;
    private Boolean deletedState;
    private String settingValue;
    private Long orgOwner;
    private Long identificator;



    public Long getIdentificator() {
        return identificator;
    }

    public void setIdentificator(Long identificator) {
        this.identificator = identificator;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getGlobalVersion() {
        return globalVersion;
    }

    public void setGlobalVersion(Long globalVersion) {
        this.globalVersion = globalVersion;
    }


    public Long getIdOfECafeSetting() {
        return idOfECafeSetting;
    }

    public void setIdOfECafeSetting(Long idOfECafeSetting) {
        this.idOfECafeSetting = idOfECafeSetting;
    }
}
