/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: regal
 * Date: 29.03.15
 * Time: 13:03
 */
@Repository
public class ClientReadOnlyRepository  extends BaseJpaDao {

    public static ClientReadOnlyRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientReadOnlyRepository.class);
    }

    @Transactional(readOnly = true)
    public Client findByContractId(long contractId){
        return  entityManager.createQuery("from Client c where c.contractId = :contractId", Client.class)
                .setParameter("contractId", contractId).getSingleResult();
    }
}
