/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import ru.axetta.ecafe.processor.core.daoservices.client.ClientRepository;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 16.01.13
 * Time: 16:16
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class ClientService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Transactional
    public List<ClientMigrationItemInfo> reloadMigrationInfoByClient(Long idOfClient){
        TypedQuery<ClientMigration> clientMigrationTypedQuery = entityManager.createQuery("from ClientMigration where client.idOfClient=:idOfClient",ClientMigration.class);
        clientMigrationTypedQuery.setParameter("idOfClient",idOfClient);
        List<ClientMigration> clientMigrationList = clientMigrationTypedQuery.getResultList();
        List<ClientMigrationItemInfo> clientMigrationItemInfoList = new ArrayList<ClientMigrationItemInfo>(clientMigrationList.size());
        for (ClientMigration clientMigration: clientMigrationList){
            clientMigrationItemInfoList.add(new ClientMigrationItemInfo(clientMigration));
        }
        return clientMigrationItemInfoList;
    }

    @Autowired
    private ClientRepository repository;

    @Transactional(readOnly = true)
    public Client getGuardian(Long idOfClient){
        Client client = null;
        try {
            ClientGuardian clientGuardian = repository.findClientGuardianByChildren(idOfClient);
            client = repository.getClient(clientGuardian.getIdOfGuardian());
        } catch (NoResultException ignore){}
        return client;
    }

    @Transactional
    public void setGuardian(Long idOfChildren, Long idOfGuardian){
        ClientGuardian clientGuardian = new ClientGuardian(idOfChildren, idOfGuardian);
        repository.createClientGuardian(clientGuardian);
    }

}
