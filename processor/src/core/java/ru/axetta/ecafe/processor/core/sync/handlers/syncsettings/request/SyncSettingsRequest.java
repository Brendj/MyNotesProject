/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SyncSettingsRequest implements SectionRequest {
    public static final String SECTION_NAME = "SyncSettingsRequest";

    private List<SyncSettingsSectionItem> itemList = new LinkedList<>();
    private Long maxVersion;
    private Long owner;

    public SyncSettingsRequest(Node sectionElement) throws Exception {
        this.maxVersion = XMLUtils.getLongAttributeValue(sectionElement, "V");
        Node nodeElement = sectionElement.getFirstChild();
        while (nodeElement != null) {
            if (nodeElement.getNodeName().equals("SS")) {
                SyncSettingsSectionItem setting = new SyncSettingsSectionItem();
                setting.setContentType(XMLUtils.getIntegerAttributeValue(nodeElement, "ContentType"));
                String concreteTime = XMLUtils.getAttributeValue(nodeElement, "ConcreteTime");
                if(concreteTime != null) {
                    setting.setConcreteTime(Arrays.asList(
                            StringUtils.split(concreteTime, ";"))
                    );
                }
                setting.setEverySeconds(XMLUtils.getIntegerAttributeValue(nodeElement, "EverySeconds"));
                setting.setLimitStartHour(XMLUtils.getIntegerAttributeValue(nodeElement, "LimitStartHour"));
                setting.setLimitEndHour(XMLUtils.getIntegerAttributeValue(nodeElement, "LimitEndHour"));
                setting.setMonday(XMLUtils.getBooleanAttributeValue(nodeElement, "Monday"));
                setting.setTuesday(XMLUtils.getBooleanAttributeValue(nodeElement, "Tuesday"));
                setting.setWednesday(XMLUtils.getBooleanAttributeValue(nodeElement, "Wednesday"));
                setting.setThursday(XMLUtils.getBooleanAttributeValue(nodeElement, "Thursday"));
                setting.setFriday(XMLUtils.getBooleanAttributeValue(nodeElement, "Friday"));
                setting.setSaturday(XMLUtils.getBooleanAttributeValue(nodeElement, "Saturday"));
                setting.setSunday(XMLUtils.getBooleanAttributeValue(nodeElement, "Sunday"));
                setting.setVersion(XMLUtils.getLongAttributeValue(nodeElement, "V"));
                setting.setDeleteState(XMLUtils.getBooleanAttributeValue(nodeElement, "D"));

                itemList.add(setting);
            }
            nodeElement = nodeElement.getNextSibling();
        }
    }

    public List<SyncSettingsSectionItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<SyncSettingsSectionItem> itemList) {
        this.itemList = itemList;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    @Override
    public String getRequestSectionName() {
        return null;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }
}
