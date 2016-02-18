/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.02.16
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class ReestrTaloonApproval {

    private final List<TaloonApprovalItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public ReestrTaloonApproval(Node taloonApprovalRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(taloonApprovalRequestNode, "V");
        idOfOrgOwner = orgOwner;
        this.items = new ArrayList<TaloonApprovalItem>();

        Node itemNode = taloonApprovalRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("RTA")) {
                TaloonApprovalItem item = TaloonApprovalItem.build(itemNode, orgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public List<TaloonApprovalItem> getItems() {
        return items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }
}
