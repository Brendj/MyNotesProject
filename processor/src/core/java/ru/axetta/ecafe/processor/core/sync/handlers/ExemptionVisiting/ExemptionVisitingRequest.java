/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class ExemptionVisitingRequest implements SectionRequest {

    public static final String SECTION_NAME = "ExemptionVisiting";
    private Long maxVersion;
    private List<ExemptionVisitingSyncFromARMPOJO> items = new LinkedList<>();

    public ExemptionVisitingRequest(Node sectionElement) throws Exception {
        this.maxVersion = XMLUtils.getLongAttributeValue(sectionElement, "V");
        Node nodeElement = sectionElement.getFirstChild();
        while (nodeElement != null) {
            if (nodeElement.getNodeName().equals("Record")) {
                ExemptionVisitingSyncFromARMPOJO exemptionVisitingSyncFromARMPOJO = new ExemptionVisitingSyncFromARMPOJO();
                exemptionVisitingSyncFromARMPOJO.setIdExemption(XMLUtils.getLongAttributeValue(nodeElement, "idExemption"));
                exemptionVisitingSyncFromARMPOJO
                        .setAccepted(Boolean.valueOf(XMLUtils.getAttributeValue(nodeElement, "accepted")));
                String dateAccept = XMLUtils.getAttributeValue(nodeElement, "acceptedDateTime");
                if (StringUtils.isEmpty(dateAccept) || dateAccept.equals("null"))
                {
                    exemptionVisitingSyncFromARMPOJO.setAcceptedDateTime(null);
                }
                else
                    exemptionVisitingSyncFromARMPOJO.setAcceptedDateTime(XMLUtils.getDateTimeAttributeValue(nodeElement, "acceptedDateTime"));
                items.add(exemptionVisitingSyncFromARMPOJO);
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

    public List<ExemptionVisitingSyncFromARMPOJO> getItems() {
        if (items == null) {
            items = new LinkedList<>();
        }
        return items;
    }

    public void setItems(List<ExemptionVisitingSyncFromARMPOJO> items) {
        this.items = items;
    }
}