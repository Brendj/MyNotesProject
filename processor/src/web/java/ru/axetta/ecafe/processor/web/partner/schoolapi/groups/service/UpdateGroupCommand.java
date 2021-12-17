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
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.updateClientRegistryVersion;

@Component
class UpdateGroupCommand {

    private final Logger logger = LoggerFactory.getLogger(UpdateGroupCommand.class);
    private final RuntimeContext runtimeContext;
    private final MoveClientsCommand moveClientsCommand;

    @Autowired
    public UpdateGroupCommand(RuntimeContext runtimeContext, MoveClientsCommand moveClientsCommand) {
        this.runtimeContext = runtimeContext;
        this.moveClientsCommand = moveClientsCommand;
    }

    public GroupClientsUpdateResponse updateGroup(Long id, Long orgId, GroupClientsUpdateRequest request, User user) {
        GroupClientsUpdateResponse response;
        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            CompositeIdOfClientGroup idOfClientGroup = new CompositeIdOfClientGroup(orgId, id);
            ClientGroup clientGroup = (ClientGroup) session.get(ClientGroup.class, idOfClientGroup);

            if (clientGroup == null) {
                throw WebApplicationException.notFound(ResponseCodes.CLIENT_GROUP_NOT_FOUND.getCode(),
                        String.format("Группа с ID: '%d' и OrgID: '%d' не найдена", id, orgId));
            }
            response = GroupClientsUpdateResponse.from(clientGroup);
            updateBindingOrg(request, clientGroup, response, session);
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.ARM);
            clientGuardianHistory.setReason("Rest метод (АРМ администратора)");
            clientGuardianHistory.setAction("Обновить группу клиента");
            clientGuardianHistory.setUser(user);
            clientGuardianHistory.setChangeDate(new Date());
            updateBindingClients(request.getBindingOrgId(), clientGroup, session, user, clientGuardianHistory);
            session.flush();
            transaction.commit();
            transaction = null;
            return response;
        } catch (WebApplicationException wex) {
            throw wex;
        } catch (Exception e) {
            logger.error("Error in update middle group, ", e);
            throw new WebApplicationException(
                    String.format("Ошибка при обновлении группы с ID: '%d'и OrgID: '%d'", id, orgId), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

    }

    private void updateBindingClients(Long bindingOrg, ClientGroup clientGroup, Session session, User user, ClientGuardianHistory clientGuardianHistory)
            throws Exception {
        if (bindingOrg == null) {
            return;
        }
        ClientGroup.Predefined predefinedGroup = ClientGroup.Predefined
                .parse(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        if (predefinedGroup != null) {
            /* для клиентов предопределенных групп никого не перемещаем */
            return;
        }

        List<Client> clients = loadClientsFromGroupsWithName(session, getFriendlyOrgIds(clientGroup), clientGroup.getGroupName());
        if (clients.isEmpty()) {
            return;
        }

        ClientGroup foundNewGroup;
        try {
            foundNewGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(session, bindingOrg, clientGroup.getGroupName());
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage());
        }
        if (foundNewGroup == null) {
            // если группу не нашли, нужно создать для успешного перемещения туда клиентов
            foundNewGroup = DAOUtils.createClientGroup(session, bindingOrg, clientGroup.getGroupName());
        }

        long version = updateClientRegistryVersion(null);
        for (Client client : clients) {
            ClientUpdateItem clientUpdateItem = ClientUpdateItem.from(client, foundNewGroup);
            String error = moveClientsCommand
                    .executeMoveOrError(session, clientUpdateItem, client, foundNewGroup, version, user, false, clientGuardianHistory);
            if (StringUtils.isNotEmpty(error)) {
                throw new WebApplicationException(
                        String.format("Ошибка при смене привязки у клинета c ID='%d', ", client.getIdOfClient()));
            }
        }
    }

    private List<Client> loadClientsFromGroupsWithName(Session session, List<Long> orgIds, String groupName) {
        return (List<Client>) session.createQuery(
                "select cl from Client cl where cl.clientGroup.groupName=:groupName and cl.org.idOfOrg in (:orgs) ")
                .setParameterList("orgs", orgIds).setParameter("groupName", groupName).list();
    }

    private void updateBindingOrg(GroupClientsUpdateRequest request, ClientGroup clientGroup,
            GroupClientsUpdateResponse response, Session session) {
        long version = DAOUtils.nextVersionByGroupNameToOrg(session);
        Org org = clientGroup.getOrg();
        Long idOfMainOrg = getMainBuildingOrgId(org);
        GroupNamesToOrgs groupNamesToOrgs = DAOUtils
                .getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(session, org.getIdOfOrg(), clientGroup.getGroupName());
        if (groupNamesToOrgs == null) {
            groupNamesToOrgs = new GroupNamesToOrgs(idOfMainOrg, request.getBindingOrgId(), 1,
                    clientGroup.getGroupName(), version, null, false);
        }
        groupNamesToOrgs.setIdOfOrg(request.getBindingOrgId());
        groupNamesToOrgs.setIdOfMainOrg(idOfMainOrg);
        groupNamesToOrgs.setVersion(version);
        session.saveOrUpdate(groupNamesToOrgs);
        response.setBindingOrgId(request.getBindingOrgId());
    }

    private Long getMainBuildingOrgId(Org org) {
        for (Org friendlyOrg : org.getFriendlyOrg()) {
            if (friendlyOrg.isMainBuilding()) {
                return friendlyOrg.getIdOfOrg();
            }
        }
        return org.getIdOfOrg();
    }

    private List<Long> getFriendlyOrgIds(ClientGroup group) {
        List<Long> result = new ArrayList<>();
        for (Org org : group.getOrg().getFriendlyOrg()) {
            result.add(org.getIdOfOrg());
        }
        return result;
    }

}
