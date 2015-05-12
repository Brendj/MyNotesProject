/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: shamil
 * Date: 22.04.15
 * Time: 13:53
 */
@Repository
@Transactional
public class ClientWritableRepository extends WritableJpaDao {

    public static ClientWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(ClientWritableRepository.class);
    }

    public Client find( Long id ){
        return entityManager.find( Client.class, id );
    }

    public void saveEntity(Client client) {
        entityManager.merge(client);
    }
    public void update(Client client) {
        entityManager.merge(client);
    }
}
