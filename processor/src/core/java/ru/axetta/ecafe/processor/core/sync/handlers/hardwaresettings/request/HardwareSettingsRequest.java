/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;


import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items.HardwareSettingsRequestHSItem;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HardwareSettingsRequest implements SectionRequest {

    public static final String SECTION_NAME = "HardwareSettings";

    private final Long orgOwner;
    private final List<HardwareSettingsRequestHSItem> sectionItem;

    public enum ModuleType {
        HS("HS"),
        MT("MT"),
        IP("IP"),
        DOTNETVER("DotNetVer"),
        OSVER("OsVer"),
        RAM("RAM"),
        CPU("CPU"),
        READERS("Readers");

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
        this.sectionItem = new LinkedList<>();
        Node hsNode = hardwareSettingNode.getFirstChild();

        while (null != hsNode) {
            HardwareSettingsRequestHSItem hsItem = HardwareSettingsRequestHSItem.build(hsNode);
            sectionItem.add(hsItem);
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

    public List<HardwareSettingsRequestHSItem> getSectionItem() {
        return sectionItem;
    }

}
