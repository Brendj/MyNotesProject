/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.commons.lang.NullArgumentException;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
        throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
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
        friendlyOrgs.add(DAOUtils.findOrg(persistanceSession, orgId));
        Disjunction restrictionOrgIdIn = Restrictions.disjunction();
        List<ClientGroup> orgGroups = new LinkedList<ClientGroup>();
        for (Org friendlyOrg:friendlyOrgs) {
            restrictionOrgIdIn.add(Restrictions.eq("idOfMainOrg", friendlyOrg.getIdOfOrg()));
            orgGroups.addAll(DAOUtils.getClientGroupsByIdOfOrg(persistanceSession, friendlyOrg.getIdOfOrg()));
        }
        if(orgGroups.isEmpty())
            throw new RequestProcessingException(GroupManagementErrors.GROUPS_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.GROUP_NOT_FOUND.getErrorMessage());
        for (ClientGroup orgGroup: orgGroups){
            if(orgGroup.getGroupName() == null || orgGroup.getGroupName().isEmpty())
                continue;
            if(isGroupNotPredefined(orgGroup)){

                Criteria groupNamesToOrgCriteria = persistanceSession.createCriteria(GroupNamesToOrgs.class);
                groupNamesToOrgCriteria.add(restrictionOrgIdIn);
                groupNamesToOrgCriteria.add(Restrictions.eq("groupName", orgGroup.getGroupName()));
                groupNamesToOrgCriteria.setMaxResults(1);
                GroupNamesToOrgs groupNamesToOrgs = (GroupNamesToOrgs) groupNamesToOrgCriteria.uniqueResult();
                if(groupNamesToOrgs != null && groupNamesToOrgs.getIdOfOrg().longValue() != orgGroup.getCompositeIdOfClientGroup().getIdOfOrg().longValue())
                    continue;
                GroupInfo groupInfo = getGroupInfo(persistanceSession, orgGroup, getClientGroupManagerByGroupName(persistanceSession,
                        orgGroup.getGroupName(), orgGroup.getOrg().getIdOfOrg()));
                groupInfo.setOrgId(orgGroup.getCompositeIdOfClientGroup().getIdOfOrg());
                groupInfoList.add(groupInfo);
            }
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
        ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistanceSession, orgId, groupName);
        if(!isGroupNotPredefined(clientGroup))
            throw new RequestProcessingException(GroupManagementErrors.GROUP_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.GROUP_NOT_FOUND.getErrorMessage());
        if(!isEmployeeExists(orgId,contractId))
            throw new RequestProcessingException(GroupManagementErrors.EMPLOYEE_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.EMPLOYEE_NOT_FOUND.getErrorMessage());
        Client client = DAOUtils.findClientByContractId(persistanceSession, contractId);
        Criteria employeeGroupCriteria = persistanceSession.createCriteria(ClientGroupManager.class);
        employeeGroupCriteria.add(Restrictions.eq("clientGroupName", clientGroup.getGroupName()));
        employeeGroupCriteria.add(Restrictions.eq("orgOwner", orgId));
        employeeGroupCriteria.add(Restrictions.eq("idOfClient", client.getIdOfClient()));
        employeeGroupCriteria.setMaxResults(1);
        ClientGroupManager clientGroupManager = (ClientGroupManager) employeeGroupCriteria.uniqueResult();
        boolean deletedStatus = !status;
        if(clientGroupManager != null && clientGroupManager.isDeleted() == deletedStatus)
            return;
        if(status){
            if(clientGroupManager != null && clientGroupManager.isDeleted() != deletedStatus){
                clientGroupManager.setDeleted(deletedStatus);
                clientGroupManager.setVersion((clientGroupManager.getVersion()+1));
                persistanceSession.update(clientGroupManager);
                return;
            }
            if(clientGroupManager == null){
                ClientGroupManager newClientGroupManager = new ClientGroupManager();
                newClientGroupManager.setVersion(DAOUtils.nextVersionByClientgroupManager(persistanceSession));
                newClientGroupManager.setDeleted(deletedStatus);
                newClientGroupManager.setIdOfClient(client.getIdOfClient());
                newClientGroupManager.setOrgOwner(orgId);
                newClientGroupManager.setClientGroupName(clientGroup.getGroupName());
                persistanceSession.save(newClientGroupManager);
                return;
            }
        }
        if(!status){
            if(clientGroupManager == null)
                throw new RequestProcessingException(GroupManagementErrors.BUNCH_NOT_FOUND.getErrorCode(),
                        GroupManagementErrors.BUNCH_NOT_FOUND.getErrorMessage());
            if(clientGroupManager != null){
                clientGroupManager.setDeleted(deletedStatus);
                clientGroupManager.setVersion(DAOUtils.nextVersionByClientgroupManager(persistanceSession));
                persistanceSession.update(clientGroupManager);
                return;
            }
        }
    }

    @Override
    public ResponseClients getClientsList(List<Long> groupsList, long idOfOrg) throws Exception {
        ResponseClients responseClients = new ResponseClients();
        responseClients.setOrgId(idOfOrg);
        for (Long idOfGroup : groupsList) {
            FPGroup fpGroup = new FPGroup();
            fpGroup.setGroupId(idOfGroup);
            Query query = persistanceSession.createQuery("select c from Client c "
                    + "join fetch c.clientGroup "
                    + "join fetch c.person "
                    + "left join fetch c.categoriesInternal "
                    + "where c.org.idOfOrg = :idOfOrg and c.clientGroup.compositeIdOfClientGroup.idOfClientGroup = :idOfGroup order by c.contractId");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("idOfGroup", idOfGroup);
            List<Client> list = query.list();
            for (Client client : list) {
                fpGroup.setGroupName(client.getClientGroup().getGroupName());
                FPClient fpClient = new FPClient(client);
                fpGroup.getClients().add(fpClient);
            }
            responseClients.getGroups().add(fpGroup);
        }
        return responseClients;
    }

    private Boolean isGroupNotPredefined(ClientGroup group){
        if(group == null)
            return false;
        if(group.getCompositeIdOfClientGroup().getIdOfClientGroup().longValue() < ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES)
            return true;
        return false;
    }

    private List<ClientGroupManager> getClientGroupManagerByGroupName(Session persistanceSession, String groupName, long orgId) throws Exception{
        Criteria managersCriteria = persistanceSession.createCriteria(ClientGroupManager.class);
        if(groupName == null || groupName.isEmpty())
            throw new NullArgumentException("Group name cannot be null or empty");
        managersCriteria.add(Restrictions.eq("orgOwner", orgId));
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
            groupManager.setContractId(client.getContractId());
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
        groupInfo.setGroupId(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
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
        groupEmployee.setGroupId(groupId);
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

    private void checkOrganizationId(long orgId) throws Exception {
        if(!isOrgExists(orgId))
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
    }

    private Boolean isGroupNameAlreadyExists(long orgId, String groupName) throws Exception {
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, orgId);
        ClientGroup group;
        group = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistanceSession,orgId, groupName);
        if(isGroupNotPredefined(group))
            return true;
        for (Org friendlyOrg:friendlyOrgs
        ) {
            group = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistanceSession, friendlyOrg.getIdOfOrg(), groupName);
            if(isGroupNotPredefined(group))
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
            if(client.getOrg().getIdOfOrg().longValue() == org.getIdOfOrg().longValue()){
                clientIsBelongToOrg = true;
                break;
            }
        }
        return clientIsBelongToOrg;
    }

    private Boolean clientIsEmployee(Client client){
        Long clientGroupId = client.getIdOfClientGroup();
        if((clientGroupId.equals(ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue()))
                | (clientGroupId.equals(ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()))
                | (clientGroupId.equals(ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue()))) {
            return true;
        }
        return false;
    }

}
