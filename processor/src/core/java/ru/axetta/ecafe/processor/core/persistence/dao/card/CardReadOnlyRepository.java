/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 13:53
 */
@Repository
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class CardReadOnlyRepository extends BaseJpaDao {

    public static CardReadOnlyRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(CardReadOnlyRepository.class);
    }


    public List<Card> findAllByOrg(long idOfOrg){
        Query query = entityManager
                .createQuery("from Card c where c.org.idOfOrg=:idOfOrg", Card.class)
                .setParameter("idOfOrg",idOfOrg);

        return query.getResultList();
    }

    public List<Card> findAllFreeByOrg(long idOfOrg){
        Query query = entityManager
                .createQuery("from Card c where c.org.idOfOrg=:idOfOrg and c.client = null ", Card.class)
                .setParameter("idOfOrg",idOfOrg);

        return query.getResultList();
    }

    public List<Card> findAllByClient(Client client) {
        return entityManager.createQuery("from Card c where c.client.idOfClient=:client",Card.class)
                .setParameter("client",client.getIdOfClient())
                .getResultList();
    }
}
