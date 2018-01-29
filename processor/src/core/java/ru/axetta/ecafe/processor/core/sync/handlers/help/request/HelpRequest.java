/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.help.request;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class HelpRequest implements SectionRequest {
    public static final String SECTION_NAME="HelpRequests";

    private final List<HelpRequestItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public HelpRequest(Node helpRequestRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(helpRequestRequestNode, "V");
        idOfOrgOwner = orgOwner;
        this.items = new ArrayList<HelpRequestItem>();

        Node itemNode = helpRequestRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("HR")) {
                HelpRequestItem item = HelpRequestItem.build(itemNode, orgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public List<HelpRequestItem> getItems() {
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
