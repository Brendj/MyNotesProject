/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
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

import java.util.List;

@Component
class CreateMiddleGroupCommand extends BaseMiddleGroupCommand {
    private final Logger logger = LoggerFactory.getLogger(CreateMiddleGroupCommand.class);
    private final RuntimeContext runtimeContext;
    private static final int DUPLICATE_GROUP_NAME = 409, GROUP_NOT_FOUND = 404, GROUP_NOT_PREDEFINED = 410;

    @Autowired
    public CreateMiddleGroupCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public MiddleGroupResponse createGroup(Long idOfGroupClients, Long idOfOrg, MiddleGroupRequest request) {
        MiddleGroupResponse response;
        Session session = null;
        Transaction transaction = null;
        try {
            boolean isPredefined = isPredefined(idOfGroupClients);
            if (!isPredefined) {
                throw new WebApplicationException(GROUP_NOT_PREDEFINED, String.format("Group with ID='%d' not predefined", idOfGroupClients));
            }

            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Long mainBuildingOrgId = getMainBuilding(session, idOfOrg);
            GroupNamesToOrgs groupNamesToOrgs = foundMiddleGroupByName(session, mainBuildingOrgId, request);
            if (groupNamesToOrgs == null) {
                GroupNamesToOrgs subGroup = createSubGroup(request, mainBuildingOrgId, session);
                response = MiddleGroupResponse.from(subGroup);
            } else {
                throw new WebApplicationException(DUPLICATE_GROUP_NAME,
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
            throw new WebApplicationException(String.format("Ошибка при создании подгруппы '%s'", request.getName()),
                    e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private boolean isPredefined(Long idOfGroupClients) {
        ClientGroup.Predefined predefinedGroup = ClientGroup.Predefined.parse(idOfGroupClients);
        return predefinedGroup != null;
    }

    public MiddleGroupResponse updateGroup(Long idOfGroupClients, Long idOfOrg, MiddleGroupRequest request) {
        MiddleGroupResponse response;
        Session session = null;
        Transaction transaction = null;
        try {
            boolean isPredefined = isPredefined(idOfGroupClients);
            if (!isPredefined) {
                throw new WebApplicationException(ResponseCodes.BAD_REQUEST_ERROR.getCode(), String.format("Group with ID='%d' not predefined", idOfGroupClients));
            }
            if (StringUtils.isEmpty(request.getName())){
                throw new WebApplicationException(ResponseCodes.BAD_REQUEST_ERROR.getCode(), "Is empty middle group name");
            }
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            GroupNamesToOrgs foundCurrentGroup = foundMiddleGroupById(session, request.getId());
            if (foundCurrentGroup != null) {
                Long mainBuildingOrgId = getMainBuilding(session, idOfOrg);
                GroupNamesToOrgs foundGroupWithNewName = foundMiddleGroupByName(session, mainBuildingOrgId, request);
                if (foundGroupWithNewName == null || foundGroupWithNewName.getIdOfGroupNameToOrg().equals(foundCurrentGroup.getIdOfGroupNameToOrg())) {
                    GroupNamesToOrgs middleGroup = updateMiddleGroup(request, foundCurrentGroup, session);
                    response = MiddleGroupResponse.from(middleGroup);
                } else {
                    throw new WebApplicationException(DUPLICATE_GROUP_NAME,
                            String.format("Подгруппа '%s' для группы '%s' уже существует", request.getName(),
                                    request.getParentGroupName()));
                }
            } else {
                throw new WebApplicationException(GROUP_NOT_FOUND,
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

    private GroupNamesToOrgs updateMiddleGroup(MiddleGroupRequest request, GroupNamesToOrgs groupNamesToOrgs,
            Session session) {
        long version = getNextVersion(session);
        updateMiddleGroupForClients(request, groupNamesToOrgs, session, version);
        groupNamesToOrgs.setVersion(version);
        groupNamesToOrgs.setGroupName(request.getName());
        groupNamesToOrgs.setIdOfOrg(request.getBindingOrgId());
        groupNamesToOrgs.setParentGroupName(request.getParentGroupName());
        session.update(groupNamesToOrgs);
        return groupNamesToOrgs;
    }

    private void updateMiddleGroupForClients(MiddleGroupRequest request, GroupNamesToOrgs groupNamesToOrgs, Session session,
            long version) {
        List<Client> clientListWithMiddleGroup = getClientListWithMiddleGroup(session, groupNamesToOrgs);
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
