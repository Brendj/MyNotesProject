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
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistryItem;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
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

    private boolean isSocial(int type) {
        return (type == 6 || type == 7 || type == 8);
    }

    private void checkVerifyCardSign(Org org, Integer cardSignVerifyRes, Integer cardSignCertNum, int type, long cardNo) throws Exception {
        if (!org.getNeedVerifyCardSign()) {
            return;
        }
        if (cardExistsInSpecial(cardNo)) {
            return; //не проверяем подпись для карт с номерами из таблицы cf_cards_special
        }
        if (cardSignVerifyRes == null || cardSignCertNum == null) throw new IllegalStateException("Ошибка регистрации");
        switch (CardSignVerifyType.fromInteger(cardSignVerifyRes)) {
            case NOT_PROCESSED:
                if (isSocial(type)) {
                    return;
                } else {
                    throw new IllegalStateException("Ошибка регистрации");
                }
            case VERIFY_FAIL:
                throw new IllegalStateException("Ошибка регистрации");
            case VERIFY_SUCCESS:
                CardSign cardSign = entityManager.find(CardSign.class, cardSignCertNum);
                if (cardSign == null) throw new IllegalStateException("Ошибка регистрации");
                return;
            default: throw new IllegalStateException("Неизвестное значение параметра cardSignVerifyRes");
        }
    }

    private boolean cardExistsInSpecial(long cardNo) {
        Query query = entityManager.createNativeQuery("select count(cardno) from cf_cards_special cs where cs.cardno = :cardNo");
        query.setParameter("cardNo", cardNo);
        Long result = ((BigInteger) query.getSingleResult()).longValue();
        return (result > 0);
    }

    public Card createCard(Org org, long cardNo, long cardPrintedNo, int type,
            Integer cardSignVerifyRes, Integer cardSignCertNum, Boolean isLongUid) throws Exception {
        checkVerifyCardSign(org, cardSignVerifyRes, cardSignCertNum, type, cardNo);
        return createCardInternal(org, cardNo, cardPrintedNo, type, cardSignCertNum, isLongUid);
    }

    public Card createCardSpecial(Org org, long cardNo, long cardPrintedNo, int type,
            Integer cardSignCertNum) throws Exception {
        if (cardExistsInSpecial(cardNo)) {
            return createCardInternal(org, cardNo, cardPrintedNo, type, cardSignCertNum, null);
        } else {
            throw new Exception("cardNo not found");
        }
    }

    private Card createCardInternal(Org org, long cardNo, long cardPrintedNo, int type,
            Integer cardSignCertNum, Boolean isLongUid) throws Exception {
        Card card = new Card(org,cardNo,type, CardState.FREE.getValue(),cardPrintedNo,Card.READY_LIFE_STATE);
        card.setUpdateTime(new Date());
        card.setValidTime(new Date());
        card.setCreateTime(new Date());
        card.setTransitionState(CardTransitionState.OWN);
        if (null != isLongUid)
            card.setIsLongUid(isLongUid);
        if (org.getNeedVerifyCardSign() && !(cardSignCertNum == null || cardSignCertNum == 0) && !isSocial(type))
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
        if (o.getRequestGuid() != null) {
            Card card = CardReadOnlyRepository.getInstance().findByCardNo(o.getCardNo());
            if (card != null) {
                Long version = DAOUtils.nextVersionByCardRequest((Session)entityManager.getDelegate());
                Query query = entityManager.createQuery("update CardRequest cr "
                        + "set cr.card.idOfCard = :idOfCard, "
                        + "cr.cardIssueDate = :issueDate, "
                        + "cr.version = :version "
                        + "where cr.guid = :guid");
                query.setParameter("idOfCard", card.getIdOfCard());
                query.setParameter("issueDate", o.getOperationDate());
                query.setParameter("version", version);
                query.setParameter("guid", o.getRequestGuid());
                query.executeUpdate();
            }
        }
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
