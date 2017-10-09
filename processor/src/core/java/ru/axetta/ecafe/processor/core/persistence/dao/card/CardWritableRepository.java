/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.WritableJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.visitor.VisitorReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardSignVerifyType;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistryItem;

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
    public Card findByCardNoWithoutClient( Long cardno ){
        TypedQuery<Card> query = entityManager.createQuery("from Card c where c.cardNo=:cardno", Card.class);
        query.setParameter("cardno",cardno);
        List<Card> resultList = query.getResultList();
        if(resultList.size()> 0){
            return query.getResultList().get(0);
        }else return null;
    }

    public void saveEntity(Card card) {
        entityManager.merge(card);
    }

    private void checkVerifyCardSign(Org org, Integer cardSignVerifyRes, Integer cardSignCertNum) throws Exception {
        if (!org.getNeedVerifyCardSign()) {
            return;
        }
        if (cardSignVerifyRes == null || cardSignCertNum == null) throw new IllegalStateException("Ошибка регистрации");
        switch (CardSignVerifyType.fromInteger(cardSignVerifyRes)) {
            case NOT_PROCESSED:
            case VERIFY_FAIL:
                throw new IllegalStateException("Ошибка регистрации");
            case VERIFY_SUCCESS:
                CardSign cardSign = entityManager.find(CardSign.class, cardSignCertNum);
                if (cardSign == null) throw new Exception("Ошибка регистрации");
                return;
            default: throw new IllegalStateException("Неизвестное значение параметра cardSignVerifyRes");
        }
    }

    public Card createCard(Org org, long cardNo, long cardPrintedNo, int type,
            Integer cardSignVerifyRes, Integer cardSignCertNum) throws Exception {
        checkVerifyCardSign(org, cardSignVerifyRes, cardSignCertNum);
        Card card = new Card(org,cardNo,type, CardState.FREE.getValue(),cardPrintedNo,Card.READY_LIFE_STATE);
        card.setUpdateTime(new Date());
        card.setValidTime(new Date());
        card.setCreateTime(new Date());
        if (!(cardSignCertNum == null || cardSignCertNum == 0))
            card.setCardSignCertNum(cardSignCertNum);

        entityManager.persist(card);
        return card;
    }

    public void update(Card card) {
        entityManager.merge(card);
    }

    /*public void resetAllWithStateBlockAndResetInOrg(long idOfOrg) {
        List<Card> resultList = entityManager.createQuery("from Card c where c.org.idOfOrg = :idOfOrg and c.state=:state")
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("state", CardState.BLOCKED.getValue())
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
    }*/

    public int block(long cardNo, long idOfOrg) {
        return entityManager.createQuery("update Card set "
                + " state = :state, "
                + " updateTime = :updateTime "
                + " where cardNo = :cardNo")
                .setParameter("state", CardState.TEMPBLOCKED.getValue())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .executeUpdate();
    }

    public int blockAndReset(long cardNo, long idOfOrg) {

        return entityManager.createQuery("update Card set "
                + " state = :state, "
                + " validTime = :validTime,"
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime "
                + " where cardNo = :cardNo")
                .setParameter("state", CardState.BLOCKED.getValue())
                .setParameter("validTime", new Date())
                .setParameter("issueTime", new Date())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .executeUpdate();
    }

    public int reset(long cardNo, long idOfOrg) {
        return entityManager.createQuery("update Card set "
                + " client = null, "
                + " state = :state, "
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " validTime = :validTime "
                + " where cardNo = :cardNo")
                .setParameter("state", CardState.FREE.getValue())
                .setParameter("validTime", new Date())
                .setParameter("issueTime", new Date())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .executeUpdate();
    }

    public int issueToVisitor(CardsOperationsRegistryItem o,  long idOfOrg) {
        Visitor visitor = VisitorReadOnlyRepository.getInstance().find(o.getGlobalId());
        return entityManager.createQuery("update Card set "
                + " client = null, "
                + " state = :state, "
                + " validTime = :validTime,"
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " visitor = :visitor "
                + " where cardNo = :cardNo")
                .setParameter("state", CardState.ISSUED.getValue())
                .setParameter("validTime", o.getValidDate())
                .setParameter("issueTime", o.getOperationDate())
                .setParameter("updateTime", new Date())
                .setParameter("visitor", visitor)
                .setParameter("cardNo", o.getCardNo())
                .executeUpdate();
    }

    public int issueToClient(CardsOperationsRegistryItem o,  long idOfOrg) {
        Client client = ClientReadOnlyRepository.getInstance().findById(o.getIdOfClient());
        return entityManager.createQuery("update Card set "
                + " client = :client, "
                + " state = :state, "
                + " validTime = :validTime,"
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " visitor = null "
                + " where cardNo = :cardNo")
                .setParameter("state", CardState.ISSUED.getValue())
                .setParameter("client", client)
                .setParameter("validTime", o.getValidDate())
                .setParameter("issueTime", o.getOperationDate())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", o.getCardNo())
                .executeUpdate();
    }

    public int issueToClientTemp(CardsOperationsRegistryItem o, long idOfOrg) {
        Client client = ClientReadOnlyRepository.getInstance().findById(o.getIdOfClient());
        return entityManager.createQuery("update Card set "
                + " client = :client, "
                + " state = :state, "
                + " validTime = :validTime,"
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " visitor = null "
                + " where cardNo = :cardNo")
                .setParameter("state", CardState.TEMPISSUED.getValue())
                .setParameter("client", client)
                .setParameter("validTime", o.getValidDate())
                .setParameter("issueTime", o.getOperationDate())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", o.getCardNo())
                .executeUpdate();
    }
}
