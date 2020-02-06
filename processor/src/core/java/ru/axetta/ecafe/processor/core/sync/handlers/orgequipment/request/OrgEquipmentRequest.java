/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.orgequipment.request;


import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgEquipmentRequest implements SectionRequest {

    public static final String SECTION_NAME = "HardwareSettings";

    private final Long orgOwner;
    private final List<OrgEquipmentRequestItem> items;
    private final Long maxVersion;


    public enum ModuleType {
        MT("MT"), IP("IP"), DOTNETVER("DotNetVer"), OSVER("OsVer"), RAM("RAM"), CPU("CPU"), READERS("Readers");

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

    public OrgEquipmentRequest(Node orgEquipmentNode, Long orgOwner) {

        this.items = new ArrayList<OrgEquipmentRequestItem>();
        this.orgOwner = orgOwner;
        maxVersion = XMLUtils.getLongAttributeValue(orgEquipmentNode, "V");

        Node itemNode = orgEquipmentNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType()) {
                ModuleType moduleType = ModuleType.fromString(itemNode.getNodeName());
                switch (moduleType) {
                    case MT:
                        OrgEquipmentRequestItem itemMT = OrgEquipmentRequestMTItem.build(itemNode);
                        items.add(itemMT);
                        itemNode = itemNode.getNextSibling();
                        break;
                    //case IP:
                    //    OrgEquipmentRequestItem item = OrgEquipmentRequestItem.build(itemNode, orgOwner);
                    //    items.add(item);
                    //    itemNode = itemNode.getNextSibling();
                    //    break;
                }
            }


        }
    }

    public List<OrgEquipmentRequestItem> getItems() {
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
