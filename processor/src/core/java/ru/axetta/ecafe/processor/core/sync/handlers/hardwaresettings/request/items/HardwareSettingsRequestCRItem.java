/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HardwareSettingsRequestCRItem extends HardwareSettingsRequestItem {

    private Integer usedByModule;
    private String readerName;
    private String firmwareVer;

    public HardwareSettingsRequestCRItem(Integer usedByModule, String readerName, String firmwareVer, Date lastUpdate,
            String type, String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.usedByModule = usedByModule;
        this.readerName = readerName;
        this.firmwareVer = firmwareVer;
    }

    public static HardwareSettingsRequestCRItem build(Node itemNode) {

        Integer usedByModule = null;
        String readerName = null;
        String firmwareVer = null;
        Date lastUpdate = null;

        String type = "CR";

        StringBuilder errorMessage = new StringBuilder();

        usedByModule = XMLUtils.getIntegerAttributeValue(itemNode, "UsedByModule");
        if (null == usedByModule) {
            errorMessage.append("Attribute UsedByModule not found");
        }

        readerName = XMLUtils.getAttributeValue(itemNode, "ReaderName");
        if (null == readerName) {
            errorMessage.append("Attribute ReaderName not found");
        }

        firmwareVer = XMLUtils.getAttributeValue(itemNode, "FirmwareVer");
        if (null == firmwareVer) {
            errorMessage.append("Attribute FirmwareVer not found");
        }

        String requestDateString = XMLUtils.getAttributeValue(itemNode, "LastUpdate");
        if (StringUtils.isNotEmpty(requestDateString)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                lastUpdate = simpleDateFormat.parse(requestDateString);
            } catch (Exception e) {
                errorMessage.append("Attribute LastUpdate not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute RequestDate not found");
        }
        return new HardwareSettingsRequestCRItem(usedByModule, readerName, firmwareVer, lastUpdate, type,
                errorMessage.toString());
    }

    public Integer getUsedByModule() {
        return usedByModule;
    }

    public void setUsedByModule(Integer usedByModule) {
        this.usedByModule = usedByModule;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getFirmwareVer() {
        return firmwareVer;
    }

    public void setFirmwareVer(String firmwareVer) {
        this.firmwareVer = firmwareVer;
    }
}
