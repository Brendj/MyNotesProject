/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.HardwareSettingsRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HardwareSettingsRequestHSItem {
    private Long idOfHardwareSetting;
    private List<HardwareSettingsRequestItem> items = new LinkedList<>();
    private HardwareSettingsRequestIPItem ipItem;

    public HardwareSettingsRequestHSItem(Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
    }

    public static HardwareSettingsRequestHSItem build(Node hsNode) {
        Long idOfHardwareSetting = XMLUtils.getLongAttributeValue(hsNode, "HostId");
        HardwareSettingsRequestHSItem hsItem = new HardwareSettingsRequestHSItem(idOfHardwareSetting);

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
                        List<HardwareSettingsRequestItem> crItemList = new ArrayList<>();
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
}
