/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfClientGroup;
import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.groups.dto.GroupClientsUpdateResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class UpdateGroupCommand {
    private Logger logger = LoggerFactory.getLogger(UpdateGroupCommand.class);
    private final RuntimeContext runtimeContext;
    private static final int DUPLICATE_GROUP_NAME = 409, GROUP_NOT_FOUND = 404;

    @Autowired
    public UpdateGroupCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public GroupClientsUpdateResponse updateGroup(Long id, Long orgId, GroupClientsUpdateRequest request) {
        GroupClientsUpdateResponse response;
        Session session = null;
        Transaction transaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();

            CompositeIdOfClientGroup idOfClientGroup = new CompositeIdOfClientGroup(orgId, id);
            ClientGroup clientGroup = (ClientGroup) session.get(ClientGroup.class, idOfClientGroup);

            if (clientGroup == null) {
                throw new WebApplicationException(GROUP_NOT_FOUND,
                        String.format("Группа с ID: '%d' и OrgID: '%d' не найдена", id, orgId));
            }
            response = GroupClientsUpdateResponse.from(clientGroup);
            updateBindingOrg(request, clientGroup, response, session);
            updateExcludeFromPlan(request, clientGroup, response, session);
            session.flush();
            transaction.commit();
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

    private void updateExcludeFromPlan(GroupClientsUpdateRequest request, ClientGroup clientGroup,
            GroupClientsUpdateResponse response,
            Session session) {

    }

    private void updateBindingOrg(GroupClientsUpdateRequest request, ClientGroup clientGroup,
            GroupClientsUpdateResponse response, Session session) {
        long version = DAOUtils.nextVersionByGroupNameToOrg(session);
        Org org = clientGroup.getOrg();
        Long idOfMainOrg = getMainBuildingOrgId(org);
        GroupNamesToOrgs groupNamesToOrgs = DAOUtils.getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(session,
                org.getIdOfOrg(), clientGroup.getGroupName());
        if (groupNamesToOrgs == null) {
            DAOUtils.createGroupNamesToOrg(session, org, version, clientGroup.getGroupName());
        }
        else {
            groupNamesToOrgs.setIdOfOrg(request.getBindingOrgId());
            groupNamesToOrgs.setIdOfMainOrg(idOfMainOrg);
            groupNamesToOrgs.setVersion(version);
            session.update(groupNamesToOrgs);
        }
        response.setBindingOrgId(request.getBindingOrgId());
    }

    private Long getMainBuildingOrgId(Org org) {
        //Org org = (Org) session.get(Org.class, parentGroupOrgId);
        for (Org friendlyOrg : org.getFriendlyOrg()) {
            if (friendlyOrg.isMainBuilding()) {
                return friendlyOrg.getIdOfOrg();
            }
        }
        return org.getIdOfOrg();
    }

}
