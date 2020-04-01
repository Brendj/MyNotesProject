/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class EmiasRequest implements SectionRequest {

    public static final String SECTION_NAME = "EMIAS";
    private Long maxVersion;
    private List<EMIASSyncFromARMPOJO> items = new LinkedList<>();

    public EmiasRequest(Node sectionElement) throws Exception {
        this.maxVersion = XMLUtils.getLongAttributeValue(sectionElement, "V");
        Node nodeElement = sectionElement.getFirstChild();
        while (nodeElement != null) {
            if (nodeElement.getNodeName().equals("Record")) {
                EMIASSyncFromARMPOJO emiasSyncFromARMPOJO = new EMIASSyncFromARMPOJO();
                emiasSyncFromARMPOJO.setIdEventEMIAS(XMLUtils.getLongAttributeValue(nodeElement, "idEventEMIAS"));
                emiasSyncFromARMPOJO.setAccepted(Boolean.valueOf(XMLUtils.getAttributeValue(nodeElement, "accepted")));
                items.add(emiasSyncFromARMPOJO);
            }
            nodeElement = nodeElement.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Long maxVersion) {
        this.maxVersion = maxVersion;
    }

    public List<EMIASSyncFromARMPOJO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<EMIASSyncFromARMPOJO> items) {
        this.items = items;
    }
}
