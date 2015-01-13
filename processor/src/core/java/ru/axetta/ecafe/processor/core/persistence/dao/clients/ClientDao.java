/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class ClientDao extends WritableJpaDao {


    public static ClientDao getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientDao.class);
    }
    @Transactional
    public Client update( Client entity ){
        return entityManager.merge( entity );
    }

    @Transactional
    public List<Client> findAllByPassword(String password) {
        TypedQuery<Client> query = entityManager
                .createQuery("from Client c where c.cypheredPassword = :password", Client.class)
                .setParameter("password", password);
        return query.getResultList();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
