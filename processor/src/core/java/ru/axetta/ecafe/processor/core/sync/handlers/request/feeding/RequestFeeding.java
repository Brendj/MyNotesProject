/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestFeeding implements SectionRequest {
    public static final String SECTION_NAME="RequestFeeding";

    private final Long maxVersion;
    private final Long idOfOrgOwner;
    private final List<RequestFeedingItem> items;

    public RequestFeeding(Node requestFeedingRequestNode, Long orgOwner) throws Exception {
        maxVersion = XMLUtils.getLongAttributeValue(requestFeedingRequestNode, "V");
        idOfOrgOwner = orgOwner;
        this.items = new ArrayList<RequestFeedingItem>();

        Node itemNode = requestFeedingRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("RF")) {
                RequestFeedingItem item = RequestFeedingItem.build(itemNode, orgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public RequestFeeding(RequestFeedingItem item, long idOfOrgOwner) {
        Objects.requireNonNull(item);
        this.idOfOrgOwner = idOfOrgOwner;
        this.maxVersion = 0L;
        this.items = new ArrayList<>();
        this.items.add(item);
    }

    public List<RequestFeedingItem> getItems() {
        return items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
