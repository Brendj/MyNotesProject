/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroupMigrationHistory;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: shamil
 * Date: 21.11.14
 * Time: 17:31
 */
@Repository
public class ClientGroupMigrationHistoryRepository extends AbstractJpaDao<ClientGroupMigrationHistory> {
    public static ClientGroupMigrationHistoryRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientGroupMigrationHistoryRepository.class);
    }

    public List<ClientGroupMigrationHistory> findAll(Org org, Client client){
        return entityManager.createQuery("select c from ClientGroupMigrationHistory c left join fetch c.org left join fetch c.client "
                + "where"
                + " c.client=:client "
                + " order by c.registrationDate desc" , ClientGroupMigrationHistory.class)
                .setParameter("client", client)
                .getResultList();
    }
}
