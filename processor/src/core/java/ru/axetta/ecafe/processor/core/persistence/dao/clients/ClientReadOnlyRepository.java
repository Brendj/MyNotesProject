/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
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

    @Transactional(readOnly = true)
    public List<Client> findAllActiveByOrg(List<Long> idOfOrg) {
        Query query = entityManager
                .createQuery("from Client c where c.org.idOfOrg in (:idOfOrg) and (c.idOfClientGroup < :idOfClientGroup or c.idOfClientGroup = :idOfDisplacedGroup) ")
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue())
                .setParameter("idOfDisplacedGroup", ClientGroup.Predefined.CLIENT_DISPLACED.getValue());

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<Client> findAllAllocatedClients(Long idOfOrg) {
        Session ses = entityManager.unwrap(Session.class);
        Org org = (Org) ses.load(Org.class, idOfOrg);
        return ClientManager.findAllAllocatedClients(ses, org);
    }

    public List<Client> findById(List<Long> idOfClients) {
        return entityManager
                .createQuery("from Client c where c.idOfClient in (:idOfClients)")
                .setParameter("idOfClients", idOfClients)
                .getResultList();
    }

    public Client findWithCards( Long id ){
        TypedQuery<Client> query = entityManager
                .createQuery("select c from Client c left join fetch c.cardsInternal where c.idOfClient=:idOfClient", Client.class);
        query.setParameter("idOfClient",id);
        List<Client> resultList = query.getResultList();
        if(resultList.size()> 0){
            return resultList.get(0);
        }else {
            return null;
        }
    }

    public Client findById(long id) {
        return entityManager.find( Client.class, id );
    }
}
