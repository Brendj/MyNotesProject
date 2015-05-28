/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Date;
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
                .createQuery("from Client c where c.org.idOfOrg=:idOfOrg and c.idOfClientGroup < :idOfClientGroup ")
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());

        return query.getResultList();
    }

    public List<Client> findAllActiveByOrg(List<Long> idOfOrg) {
        Query query = entityManager
                .createQuery("from Client c where c.org.idOfOrg in (:idOfOrg) and c.idOfClientGroup < :idOfClientGroup ")
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());

        return query.getResultList();
    }


    public List<Client> findAllActiveByOrgAndUpdateDate(List<Long> idOfOrgs, Date lastAccRegistrySync) {
        Query query = entityManager
                .createQuery("select c from Client c , OrgSync os where c.org.idOfOrg in (:idOfOrgs) "
                        + " and c.idOfClientGroup<:idOfClientGroup " + " and c.org = os.org "
                        + " and c.updateTime >  :lastAccRegistrySync ")
                .setParameter("idOfOrgs", idOfOrgs)
                .setParameter("idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue())
                .setParameter("lastAccRegistrySync", lastAccRegistrySync);

        return query.getResultList();
    }

    public List<Client> findById(List<Long> idOfClients) {
        return entityManager
                .createQuery("from Client c where c.idOfClient in (:idOfClients)")
                .setParameter("idOfClients", idOfClients)
                .getResultList();
    }
}
