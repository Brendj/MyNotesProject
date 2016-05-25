/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.process;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.sync.response.ClientGuardianData;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.01.14
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianDataProcessor extends AbstractProcessor<ClientGuardianData> {

    private final Long idOfOrg;
    private final Long maxVersion;

    public ClientGuardianDataProcessor(Session session, Long idOfOrg, Long maxVersion) {
        super(session);
        this.idOfOrg = idOfOrg;
        this.maxVersion = maxVersion;
    }

    @Override
    public ClientGuardianData process() throws Exception {

        Criteria clientsCriteria = session.createCriteria(Client.class);
        clientsCriteria.createAlias("org", "o");
        clientsCriteria.add(Restrictions.in("o.idOfOrg", getFriendlyOrgsId(idOfOrg)));
        clientsCriteria.setProjection(Property.forName("idOfClient"));
        List<Long> clientIds = clientsCriteria.list();
        List<Long> migrantIds = DAOUtils.getActiveMigrantsIdsForOrg(session, idOfOrg);

        ClientGuardianData clientGuardianData;
        if (clientIds.size() > 0 || migrantIds.size() > 0) {

            List<ClientGuardian> clientList = new ArrayList<ClientGuardian>();
            if(clientIds.size() > 0) {
                Criteria criteria = session.createCriteria(ClientGuardian.class);
                criteria.add(Restrictions
                        .or(Restrictions.in("idOfGuardian", clientIds), Restrictions.in("idOfChildren", clientIds)));
                criteria.add(Restrictions.gt("version", maxVersion));
                clientList = criteria.list();
            }

            List<ClientGuardian> migrantList = new ArrayList<ClientGuardian>();
            if(migrantList.size() > 0) {
                Criteria migrantsCriteria = session.createCriteria(ClientGuardian.class);
                migrantsCriteria.add(Restrictions
                        .or(Restrictions.in("idOfGuardian", migrantIds), Restrictions.in("idOfChildren", migrantIds)));
                migrantsCriteria.add(Restrictions.gt("version", maxVersion));
                migrantList = migrantsCriteria.list();
            }

            // Получаем полный список клиентов, в котором должны находится опекуны и опекаемые
            clientIds.addAll(migrantIds);

            clientList.addAll(migrantList);

            clientGuardianData = new ClientGuardianData(new ResultOperation(0, null));
            for (ClientGuardian clientGuardian : clientList) {
                // Связь передается только если и опекун и опекаемый находятся в списке
                if(clientIds.contains(clientGuardian.getIdOfChildren()) && clientIds.contains(clientGuardian.getIdOfClientGuardian())) {
                    clientGuardianData.addItem(clientGuardian);
                }
            }
        } else {
            clientGuardianData = new ClientGuardianData(new ResultOperation(400, "Client not found by this org"));
        }
        return clientGuardianData;
    }

    private Collection<Long> getFriendlyOrgsId(Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        return OrgUtils.getFriendlyOrgIds(org);
    }
}
