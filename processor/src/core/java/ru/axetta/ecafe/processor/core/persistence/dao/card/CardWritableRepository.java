/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientWritableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * User: shamil
 * Date: 22.04.15
 * Time: 13:53
 */
@Repository
@Transactional
public class CardWritableRepository extends WritableJpaDao {

    public static CardWritableRepository getInstance(){
        return RuntimeContext.getAppContext().getBean(CardWritableRepository.class);
    }

    public Card findOne( Long id ){
        return entityManager.find( Card.class, id );
    }

    public Card findByCardNo( Long cardno ){
        TypedQuery<Card> query = entityManager.createQuery("from Card c left join fetch c.client where c.cardNo=:cardno", Card.class);
        query.setParameter("cardno",cardno);
        List<Card> resultList = query.getResultList();
        if(resultList.size()> 0){
            return query.getResultList().get(0);
        }else return null;
    }

    public void saveEntity(Card card) {
        entityManager.merge(card);
    }

    public Card createCard(Org org, long cardNo, long cardPrintedNo, int type) {
        Card card = new Card(org,cardNo,type, CardState.FREE.getValue(),cardPrintedNo,Card.READY_LIFE_STATE);
        card.setUpdateTime(new Date());
        card.setValidTime(new Date());
        card.setCreateTime(new Date());

        entityManager.persist(card);
        return card;
    }

    public void update(Card card) {
        entityManager.merge(card);
    }

    public void resetAllWithStateBlockAndResetInOrg(long idOfOrg) {
        List<Card> resultList = entityManager.createQuery("from Card c where c.org.idOfOrg = :idOfOrg and c.state=:state")
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("state", CardState.BLOCKEDANDRESET.getValue())
                .getResultList();
        ClientWritableRepository clientWritableRepository = null;
        if(resultList.size() > 0){
             clientWritableRepository = ClientWritableRepository.getInstance();
        }
        for (Card card : resultList) {
            //Client client  = card.getClient();
            //client.getCards().remove(card);
            card.setClient(null);
            card.setState(CardState.FREE.getValue());
            card.setValidTime(new Date());
            card.setIssueTime(new Date());
            card.setUpdateTime(new Date());
            //clientWritableRepository.update(client);
        }
    }
}
