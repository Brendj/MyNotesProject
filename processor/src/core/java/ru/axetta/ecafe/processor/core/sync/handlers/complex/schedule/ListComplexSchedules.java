/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 25.03.16
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class ListComplexSchedules implements SectionRequest{
    public static final String SECTION_NAME="ComplexSchedule";
    private final List<ComplexScheduleItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;

    public ListComplexSchedules(Node complexScheduleRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(complexScheduleRequestNode, "V");
        idOfOrgOwner = orgOwner;
        this.items = new ArrayList<ComplexScheduleItem>();

        Node itemNode = complexScheduleRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CS")) {
                ComplexScheduleItem item = ComplexScheduleItem.build(itemNode, orgOwner);
                getItems().add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public static class Builder implements SectionRequestBuilder {
        private final long idOfOrg;

        public Builder(long idOfOrg){
            this.idOfOrg = idOfOrg;
        }

        public ListComplexSchedules build(Node envelopeNode) throws Exception {
            SectionRequest sectionRequest = searchSectionNodeAndBuild(envelopeNode);
            return sectionRequest!=null? (ListComplexSchedules) sectionRequest :null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ListComplexSchedules.SECTION_NAME);
            if (sectionElement != null) {
                return new ListComplexSchedules(sectionElement, idOfOrg);
            } else
                return null;
        }
    }

    public List<ComplexScheduleItem> getItems() {
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
