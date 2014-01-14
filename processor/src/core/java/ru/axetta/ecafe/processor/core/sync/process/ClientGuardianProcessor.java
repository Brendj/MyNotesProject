/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.process;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.response.ClientGuardianResponse;
import ru.axetta.ecafe.processor.core.sync.response.ClientGuardianResponseElement;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.01.14
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class ClientGuardianProcessor extends AbstractProcessor<ClientGuardianResponse> {

    private final List<ClientGuardianResponseElement> clientGuardianResponseElements;

    public ClientGuardianProcessor(Session session, List<ClientGuardianResponseElement> items) {
        super(session);
        this.clientGuardianResponseElements = items;
    }

    @Override
    public ClientGuardianResponse process() throws Exception {
        ClientGuardianResponse clientGuardianResponse = new ClientGuardianResponse();
        for (ClientGuardianResponseElement item: clientGuardianResponseElements){
            if(item.getDeleteState()==0){
                Criteria criteria = session.createCriteria(ClientGuardian.class);
                ClientGuardian clientGuardian = item.createNewClientGuardian();
                criteria.add(Example.create(clientGuardian));
                List list = criteria.list();
                if(list==null || list.isEmpty() || list.get(0)==null){
                    session.persist(clientGuardian);
                    clientGuardianResponse.addItem(clientGuardian);
                } else {
                    clientGuardianResponse.addItem(clientGuardian, 0, "Client guardian exist");
                }
            } else {
                Criteria criteria = session.createCriteria(ClientGuardian.class);
                ClientGuardian clientGuardian = item.createNewClientGuardian();
                criteria.add(Example.create(clientGuardian));
                List list = criteria.list();
                if(list==null || list.isEmpty() || list.get(0)==null){
                    clientGuardianResponse.addItem(clientGuardian, 0, "Client guardian is removed");
                } else {
                    session.delete(clientGuardian);
                    clientGuardianResponse.addItem(clientGuardian);
                }
            }
        }
        return clientGuardianResponse;
    }
}
