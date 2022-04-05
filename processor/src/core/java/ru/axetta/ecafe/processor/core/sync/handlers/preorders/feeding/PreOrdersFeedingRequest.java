/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class PreOrdersFeedingRequest implements SectionRequest {
    public static final String SECTION_NAME="PreOrdersFeedingRequest";
    private final Long maxVersion;
    private final Long orgOwner;
    private List<PreOrdersFeedingToPayItem> items = new LinkedList<>();

    public PreOrdersFeedingRequest(Node PreOrdersFeedingRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(PreOrdersFeedingRequestNode, "V");
        this.orgOwner = orgOwner;
        Node nodeElement = PreOrdersFeedingRequestNode.getFirstChild();
        while (nodeElement != null) {
            if (nodeElement.getNodeName().equals("POF")) {
                PreOrdersFeedingToPayItem item = new PreOrdersFeedingToPayItem(XMLUtils.getAttributeValue(nodeElement, "Guid"),
                        XMLUtils.getIntegerAttributeValue(nodeElement, "ToPay"));
                Node podElement = nodeElement.getFirstChild();
                while (podElement != null) {
                    if (podElement.getNodeName().equals("POD")) {
                        PreOrdersFeedingToPayDetailItem detailItem = new PreOrdersFeedingToPayDetailItem(XMLUtils.getLongAttributeValue(podElement, "IdOfDish"),
                                XMLUtils.getIntegerAttributeValue(podElement, "ToPay"), XMLUtils.getIntegerAttributeValue(podElement, "Qty"));
                        item.getDetails().add(detailItem);
                    }
                    podElement = podElement.getNextSibling();
                }
                items.add(item);
            }
            nodeElement = nodeElement.getNextSibling();
        }
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public List<PreOrdersFeedingToPayItem> getItems() {
        return items;
    }

    public void setItems(List<PreOrdersFeedingToPayItem> items) {
        this.items = items;
    }
}
