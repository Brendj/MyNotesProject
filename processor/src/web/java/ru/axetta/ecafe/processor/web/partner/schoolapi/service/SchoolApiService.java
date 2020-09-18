/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.*;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.GroupManagementErrors;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.RequestProcessingException;

import org.apache.commons.lang.NullArgumentException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            long contractId = RuntimeContext.getInstance().getClientContractIdGenerator().generateTransactionFree(client.getOrg().getIdOfOrg());
            client.setContractId(contractId);
            client.setContractTime(new Date());
            persistanceSession.save(client);
            RuntimeContext.getInstance().getClientContractIdGenerator().updateUsedContractId(persistanceSession, contractId, client.getOrg().getIdOfOrg());
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

}
