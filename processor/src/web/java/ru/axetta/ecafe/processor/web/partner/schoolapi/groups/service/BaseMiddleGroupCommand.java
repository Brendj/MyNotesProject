/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.groups.service;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.GroupNamesToOrgs;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

class BaseMiddleGroupCommand {

     void resetMiddleGroupForClient(Session session, Client client, long version) {
        setMiddleGroupForClient(session, client, null, version);
    }

    void setMiddleGroupForClient(Session session, Client client, String middleGroup, long version) {
        client.setMiddleGroup(middleGroup);
        client.setUpdateTime(new Date());
        client.setClientRegistryVersion(version);
        session.update(client);
    }

    GroupNamesToOrgs foundMiddleGroupById(Session session, Long id) {
        return (GroupNamesToOrgs) session.get(GroupNamesToOrgs.class, id);
    }

    @SuppressWarnings("unchecked")
    List<Client> getClientListWithMiddleGroup(Session session, GroupNamesToOrgs middleGroup) {
        Long mainBuilding = middleGroup.getIdOfMainOrg();
        Org mainOrg = (Org) session.load(Org.class, mainBuilding);
        Set<Org> friendlyOrg = mainOrg.getFriendlyOrg();
        List<Long> friendlyOrgIds = new ArrayList<>();
        for (Org org : friendlyOrg) {
            friendlyOrgIds.add(org.getIdOfOrg());
        }
        return (List<Client>) session.createQuery(
                "select cl from Client cl  join cl.clientGroup where cl.org.idOfOrg in :orgs and cl.clientGroup.groupName = :groupName and  cl.middleGroup = :middleGroupName ")
                .setParameter("groupName", middleGroup.getParentGroupName())
                .setParameter("middleGroupName", middleGroup.getGroupName()).setParameterList("orgs", friendlyOrgIds)
                .list();
    }

    long getNextVersion(Session session) {
        return DAOUtils.nextVersionByGroupNameToOrg(session);
    }

}
