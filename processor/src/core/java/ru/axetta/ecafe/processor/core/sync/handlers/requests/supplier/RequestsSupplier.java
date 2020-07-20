/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class RequestsSupplier implements SectionRequest {
    public static final String SECTION_NAME = "RequestsSupplier";

    private final List<RequestsSupplierItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public RequestsSupplier(Node requestSupplierNode, Long idOfOrgOwner) {
        this.maxVersion = XMLUtils.getLongAttributeValue(requestSupplierNode, "V");
        this.idOfOrgOwner = idOfOrgOwner;
        this.items = new ArrayList<>();

        Node itemNode = requestSupplierNode.getFirstChild();
        while (itemNode != null) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("RI")) {
                RequestsSupplierItem item = new RequestsSupplierItem();
                item.build(itemNode, idOfOrgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public List<RequestsSupplierItem> getItems() {
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

