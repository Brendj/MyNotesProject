/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.special.dates;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 10:27
 */
public class SpecialDates {
    private final List<SpecialDatesItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public SpecialDates(Node specialDateRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(specialDateRequestNode, "V");
        this.items = new ArrayList<SpecialDatesItem>();
        this.idOfOrgOwner = orgOwner;

        Node itemNode = specialDateRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("SD")) {
                SpecialDatesItem item = SpecialDatesItem.build(itemNode, orgOwner);
                getItems().add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }

    public List<SpecialDatesItem> getItems() {
        return items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }
}
