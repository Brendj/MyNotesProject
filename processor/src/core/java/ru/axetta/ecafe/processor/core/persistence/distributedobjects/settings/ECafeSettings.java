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
        setAttribute(element,"Text", settingText);
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
        String stringSettingText = getStringAttributeValue(node, "Text", 128);
        if(stringSettingText!=null) setSettingText(stringSettingText);
        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ECafeSettings) distributedObject).getOrgOwner());
        setSettingValue(((ECafeSettings) distributedObject).getSettingValue());
        setSettingText(((ECafeSettings) distributedObject).getSettingText());
        setIdentificator(((ECafeSettings) distributedObject).getIdentificator());
    }

    private String settingValue;

    private Long identificator;
    private String settingText;

    public String getSettingText() {
        return settingText;
    }

    public void setSettingText(String settingText) {
        this.settingText = settingText;
    }


    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public Long getIdentificator() {
        return identificator;
    }

    public void setIdentificator(Long identificator) {
        this.identificator = identificator;
    }
}
