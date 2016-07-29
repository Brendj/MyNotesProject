/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: akmukov
 * Date: 29.07.2016
 */
public class GroupsOrganizationDataProcessor extends AbstractProcessor<ProcessGroupsOrganizationData> {
    private static final Logger logger = LoggerFactory.getLogger(GroupsOrganizationDataProcessor.class);
    private final GroupsOrganizationRequest sectionRequest;

    public GroupsOrganizationDataProcessor(Session persistenceSession, GroupsOrganizationRequest sectionRequest) {
        super(persistenceSession);
        this.sectionRequest = sectionRequest;
    }

    @Override
    public ProcessGroupsOrganizationData process() throws Exception {
        ProcessGroupsOrganizationData result = new ProcessGroupsOrganizationData();
        ArrayList<ProcessGroupsOrganizationDataItem> resultItems = new ArrayList<ProcessGroupsOrganizationDataItem>();
        try {
            Org mainBuilding = foundMainBuilding();
            if (mainBuilding == null) return result;

            long maxVersion = sectionRequest.getMaxVersion();
            List<GroupNamesToOrgs> groupNamesToOrgsForOrgSinceVersion = DAOUtils.getGroupNamesToOrgsForOrgSinceVersion(session, mainBuilding.getIdOfOrg(), maxVersion);
            for (GroupNamesToOrgs groupNamesToOrgs : groupNamesToOrgsForOrgSinceVersion) {
                ProcessGroupsOrganizationDataItem item = new ProcessGroupsOrganizationDataItem(groupNamesToOrgs.getGroupName(),
                        groupNamesToOrgs.getVersion(), groupNamesToOrgs.getIdOfOrg());
                resultItems.add(item);
            }
        } catch (Exception e) {
            logger.error("failed to process groups organization data,", e);
        }
        result.setItems(resultItems);
        return result;
    }

    private Org foundMainBuilding() {
        Org currentOrg = (Org) session.load(Org.class,sectionRequest.getIdOfOrg());
        if (currentOrg.isMainBuilding()) return currentOrg;
        Set<Org> friendlyOrgs = currentOrg.getFriendlyOrg();
        for (Org friendlyOrg : friendlyOrgs) {
            if (friendlyOrg.isMainBuilding()) {
                return friendlyOrg;
            }
        }
        return null;
    }

}
