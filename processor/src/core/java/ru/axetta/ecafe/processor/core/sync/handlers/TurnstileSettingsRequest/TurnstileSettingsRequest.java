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
    private final Long maxVersion;
    private final List<List<TurnstileSettingsRequestItem>> sectionItem;

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
        List<TurnstileSettingsRequestItem> items;
        this.orgOwner = orgOwner;
        maxVersion = XMLUtils.getLongAttributeValue(turnstileSettingNode, "V");
        this.sectionItem = new ArrayList<List<TurnstileSettingsRequestItem>>();

        Node tsNode = turnstileSettingNode.getFirstChild();

        while (null != tsNode) {
            items = new ArrayList<TurnstileSettingsRequestItem>();
            TurnstileSettingsRequestTSItem tsItem = TurnstileSettingsRequestTSItem.build(tsNode);
            items.add(tsItem);
            Node turnstilesNode = tsNode.getFirstChild();
            Node trNode = turnstilesNode.getFirstChild();
            while (null != trNode) {
                TurnstileSettingsRequestTRItem trItem = TurnstileSettingsRequestTRItem.build(trNode);
                items.add(trItem);
                trNode = trNode.getNextSibling();
            }
            sectionItem.add(items);
            tsNode = tsNode.getNextSibling();
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

    public Long getMaxVersion() {
        return maxVersion;
    }

    public List<List<TurnstileSettingsRequestItem>> getSectionItem() {
        return sectionItem;
    }
}
