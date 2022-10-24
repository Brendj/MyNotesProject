/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
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

    public List<Card> findAllFreeAndBlockedWithTransitionStateByOrg(List<Long> idOfOrgs){
        Query query = entityManager
                .createQuery("from Card c where c.org.idOfOrg in :idOfOrgs " +
                        "and (c.state = :freeState or (c.state = :blockedState and c.transitionState = :givenTransitionState and c.client is null)) ", Card.class)
                .setParameter("idOfOrgs",idOfOrgs)
                .setParameter("freeState", CardState.FREE.getValue())
                .setParameter("blockedState", CardState.BLOCKED.getValue())
                .setParameter("givenTransitionState", CardTransitionState.GIVEN_AWAY.getCode());
        return query.getResultList();
    }

    public List<Card> findAllByClient(Client client) {
        return entityManager.createQuery("from Card c where c.client.idOfClient=:client",Card.class)
                .setParameter("client",client.getIdOfClient())
                .getResultList();
    }

    public List<Card> findAllByClientList(List<Client> clients) {
        List<Long> ids = new ArrayList<Long>();
        for (Client cl : clients) {
            ids.add(cl.getIdOfClient());
        }
        return entityManager.createQuery("from Card c where c.client.idOfClient in :clients",Card.class)
                .setParameter("clients", ids)
                .getResultList();
    }

    public List<Card> findAllFreeByOrgAndUpdateDateAndBlockedWithTransitionState(List<Long> idOfOrgs, Date lastAccRegistrySync) {
        Query query = entityManager
                .createQuery("select c from Card c "
                        + " where c.org.idOfOrg in (:idOfOrgs) "
                        + " and (c.state = :freeState or (c.state = :blockedState and c.transitionState = :givenTransitionState and c.client is null)) "
                        + " and c.updateTime >  :lastAccRegistrySync "
                        , Card.class)
                .setParameter("idOfOrgs", idOfOrgs)
                .setParameter("freeState",CardState.FREE.getValue())
                .setParameter("lastAccRegistrySync", lastAccRegistrySync)
                .setParameter("blockedState", CardState.BLOCKED.getValue())
                .setParameter("givenTransitionState", CardTransitionState.GIVEN_AWAY.getCode());

        return query.getResultList();
    }

    public List<Card> findByIdAndState(List<Long> idOfCards, int state) {
        return entityManager.createQuery("from Card c where c.cardNo in (:idOfCards) and state=:state ", Card.class)
                .setParameter("idOfCards", idOfCards)
                .setParameter("state", state)
                .getResultList();
    }

    public List<Card> findByOrgandStateChange(Long statechange, Long idOfOrg) {
        return entityManager.createQuery("select c from Card c left join c.cardsync cs where cs.statechange=:statechange"
                + " and cs.org.idOfOrg =:idOfOrg ", Card.class)
                .setParameter("statechange", statechange)
                .setParameter("idOfOrg", idOfOrg)
                .getResultList();
    }

    //ниже странное условие where c.cardNo in (:idOfOrgs)
    @Deprecated
    public List<Visitor> findVisitorsWithCardsByOrg(List<Long> idOfOrgs) {
        return entityManager.createQuery(
                "select c.visitor from Card c where c.cardNo in (:idOfOrgs) and c.client is null and c.visitor is not null",
                Visitor.class)
                .setParameter("idOfOrgs", idOfOrgs)
                .getResultList();
    }

    public List<Visitor> findVisitorsWithCardsByOrgAndDate(List<Long> idOfOrgs, Date lastAccRegistrySyncDate) {
        return entityManager.createQuery(
                "select c.visitor from Card c inner join fetch c.visitor.cardsInternal where c.cardNo in (:idOfOrgs) and c.client is null and c.visitor is not null"
                + " and c.updateTime >  :lastAccRegistrySync ",
                Visitor.class)
                .setParameter("idOfOrgs", idOfOrgs)
                .setParameter("lastAccRegistrySync", lastAccRegistrySyncDate)
                .getResultList();
    }
}
