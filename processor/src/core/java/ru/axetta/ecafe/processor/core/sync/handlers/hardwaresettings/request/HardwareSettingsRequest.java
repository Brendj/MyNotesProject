/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;


import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items.*;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardwareSettingsRequest implements SectionRequest {

    public static final String SECTION_NAME = "HardwareSettings";

    private final Long orgOwner;
    private final List<List<HardwareSettingsRequestItem>> sectionItem;

    public enum ModuleType {
        HS("HS"), MT("MT"), IP("IP"), DOTNETVER("DotNetVer"), OSVER("OsVer"), RAM("RAM"), CPU("CPU"), READERS(
                "Readers");

        private final String section;
        static Map<Integer, ModuleType> map = new HashMap<>();

        ModuleType(String section) {
            this.section = section;
        }

        public String getSection() {
            return section;
        }

        static {
            for (ModuleType moduleType : ModuleType.values()) {
                map.put(moduleType.ordinal(), moduleType);
            }
        }

        public static ModuleType fromString(String description) {
            for (ModuleType e : map.values()) {
                if (e.section.equals(description)) {
                    return e;
                }
            }
            return null;
        }
    }

    public HardwareSettingsRequest(Node hardwareSettingNode, Long orgOwner) {

        this.orgOwner = orgOwner;
        this.sectionItem = new ArrayList<List<HardwareSettingsRequestItem>>();
        List<HardwareSettingsRequestItem> items;
        Node hsNode = hardwareSettingNode.getFirstChild();

        while (null != hsNode) {
            if (Node.ELEMENT_NODE == hsNode.getNodeType() && hsNode.getNodeName().equals("HS")) {
                HardwareSettingsRequestHSItem hsItem = null;
                items = new ArrayList<HardwareSettingsRequestItem>();
                hsItem = HardwareSettingsRequestHSItem.build(hsNode);
                items.add(hsItem);
                Node itemNode = hsNode.getFirstChild();
                while (null != itemNode) {
                    if (Node.ELEMENT_NODE == itemNode.getNodeType()) {
                        ModuleType moduleType = ModuleType.fromString(itemNode.getNodeName());
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
                                items.addAll(crItemList);
                                break;
                        }
                        if (item != null) {
                            items.add(item);
                        }
                    }
                    itemNode = itemNode.getNextSibling();
                }
                sectionItem.add(items);
            }
            hsNode = hsNode.getNextSibling();
        }
    }

    public static String getSectionName() {
        return SECTION_NAME;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public List<List<HardwareSettingsRequestItem>> getSectionItem() {
        return sectionItem;
    }

}
