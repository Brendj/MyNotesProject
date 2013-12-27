/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.client;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 27.12.13
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ClientRepository {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public ClientGuardian findClientGuardianByChildren(Long idOfClient){
        TypedQuery<ClientGuardian> query = entityManager.createQuery("from ClientGuardian where idOfChildren=:idOfClient", ClientGuardian.class);
        query.setParameter("idOfClient", idOfClient);
        return query.getSingleResult();
    }

    public Client getClient(long idOfGuardian) {
        return entityManager.find(Client.class, idOfGuardian);
    }

    public void createClientGuardian(ClientGuardian clientGuardian) {
        entityManager.persist(clientGuardian);
    }
}
