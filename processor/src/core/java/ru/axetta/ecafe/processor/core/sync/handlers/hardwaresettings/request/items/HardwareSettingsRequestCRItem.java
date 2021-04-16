/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.Date;

public class HardwareSettingsRequestCRItem extends HardwareSettingsRequestItem {

    private Integer usedByModule;
    private String readerName;
    private String firmwareVer;

    public HardwareSettingsRequestCRItem(Integer usedByModule, String readerName, String firmwareVer, Date lastUpdate,
            HardwareSettingsRequest.ModuleType type, String errorMessage) {
        super(lastUpdate, type, errorMessage);
        this.usedByModule = usedByModule;
        this.readerName = readerName;
        this.firmwareVer = firmwareVer;
    }

    public static HardwareSettingsRequestCRItem build(Node itemNode) {
        HardwareSettingsRequest.ModuleType type = HardwareSettingsRequest.ModuleType.CR;
        StringBuilder errorMessage = new StringBuilder();

        Integer usedByModule = XMLUtils.getIntegerAttributeValue(itemNode, "UsedByModule");
        if (null == usedByModule) {
            errorMessage.append("Attribute UsedByModule not found");
        }

        String readerName = XMLUtils.getAttributeValue(itemNode, "ReaderName");
        if (null == readerName) {
            errorMessage.append("Attribute ReaderName not found");
        }

        String firmwareVer = XMLUtils.getAttributeValue(itemNode, "FirmwareVer");
        if (null == firmwareVer) {
            errorMessage.append("Attribute FirmwareVer not found");
        }
        return new HardwareSettingsRequestCRItem(usedByModule, readerName, firmwareVer,
                getLastUpdate(itemNode, errorMessage), type, errorMessage.toString());
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
