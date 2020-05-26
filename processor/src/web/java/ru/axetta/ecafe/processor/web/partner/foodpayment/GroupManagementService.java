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
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import java.util.LinkedList;
import java.util.List;

public class GroupManagementService implements IGroupManagementService {


    private Session persistanceSession;

    public GroupManagementService(Session persistanceSession){
        this.persistanceSession = persistanceSession;
    }


    @Override
    public Boolean checkPermission(String token) throws Exception {
        return null;
    }

    @Override
    public void addOrgGroup(long orgId, String groupName) throws Exception {
        if(!isOrgExists(orgId))
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
        if(isGroupNameAlreadyExists(orgId, groupName))
            throw new RequestProcessingException(GroupManagementErrors.GROUP_IS_EXISTS.getErrorCode(),
                    GroupManagementErrors.GROUP_IS_EXISTS.getErrorMessage());
        ClientGroup clientGroup = DAOUtils.createClientGroup(persistanceSession, orgId, groupName);
    }

    @Override
    public List<GroupInfo> getOrgGroups(long orgId) throws Exception {
        if(!isOrgExists(orgId))
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
        List<GroupInfo> groupInfoList = new LinkedList<GroupInfo>();
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, orgId);
        List<ClientGroup> orgGroups = DAOUtils.getClientGroupsByIdOfOrg(persistanceSession, orgId);
        for (Org friendlyOrg:friendlyOrgs) {
            orgGroups.addAll(DAOUtils.getClientGroupsByIdOfOrg(persistanceSession, friendlyOrg.getIdOfOrg()));
        }
        if(orgGroups.isEmpty())
            throw new RequestProcessingException(GroupManagementErrors.GROUPS_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.GROUP_NOT_FOUND.getErrorMessage());
        for (ClientGroup orgGroup: orgGroups){
            GroupInfo groupInfo = getGroupInfo(persistanceSession, orgGroup, getClientGroupManagerByGroupName(persistanceSession,
                    orgGroup.getGroupName()));
            groupInfo.setGroupId(orgGroup.getOrg().getIdOfOrg().toString());
            groupInfoList.add(groupInfo);
        }
        return groupInfoList;
    }

    @Override
    public List<GroupEmployee> getEmployees(long orgId) throws Exception {
        if(!isOrgExists(orgId))
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
        List<Org> organizations = new LinkedList<Org>();
        Org organization = DAOUtils.findOrg(persistanceSession, orgId);
        organizations.add(organization);
        organizations.addAll(DAOUtils.findFriendlyOrgs(persistanceSession, orgId));
        List<GroupEmployee> groupEmployeeList = new LinkedList<GroupEmployee>();
        GroupEmployee administrationEmployees = getOrgGroupEmployee(persistanceSession,
                ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue(),
                ClientGroup.Predefined.CLIENT_ADMINISTRATION.getNameOfGroup(),organizations);
        GroupEmployee teacherEmployees = getOrgGroupEmployee(persistanceSession,
                ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue(),
                ClientGroup.Predefined.CLIENT_EMPLOYEES.getNameOfGroup(),organizations);
        GroupEmployee techEmployees = getOrgGroupEmployee(persistanceSession,
                ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue(),
                ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getNameOfGroup(),organizations);
        if(administrationEmployees.getEmployees().isEmpty() && teacherEmployees.getEmployees().isEmpty() && techEmployees.getEmployees().isEmpty())
            throw new RequestProcessingException(GroupManagementErrors.EMPLOYEES_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.EMPLOYEES_NOT_FOUND.getErrorMessage());
        groupEmployeeList.add(administrationEmployees);
        groupEmployeeList.add(teacherEmployees);
        groupEmployeeList.add(techEmployees);
        return groupEmployeeList;
    }

    @Override
    public void editEmployee(long orgId, String groupName, long contractId, Boolean status) throws Exception {
        checkOrganizationId(orgId);
        if(!isGroupNameAlreadyExists(orgId, groupName))
            throw new RequestProcessingException(GroupManagementErrors.GROUP_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.GROUP_NOT_FOUND.getErrorMessage());
        if(!isEmployeeExists(orgId,contractId))
            throw new RequestProcessingException(GroupManagementErrors.EMPLOYEE_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.EMPLOYEE_NOT_FOUND.getErrorMessage());

        //ClientGroup group = DAOUtils.findClientGroupByIdOfClientGroupAndIdOfOrg()

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
        managersCriteria.add(Restrictions.eq("clientGroupName", groupName));
        managersCriteria.add(Restrictions.eq("deleted", false));
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

    private GroupEmployee getOrgGroupEmployee(Session persistanceSession, Long groupId, String groupName, List<Org> organizations){
        if(organizations == null)
            throw new NullArgumentException("List of organizations cannot be null.");
        GroupEmployee groupEmployee = new GroupEmployee();
        groupEmployee.setGroupId(groupId.toString());
        groupEmployee.setGroupName(groupName);
        Criteria employeeCriteria = persistanceSession.createCriteria(Client.class);
        Disjunction restrictionGroupOr = Restrictions.disjunction();
        employeeCriteria.add(Restrictions.eq("idOfClientGroup", groupId));
        for (Org organization: organizations){
            restrictionGroupOr.add(Restrictions.eq("org.idOfOrg", organization.getIdOfOrg()));
        }
        employeeCriteria.add(restrictionGroupOr);
        groupEmployee.setEmployees(getGroupManagersFromClients(employeeCriteria.list()));
        return groupEmployee;
    }

    private void checkGroupName(long orgId, String groupName) throws Exception {
        if(isGroupNameAlreadyExists(orgId, groupName))
            throw new RequestProcessingException(GroupManagementErrors.GROUP_IS_EXISTS.getErrorCode(),
                    GroupManagementErrors.GROUP_IS_EXISTS.getErrorMessage());
    }

    private void checkOrganizationId(long orgId) throws Exception {
        if(!isOrgExists(orgId))
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
    }

    private Boolean isGroupNameAlreadyExists(long orgId, String groupName) throws Exception {
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


    private Boolean isOrgExists(long orgId) throws Exception {
        Org org = DAOUtils.findOrg(persistanceSession, orgId);
        if(org == null)
            return false;
        else
            return true;
    }

    private Boolean isEmployeeExists(long orgId, long contractId) throws Exception{
        Client client = DAOUtils.findClientByContractId(persistanceSession, contractId);
        if(client == null)
            return false;
        List<Org> orgList = new LinkedList<Org>();
        orgList.add(DAOUtils.findOrg(persistanceSession, orgId));
        orgList.addAll(DAOUtils.findFriendlyOrgs(persistanceSession, orgId));
        if(!clientIsEmployee(client))
            return false;
        Boolean clientIsBelongToOrg = false;
        for (Org org: orgList){
            if(client.getOrg().getIdOfOrg() == org.getIdOfOrg()){
                clientIsBelongToOrg = true;
                break;
            }
        }
        return clientIsBelongToOrg;
    }

    private Boolean clientIsEmployee(Client client){
        if(client.getIdOfClientGroup() == ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue()
                || client.getIdOfClientGroup() == ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                || client.getIdOfClientGroup() == ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue())
            return true;
        return false;
    }

}
