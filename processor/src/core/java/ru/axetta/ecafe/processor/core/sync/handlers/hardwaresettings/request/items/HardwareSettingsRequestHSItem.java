/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class HardwareSettingsRequestHSItem {
    private Long idOfHardwareSetting;
    private List<HardwareSettingsRequestItem> items = new LinkedList<>();
    private HardwareSettingsRequestIPItem ipItem;
    private String errorMessage;
    private Integer resCode;

    public HardwareSettingsRequestHSItem(Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
    }

    public static HardwareSettingsRequestHSItem build(Node hsNode) {
        Long idOfHardwareSetting = null;
        String errorMessage = null;
        Integer resCode = HardwareSettingsRequestItem.ERROR_CODE_ALL_OK;
        try {
            idOfHardwareSetting = XMLUtils.getLongAttributeValue(hsNode, "HostId");
            if(idOfHardwareSetting == null){
                throw new Exception("HostId is Null");
            }
        } catch (Exception e){
            errorMessage = e.getMessage();
            resCode = HardwareSettingsRequestItem.ERROR_CODE_NOT_VALID_ATTRIBUTE;
        }
        HardwareSettingsRequestHSItem hsItem = new HardwareSettingsRequestHSItem(idOfHardwareSetting);
        hsItem.setErrorMessage(errorMessage);
        hsItem.setResCode(resCode);

        Node itemNode = hsNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType()) {
                HardwareSettingsRequest.ModuleType moduleType = HardwareSettingsRequest.ModuleType.fromString(itemNode.getNodeName());
                HardwareSettingsRequestItem item = null;
                if (moduleType == null) {
                    itemNode = itemNode.getNextSibling();
                    continue;
                }
                switch (moduleType) {
                    case MT:
                        item = HardwareSettingsRequestMTItem.build(itemNode);
                        break;
                    case IP:
                        hsItem.ipItem = HardwareSettingsRequestIPItem.build(itemNode);
                        break;
                    case DOTNETVER:
                        item = HardwareSettingsRequestDotNetVerItem.build(itemNode);
                        break;
                    case OSVER:
                        item = HardwareSettingsRequestOsVerItem.build(itemNode);
                        break;
                    case RAM:
                        item = HardwareSettingsRequestRAMItem.build(itemNode);
                        break;
                    case CPU:
                        item = HardwareSettingsRequestCPUItem.build(itemNode);
                        break;
                    case READERS:
                        Node readersNode = itemNode.getFirstChild();
                        HardwareSettingsRequestItem crItem;
                        List<HardwareSettingsRequestItem> crItemList = new LinkedList<>();
                        while (null != readersNode) {
                            if (Node.ELEMENT_NODE == readersNode.getNodeType()) {
                                crItem = HardwareSettingsRequestCRItem.build(readersNode);
                                crItemList.add(crItem);
                            }
                            readersNode = readersNode.getNextSibling();
                        }
                        hsItem.items.addAll(crItemList);
                        break;
                }
                if (item != null) {
                    hsItem.items.add(item);
                }
            }
            itemNode = itemNode.getNextSibling();
        }
        return hsItem;
    }

    public Long getIdOfHardwareSetting() {
        return idOfHardwareSetting;
    }

    public void setIdOfHardwareSetting(Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
    }

    public List<HardwareSettingsRequestItem> getItems() {
        return items;
    }

    public void setItems(List<HardwareSettingsRequestItem> items) {
        this.items = items;
    }

    public HardwareSettingsRequestIPItem getIpItem() {
        return ipItem;
    }

    public void setIpItem(HardwareSettingsRequestIPItem ipItem) {
        this.ipItem = ipItem;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }
}
