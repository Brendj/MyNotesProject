/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service.MoveClientsCommand;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.MiddleGroupResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
class CreateOrUpdateMiddleGroupCommand extends BaseMiddleGroupCommand {

    private final Logger logger = LoggerFactory.getLogger(CreateOrUpdateMiddleGroupCommand.class);
    private final RuntimeContext runtimeContext;
    private final MoveClientsCommand moveClientsCommand;
    private static final int DUPLICATE_GROUP_NAME = 409, GROUP_NOT_FOUND = 404, GROUP_NOT_PREDEFINED = 410;

    @Autowired
    public CreateOrUpdateMiddleGroupCommand(RuntimeContext runtimeContext, MoveClientsCommand moveClientsCommand) {
        this.runtimeContext = runtimeContext;
        this.moveClientsCommand = moveClientsCommand;
    }

    public MiddleGroupResponse createGroup(Long idOfGroupClients, Long idOfOrg, MiddleGroupRequest request, User user) {
        MiddleGroupResponse response;
        Session session = null;
        Transaction transaction = null;
        try {
            boolean isPredefined = isPredefined(idOfGroupClients);
            if (!isPredefined) {
                throw WebApplicationException.badRequest(GROUP_NOT_PREDEFINED,
                        String.format("Group with ID='%d' not predefined", idOfGroupClients));
            }

            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Long mainBuildingOrgId = getMainBuilding(session, idOfOrg);
            GroupNamesToOrgs groupNamesToOrgs = foundMiddleGroupByName(session, mainBuildingOrgId, request);
            if (groupNamesToOrgs == null) {
                GroupNamesToOrgs subGroup = createSubGroup(request, mainBuildingOrgId, session);
                response = MiddleGroupResponse.from(subGroup);
            } else {
                throw WebApplicationException.badRequest(DUPLICATE_GROUP_NAME,
                        String.format("Подгруппа '%s' для группы '%s' уже существует", request.getName(),
                                request.getParentGroupName()));
            }
            session.flush();
            transaction.commit();
            transaction = null;
            return response;
        } catch (WebApplicationException wex) {
            throw wex;
        } catch (Exception e) {
            logger.error("Error in create middle group, ", e);
            throw new WebApplicationException(String.format("Ошибка при создании подгруппы '%s'", request.getName()), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private boolean isPredefined(Long idOfGroupClients) {
        ClientGroup.Predefined predefinedGroup = ClientGroup.Predefined.parse(idOfGroupClients);
        return predefinedGroup != null;
    }

    public MiddleGroupResponse updateGroup(Long idOfGroupClients, Long idOfOrg, MiddleGroupRequest request, User user) {
        MiddleGroupResponse response;
        Session session = null;
        Transaction transaction = null;
        try {
            boolean isPredefined = isPredefined(idOfGroupClients);
            if (!isPredefined) {
                throw WebApplicationException.badRequest(ResponseCodes.BAD_REQUEST_ERROR.getCode(),
                        String.format("Group with ID='%d' not predefined", idOfGroupClients));
            }
            if (StringUtils.isEmpty(request.getName())) {
                throw WebApplicationException.badRequest(ResponseCodes.BAD_REQUEST_ERROR.getCode(),
                        "Is empty middle group name");
            }
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            GroupNamesToOrgs foundCurrentGroup = foundMiddleGroupById(session, request.getId());
            if (foundCurrentGroup != null) {
                Long mainBuildingOrgId = getMainBuilding(session, idOfOrg);
                GroupNamesToOrgs foundGroupWithNewName = foundMiddleGroupByName(session, mainBuildingOrgId, request);
                if (foundGroupWithNewName == null || foundGroupWithNewName.getIdOfGroupNameToOrg()
                        .equals(foundCurrentGroup.getIdOfGroupNameToOrg())) {
                    ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
                    clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.ARM);
                    clientGuardianHistory.setReason("Rest метод subgroups (АРМ администратора)");
                    clientGuardianHistory.setAction("Обновить подгруппу клиента");
                    clientGuardianHistory.setUser(user);
                    clientGuardianHistory.setChangeDate(new Date());
                    GroupNamesToOrgs middleGroup = updateMiddleGroup(request, foundCurrentGroup, user, session, clientGuardianHistory);
                    response = MiddleGroupResponse.from(middleGroup);
                } else {
                    throw WebApplicationException.badRequest(DUPLICATE_GROUP_NAME,
                            String.format("Подгруппа '%s' для группы '%s' уже существует", request.getName(),
                                    request.getParentGroupName()));
                }
            } else {
                throw WebApplicationException.notFound(GROUP_NOT_FOUND,
                        String.format("Подгруппа '%s' для группы '%s' не найдена", request.getName(),
                                request.getParentGroupName()));
            }

            session.flush();
            transaction.commit();
            transaction = null;
            return response;
        } catch (WebApplicationException wex) {
            throw wex;
        } catch (Exception e) {
            logger.error("Error in update middle group, ", e);
            throw new WebApplicationException(String.format("Ошибка при обновлении подгруппы '%s'", request.getName()),
                    e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private GroupNamesToOrgs updateMiddleGroup(MiddleGroupRequest request, GroupNamesToOrgs groupNamesToOrgs, User user,
            Session session, ClientGuardianHistory clientGuardianHistory) throws Exception {
        Long oldBindingOrg = groupNamesToOrgs.getIdOfOrg();
        List<Client> clientListWithMiddleGroup = getClientListWithMiddleGroup(session, groupNamesToOrgs);
        // обновить саму подгруппу
        groupNamesToOrgs.setVersion(getNextVersion(session));
        groupNamesToOrgs.setGroupName(request.getName());
        groupNamesToOrgs.setIdOfOrg(request.getBindingOrgId());
        groupNamesToOrgs.setParentGroupName(request.getParentGroupName());
        session.update(groupNamesToOrgs);

        //обновление клиентов
        if (!clientListWithMiddleGroup.isEmpty()) {
            if (Objects.equals(oldBindingOrg, request.getBindingOrgId())) {
                // если привзяка такая же, то простое изменение имени подгруппы для клиента
                updateMiddleGroupForClients(request, clientListWithMiddleGroup, session);
            } else {
                // смена группы, подгруппы для клиента
                updateGroupAndMiddleGroupForClients(request, clientListWithMiddleGroup,
                        groupNamesToOrgs.getIdOfGroupNameToOrg(), user, session, clientGuardianHistory);
            }
        }
        return groupNamesToOrgs;
    }

    private void updateGroupAndMiddleGroupForClients(MiddleGroupRequest request, List<Client> clientListWithMiddleGroup,
            Long idOfGroupNamesToOrg, User user, Session session, ClientGuardianHistory clientGuardianHistory) throws Exception {
        if (!clientListWithMiddleGroup.isEmpty()) {
            ClientGroup foundNewGroup = DAOUtils
                    .findClientGroupByGroupNameAndIdOfOrg(session, request.getBindingOrgId(),
                            request.getParentGroupName());
            if (foundNewGroup == null) {
                // создаем группу для успешного перемещения туда клиентов
                foundNewGroup = DAOUtils
                        .createClientGroup(session, request.getBindingOrgId(), request.getParentGroupName());
            }
            long version = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            for (Client client : clientListWithMiddleGroup) {
                ClientUpdateItem clientUpdateItem = ClientUpdateItem.from(client, foundNewGroup);
                clientUpdateItem.setIdOfMiddleGroup(idOfGroupNamesToOrg);

                String error = moveClientsCommand
                        .executeMoveOrError(session, clientUpdateItem, client, foundNewGroup, version, user, true, clientGuardianHistory);
                if (StringUtils.isNotEmpty(error)) {
                    throw new WebApplicationException(
                            String.format("Ошибка при обновлении клиентов для подгруппы с ID='%d'",
                                    idOfGroupNamesToOrg));
                }
            }
        }
    }

    private void updateMiddleGroupForClients(MiddleGroupRequest request, List<Client> clientListWithMiddleGroup,
            Session session) throws Exception {
        if (clientListWithMiddleGroup.isEmpty()) {
            return;
        }
        long version = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
        for (Client client : clientListWithMiddleGroup) {
            setMiddleGroupForClient(session, client, request.getName(), version);
        }
    }

    private GroupNamesToOrgs foundMiddleGroupByName(Session session, Long mainBuildingOrgId,
            MiddleGroupRequest request) {
        List<GroupNamesToOrgs> allGroupsAndSubGroupsBindingToCurrentOrg = DAOUtils
                .getAllGroupnamesToOrgsByIdOfMainOrg(session, mainBuildingOrgId);
        for (GroupNamesToOrgs item : allGroupsAndSubGroupsBindingToCurrentOrg) {
            if (item.getIsMiddleGroup() != null && item.getIsMiddleGroup() && item.getGroupName()
                    .equalsIgnoreCase(request.getName()) && item.getParentGroupName() != null && item
                    .getParentGroupName().equalsIgnoreCase(request.getParentGroupName())) {
                return item;
            }
        }
        return null;
    }

    private Long getMainBuilding(Session session, Long parentGroupOrgId) {
        Org org = (Org) session.get(Org.class, parentGroupOrgId);
        for (Org friendlyOrg : org.getFriendlyOrg()) {
            if (friendlyOrg.isMainBuilding()) {
                return friendlyOrg.getIdOfOrg();
            }
        }
        return org.getIdOfOrg();
    }

    private GroupNamesToOrgs createSubGroup(MiddleGroupRequest request, Long mainBuildingOrgId,
            Session persistenceSession) {
        long version = getNextVersion(persistenceSession);
        GroupNamesToOrgs subgroup = new GroupNamesToOrgs(mainBuildingOrgId, request.getBindingOrgId(), 1,
                request.getName(), version, request.getParentGroupName(), true);
        persistenceSession.save(subgroup);
        return subgroup;
    }


}
