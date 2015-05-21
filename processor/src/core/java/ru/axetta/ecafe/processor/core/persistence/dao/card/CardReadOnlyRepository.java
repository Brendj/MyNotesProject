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
import javax.persistence.TypedQuery;
import java.util.Date;
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

    public Card find( Long id ){
        return entityManager.find( Card.class, id );
    }

    public Card findByCardNo( Long cardno ){
        TypedQuery<Card> query = entityManager.createQuery("from Card c where c.cardNo=:cardno", Card.class);
        query.setParameter("cardno",cardno);
        List<Card> resultList = query.getResultList();
        if(resultList.size()> 0){
            return query.getResultList().get(0);
        }else return null;
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

    public List<Card> findAllFreeByOrgAndUpdateDate(long idOfOrg, long lastUpdateDate) {
        Query query = entityManager
                .createQuery("from Card c where c.org.idOfOrg=:idOfOrg and c.client = null and c.updateTime>:lastUpdateDate ", Card.class)
                .setParameter("idOfOrg",idOfOrg)
                .setParameter("lastUpdateDate",new Date(lastUpdateDate));

        return query.getResultList();
    }

    public List<Card> findById(List<Long> idOfCards) {
       return entityManager
                .createQuery("from Card c where c.idOfCard in (:idOfCards) ", Card.class)
                .setParameter("idOfCards",idOfCards).getResultList();
    }
}
