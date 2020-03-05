/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 16.12.19
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class ReestrTaloonPreorder implements SectionRequest{
    public static final String SECTION_NAME="ReestrTaloonPreorders";

    private final List<TaloonPreorderItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public ReestrTaloonPreorder(Node taloonPreorderRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(taloonPreorderRequestNode, "V");
        idOfOrgOwner = orgOwner;
        this.items = new ArrayList<>();

        Node itemNode = taloonPreorderRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("RTP")) {
                TaloonPreorderItem item = TaloonPreorderItem.build(itemNode, orgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public List<TaloonPreorderItem> getItems() {
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
