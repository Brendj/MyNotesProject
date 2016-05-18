/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.findFirstChildElement;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 10:25
 */

public class Migrants {
    private final Long idOfOrg;
    private final List<OutcomeMigrationRequestsItem> outcomeMigrationRequestsItems;
    private final List<OutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems;
    private final List<IncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems;

    public Migrants(Node migrantsRequestNode, Long idOfOrgRegistry) {
        this.idOfOrg = idOfOrgRegistry;

        Node outcomeMigrationRequestsNode = findFirstChildElement(migrantsRequestNode, "OutcomeMigrationRequests");
        this.outcomeMigrationRequestsItems = new ArrayList<OutcomeMigrationRequestsItem>();
        if(outcomeMigrationRequestsNode != null) {
            Node outMigReqItemNode = outcomeMigrationRequestsNode.getFirstChild();
            while (null != outMigReqItemNode) {
                if (Node.ELEMENT_NODE == outMigReqItemNode.getNodeType() && outMigReqItemNode.getNodeName()
                        .equals("OMR")) {
                    OutcomeMigrationRequestsItem item = OutcomeMigrationRequestsItem
                            .build(outMigReqItemNode, idOfOrgRegistry);
                    getOutcomeMigrationRequestsItems().add(item);
                }
                outMigReqItemNode = outMigReqItemNode.getNextSibling();
            }
        }

        Node outcomeMigrationRequestsHistoryNode = findFirstChildElement(migrantsRequestNode, "OutcomeMigrationRequestsHistory");
        this.outcomeMigrationRequestsHistoryItems = new ArrayList<OutcomeMigrationRequestsHistoryItem>();
        if(outcomeMigrationRequestsHistoryNode != null) {
            Node outMigReqHisItemNode = outcomeMigrationRequestsHistoryNode.getFirstChild();
            while (null != outMigReqHisItemNode) {
                if (Node.ELEMENT_NODE == outMigReqHisItemNode.getNodeType() && outMigReqHisItemNode.getNodeName()
                        .equals("OMRH")) {
                    OutcomeMigrationRequestsHistoryItem item = OutcomeMigrationRequestsHistoryItem
                            .build(outMigReqHisItemNode, idOfOrgRegistry);
                    getOutcomeMigrationRequestsHistoryItems().add(item);
                }
                outMigReqHisItemNode = outMigReqHisItemNode.getNextSibling();
            }
        }

        Node incomeMigrationRequestsHistoryNode = findFirstChildElement(migrantsRequestNode, "IncomeMigrationRequestsHistory");
        this.incomeMigrationRequestsHistoryItems = new ArrayList<IncomeMigrationRequestsHistoryItem>();
        if(incomeMigrationRequestsHistoryNode != null){
            Node inMigReqHisItemNode = incomeMigrationRequestsHistoryNode.getFirstChild();
            while (null != inMigReqHisItemNode) {
                if (Node.ELEMENT_NODE == inMigReqHisItemNode.getNodeType() && inMigReqHisItemNode.getNodeName().equals("IMRH")) {
                    IncomeMigrationRequestsHistoryItem item = IncomeMigrationRequestsHistoryItem.build(inMigReqHisItemNode, idOfOrgRegistry);
                    getIncomeMigrationRequestsHistoryItems().add(item);
                }
                inMigReqHisItemNode = inMigReqHisItemNode.getNextSibling();
            }
        }

    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public List<OutcomeMigrationRequestsItem> getOutcomeMigrationRequestsItems() {
        return outcomeMigrationRequestsItems;
    }

    public List<OutcomeMigrationRequestsHistoryItem> getOutcomeMigrationRequestsHistoryItems() {
        return outcomeMigrationRequestsHistoryItems;
    }

    public List<IncomeMigrationRequestsHistoryItem> getIncomeMigrationRequestsHistoryItems() {
        return incomeMigrationRequestsHistoryItems;
    }
}
