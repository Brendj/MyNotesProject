/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequestBuilder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientBalanceHoldData implements SectionRequest {
    public static final String SECTION_NAME="ClientBalanceHold";
    private final Long orgOwner;
    private final List<ClientBalanceHoldItem> items;

    public ClientBalanceHoldData(Node clientBalanceHoldDataNode, Long orgOwner) {
        this.orgOwner = orgOwner;
        this.items = new ArrayList<ClientBalanceHoldItem>();

        Node itemNode = clientBalanceHoldDataNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CBH")) {
                ClientBalanceHoldItem item = Builder.build(itemNode, orgOwner);
                items.add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public List<ClientBalanceHoldItem> getItems() {
        return items;
    }

    public static class Builder implements SectionRequestBuilder {

        private final long owner;

        public Builder(long owner){
            this.owner = owner;
        }

        public static ClientBalanceHoldItem build(Node itemNode, Long orgOwner) {
            StringBuilder errorMessage = new StringBuilder();

            String guid = XMLUtils.getAttributeValue(itemNode, "Guid");
            if (StringUtils.isEmpty(guid)) {
                errorMessage.append("Attribute Guid not found.");
            }
            String phoneOfDeclarer = XMLUtils.getAttributeValue(itemNode, "PhoneOfDeclarer");
            Long idOfClient = (Long)getValue(itemNode, "ClientId", errorMessage, false, 'L');
            Long idOfDeclarer = (Long)getValue(itemNode, "DeclarerId", errorMessage, true, 'L');
            Long holdSum = (Long)getValue(itemNode, "HoldSum", errorMessage, false, 'L');
            Long idOfOldOrg = (Long)getValue(itemNode, "OrgId", errorMessage, false, 'L');
            Date createdDate = (Date)getValue(itemNode, "CreatedDate", errorMessage, false, 'D');
            Long version = (Long)getValue(itemNode, "Version", errorMessage, false, 'L');
            Integer createStatus = (Integer)getValue(itemNode, "CreateStatus", errorMessage, false, 'I');
            Integer requestStatus = (Integer)getValue(itemNode, "RequestStatus", errorMessage, false, 'I');
            String declarerInn = XMLUtils.getAttributeValue(itemNode, "DeclarerInn");
            String declarerAccount = XMLUtils.getAttributeValue(itemNode, "DeclarerAccount");
            String declarerBank = XMLUtils.getAttributeValue(itemNode, "DeclarerBank");
            String declarerBik = XMLUtils.getAttributeValue(itemNode, "DeclarerBik");
            String declarerCorrAccount = XMLUtils.getAttributeValue(itemNode, "DeclarerCorrAccount");
            return new ClientBalanceHoldItem(idOfClient, idOfDeclarer, phoneOfDeclarer, guid, holdSum,
                    idOfOldOrg, null, createdDate, version, createStatus, requestStatus,
                    declarerInn, declarerAccount, declarerBank, declarerBik, declarerCorrAccount, errorMessage.toString());
        }

        private static Object getValue(Node itemNode, String attr, StringBuilder errorMessage, boolean canBeEmtpy, char type) {
            try {
                String str = XMLUtils.getAttributeValue(itemNode, attr);
                if (!StringUtils.isEmpty(str)) {
                    switch (type) {
                        case 'L': return Long.parseLong(str);
                        case 'I': return Integer.parseInt(str);
                        case 'D': return CalendarUtils.parseDateWithDayTime(str);
                    }
                } else {
                    if (!canBeEmtpy) errorMessage.append(String.format("Attribute %s is empty.", attr));
                }
            } catch (java.lang.Exception e) {
                errorMessage.append(String.format("Error in attribute %s.", attr));
            }
            return null;
        }

        @Override
        public SectionRequest searchSectionNodeAndBuild(Node envelopeNode) throws Exception {
            Node sectionElement = XMLUtils.findFirstChildElement(envelopeNode, ClientBalanceHoldData.SECTION_NAME);
            if (sectionElement != null) {
                return new ClientBalanceHoldData(sectionElement, owner);
            } else
                return null;
        }
    }
}
