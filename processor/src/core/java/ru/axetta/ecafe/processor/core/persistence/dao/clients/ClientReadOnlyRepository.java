/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

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

    @Transactional(readOnly = true,propagation = Propagation.REQUIRED)
    public Client findByContractId(long contractId){
        return  entityManager.createQuery("from Client c inner join  fetch c.cards inner join fetch c.cardsInternal where c.contractId = :contractId", Client.class)
                .setParameter("contractId", contractId).getSingleResult();
    }

    public List<Client> findAllActiveByOrg(long idOfOrg) {
        Query query = entityManager
                .createQuery("from Client c where c.org.idOfOrg=:idOfOrg and c.contractState=:contractState ")
                .setParameter("idOfOrg", idOfOrg).setParameter("contractState", Client.ACTIVE_CONTRACT_STATE);

        return query.getResultList();
    }

    public List<Client> findAllActiveByOrgAndUpdateDate(long idOfOrg, long lastUpdateDate) {
        Query query = entityManager
                .createQuery("from Client c where c.org.idOfOrg=:idOfOrg and c.contractState=:contractState and c.updateTime > :lastUpdateDate ")
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("contractState", Client.ACTIVE_CONTRACT_STATE)
                .setParameter("lastUpdateDate", lastUpdateDate);

        return query.getResultList();
    }
}
