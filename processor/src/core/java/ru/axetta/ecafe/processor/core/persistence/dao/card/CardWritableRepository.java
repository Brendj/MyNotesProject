/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    public void saveEntity(Card card) {
        entityManager.merge(card);
    }


    @Transactional
    public Card createCard(Org org, long cardNo, long cardPrintedNo, int type) {
        Card card = new Card(org,cardNo,type, CardState.FREE.getValue(),cardPrintedNo,Card.READY_LIFE_STATE);
        card.setUpdateTime(new Date());
        entityManager.persist(card);
        return card;
    }

    public void update(Card card) {
        entityManager.merge(card);
    }
}
