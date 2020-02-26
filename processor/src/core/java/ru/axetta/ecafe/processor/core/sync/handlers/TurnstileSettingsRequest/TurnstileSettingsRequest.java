/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest;

import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.TurnstileSettingsRequestItem;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.TurnstileSettingsRequestTRItem;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.TurnstileSettingsRequestTSItem;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnstileSettingsRequest implements SectionRequest {

    public static final String SECTION_NAME = "TurnstileSettings";

    private final Long orgOwner;
    private final List<TurnstileSettingsRequestItem> items;
    private final Long maxVersion;

    public enum ModuleType {
        TS("TS"), TR("TR");

        private final String section;
        static Map<Integer, TurnstileSettingsRequest.ModuleType> map = new HashMap<>();

        ModuleType(String section) {
            this.section = section;
        }

        public String getSection() {
            return section;
        }

        static {
            for (TurnstileSettingsRequest.ModuleType moduleType : TurnstileSettingsRequest.ModuleType.values()) {
                map.put(moduleType.ordinal(), moduleType);
            }
        }

        public static TurnstileSettingsRequest.ModuleType fromString(String description) {
            for (TurnstileSettingsRequest.ModuleType e : map.values()) {
                if (e.section.equals(description)) {
                    return e;
                }
            }
            return null;
        }
    }

    public TurnstileSettingsRequest(Node turnstileSettingNode, Long orgOwner) {
        this.items = new ArrayList<TurnstileSettingsRequestItem>();
        this.orgOwner = orgOwner;
        maxVersion = XMLUtils.getLongAttributeValue(turnstileSettingNode, "V");

        Node itemNode = turnstileSettingNode.getFirstChild();

        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType()) {
                ModuleType moduleType = ModuleType.fromString(itemNode.getNodeName());
                if (moduleType == null) {
                    itemNode.getNextSibling();
                    continue;
                }
                TurnstileSettingsRequestItem item = null;
                switch (moduleType) {
                    case TS:
                        item = TurnstileSettingsRequestTSItem.build(itemNode);
                        break;
                    case TR:
                        item = TurnstileSettingsRequestTRItem.build(itemNode);
                        break;
                }
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
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

    public List<TurnstileSettingsRequestItem> getItems() {
        return items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }
}
