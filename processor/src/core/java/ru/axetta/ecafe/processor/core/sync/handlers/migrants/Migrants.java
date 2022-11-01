/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfMigrant;
import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class Migrants implements SectionRequest{
    public static final String SECTION_NAME="Migrants";
    private final Long idOfOrg;
    private final List<Long> currentActiveOutcome;
    private final List<CompositeIdOfMigrant> currentActiveIncome;
    private final List<OutcomeMigrationRequestsItem> outcomeMigrationRequestsItems;
    private final List<OutcomeMigrationRequestsHistoryItem> outcomeMigrationRequestsHistoryItems;
    private final List<IncomeMigrationRequestsHistoryItem> incomeMigrationRequestsHistoryItems;

    private static Logger logger = LoggerFactory.getLogger(Migrants.class);

    public Migrants(Node migrantsRequestNode, Long idOfOrgRegistry) {
        this.idOfOrg = idOfOrgRegistry;

        Node outcomeMigrationRequestsNode = findFirstChildElement(migrantsRequestNode, "OutcomeMigrationRequests");
        this.outcomeMigrationRequestsItems = new ArrayList<OutcomeMigrationRequestsItem>();

        currentActiveOutcome = new ArrayList<Long>();
        String outcomeCurrentActive = XMLUtils.getStringAttributeValue(outcomeMigrationRequestsNode, "CurrentActive", 100000);
        if(outcomeCurrentActive != null) {
            if(outcomeCurrentActive.length() > 0) {
                String[] outcomeIds = outcomeCurrentActive.split(",");
                for (String id : outcomeIds) {
                    try {
                        currentActiveOutcome.add(Long.parseLong(id));
                    } catch (NumberFormatException e) {
                        logger.error("Unable to parse data from \"CurrentActive\" attribute in \"OutcomeMigrationRequests\" tag", e);
                    }
                }
            }
        }

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

        Node incomeMigrationRequestsNode = findFirstChildElement(migrantsRequestNode, "IncomeMigrationRequests");

        currentActiveIncome = new ArrayList<CompositeIdOfMigrant>();
        if(incomeMigrationRequestsNode != null) {
            String incomeCurrentActive = XMLUtils
                    .getStringAttributeValue(incomeMigrationRequestsNode, "CurrentActive", 1000000);
            if (incomeCurrentActive != null) {
                if (incomeCurrentActive.length() > 0) {
                    String[] incomeIdsForOrg = incomeCurrentActive.split(";");
                    for (String idForOrg : incomeIdsForOrg) {
                        String[] orgAndId = idForOrg.split(":");
                        if (orgAndId.length != 2) continue;
                        Long idOfOrg = Long.parseLong(orgAndId[0]);
                        for (String id : orgAndId[1].split(",")) {
                            try {
                                currentActiveIncome.add(new CompositeIdOfMigrant(Long.parseLong(id), idOfOrg));
                            } catch (NumberFormatException e) {
                                logger.error("Unable to parse data from \"CurrentActive\" attribute in \"IncomeMigrationRequests\" tag", e);
                            }
                        }

                    }
                }
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

    public List<Long> getCurrentActiveOutcome() {
        return currentActiveOutcome;
    }

    public List<CompositeIdOfMigrant> getCurrentActiveIncome() {
        return currentActiveIncome;
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

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
