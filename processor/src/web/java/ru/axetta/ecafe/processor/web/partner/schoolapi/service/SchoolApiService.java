/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.idGenerator.IIdGenerator;
import ru.axetta.ecafe.processor.core.utils.idGenerator.OrganizationUniqueGeneratorId;
import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.GroupManagementErrors;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.RequestProcessingException;

import org.apache.commons.lang.NullArgumentException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class SchoolApiService implements ISchoolApiService {
    Logger logger = LoggerFactory.getLogger(SchoolApiService.class);

    private Session persistanceSession;

    public SchoolApiService(Session persistanceSession){
        this.persistanceSession = persistanceSession;
    }


    @Override
    public boolean isFriendlyOrg(long orgId, long friendlyOrgId) throws Exception {
        if(orgId != friendlyOrgId){
            if(!orgIsFriendly(orgId, friendlyOrgId))
                return false;
        }
        return true;
    }


    public void checkPermission(int userIdOfRole, int permittedIdOfRole, long userOrgId, long requestOrgId)
            throws Exception {
        if(userIdOfRole != permittedIdOfRole)
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        if(userOrgId != requestOrgId){
            if(!orgIsFriendly(requestOrgId, userOrgId))
                throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                        GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        }
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
    public ResponseClients getClientsList(List<String> groupsList, long idOfOrg) throws Exception {
        List<Long> idOfOrgList = DAOUtils.findFriendlyOrgIds(persistanceSession, idOfOrg);
        if (idOfOrgList.size() == 0) throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND);
        ResponseClients responseClients = new ResponseClients();
        boolean groupsNotFound = true;
        for (String nameOfGroup : groupsList) {
            nameOfGroup = nameOfGroup.trim();
            List groups = DAOUtils.findClientGroupsByGroupNameForAllOrgsIgnoreCase(persistanceSession, idOfOrgList, nameOfGroup);
            if (groups.size() == 0) {
                addFPGroupNotFound(responseClients, nameOfGroup);
                continue;
            }
            GroupNamesToOrgs gnto = DAOUtils.findGroupFromGroupNamesToOrgs(persistanceSession, idOfOrgList, nameOfGroup);
            ClientGroup cg = findClientGroup(gnto, groups, idOfOrg);
            if (cg == null) {
                addFPGroupNotFound(responseClients, nameOfGroup);
                continue;
            }

            groupsNotFound = false;
            FPGroup fpGroup = new FPGroup();
            fpGroup.setCode(0);
            fpGroup.setMessage("OK");
            fpGroup.setGroupName(nameOfGroup);
            Query query = persistanceSession.createQuery("select distinct c from Client c "
                    + "join fetch c.clientGroup "
                    + "join fetch c.person "
                    + "left join fetch c.categoriesInternal "
                    + "where c.org.idOfOrg in (:idOfOrgList) and upper(c.clientGroup.groupName) = :nameOfGroup order by c.contractId");
            query.setParameterList("idOfOrgList", idOfOrgList);
            query.setParameter("nameOfGroup", nameOfGroup.toUpperCase());
            List<Client> list = query.list();
            for (Client client : list) {
                fpGroup.setGroupId(cg.getCompositeIdOfClientGroup().getIdOfClientGroup());
                fpGroup.setOrgId(client.getOrg().getIdOfOrg());
                FPClient fpClient = new FPClient(client);
                fpGroup.getClients().add(fpClient);
            }
            responseClients.getGroups().add(fpGroup);
        }
        if (groupsNotFound) throw new RequestProcessingException(GroupManagementErrors.GROUP_NOT_FOUND);
        return responseClients;
    }

    private void addFPGroupNotFound(ResponseClients responseClients, String groupName) {
        FPGroup fpGroup = new FPGroup();
        fpGroup.setGroupName(groupName);
        fpGroup.setCode(GroupManagementErrors.GROUP_NOT_FOUND.getErrorCode());
        fpGroup.setMessage(GroupManagementErrors.GROUP_NOT_FOUND.getErrorMessage());
        responseClients.getGroups().add(fpGroup);
    }

    @Override
    public ResponseDiscounts getDiscountsList(Long idOfOrg) throws Exception {
        List<Long> idOfOrgList = DAOUtils.findFriendlyOrgIds(persistanceSession, idOfOrg);
        if (idOfOrgList.size() == 0) throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND);
        ResponseDiscounts responseDiscounts = new ResponseDiscounts();
        List<CategoryDiscount> categoryDiscounts = DiscountManager.getCategoryDiscounts(persistanceSession);
        for (CategoryDiscount categoryDiscount : categoryDiscounts) {
            if (categoryDiscount.getCategoryName().equals(DiscountManager.RESERV_DISCOUNT)) {
                ResponseDiscountItem item = new ResponseDiscountItem(categoryDiscount);
                responseDiscounts.addItem(item);
                continue;
            }
            if (categoryDiscount.getDiscountsRules().size() == 0) continue;
            for (DiscountRule discountRule : categoryDiscount.getDiscountsRules()) {
                if (discountRule.getCategoryOrgs().size() == 0) {
                    ResponseDiscountItem item = new ResponseDiscountItem(categoryDiscount);
                    responseDiscounts.addItem(item);
                    continue;
                }
                ResponseDiscountItem item = null;
                for (CategoryOrg categoryOrg : discountRule.getCategoryOrgs()) {
                    for (Org o : categoryOrg.getOrgs()) {
                        if (idOfOrgList.contains(o.getIdOfOrg())) {
                            if (item == null) item = new ResponseDiscountItem(categoryDiscount);
                            item.addCategoryOrg(categoryOrg.getCategoryName(), o.getIdOfOrg());
                        }
                    }
                }
                if (item != null) responseDiscounts.addItem(item);
            }
        }

        return responseDiscounts;
    }

    @Override
    public ResponseDiscountClients processDiscountClientsList(Long orgId, Long discountId, Boolean status, List<Long> clients) throws Exception {
        ResponseDiscountClients result = new ResponseDiscountClients();
        CategoryDiscount categoryDiscount = getCategoryDiscount(discountId);
        ResponseDiscounts availableDiscounts = getDiscountsList(orgId);
        List<Long> allOrgs = DAOUtils.findFriendlyOrgIds(persistanceSession, orgId);
        for (Long contractId : clients) {
            result.addItem(processDiscountClient(allOrgs, contractId, status,
                    categoryDiscount, availableDiscounts));
        }
        return result;
    }

    @Override
    public ResponseDiscountGroups processDiscountGroupsList(Long orgId, Long discountId, Boolean status, List<String> groups) throws Exception {
        CategoryDiscount categoryDiscount = getCategoryDiscount(discountId);
        ResponseDiscounts availableDiscounts = getDiscountsList(orgId);
        ResponseDiscountGroups result = new ResponseDiscountGroups();
        ResponseClients responseClients = getClientsList(groups, orgId);
        List<Long> allOrgs = DAOUtils.findFriendlyOrgIds(persistanceSession, orgId);
        for (FPGroup fpGroup : responseClients.getGroups()) {
            ResponseDiscountGroupItem resultClients = new ResponseDiscountGroupItem(fpGroup.getGroupName());
            for (FPClient fpClient : fpGroup.getClients()) {
                resultClients.addItem(processDiscountClient(allOrgs, fpClient.getContractId(), status,
                        categoryDiscount, availableDiscounts));
            }
            result.addItem(resultClients);
        }
        return result;
    }

    @Override
    public ClientGroup getClientGroupByOrgIdAndGroupName(Long orgId, String groupName) throws Exception {
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, orgId);
        Disjunction restrictionOrgIdIn = Restrictions.disjunction();
        friendlyOrgs.add(DAOUtils.findOrg(persistanceSession, orgId));
        ClientGroup group = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistanceSession,orgId, groupName);
        if(group == null)
            throw new RequestProcessingException(GroupManagementErrors.GROUPS_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.GROUPS_NOT_FOUND.getErrorMessage());
        for (Org friendlyOrg:friendlyOrgs) {
            restrictionOrgIdIn.add(Restrictions.eq("idOfMainOrg", friendlyOrg.getIdOfOrg()));
        }
        Criteria groupNamesToOrgCriteria = persistanceSession.createCriteria(GroupNamesToOrgs.class);
        groupNamesToOrgCriteria.add(restrictionOrgIdIn);
        groupNamesToOrgCriteria.add(Restrictions.eq("groupName", group.getGroupName()));
        groupNamesToOrgCriteria.setMaxResults(1);
        GroupNamesToOrgs groupNamesToOrgs = (GroupNamesToOrgs) groupNamesToOrgCriteria.uniqueResult();
        if(groupNamesToOrgs != null && groupNamesToOrgs.getIdOfOrg() != null){
            group = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistanceSession,
                    groupNamesToOrgs.getIdOfOrg(), groupNamesToOrgs.getGroupName());
            if(group == null)
                throw new RequestProcessingException(GroupManagementErrors.GROUPS_NOT_FOUND.getErrorCode(),
                        GroupManagementErrors.GROUPS_NOT_FOUND.getErrorMessage());
            return group;
        }
        else {
            return group;
        }
    }

    @Override
    public List<Client> getClientsForContractIds(ClientGroup newClientGroup, List<Long> contractIds,
            boolean strictEditMode) throws Exception {
        Criteria clientsCriteria = persistanceSession.createCriteria(Client.class);
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, newClientGroup.getCompositeIdOfClientGroup().getIdOfOrg());
        friendlyOrgs.add(DAOUtils.findOrg(persistanceSession, newClientGroup.getCompositeIdOfClientGroup().getIdOfOrg()));
        List<Long> orgIds = getOrgIds(friendlyOrgs);
        clientsCriteria.add(Restrictions.and(
                Restrictions.in("contractId", contractIds),
                Restrictions.in("org.idOfOrg", orgIds)
        ));
        List<Client> clients = clientsCriteria.list();
        if(!strictEditMode)
            return clients;
        else {
            return getClientsForStrictMode(newClientGroup, clients);
        }
    }

    @Override
    public List<Client> getClientsForGroups(ClientGroup newClientGroup, List<String> oldGroups,
            boolean strictEditMode) throws Exception {
        List<Client> clients;
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession,
                newClientGroup.getCompositeIdOfClientGroup().getIdOfOrg());
        friendlyOrgs.add(DAOUtils.findOrg(persistanceSession,
                newClientGroup.getCompositeIdOfClientGroup().getIdOfOrg()));
        List<Long> orgIds = getOrgIds(friendlyOrgs);
        List<String> oldGroupsInUpperCase = new LinkedList<>();
        for(String groupName: oldGroups){
            oldGroupsInUpperCase.add(groupName.toUpperCase());
        }
        Query query = persistanceSession.createQuery("from Client c "
                + "join fetch c.clientGroup "
                + "join fetch c.person "
                + "where c.org.idOfOrg in (:idOfOrgList) and upper(c.clientGroup.groupName) in (:nameOfGroupList)");
        query.setParameterList("idOfOrgList", orgIds);
        query.setParameterList("nameOfGroupList", oldGroupsInUpperCase);

        clients = query.list();
        if(!strictEditMode)
            return clients;
        else {
            return getClientsForStrictMode(newClientGroup,clients);
        }
    }

    @Override
    public List<EditClientsGroupsGroupDTO> moveClientsInGroup(ClientGroup newClientGroup, List<Client> clients, String username)
            throws Exception {
        String comment = "Изменено пользователем " + username;
        Org newGroupOrg = DAOUtils.findOrg(persistanceSession, newClientGroup.getCompositeIdOfClientGroup().getIdOfOrg());
        Long newClientGroupId = newClientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
        HashMap<String, List<Client>> orderedClients = getClientsGroupByClientGroup(clients);
        List<EditClientsGroupsGroupDTO> editClientsGroupsGroupDTOList = new LinkedList<>();
        for(Map.Entry entry: orderedClients.entrySet()){
            EditClientsGroupsGroupDTO editClientsGroupsGroupDTO = new EditClientsGroupsGroupDTO();
            editClientsGroupsGroupDTO.setNewGroupName(newClientGroup.getGroupName());
            editClientsGroupsGroupDTO.setOrgId(newClientGroup.getOrg().getIdOfOrg());
            editClientsGroupsGroupDTO.setOldGroupName((String) entry.getKey());
            List<EditClientsGroupsClientDTO> editClientsGroupsClientDTOList = new LinkedList<>();
            Client firstClient = ((List<Client>) entry.getValue()).get(0);
            if(((List<Client>) entry.getValue()).isEmpty() && firstClient == null){
               continue;
            }
            for(Client client: (List<Client>)entry.getValue()){
                ClientGroupMigrationHistory clientGroupMigrationHistory = new ClientGroupMigrationHistory(newGroupOrg, client);
                clientGroupMigrationHistory.setNewGroupId(newClientGroupId);
                clientGroupMigrationHistory.setNewGroupName(newClientGroup.getGroupName());
                clientGroupMigrationHistory.setOldGroupId(client.getIdOfClientGroup());
                clientGroupMigrationHistory.setOldGroupName(client.getClientGroup().getGroupName());
                clientGroupMigrationHistory.setComment(comment);
                persistanceSession.save(clientGroupMigrationHistory);
                client.setOrg(newGroupOrg);
                client.setIdOfClientGroup(newClientGroupId);
                client.setClientRegistryVersion(DAOUtils.updateClientRegistryVersion(persistanceSession));
                persistanceSession.update(client);
                editClientsGroupsClientDTOList.add(new EditClientsGroupsClientDTO(client.getContractId()));
            }
            editClientsGroupsGroupDTO.setClients(editClientsGroupsClientDTOList);
            editClientsGroupsGroupDTOList.add(editClientsGroupsGroupDTO);
        }
        return editClientsGroupsGroupDTOList;
    }

    @Override
    public void createClient(ClientGroup clientGroup, Client client, String username) throws Exception {
        Client iacClient = DAOUtils.findClientByIacregid(persistanceSession, client.getIacRegId());
        Org clientGroupOrg = DAOUtils.findOrg(persistanceSession, clientGroup.getCompositeIdOfClientGroup().getIdOfOrg());
        client.setClientGroup(clientGroup);
        client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        client.setOrg(clientGroupOrg);
        if(client.getIacRegId() == null || client.getIacRegId().trim().isEmpty() || iacClient == null){
            String comment = "Создан пользователем " + username;
            persistanceSession.save(client.getPerson());
            persistanceSession.save(client.getContractPerson());
            client.setClientRegistryVersion(DAOUtils.updateClientRegistryVersion(persistanceSession));
            long contractId = RuntimeContext.getInstance().getClientContractIdGenerator().generate(client.getOrg().getIdOfOrg());
            client.setContractId(contractId);
            client.setContractTime(new Date());
            persistanceSession.save(client);
            ClientMigration clientMigration = new ClientMigration(client, client.getOrg());
            clientMigration.setComment(comment);
            persistanceSession.save(clientMigration);
            return;
        }
        else {
            String comment = "Изменен пользователем " + username;
            if(iacClient.getOrg().getIdOfOrg() != client.getOrg().getIdOfOrg()){
                ClientMigration clientMigration = new ClientMigration(iacClient, client.getOrg(), iacClient.getOrg());
                if(iacClient.getIdOfClientGroup() != client.getIdOfClientGroup()){
                    clientMigration.setOldGroupName(iacClient.getClientGroup().getGroupName());
                    clientMigration.setNewGroupName(client.getClientGroup().getGroupName());
                }
                clientMigration.setComment(comment);
                persistanceSession.save(clientMigration);
            }
            else if(iacClient.getIdOfClientGroup() != client.getIdOfClientGroup()){
                ClientGroupMigrationHistory clientGroupMigrationHistory = new ClientGroupMigrationHistory(iacClient.getOrg(), iacClient);
                clientGroupMigrationHistory.setComment(comment);
                clientGroupMigrationHistory.setOldGroupName(iacClient.getClientGroup().getGroupName());
                clientGroupMigrationHistory.setOldGroupId(iacClient.getIdOfClientGroup());
                clientGroupMigrationHistory.setNewGroupName(client.getClientGroup().getGroupName());
                clientGroupMigrationHistory.setOldGroupId(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup());
                persistanceSession.save(clientGroupMigrationHistory);
            }
            iacClient.setOrg(client.getOrg());
            iacClient.setClientGroup(client.getClientGroup());
            iacClient.setIdOfClientGroup(client.getIdOfClientGroup());
            Person clientPerson = iacClient.getPerson();
            clientPerson.setFirstName(client.getPerson().getFirstName());
            clientPerson.setSurname(client.getPerson().getSurname());
            clientPerson.setSecondName(client.getPerson().getSecondName());
            iacClient.setGender(client.getGender());
            iacClient.setBirthDate(client.getBirthDate());
            iacClient.setMobile(client.getMobile());
            iacClient.setPassportSeries(client.getPassportSeries());
            iacClient.setPassportNumber(client.getPassportNumber());
            iacClient.setClientRegistryVersion(DAOUtils.updateClientRegistryVersion(persistanceSession));
            persistanceSession.update(clientPerson);
            persistanceSession.update(iacClient);
            return;
        }
    }

    @Override
    public List<ClientGroup> getClientGroupsByGroupNamesAndOrgId(List<String> groupsNames, Long orgId)
            throws Exception {
        List<ClientGroup> clientGroups = new ArrayList<>();
        for(String groupName: groupsNames){
            try{
                clientGroups.add(getClientGroupByOrgIdAndGroupName(orgId, groupName));
            }
            catch (Exception e){
                logger.error("get clientGroup error: "+e.getMessage());
                continue;
            }
        }
        return clientGroups;
    }

    @Override
    public List<ClientGroup> getClientGroupsForClientManager(List<ClientGroup> clientGroups, Long idOfClient)
            throws Exception {
        List<ClientGroup> managerClientGroups = new ArrayList<>();
        for(ClientGroup clientGroup: clientGroups){
            Criteria clientGroupManagerCriteria = persistanceSession.createCriteria(ClientGroupManager.class);
            Criterion groupNameAndClientCriteria = Restrictions.and(
                    Restrictions.eq("clientGroupName", clientGroup.getGroupName()),
                    Restrictions.eq("idOfClient",idOfClient)
            );
            clientGroupManagerCriteria.add(Restrictions.and(
                    groupNameAndClientCriteria,
                    Restrictions.eq("deleted", false)
            ));
            ClientGroupManager clientGroupManager = (ClientGroupManager) clientGroupManagerCriteria.uniqueResult();
            if(clientGroupManager != null){
                managerClientGroups.add(clientGroup);
            }
        }
        return managerClientGroups;
    }

    @Override
    public List<PlanOrderClientDTO> getClientsForGroupAndOrgByCategoryDiscount(ClientGroup clientGroup, Long orgId,
            CategoryDiscountEnumType categoryDiscountEnumType) throws Exception {
        List<PlanOrderClientDTO> clientDTOList = new ArrayList<>();
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, orgId);
        friendlyOrgs.add(DAOUtils.findOrg(persistanceSession, orgId));
        Query query = persistanceSession.createQuery("select distinct c from Client c "
                + "join fetch c.clientGroup "
                + "join fetch c.person "
                + "left join fetch c.categoriesInternal as ci "
                + "left join fetch ci.discountRulesInternal as dr "
                + "left join fetch dr.categoryOrgsInternal "
                + "where c.org.idOfOrg in (:idOfOrgList) and upper(c.clientGroup.groupName)=upper(:clientGroupName) ");
        query.setParameterList("idOfOrgList", getOrgIds(friendlyOrgs));
        query.setParameter("clientGroupName", clientGroup.getGroupName().toUpperCase());
        List<Client> clients = query.list();
        for(Client client: clients){
            PlanOrderClientDTO planOrderClientDTO = new PlanOrderClientDTO(client);
            List<CategoryDiscount> filteredCategoryDiscounts = new ArrayList<>();
            for(CategoryDiscount categoryDiscount: client.getCategories()){
                if(categoryDiscount.getCategoryType().getValue().equals(categoryDiscountEnumType.getValue())){
                    filteredCategoryDiscounts.add(categoryDiscount);
                }
            }
            if(!filteredCategoryDiscounts.isEmpty()){
                planOrderClientDTO.setFilteredClientCategoryDiscounts(filteredCategoryDiscounts);
                clientDTOList.add(planOrderClientDTO);
            }
        }
        return clientDTOList;
    }

    @Override
    public List<PlanOrderClientDTO> setEnterEventsForClients(List<PlanOrderClientDTO> clients, Date planDate)
            throws Exception {
        List<Integer> enterPassDirections = Arrays.asList(EnterEvent.ENTRY, EnterEvent.RE_ENTRY, EnterEvent.DETECTED_INSIDE,
                EnterEvent.CHECKED_BY_TEACHER_EXT, EnterEvent.CHECKED_BY_TEACHER_INT);
        List<Integer> exitPassDirections = Arrays.asList(EnterEvent.DIRECTION_EXIT, EnterEvent.RE_EXIT);
        Date startDate = getDateWithAddDay(planDate, 0);
        Date endDate = getDateWithAddDay(startDate, 1);
        Query queryEvent = persistanceSession.createQuery("from EnterEvent "
                + "where client=:client "
                + "and evtDateTime>=:startDate and evtDateTime<:endDate "
                + "and passDirection in (:passDirectionsList) "
                + "order by evtDateTime desc");
        queryEvent.setParameter("startDate", startDate);
        queryEvent.setParameter("endDate", endDate);
        for(PlanOrderClientDTO clientDTO: clients){
            queryEvent.setParameter("client", clientDTO.getClient());
            queryEvent.setParameterList("passDirectionsList", enterPassDirections);
            queryEvent.setMaxResults(1);
            EnterEvent enterEvent = (EnterEvent) queryEvent.uniqueResult();
            clientDTO.setEnterEvent(enterEvent);
            queryEvent.setParameterList("passDirectionsList", exitPassDirections);
            queryEvent.setMaxResults(1);
            EnterEvent exitEvent = (EnterEvent) queryEvent.uniqueResult();
            clientDTO.setExitEvent(exitEvent);
        }
        return clients;
    }

    @Override
    public List<PlanOrderClientDTO> setComplexesForClients(List<PlanOrderClientDTO> clients, Date planDate, Long orgId) throws Exception {
        Org org = DAOUtils.findOrg(persistanceSession, orgId);
        if(org == null)
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
        Date startDate = getDateWithAddDay(planDate, 0);
        Date endDate = getDateWithAddDay(startDate, 1);
        Query complexInfoQuery = persistanceSession.createQuery("from ComplexInfo "
                + "where org.idOfOrg=:orgId "
                + "and menuDate>=:startDate and menuDate<:endDate "
                + "and modeFree=1 "
                + "and idOfComplex in (:complexIds) ");
        complexInfoQuery.setParameter("orgId", orgId);
        complexInfoQuery.setParameter("startDate", startDate);
        complexInfoQuery.setParameter("endDate", endDate);
        for(PlanOrderClientDTO client: clients){
            List<ClientComplexDTO> clientComplexList = new ArrayList<>();
            HashMap<CategoryDiscount, List<DiscountRule>> filteredDiscountRulesMap = new HashMap<>();
            List<DiscountRule> clientFilteredDiscountRulesList = new ArrayList<>();
            for(CategoryDiscount categoryDiscount: client.getFilteredClientCategoryDiscounts()){
                if(categoryDiscount.getDiscountsRules().isEmpty())
                    continue;
                for(DiscountRule discountRule: categoryDiscount.getDiscountsRules()){
                    if(discountRule.getCategoryOrgs() != null && !discountRule.getCategoryOrgs().isEmpty()
                            && !orgHasCategoryOrg(org.getCategories(), discountRule.getCategoryOrgs())){
                        continue;
                    }
                    if(clientFilteredDiscountRulesList.isEmpty()){
                        clientFilteredDiscountRulesList.add(discountRule);
                        filteredDiscountRulesMap.put(categoryDiscount, new ArrayList<DiscountRule>());
                        filteredDiscountRulesMap.get(categoryDiscount).add(discountRule);
                    }
                    else {
                        for(DiscountRule filteredDiscountRule: clientFilteredDiscountRulesList){
                            if(filteredDiscountRule.getPriority() < discountRule.getPriority()){
                                clientFilteredDiscountRulesList.clear();
                                clientFilteredDiscountRulesList.add(discountRule);
                                filteredDiscountRulesMap.clear();
                                filteredDiscountRulesMap.put(categoryDiscount, new ArrayList<DiscountRule>());
                                filteredDiscountRulesMap.get(categoryDiscount).add(discountRule);
                                break;
                            }
                            if(filteredDiscountRule.getPriority() == discountRule.getPriority()){
                                clientFilteredDiscountRulesList.add(discountRule);
                                if(filteredDiscountRulesMap.containsKey(categoryDiscount))
                                    filteredDiscountRulesMap.get(categoryDiscount).add(discountRule);
                                else {
                                    filteredDiscountRulesMap.put(categoryDiscount, new ArrayList<DiscountRule>());
                                    filteredDiscountRulesMap.get(categoryDiscount).add(discountRule);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            for(Map.Entry categoryDiscountEntry: filteredDiscountRulesMap.entrySet()){
                CategoryDiscount categoryDiscount = (CategoryDiscount) categoryDiscountEntry.getKey();
                for(DiscountRule categoryDiscountRule: ((List<DiscountRule>) categoryDiscountEntry.getValue())){
                    complexInfoQuery.setParameterList("complexIds",categoryDiscountRule.getComplexIdsFromComplexMap());
                    List<ComplexInfo> complexInfoList = complexInfoQuery.list();
                    for(ComplexInfo complexInfo: complexInfoList){
                        ClientComplexDTO clientComplexDTO = new ClientComplexDTO(categoryDiscount.getCategoryName(),
                                complexInfo, null, null, null, categoryDiscountRule);
                        clientComplexList.add(clientComplexDTO);
                    }
                }
            }
            client.setComplexes(clientComplexList);
        }
        return clients;
    }

    @Override
    public List<PlanOrderClientDTO> createOrUpdatePlanOrderForClientsComplexes(List<PlanOrderClientDTO> clients,
            Date planDate, Long orgId, String groupName) throws Exception {
        Org org = DAOUtils.findOrg(persistanceSession, orgId);
        if(org == null){
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
        }
        IIdGenerator<Long> uniqueIdGenerator = OrganizationUniqueGeneratorId.getInstance(orgId);
        Date startDate = getDateWithAddDay(planDate, 0);
        Date endDate = getDateWithAddDay(startDate, 1);
        Query planOrderQuery = persistanceSession.createQuery("from PlanOrder po "
                + "where po.org.idOfOrg=:orgId "
                + "and po.client.idOfClient=:clientId "
                + "and po.complexInfo.idOfComplexInfo=:idOfComplex "
                + "and po.planDate>=:startDate and po.planDate<:endDate ");
        planOrderQuery.setParameter("orgId", orgId);
        planOrderQuery.setParameter("startDate", startDate);
        planOrderQuery.setParameter("endDate", endDate);

        for(PlanOrderClientDTO planOrderClientDTO: clients){
            if(planOrderClientDTO.getClient() == null){
                clients.remove(planOrderClientDTO);
                continue;
            }
            planOrderQuery.setParameter("clientId",planOrderClientDTO.getClient().getIdOfClient());
            for(ClientComplexDTO complexDTO: planOrderClientDTO.getComplexes()){
                planOrderQuery.setParameter("idOfComplex", complexDTO.getComplexInfo().getIdOfComplexInfo());
                planOrderQuery.setMaxResults(1);
                PlanOrder planOrder = (PlanOrder) planOrderQuery.uniqueResult();
                if(planOrder != null){
                    if(planOrder.getOrder() == null){
                        planOrder.setGroupName(groupName);
                        planOrder.setComplexName(complexDTO.getComplexName());
                        planOrder.setDiscountRule(complexDTO.getDiscountRule());
                        planOrder.setLastUpdate(new Date());
                        persistanceSession.update(planOrder);
                    }
                }
                else{
                    ComplexInfo complexInfo = (ComplexInfo) persistanceSession.get(ComplexInfo.class, complexDTO.getComplexInfo().getIdOfComplexInfo());
                    planOrder = new PlanOrder(uniqueIdGenerator.createId(), org, groupName, planOrderClientDTO.getClient(), new Date(planDate.getTime()), complexInfo,
                            complexInfo.getComplexName(),
                            null, false, null, null, complexDTO.getDiscountRule());
                    persistanceSession.save(planOrder);
                }

                if(planOrder.getOrder() == null){
                    complexDTO.setOrder(null);
                }
                else{
                    if(planOrder.getOrder().getState() == 0){
                        complexDTO.setOrder(true);
                    }
                    else if(planOrder.getOrder().getState() == 1){
                        complexDTO.setOrder(false);
                    }
                    else
                        complexDTO.setOrder(null);
                }
                if(planOrder.getUserRequestToPay() != null){
                    Person person = planOrder.getUserRequestToPay().getPerson();
                    if(person != null){
                        complexDTO.setmName(person.getFirstName());
                        complexDTO.setmSurname(person.getSurname());
                        complexDTO.setmSecondName(person.getSecondName());
                    }
                }
                complexDTO.setToPay(planOrder.getToPay());
            }
        }
        return clients;
    }

    @Override
    public List<Client> getClientsByContractIdsForOrg(List<Long> contractIds, Long idOfOrg) throws Exception {
        List<Org> orgs = DAOUtils.findFriendlyOrgs(persistanceSession, idOfOrg);
        orgs.add(DAOUtils.findOrg(persistanceSession, idOfOrg));
        List<Long> orgsIds = getOrgIds(orgs);
        Criteria clientsCriteria = persistanceSession.createCriteria(Client.class);
        clientsCriteria.add(Restrictions.and(
                Restrictions.in("contractId", contractIds),
                Restrictions.in("org.idOfOrg", orgsIds)
        ));
        return clientsCriteria.list();
    }

    @Override
    public List<Client> getClientsByGroupsAndContractIds(List<String > groupsNames, List<Long> contractIds, Long idOfOrg)
            throws Exception {
        List<Org> orgs = DAOUtils.findFriendlyOrgs(persistanceSession, idOfOrg);
        orgs.add(DAOUtils.findOrg(persistanceSession, idOfOrg));
        List<Long> orgsIds = getOrgIds(orgs);
        List<String> groupsNamesInUpperCase = new ArrayList<>();
        for(String groupName: groupsNames){
            groupsNamesInUpperCase.add(groupName.toUpperCase());
        }
        Query query = persistanceSession.createQuery("from Client c "
                + "join fetch c.clientGroup "
                + "join fetch c.person "
                + "where c.contractId in (:contractsIdsList) and c.org.idOfOrg in (:idOfOrgList) and upper(c.clientGroup.groupName) in (:nameOfGroupList)");
        query.setParameterList("contractsIdsList", contractIds);
        query.setParameterList("idOfOrgList", orgsIds);
        query.setParameterList("nameOfGroupList", groupsNamesInUpperCase);
        return query.list();
    }

    @Override
    public List<PlanOrder> getPlanOrdersWithoutOrder(Date planDate, List<Long> contractIds, String complexName)
            throws Exception {
        Date startDate = getDateWithAddDay(planDate, 0);
        Date endDate = getDateWithAddDay(startDate, 1);
        Criteria planOrdersCriteria = persistanceSession.createCriteria(PlanOrder.class).createAlias("client", "c",
                JoinType.LEFT_OUTER_JOIN).createAlias("order","o", JoinType.LEFT_OUTER_JOIN);
        planOrdersCriteria.add(Restrictions.and(
                Restrictions.and(
                        Restrictions.and(
                                Restrictions.isNull("o.compositeIdOfOrder.idOfOrder"),
                                Restrictions.eq("complexName", complexName)
                        ),
                        Restrictions.and(
                                Restrictions.ge("planDate", startDate),
                                Restrictions.lt("planDate", endDate)
                        )
                ),
                Restrictions.in("c.contractId", contractIds)
        ));
        List<PlanOrder> planOrders = planOrdersCriteria.list();
        return planOrders;
    }

    @Override
    public List<PlanOrder> setToPayForPlanOrders(List<PlanOrder> planOrders, Long idOfUser, Boolean toPay)
            throws Exception {
        User requestUser = DAOUtils.findUser(persistanceSession, idOfUser);
        Date lastUpdateDate = new Date();
        if(requestUser == null)
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        for(PlanOrder planOrder: planOrders){
            planOrder.setToPay(toPay);
            planOrder.setUserRequestToPay(requestUser);
            planOrder.setLastUpdate(lastUpdateDate);
            persistanceSession.update(planOrder);
        }
        return planOrders;
    }

    @Override
    public List<PlanOrder> getPlanOrdersByToPay(Date planDate, List<Long> contractIds, String complexName,
            Boolean toPay) throws Exception {
        Date startDate = getDateWithAddDay(planDate, 0);
        Date endDate = getDateWithAddDay(startDate, 1);
        Criteria planOrdersCriteria = persistanceSession.createCriteria(PlanOrder.class).createAlias("client", "c",
                JoinType.LEFT_OUTER_JOIN).createAlias("order","o", JoinType.LEFT_OUTER_JOIN);
        planOrdersCriteria.add(Restrictions.and(
                Restrictions.and(
                        Restrictions.and(
                                Restrictions.eq("toPay", toPay),
                                Restrictions.eq("complexName", complexName)
                        ),
                        Restrictions.and(
                                Restrictions.ge("planDate", startDate),
                                Restrictions.lt("planDate", endDate)
                        )
                ),
                Restrictions.in("c.contractId", contractIds)
        ));
        return planOrdersCriteria.list();
    }

    @Override
    public List<PlanOrder> createOrderForPlanOrders(List<PlanOrder> planOrders, Boolean orderState, Long idOfUser)
            throws Exception {
        User requestUser = DAOUtils.findUser(persistanceSession, idOfUser);
        String orderComment = "Льготный план.  Карта не указана Баланс счета после оплаты: 0,00 р.";
        Date lastUpdateDate = new Date();
        if(requestUser == null)
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        for(PlanOrder planOrder: planOrders){
            if(planOrder.getOrg() == null)
                continue;
            Order planOrderOrder = null;
            List<OrderDetail> orderDetails = new ArrayList<>();
            if(orderState){
                if(planOrder.getOrder() == null || planOrder.getOrder().getState() == Order.STATE_CANCELED){
                    Long orgId = planOrder.getOrg().getIdOfOrg();
                    Long idOfRule = null;
                    if(planOrder.getDiscountRule() != null){
                        idOfRule = planOrder.getDiscountRule().getIdOfRule();
                    }
                    IIdGenerator<Long> orderIdGenerator = OrganizationUniqueGeneratorId.getInstance(orgId);
                    CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(planOrder.getOrg().getIdOfOrg(), orderIdGenerator.createId());
                    ComplexInfo planOrderComplex = planOrder.getComplexInfo();
                    if(planOrderComplex == null)
                        continue;
                    planOrderOrder = new Order(compositeIdOfOrder, idOfUser, planOrderComplex.getCurrentPrice(),
                            0L,  0, 0, new Date(), new Date(), 0, 0,
                            orderComment, planOrder.getClient(),null,null,null,
                            planOrder.getOrg().getDefaultSupplier(), OrderTypeEnumType.REDUCED_PRICE_PLAN, null);
                    List<ComplexInfoDetail> complexInfoDetails = getComplexInfoDetails(planOrderComplex);
                    String complexItemCode = null;
                    OrderDetailFRationType orderDetailFRationType = null;
                    if(planOrderComplex.getGood()!= null){
                        complexItemCode = planOrderComplex.getGood().getGoodsCode();
                        if(planOrderComplex.getGood().getGoodType() != null){
                            orderDetailFRationType = OrderDetailFRationType.fromInteger(planOrderComplex.getGood().getGoodType().getCode());
                        }
                    }
                    if(complexInfoDetails.isEmpty()){
                        CompositeIdOfOrderDetail compositeIdOfOrderDetail = new CompositeIdOfOrderDetail(orgId, orderIdGenerator.createId());
                        OrderDetail complexOrderDetail = new OrderDetail(compositeIdOfOrderDetail, planOrderOrder.getCompositeIdOfOrder().getIdOfOrder(),
                                1, planOrderComplex.getCurrentPrice(), planOrderComplex.getCurrentPrice(), 0,
                                planOrderComplex.getComplexName(), "","", 0, "",
                                50, null, null,false, complexItemCode, idOfRule, orderDetailFRationType);
                        complexOrderDetail.setGood(planOrderComplex.getGood());
                        orderDetails.add(complexOrderDetail);
                        if(planOrderComplex.getGood()!= null){
                            CompositeIdOfOrderDetail compositeIdOfOrderDetailForGood = new CompositeIdOfOrderDetail(orgId, orderIdGenerator.createId());
                            Good good = planOrderComplex.getGood();
                            OrderDetail goodOrderDetail = new OrderDetail(compositeIdOfOrderDetailForGood, planOrderOrder.getCompositeIdOfOrder().getIdOfOrder(),
                                    1, 0,0,0,"","","",0,
                                    "",150,null,null, false,
                                    good.getGoodsCode(), null, orderDetailFRationType);
                            goodOrderDetail.setGood(good);
                            orderDetails.add(goodOrderDetail);
                        }
                    }
                    else {
                        MenuDetail complexMenuDetail = complexInfoDetails.get(0).getMenuDetail();
                        CompositeIdOfOrderDetail compositeIdOfOrderDetail = new CompositeIdOfOrderDetail(orgId, orderIdGenerator.createId());
                        OrderDetail complexOrderDetail = new OrderDetail(compositeIdOfOrderDetail, planOrderOrder.getCompositeIdOfOrder().getIdOfOrder(),
                                planOrderComplex.getCurrentPrice(), planOrderComplex.getCurrentPrice(), 0, 0,
                                planOrderComplex.getComplexName(), (complexMenuDetail.getMenuPath().length() > 32 ?
                                complexMenuDetail.getMenuPath().substring(0,32) : complexMenuDetail.getMenuPath()), complexMenuDetail.getGroupName(),
                                complexMenuDetail.getMenuOrigin(), complexMenuDetail.getMenuDetailOutput(), 50,
                                complexMenuDetail.getIdOfMenuFromSync(), null, false,
                                complexItemCode, idOfRule,orderDetailFRationType);
                        complexOrderDetail.setGood(planOrderComplex.getGood());
                        orderDetails.add(complexOrderDetail);
                        for(ComplexInfoDetail complexInfoDetail: complexInfoDetails){
                            MenuDetail complexInfoMenuDetail = complexInfoDetail.getMenuDetail();
                            Good menuGood = getGoodById(complexInfoMenuDetail.getIdOfGood());
                            if(menuGood != null){
                                OrderDetailFRationType menuGoodFRationType = null;
                                if(menuGood.getGoodType() != null){
                                    menuGoodFRationType = OrderDetailFRationType.fromInteger(menuGood.getGoodType().getCode());
                                }
                                CompositeIdOfOrderDetail compositeIdOfOrderDetailForGood = new CompositeIdOfOrderDetail(orgId, orderIdGenerator.createId());
                                OrderDetail goodOrderDetail = new OrderDetail(compositeIdOfOrderDetailForGood, planOrderOrder.getCompositeIdOfOrder().getIdOfOrder(),
                                        0, 0, 0, 0,
                                        menuGood.getFullName(), complexInfoMenuDetail.getMenuPath().length() > 32 ?
                                        complexInfoMenuDetail.getMenuPath().substring(0, 32): complexInfoMenuDetail.getMenuPath(),
                                        complexInfoMenuDetail.getGroupName(),
                                        complexInfoMenuDetail.getMenuOrigin(), complexInfoMenuDetail.getMenuDetailOutput(), 50,
                                        complexInfoMenuDetail.getIdOfMenuFromSync(), null, false,
                                        menuGood.getGoodsCode(), null, menuGoodFRationType);
                                goodOrderDetail.setGood(menuGood);
                                orderDetails.add(goodOrderDetail);
                            }
                        }
                    }

                    if(planOrderOrder!= null){
                        persistanceSession.save(planOrderOrder);
                        for(OrderDetail orderDetail: orderDetails){
                            persistanceSession.save(orderDetail);
                        }
                        planOrder.setIdOfOrder(planOrderOrder.getCompositeIdOfOrder().getIdOfOrder());
                        planOrder.setOrder(planOrderOrder);
                    }
                    planOrder.setUserConfirmToPay(requestUser);
                    planOrder.setLastUpdate(lastUpdateDate);
                    persistanceSession.update(planOrder);
                }
                else {
                    continue;
                }
            }
            else{
                if(planOrder.getOrder() != null && planOrder.getOrder().getState() == Order.STATE_COMMITED) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String commentForCancelledOrder = "(отменен: " + simpleDateFormat.format(new Date()) + ")";
                    Order currentPlanOrderOrder = planOrder.getOrder();
                    currentPlanOrderOrder.setState(Order.STATE_CANCELED);
                    currentPlanOrderOrder.setComments(commentForCancelledOrder);
                    persistanceSession.update(currentPlanOrderOrder);
                    planOrder.setUserConfirmToPay(requestUser);
                    planOrder.setLastUpdate(lastUpdateDate);
                    persistanceSession.update(planOrder);
                }
            }
        }
        return planOrders;
    }

    private Date getDateWithAddDay(Date date, int days){
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTimeInMillis(date.getTime());
        dateCal.set(Calendar.HOUR_OF_DAY, 0);
        dateCal.set(Calendar.MINUTE, 0);
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND,0);
        dateCal.add(Calendar.DATE, days);
        return dateCal.getTime();

    }

    private List<ComplexInfoDetail> getComplexInfoDetails(ComplexInfo complexInfo) throws Exception{
        Criteria complexInfoDetailsCriteria = persistanceSession.createCriteria(ComplexInfoDetail.class);
        complexInfoDetailsCriteria.add(Restrictions.eq("complexInfo.idOfComplexInfo", complexInfo.getIdOfComplexInfo()));
        return complexInfoDetailsCriteria.list();
    }

    private Good getGoodById(Long goodId) throws Exception{
        return (Good) persistanceSession.get(Good.class, goodId);
    }

    private HashMap<String, List<Client>> getClientsGroupByClientGroup(List<Client> clients){
        HashMap<String, List<Client>> orderedClients = new LinkedHashMap<>();
        for(Client client: clients){
            if(client.getClientGroup() == null || client.getClientGroup().getGroupName() == null
                    || client.getClientGroup().getGroupName().isEmpty())
                continue;
            String key = client.getClientGroup().getGroupName();
            if(orderedClients.containsKey(key))
                orderedClients.get(key).add(client);
            else {
                List<Client> hashMapClients = new LinkedList<>();
                hashMapClients.add(client);
                orderedClients.put(key, hashMapClients);
            }
        }
        return orderedClients;
    }

    private List<Client> getClientsForStrictMode(ClientGroup clientGroup, List<Client> clients) {
        List<Client> processedClients = new ArrayList<>();
        boolean newClientGroupIsNotPredefined = isGroupNotPredefined(clientGroup);
        Long newClientGroupId = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
        for(Client client: clients){
            if(client.getClientGroup() == null)
                continue;
            Long clientClientGroupId = client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup();
            if(newClientGroupIsNotPredefined){
                if(isGroupNotPredefined(client.getClientGroup()))
                    processedClients.add(client);
                else if(clientClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_DISPLACED.getValue().longValue()
                        || clientClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_DELETED.getValue().longValue()
                        || clientClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_LEAVING.getValue().longValue()) {
                 processedClients.add(client);
                }
            }
            if(!newClientGroupIsNotPredefined){
                if(!isGroupNotPredefined(client.getClientGroup())){
                    if(clientClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_DISPLACED.getValue()
                            && (newClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_LEAVING.getValue()
                            || newClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_DELETED.getValue())){
                        processedClients.add(client);
                        continue;
                    }
                    if(clientClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_LEAVING.getValue()
                            || clientClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_DELETED.getValue()) {
                        processedClients.add(client);
                        continue;
                    }
                    if(newClientGroupId.longValue() != ClientGroup.Predefined.CLIENT_DISPLACED.getValue()){
                        processedClients.add(client);
                        continue;
                    }
                }
                else {
                    if(newClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_DISPLACED.getValue().longValue()
                            || newClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_DELETED.getValue().longValue()
                            || newClientGroupId.longValue() == ClientGroup.Predefined.CLIENT_LEAVING.getValue().longValue()){
                        processedClients.add(client);
                        continue;
                    }
                }
            }
        }
        return processedClients;
    }


    @Override
    public Long getIdOfOrgFromUser(String username) throws Exception {
        User user = DAOUtils.findUser(persistanceSession, username);
        if(user == null || user.isBlocked() || user.getDeletedState())
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        if(user.getOrg() == null && user.getClient() == null)
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
        if(user.getOrg() == null && user.getClient().getOrg() == null)
            throw new RequestProcessingException(GroupManagementErrors.ORG_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.ORG_NOT_FOUND.getErrorMessage());
        if(user.getClient() != null){
            return user.getClient().getOrg().getIdOfOrg();
        }
        else {
            return user.getOrg().getIdOfOrg();
        }
    }

    @Override
    public List<FriendlyOrgDTO> getFriendlyOrgs(Long orgId) throws Exception {
        List<FriendlyOrgDTO> friendlyOrgDTOList = new ArrayList<>();
        List<Org> friendlyOrgs = DAOUtils.findFriendlyOrgs(persistanceSession, orgId);
        friendlyOrgs.add(DAOUtils.findOrg(persistanceSession, orgId));
        for(Org org: friendlyOrgs){
            friendlyOrgDTOList.add(new FriendlyOrgDTO(org));
        }
        return friendlyOrgDTOList;
    }

    @Override
    public List<GroupNameDTO> getManagerGroups(Long contractId) throws Exception {
        Client client = DAOUtils.findClientByContractId(persistanceSession, contractId);
        if(client == null)
            throw new RequestProcessingException(GroupManagementErrors.EMPLOYEE_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.EMPLOYEE_NOT_FOUND.getErrorMessage());
        Criteria clientGroupManagersCriteria = persistanceSession.createCriteria(ClientGroupManager.class);
        clientGroupManagersCriteria.add(Restrictions.and(
                Restrictions.eq("idOfClient",client.getIdOfClient()),
                Restrictions.eq("deleted", false)
        ));
        List<ClientGroupManager> clientGroupsManager = clientGroupManagersCriteria.list();
        List<GroupNameDTO> managerGroups = new LinkedList<>();
        for(ClientGroupManager clientGroupManager: clientGroupsManager){
            managerGroups.add(new GroupNameDTO(clientGroupManager.getClientGroupName()));
        }
        return managerGroups;
    }

    private CategoryDiscount getCategoryDiscount(Long idOfCategoryDiscount) throws Exception {
        CategoryDiscount categoryDiscount = (CategoryDiscount)persistanceSession.get(CategoryDiscount.class, idOfCategoryDiscount);
        if (categoryDiscount == null) {
            throw new RequestProcessingException(GroupManagementErrors.DISCOUNT_NOT_FOUND);
        }
        if (categoryDiscount.getBlockedToChange()) {
            throw new RequestProcessingException(GroupManagementErrors.DISCOUNT_NOT_MODIFY);
        }
        return categoryDiscount;
    }

    private ResponseDiscountClientsItem processDiscountClient(List<Long> allOrgs, Long contractId, Boolean status,
            CategoryDiscount categoryDiscount, ResponseDiscounts availableDiscounts) {
        Client client = DAOUtils.findClientByContractId(persistanceSession, contractId);
        if (client == null || !allOrgs.contains(client.getOrg().getIdOfOrg()) || !client.isStudent()) {
            return new ResponseDiscountClientsItem(contractId, ResponseDiscountClientsItem.CODE_CLIENT_NOT_FOUND,
                    ResponseDiscountClientsItem.MESSAGE_CLIENT_NOT_FOUND);
        }
        if (status) {
            //добавление льготы
            if (client.getCategories().contains(categoryDiscount)) {
                return new ResponseDiscountClientsItem(contractId, ResponseDiscountClientsItem.CODE_OK,
                        ResponseDiscountClientsItem.MESSAGE_OK);
            } else {
                if (isProperDiscount(availableDiscounts, categoryDiscount.getIdOfCategoryDiscount())) {
                    try {
                        DiscountManager.addDiscount(persistanceSession, client, categoryDiscount,
                                DiscountChangeHistory.MODIFY_IN_SERVICE);
                        return new ResponseDiscountClientsItem(contractId, ResponseDiscountClientsItem.CODE_OK,
                                ResponseDiscountClientsItem.MESSAGE_OK);
                    } catch (Exception e) {
                        logger.error("Error in add discount processDiscountClientsList: ", e);
                        return new ResponseDiscountClientsItem(contractId, ResponseDiscountClientsItem.CODE_INTERNAL_ERROR,
                                ResponseDiscountClientsItem.MESSAGE_INTERNAL_ERROR);
                    }
                } else {
                    return new ResponseDiscountClientsItem(contractId, ResponseDiscountClientsItem.CODE_DISCOUNT_NOT_FOUND,
                            ResponseDiscountClientsItem.MESSAGE_DISCOUNT_NOT_FOUND);
                }
            }
        } else {
            //удаление льготы
            try {
                if (client.getCategories().contains(categoryDiscount)) {
                    DiscountManager.deleteOneDiscount(persistanceSession, client, categoryDiscount);
                }
                return new ResponseDiscountClientsItem(contractId, ResponseDiscountClientsItem.CODE_OK,
                        ResponseDiscountClientsItem.MESSAGE_OK);
            } catch (Exception e) {
                logger.error("Error in delete discount processDiscountClientsList: ", e);
                return new ResponseDiscountClientsItem(contractId, ResponseDiscountClientsItem.CODE_INTERNAL_ERROR,
                        ResponseDiscountClientsItem.MESSAGE_INTERNAL_ERROR);
            }
        }
    }

    private boolean isProperDiscount(ResponseDiscounts availableDiscounts, Long discountId) {
        for (ResponseDiscountItem item : availableDiscounts.getItems()) {
            if (item.getIdOfCategoryDiscount().equals(discountId)) return true;
        }
        return false;
    }

    private ClientGroup findClientGroup(GroupNamesToOrgs gnto, List<ClientGroup> groups, Long idOfOrg) {
        if (gnto == null || gnto.getIdOfOrg() == null) {
            for (ClientGroup clientGroup : groups) {
                if (clientGroup.getCompositeIdOfClientGroup().getIdOfOrg().equals(idOfOrg)) return clientGroup;
            }
            return null;
        }
        for (ClientGroup clientGroup : groups) {
            if (clientGroup.getCompositeIdOfClientGroup().getIdOfOrg().equals(gnto.getIdOfOrg())) return clientGroup;
        }
        return null;
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

    private boolean orgIsFriendly(long orgId, long friendlyOrgId) throws Exception{
        List<Org> orgs = DAOUtils.findFriendlyOrgs(persistanceSession,orgId);
        for (Org org: orgs){
            if(org.getIdOfOrg() != null){
                if(org.getIdOfOrg().longValue() == friendlyOrgId)
                    return true;
            }
        }
        return false;
    }

    private boolean orgIsFriendly(long orgId, List<Org> friedlyOrgs){
        for (Org org: friedlyOrgs){
            if(org.getIdOfOrg() != null){
                if(org.getIdOfOrg().longValue() == orgId)
                    return true;
            }
        }
        return false;
    }

    private List<Long> getOrgIds(List<Org> orgs){
        List<Long> orgIds = new ArrayList<>();
        for(Org org: orgs){
            if(org.getIdOfOrg() != null)
                orgIds.add(org.getIdOfOrg());
        }
        return orgIds;
    }

    private boolean orgHasCategoryOrg(Set<CategoryOrg> firstComparable, Set<CategoryOrg> secondComparable){
        for(CategoryOrg categoryOrg: firstComparable){
            for(CategoryOrg comparableCategoryOrg: secondComparable){
                if(categoryOrg.getIdOfCategoryOrg() == comparableCategoryOrg.getIdOfCategoryOrg())
                    return true;
            }
        }
        return false;
    }

}
