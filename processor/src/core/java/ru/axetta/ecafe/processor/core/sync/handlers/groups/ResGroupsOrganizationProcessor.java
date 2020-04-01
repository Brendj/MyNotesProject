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
                    String.format("Not found main building for current org, id=%s", sectionRequest.getIdOfOrg())));
            return result;
        }
        Long nextVersion = getNextVersion();
        if (nextVersion == null) {
            return null;
        }

        for (GroupOrganizationItem groupsOrganizationRequest : sectionRequest.getItems()) {
            if (groupsOrganizationRequest.getNeedDeleteMiddleGroups() != null) {
                if (groupsOrganizationRequest.getParentGroupName() == null) {
                    DAOUtils.deleteByParentGroupName(session, groupsOrganizationRequest.getName(),
                            groupsOrganizationRequest.getIdOfOrg());
                }
            } else {
                if (groupsOrganizationRequest.getParentGroupName() != null) {
                    DAOUtils.deleteByParentGroupName(session, groupsOrganizationRequest.getParentGroupName(),
                            groupsOrganizationRequest.getIdOfOrg());
                }
            }
        }

        ArrayList<ResProcessGroupsOrganizationItem> resultItems = new ArrayList<ResProcessGroupsOrganizationItem>();
        if (sectionRequest.getItems().size() > 0) {
            List<GroupNamesToOrgs> groupsFromMainBuilding = loadGroupsFromMainBuilding();
            for (GroupOrganizationItem groupItem : sectionRequest.getItems()) {
                ResultOperation resultOperation = processResultGroupOrganizationItem(groupsFromMainBuilding, groupItem,
                        nextVersion);
                if (groupItem.getMiddleGroup() == null) {
                    ResProcessGroupsOrganizationItem item = new ResProcessGroupsOrganizationItem(groupItem.getName(),
                            nextVersion, resultOperation);
                    resultItems.add(item);
                }
            }
        }
        result.setItems(resultItems);
        return result;
    }

    private List<GroupNamesToOrgs> loadGroupsFromMainBuilding() {
        Query query = session.createQuery("select g from GroupNamesToOrgs g where g.idOfMainOrg=:orgId");
        query.setParameter("orgId", mainOrg.getIdOfOrg());
        return query.list();
    }

    private GroupNamesToOrgs loadGroupFromMainBuilding(String parentGroupName) {
        Query query = session.createQuery("select g from GroupNamesToOrgs g where g.idOfMainOrg=:orgId and g.isMiddleGroup=true and g.parentGroupName=:parentGroupName order by g.version desc");
        query.setParameter("orgId", mainOrg.getIdOfOrg());
        query.setParameter("parentGroupName", parentGroupName);
        return (GroupNamesToOrgs) query.list().get(0);
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

    private ResultOperation processResultGroupOrganizationItem(List<GroupNamesToOrgs> groupsFromCurrentMainBuilding,
            GroupOrganizationItem groupItem, Long nextVersion) {
        ResultOperation resultOperation = new ResultOperation();
        GroupNamesToOrgs savedGroupNamesToOrgs = null;
        for (GroupNamesToOrgs groupNamesToOrgs : groupsFromCurrentMainBuilding) {
            if (groupNamesToOrgs.getGroupName().equals(groupItem.getName())) {
                savedGroupNamesToOrgs = groupNamesToOrgs;
                break;
            }
        }
        try {
            if (savedGroupNamesToOrgs == null) {
                savedGroupNamesToOrgs = new GroupNamesToOrgs();
                savedGroupNamesToOrgs.setGroupName(groupItem.getName());
                savedGroupNamesToOrgs.setIdOfOrg(groupItem.getBindingToOrg());
                savedGroupNamesToOrgs.setIdOfMainOrg(mainOrg.getIdOfOrg());
                savedGroupNamesToOrgs.setParentGroupName(groupItem.getParentGroupName());
                savedGroupNamesToOrgs.setIsMiddleGroup(groupItem.getMiddleGroup());
                savedGroupNamesToOrgs.setIsSixDaysWorkWeek(groupItem.getSixDaysWorkWeek());
                savedGroupNamesToOrgs.setMainBuilding(1);
                savedGroupNamesToOrgs.setVersion(nextVersion);
            } else {
                savedGroupNamesToOrgs.setGroupName(groupItem.getName());
                savedGroupNamesToOrgs.setIdOfOrg(groupItem.getBindingToOrg());
                savedGroupNamesToOrgs.setIdOfMainOrg(mainOrg.getIdOfOrg());
                savedGroupNamesToOrgs.setParentGroupName(groupItem.getParentGroupName());
                savedGroupNamesToOrgs.setIsMiddleGroup(groupItem.getMiddleGroup());
                savedGroupNamesToOrgs.setIsSixDaysWorkWeek(groupItem.getSixDaysWorkWeek());
                savedGroupNamesToOrgs.setVersion(nextVersion);
                savedGroupNamesToOrgs.setMainBuilding(1);
            }
            session.saveOrUpdate(savedGroupNamesToOrgs);
        } catch (Exception e) {
            resultOperation = new ResultOperation(500, e.getMessage());
        }
        return resultOperation;
    }

}
