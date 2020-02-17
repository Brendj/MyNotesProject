/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuc on 14.02.2020.
 */
public class PreorderFeedingStatusRequest implements SectionRequest {
    public static final String SECTION_NAME="PreOrdersFeedingStatus";
    private final Long maxVersion;
    private final Long orgOwner;
    private final List<PreorderFeedingStatusItem> items;

    public PreorderFeedingStatusRequest(Node preordersFeedingStatusRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(preordersFeedingStatusRequestNode, "V");
        this.orgOwner = orgOwner;
        this.items = new ArrayList<PreorderFeedingStatusItem>();
        Node itemNode = preordersFeedingStatusRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("PSI")) {
                PreorderFeedingStatusItem item = PreorderFeedingStatusItem.build(itemNode, orgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public List<PreorderFeedingStatusItem> getItems() {
        return items;
    }
}
