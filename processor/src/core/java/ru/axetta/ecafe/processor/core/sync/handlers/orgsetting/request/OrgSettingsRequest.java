/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgsetting.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class OrgSettingsRequest implements SectionRequest {
    public static final String SECTION_NAME = "OrgSettings";
    private Long maxVersion;
    private List<OrgSettingSyncPOJO> items = new LinkedList<>();
    private Long idOfOrgSource;

    public OrgSettingsRequest(Node sectionElement) {
        this.maxVersion = XMLUtils.getLongAttributeValue(sectionElement, "V");
        Node nodeElement = sectionElement.getFirstChild();
        while(nodeElement != null){
            if(nodeElement.getNodeName().equals("OS")){
                OrgSettingSyncPOJO setting = new OrgSettingSyncPOJO();
                setting.setGroupID(XMLUtils.getIntegerAttributeValue(nodeElement, "SGroup"));
                setting.setIdOfOrg(XMLUtils.getIntegerAttributeValue(nodeElement, "IdOfOrg"));
                Node nodeItem = nodeElement.getFirstChild();
                while (nodeItem != null){
                    if(nodeItem.getNodeName().equals("OSI")){
                        OrgSettingItemSyncPOJO item = new OrgSettingItemSyncPOJO();
                        item.setGlobalID(XMLUtils.getIntegerAttributeValue(nodeItem, "GlobalId"));
                        item.setType(XMLUtils.getIntegerAttributeValue(nodeItem, "SType"));
                        item.setValue(XMLUtils.getStringAttributeValue(nodeItem, "SValue", 512));
                        item.setVersion(XMLUtils.getLongAttributeValue(nodeItem, "V"));
                        setting.getItems().add(item);
                    }
                    nodeItem.getNextSibling();
                }
                items.add(setting);
            }
            nodeElement.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    public List<OrgSettingSyncPOJO> getItems() {
        if(items == null){
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<OrgSettingSyncPOJO> items) {
        this.items = items;
    }

    public Long getIdOfOrgSource() {
        return idOfOrgSource;
    }

    public void setIdOfOrgSource(Long idOfOrgSource) {
        this.idOfOrgSource = idOfOrgSource;
    }
}
