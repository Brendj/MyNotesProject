/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by nuc on 14.02.2020.
 */
public class PreorderFeedingStatusRequest implements SectionRequest {
    public static final String SECTION_NAME="PreOrdersFeedingStatus";
    private final Long maxVersion;
    private final Long orgOwner;
    private final Set<PreorderFeedingStatusItem> items;

    public PreorderFeedingStatusRequest(Node preordersFeedingStatusRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(preordersFeedingStatusRequestNode, "V");
        this.orgOwner = orgOwner;
        this.items = new HashSet<PreorderFeedingStatusItem>();
        Node itemNode = preordersFeedingStatusRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("PSI")) {
                PreorderFeedingStatusItem item = PreorderFeedingStatusItem.build(itemNode, orgOwner);
                if (items.contains(item)) {
                    testForReplace(items, item);
                } else {
                    items.add(item);
                }
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    private void testForReplace(Set<PreorderFeedingStatusItem> items, PreorderFeedingStatusItem item) {
        Iterator<PreorderFeedingStatusItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            PreorderFeedingStatusItem it = iterator.next();
            if (it.equals(item)) {
                if (item.getStatus() > it.getStatus()) {
                    items.remove(it);
                    items.add(item);
                }
            }
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

    public Set<PreorderFeedingStatusItem> getItems() {
        return items;
    }
}
