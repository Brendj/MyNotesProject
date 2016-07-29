/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.groups;

import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: akmukov
 * Date: 28.07.2016
 */
public class ResGroupsOrganizationProcessor extends AbstractProcessor<ResProcessGroupsOrganization> {
    private static final Logger logger = LoggerFactory.getLogger(ResGroupsOrganizationProcessor.class);
    private final GroupsOrganizationRequest sectionRequest;
    private Org currentOrg;
    private Org mainOrg;

    public ResGroupsOrganizationProcessor(Session persistenceSession, GroupsOrganizationRequest sectionRequest) {
        super(persistenceSession);
        this.sectionRequest = sectionRequest;
    }

    @Override
    public ResProcessGroupsOrganization process() throws Exception {

        ResProcessGroupsOrganization result = new ResProcessGroupsOrganization();
        loadOrgFromRequest();
        if (!foundMainBuilding()) {
            result.setErrorResult(new ResultOperation(500,
                    String.format("Not found main bilding for current org, id=%s", sectionRequest.getIdOfOrg())));
            return result;
        }
        Long nextVersion = getNextVersion();
        if (nextVersion == null) {
            return null;
        }
        ArrayList<ResProcessGroupsOrganizationItem> resultItems = new ArrayList<ResProcessGroupsOrganizationItem>();
        for (GroupOrganizationItem groupItem : sectionRequest.getItems()) {
            ResultOperation resultOperation = processResultGroupOrganizationItem(groupItem,nextVersion);
            ResProcessGroupsOrganizationItem item = new ResProcessGroupsOrganizationItem(groupItem.getName(),
                    nextVersion, resultOperation);
            resultItems.add(item);
        }
        result.setItems(resultItems);
        return result;
    }

    private boolean foundMainBuilding() {
        Set<Org> friendlyOrgs = currentOrg.getFriendlyOrg();
        for (Org friendlyOrg : friendlyOrgs) {
            if (friendlyOrg.isMainBuilding()) {
                mainOrg = friendlyOrg;
                return true;
            }
        }
        return false;
    }

    private void loadOrgFromRequest() {
        currentOrg = (Org) session.load(Org.class, sectionRequest.getIdOfOrg());
    }

    private Long getNextVersion() {
        try {
            long version = DAOUtils.nextVersionByGroupNameToOrg(session);
            return version;
        } catch (Exception e) {
            logger.error("failed get next version,", e);
            return null;
        }
    }

    private ResultOperation processResultGroupOrganizationItem(GroupOrganizationItem groupItem, Long nextVersion) {
        ResultOperation resultOperation = new ResultOperation();
        try {
            // обработать запись
            Query query = session
                    .createQuery("select g from GroupNamesToOrgs g where g.idOfMainOrg=:orgId and g.groupName =:groupName");
            query.setParameter("orgId", mainOrg.getIdOfOrg());
            query.setParameter("groupName", groupItem.getName());
            List result = query.list();
            GroupNamesToOrgs groupNameToOrg;
            if (result == null || result.size() == 0) {
                groupNameToOrg = new GroupNamesToOrgs();
                groupNameToOrg.setGroupName(groupItem.getName());
                groupNameToOrg.setIdOfOrg(groupItem.getBindingToOrg());
                groupNameToOrg.setIdOfMainOrg(mainOrg.getIdOfOrg());
                groupNameToOrg.setMainBuilding(1);
                groupNameToOrg.setVersion(nextVersion);
            } else {
                groupNameToOrg = (GroupNamesToOrgs) result.get(0);
                groupNameToOrg.setGroupName(groupItem.getName());
                groupNameToOrg.setIdOfOrg(groupItem.getBindingToOrg());
                groupNameToOrg.setIdOfMainOrg(mainOrg.getIdOfOrg());
                groupNameToOrg.setVersion(nextVersion);
                groupNameToOrg.setMainBuilding(1);
            }
            session.saveOrUpdate(groupNameToOrg);
        } catch (Exception e) {
            resultOperation = new ResultOperation(500, e.getMessage());
        }
        return resultOperation;
    }

}
