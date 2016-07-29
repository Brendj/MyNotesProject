/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * User: akmukov
 * Date: 27.07.2016
 */
public class GroupsOrganizationRequest implements SectionRequest {
    private static final String SECTION_NAME = "GroupsOrganization";
    private static Logger logger = LoggerFactory.getLogger(GroupsOrganizationRequest.class);
    private final long idOfOrg;
    private long maxVersion;
    private List<GroupOrganizationItem> items = new ArrayList();

    public GroupsOrganizationRequest(Node groupsOrganizationRequestNode, long idOfOrg) {
        maxVersion = XMLUtils.getLongAttributeValue(groupsOrganizationRequestNode, "V");
        this.idOfOrg = idOfOrg;
        Node node = groupsOrganizationRequestNode.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("CG")) {
                GroupOrganizationItem item = null;
                try {
                    item = GroupOrganizationItem.build(node, idOfOrg);
                    items.add(item);
                } catch (Exception e) {
                    logger.error("failed to create group organization item," + e);
                }
            }
            node = node.getNextSibling();
        }
    }

    public long getIdOfOrg() {
        return idOfOrg;
    }

    public long getMaxVersion() {
        return maxVersion;
    }

    public List<GroupOrganizationItem> getItems() {
        return items;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public static class Builder implements SectionRequestBuilder {

        private final long idOfOrg;

        public Builder(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public GroupsOrganizationRequest build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest != null ? (GroupsOrganizationRequest) sectionRequest : null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, SECTION_NAME);
            if (sectionElement != null) {
                return new GroupsOrganizationRequest(sectionElement, idOfOrg);
            } else
                return null;
        }
    }

}
