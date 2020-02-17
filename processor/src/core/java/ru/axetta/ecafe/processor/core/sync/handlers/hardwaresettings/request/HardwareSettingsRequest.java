/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;


import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items.*;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardwareSettingsRequest implements SectionRequest {

    public static final String SECTION_NAME = "HardwareSettings";

    private final Long orgOwner;
    private final List<HardwareSettingsRequestItem> items;
    private final Long maxVersion;

    public enum ModuleType {
        MT("MT"), IP("IP"), DOTNETVER("DotNetVer"), OSVER("OsVer"), RAM("RAM"), CPU("CPU"), CR("CR");

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

        this.items = new ArrayList<HardwareSettingsRequestItem>();
        this.orgOwner = orgOwner;
        maxVersion = XMLUtils.getLongAttributeValue(hardwareSettingNode, "V");

        Node itemNode = hardwareSettingNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType()) {
                ModuleType moduleType = ModuleType.fromString(itemNode.getNodeName());
                switch (moduleType) {
                    case MT:
                        HardwareSettingsRequestMTItem itemMT = HardwareSettingsRequestMTItem.build(itemNode);
                        items.add(itemMT);
                        break;
                    case IP:
                        HardwareSettingsRequestIPItem itemIP = HardwareSettingsRequestIPItem.build(itemNode);
                        items.add(itemIP);
                        break;
                    case DOTNETVER:
                        HardwareSettingsRequestDotNetVerItem itemDotNetVer = HardwareSettingsRequestDotNetVerItem.build(itemNode);
                        items.add(itemDotNetVer);
                        break;
                    case OSVER:
                        HardwareSettingsRequestOsVerItem itemOsVer = HardwareSettingsRequestOsVerItem.build(itemNode);
                        items.add(itemOsVer);
                        break;
                    case RAM:
                        HardwareSettingsRequestRAMItem itemRAM = HardwareSettingsRequestRAMItem.build(itemNode);
                        items.add(itemRAM);
                        break;
                    case CPU:
                        HardwareSettingsRequestCPUItem itemCPU = HardwareSettingsRequestCPUItem.build(itemNode);
                        items.add(itemCPU);
                        break;
                    case CR:
                        HardwareSettingsRequestCRItem itemCR = HardwareSettingsRequestCRItem.build(itemNode);
                        items.add(itemCR);
                        break;
                }
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public List<HardwareSettingsRequestItem> getItems() {
        return items;
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

    public Long getMaxVersion() {
        return maxVersion;
    }
}
