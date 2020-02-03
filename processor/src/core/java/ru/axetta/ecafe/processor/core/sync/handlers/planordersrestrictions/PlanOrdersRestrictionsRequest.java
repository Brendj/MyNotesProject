/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.planordersrestrictions;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 28.01.2020.
 */
public class PlanOrdersRestrictionsRequest implements SectionRequest {
    public static final String SECTION_NAME="PlanOrdersRestrictions";
    private final Long maxVersion;
    private final Long orgOwner;
    private final List<PlanOrdersRestrictionItem> items;

    public PlanOrdersRestrictionsRequest(Node planOrdersRestrictionsRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(planOrdersRestrictionsRequestNode, "V");
        this.orgOwner = orgOwner;

        this.items = new ArrayList<PlanOrdersRestrictionItem>();

        Node itemNode = planOrdersRestrictionsRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("RTA")) {
                PlanOrdersRestrictionItem item = PlanOrdersRestrictionItem.build(itemNode, orgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
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
}
