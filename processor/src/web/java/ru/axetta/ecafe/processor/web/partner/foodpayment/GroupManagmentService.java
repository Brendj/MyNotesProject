/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class GroupManagmentService implements IGroupManagmentService {


    private Session persistanceSession;

    public GroupManagmentService(Session persistanceSession){
        this.persistanceSession = persistanceSession;
    }

    @Override
    public Boolean isGroupNameAlreadyExists(long orgId, String groupName) throws Exception {
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, orgId);
        ClientGroup group;
        group = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistanceSession,orgId, groupName);
        if(isGroupExists(group))
            return true;
        for (Org friendlyOrg:friendlyOrgs
        ) {
            group = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistanceSession, friendlyOrg.getIdOfOrg(), groupName);
            if(isGroupExists(group))
                return true;
        }
        return false;
    }

    @Override
    public Boolean isOrgExists(long orgId) throws Exception {
        Org org = DAOUtils.findOrg(persistanceSession, orgId);
        if(org == null)
            return false;
        else
            return true;
    }

    @Override
    public void addOrgGroup(long orgId, String groupName) throws Exception {
        Transaction persistenceTransaction = persistanceSession.beginTransaction();
        ClientGroup clientGroup = DAOUtils.createClientGroup(persistanceSession, orgId, groupName);
    }

    @Override
    public List<GroupInfo> getOrgGroups(long orgId) {
        return null;
    }

    private Boolean isGroupExists(ClientGroup group){
        if(group != null && group.getCompositeIdOfClientGroup().getIdOfClientGroup() < ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES)
            return true;
        else
            return false;
    }
}
