/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.process;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.sync.response.ClientGuardianData;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;

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

        DetachedCriteria idOfClient = DetachedCriteria.forClass(Client.class);
        idOfClient.createAlias("org","o");
        idOfClient.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        idOfClient.setProjection(Property.forName("idOfClient"));

        Criteria subCriteria = idOfClient.getExecutableCriteria(session);
        Integer countResult = subCriteria.list().size();
        ClientGuardianData clientGuardianData;
        if(countResult>0){
            Criteria criteria = session.createCriteria(ClientGuardian.class);
            final Criterion idOfGuardian = Property.forName("idOfGuardian").in(idOfClient);
            final Criterion idOfChildren = Property.forName("idOfChildren").in(idOfClient);
            criteria.add(Restrictions.or(idOfGuardian, idOfChildren));
            criteria.add(Restrictions.gt("version", maxVersion));
            List<ClientGuardian> list = criteria.list();
            clientGuardianData = new ClientGuardianData(new ResultOperation(0, null));
            for (ClientGuardian clientGuardian: list){
                clientGuardianData.addItem(clientGuardian);
            }
        } else {
            clientGuardianData = new ClientGuardianData(new ResultOperation(400, "Client not found by this org"));
        }
        return clientGuardianData;
    }
}
