/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.ClientGroupManager;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.NullArgumentException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.LinkedList;
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
    public List<GroupInfo> getOrgGroups(long orgId) throws Exception {
        List<GroupInfo> groupInfoList = new LinkedList<GroupInfo>();
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, orgId);
        List<ClientGroup> orgGroups = DAOUtils.getClientGroupsByIdOfOrg(persistanceSession, orgId);
        for (Org friendlyOrg:friendlyOrgs) {
            orgGroups.addAll(DAOUtils.getClientGroupsByIdOfOrg(persistanceSession, friendlyOrg.getIdOfOrg()));
        }
        for (ClientGroup orgGroup: orgGroups){
            groupInfoList.add(getGroupInfo(persistanceSession, orgGroup, getClientGroupManagerByGroupName(persistanceSession,
                    orgGroup.getGroupName())));
        }
        return groupInfoList;
    }

    @Override
    public List<GroupEmployee> getEmployees(long orgId) throws Exception {
        List<Org> organizations = new LinkedList<Org>();
        Org organization = DAOUtils.findOrg(persistanceSession, orgId);
        organizations.add(organization);
        organizations.addAll(DAOUtils.findFriendlyOrgs(persistanceSession, orgId));
    }

    private Boolean isGroupExists(ClientGroup group){
        if(group != null && group.getCompositeIdOfClientGroup().getIdOfClientGroup() < ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES)
            return true;
        else
            return false;
    }

    private List<ClientGroupManager> getClientGroupManagerByGroupName(Session persistanceSession, String groupName) throws Exception{
        Criteria managersCriteria = persistanceSession.createCriteria(ClientGroupManager.class);
        if(groupName == null || groupName.isEmpty())
            throw new NullArgumentException("Group name cannot be null or empty");
        managersCriteria.add(Restrictions.eq("clientgroupname", groupName));
        managersCriteria.add(Restrictions.eq("deleted", 0));
        return managersCriteria.list();
    }

    private List<GroupManager> getGroupManagersFromClients(List<Client> clients){
        if(clients == null)
            throw new NullArgumentException("List of clients cannot be null.");
        List<GroupManager> groupManagerList = new LinkedList<GroupManager>();
        for (Client client: clients){
            GroupManager groupManager = new GroupManager();
            groupManager.setContractId(client.getContractId().toString());
            groupManager.setName(client.getPerson().getFirstName());
            groupManager.setSecondName(client.getPerson().getSecondName());
            groupManager.setSurname(client.getPerson().getSurname());
            groupManagerList.add(groupManager);
        }
        return groupManagerList;
    }

    private GroupInfo getGroupInfo(Session persistanceSession, ClientGroup clientGroup, List<ClientGroupManager> groupManagers) throws Exception{
        if(clientGroup == null || groupManagers == null)
            throw new NullArgumentException("ClientGroup and groupManagers cannot be null");
        GroupInfo groupInfo = new GroupInfo();
        List<Client> groupManagerClientList = new LinkedList<Client>();
        groupInfo.setGroupId(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup().toString());
        groupInfo.setGroupName(clientGroup.getGroupName());
        for (ClientGroupManager clientGroupManager: groupManagers){
            groupManagerClientList.add(DAOUtils.findClient(persistanceSession,clientGroupManager.getIdOfClient()));
        }
        groupInfo.setManagers(getGroupManagersFromClients(groupManagerClientList));
        return groupInfo;
    }

    private List<Client> getEmployeeClientsByOrg(Session persistanceSession, List<Org> organizations){
        if(organizations == null)
            throw new NullArgumentException("List of organizations cannot be null.");
        Criteria employeeCriteria = persistanceSession.createCriteria(Client.class);
        employeeCriteria.add(Restrictions.eq("idofclientgroup", ClientGroup.Predefined.CLIENT_ADMINISTRATION));
        employeeCriteria.add(Restrictions.eq("idofclientgroup", ClientGroup.Predefined.CLIENT_EMPLOYEES));
        employeeCriteria.add(Restrictions.eq("idofclientgroup", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES));
        for (Org organization: organizations){
            employeeCriteria.add(Restrictions.eq("idoforg", organization.getIdOfOrg()));
        }
        return employeeCriteria.list();
    }
}
