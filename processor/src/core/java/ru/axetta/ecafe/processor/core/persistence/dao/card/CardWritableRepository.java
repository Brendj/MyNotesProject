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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
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

    public Card findByCardNo( Long cardno, Long idOfOrg ){
        try {
            TypedQuery<Card> query = entityManager.createQuery(
                    "from Card c left join fetch c.client inner join fetch c.org " + "where c.cardNo=:cardno and c.org.idOfOrg=:idOfOrg", Card.class);
            query.setParameter("cardno", cardno);
            query.setParameter("idOfOrg", idOfOrg);
            List<Card> resultList = query.getResultList();
            if (resultList.size() > 0) {
                return query.getResultList().get(0);
            } else
                return null;
        } catch (PersistenceException e){
            return null;
        }
    }

    public Card findByCardNo( Long cardno, Long idOfOrg, Long idOfClient, Boolean isOldArm ) {
        if (!isOldArm) return findByCardNo(cardno, idOfOrg);

        TypedQuery<Card> query = entityManager.createQuery(
                "from Card c left join fetch c.client inner join fetch c.org "
                        + "where c.cardNo=:cardno and c.client.idOfClient=:idOfClient", Card.class);
        query.setParameter("cardno",cardno);
        query.setParameter("idOfClient", idOfClient);
        List<Card> resultList = query.getResultList();
        if(resultList.size()> 0){
            return query.getResultList().get(0);
        }else return null;
    }

    public Card findByCardNoWithoutClient( Long cardno, Long idOfOrg ){
        TypedQuery<Card> query = entityManager.createQuery(
                "from Card c inner join fetch c.org "
                 + "where c.cardNo=:cardno and c.org.idOfOrg=:idOfOrg", Card.class);
        query.setParameter("cardno",cardno);
        query.setParameter("idOfOrg", idOfOrg);
        List<Card> resultList = query.getResultList();
        if(resultList.size()> 0){
            return query.getResultList().get(0);
        }else return null;
    }

    public void saveEntity(Card card) {
        entityManager.merge(card);
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
                if (Card.isSocial(type)) {
                    return;
                } else {
                    throw new IllegalStateException("Ошибка регистрации");
                }
            case VERIFY_FAIL:
                throw new IllegalStateException("Ошибка регистрации");
            case VERIFY_SUCCESS:
                Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(CardSign.class);
                criteria.add(Restrictions.or(Restrictions.eq("deleted", false), Restrictions.isNull("deleted")));
                List<CardSign> list = criteria.list();
                //CardSign cardSign = entityManager.find(CardSign.class, cardSignCertNum);
                if (list == null || list.isEmpty()) throw new IllegalStateException("Ошибка регистрации");
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

    public Card createCard(Org org, long cardNo, long cardPrintedNo, int type, Long longCardNo,
            Integer cardSignVerifyRes, Integer cardSignCertNum, Boolean isLongUid) throws Exception {
        checkVerifyCardSign(org, cardSignVerifyRes, cardSignCertNum, type, cardNo);
        return createCardInternal(org, cardNo, cardPrintedNo, type, longCardNo, cardSignCertNum, isLongUid);
    }

    public Card createCard(Org org, long cardNo, long cardPrintedNo, int type,  Long longCardNo, Integer cardSignVerifyRes,
            Integer cardSignCertNum, Boolean isLongUid, Integer cardTransitionState) throws Exception {
        checkVerifyCardSign(org, cardSignVerifyRes, cardSignCertNum, type, cardNo);
        return createCardInternal(org, cardNo, cardPrintedNo, type, longCardNo, cardSignCertNum, isLongUid, cardTransitionState);
    }

    public Card createCardSpecial(Org org, long cardNo, long cardPrintedNo, int type, Long longCardNo,
            Integer cardSignCertNum) throws Exception {
        if (cardExistsInSpecial(cardNo)) {
            return createCardInternal(org, cardNo, cardPrintedNo, type, longCardNo, cardSignCertNum, null);
        } else {
            throw new Exception("cardNo not found");
        }
    }

    private Card createCardInternal(Org org, long cardNo, long cardPrintedNo, int type, Long longCardNo,
            Integer cardSignCertNum, Boolean isLongUid, Integer cardTransitionState) throws Exception {
        Card card = new Card(org,cardNo,type, CardState.FREE.getValue(),cardPrintedNo,Card.READY_LIFE_STATE);
        card.setUpdateTime(new Date());
        card.setValidTime(new Date());
        card.setCreateTime(new Date());
        card.setTransitionState(cardTransitionState);
        if (null != isLongUid)
            card.setIsLongUid(isLongUid);
        if(null != longCardNo){
            card.setLongCardNo(longCardNo);
        }
        if (org.getNeedVerifyCardSign() && !(cardSignCertNum == null || cardSignCertNum == 0) && !Card.isSocial(type))
            card.setCardSignCertNum(cardSignCertNum);

        entityManager.persist(card);
        return card;
    }

    private Card createCardInternal(Org org, long cardNo, long cardPrintedNo, int type, Long longCardNo,
            Integer cardSignCertNum, Boolean isLongUid) throws Exception {
        return createCardInternal(org, cardNo, cardPrintedNo, type, longCardNo, cardSignCertNum, isLongUid,
                CardTransitionState.OWN.getCode());
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

    public int block(long cardNo, long idOfOrg, String lockReason, CardState blockState) {
        return entityManager.createQuery("update Card set "
                + " state = :state, "
                + " updateTime = :updateTime, "
                + " lockReason = :lockReason "
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg")
                .setParameter("state", blockState.getValue())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("lockReason", lockReason)
                .executeUpdate();
    }

    public int block(long cardNo, long idOfOrg, long idOfClient, Boolean isOldArm, String lockReason, CardState blockState) {
        if (!isOldArm) return block(cardNo, idOfOrg, lockReason, blockState);
        return entityManager.createQuery("update Card set "
                + " state = :state, "
                + " updateTime = :updateTime, "
                + " lockReason = :lockReason "
                + " where cardNo = :cardNo"
                + "     and client.idOfClient = :idOfClient")
                .setParameter("state", blockState.getValue())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .setParameter("idOfClient", idOfClient)
                .setParameter("lockReason", lockReason)
                .executeUpdate();
    }

    public int blockAndReset(long cardNo, long idOfOrg, String lockReason, Integer transitionState) {
        String condition = (transitionState == null ? "" : ", transitionState = :transitionState");
        Query query = entityManager.createQuery("update Card set "
                + " state = :state, "
                + " validTime = :validTime,"
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime,"
                + " lockReason = :lockReason "
                + condition
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg");
        query.setParameter("state", CardState.BLOCKED.getValue());
        query.setParameter("validTime", new Date());
        query.setParameter("issueTime", new Date());
        query.setParameter("updateTime", new Date());
        query.setParameter("lockReason", lockReason);
        query.setParameter("cardNo", cardNo);
        query.setParameter("idOfOrg", idOfOrg);
        if (transitionState != null) {
            query.setParameter("transitionState", transitionState);
        }
        return query.executeUpdate();
    }

    public int blockAndReset(long cardNo, long idOfOrg, Long idOfClient, Boolean isOldArm, String lockReason,
            Integer transitionState) {
        if (!isOldArm) return blockAndReset(cardNo, idOfOrg, lockReason, transitionState);
        String condition = (idOfClient == null) ? " and org.idOfOrg in :idOfOrgs" : " and client.idOfClient = :idOfClient";
        String conditionTransition = (transitionState == null ? "" : ", transitionState = :transitionState ");
        Query query = entityManager.createQuery("update Card set "
                + " state = :state, "
                + " validTime = :validTime,"
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime,"
                + " lockReason = :lockReason "
                + conditionTransition
                + " where cardNo = :cardNo"
                + condition);
        query.setParameter("state", CardState.BLOCKED.getValue());
        query.setParameter("validTime", new Date());
        query.setParameter("issueTime", new Date());
        query.setParameter("updateTime", new Date());
        query.setParameter("cardNo", cardNo);
        query.setParameter("lockReason", lockReason);
        if (idOfClient == null) {
            query.setParameter("idOfOrgs", DAOUtils.findFriendlyOrgIds((Session)entityManager.getDelegate(), idOfOrg));
        } else {
            query.setParameter("idOfClient", idOfClient);
        }
        if (transitionState != null) {
            query.setParameter("transitionState", transitionState);
        }
        return query.executeUpdate();
    }

    public int reset(long cardNo, long idOfOrg) {
        return entityManager.createQuery("update Card set "
                + " client = null, "
                + " state = :state, "
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " validTime = :validTime "
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg")
                .setParameter("state", CardState.FREE.getValue())
                .setParameter("validTime", new Date())
                .setParameter("issueTime", new Date())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .setParameter("idOfOrg", idOfOrg)
                .executeUpdate();
    }

    public int reset(long cardNo, long idOfOrg, String reason) {
        return entityManager.createQuery("update Card set "
                + " client = null, "
                + " state = :state, "
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " validTime = :validTime, "
                + " lockReason = :reason "
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg")
                .setParameter("state", CardState.FREE.getValue())
                .setParameter("validTime", new Date())
                .setParameter("issueTime", new Date())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("reason", reason)
                .executeUpdate();
    }

    public int reset(long cardNo, long idOfOrg, Long idOfClient, Boolean isOldArm) {
        if (!isOldArm) return reset(cardNo, idOfOrg);
        String condition = (idOfClient == null) ? " and org.idOfOrg in :idOfOrgs" : " and client.idOfClient = :idOfClient";
        Query query = entityManager.createQuery("update Card set "
                + " client = null, "
                + " state = :state, "
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " validTime = :validTime "
                + " where cardNo = :cardNo"
                + condition);
        query.setParameter("state", CardState.FREE.getValue());
        query.setParameter("validTime", new Date());
        query.setParameter("issueTime", new Date());
        query.setParameter("updateTime", new Date());
        query.setParameter("cardNo", cardNo);
        if (idOfClient == null) {
            query.setParameter("idOfOrgs", DAOUtils.findFriendlyOrgIds((Session)entityManager.getDelegate(), idOfOrg));
        } else {
            query.setParameter("idOfClient", idOfClient);
        }
        return query.executeUpdate();
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
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg")
                .setParameter("state", CardState.ISSUED.getValue())
                .setParameter("validTime", o.getValidDate())
                .setParameter("issueTime", o.getOperationDate())
                .setParameter("updateTime", new Date())
                .setParameter("visitor", visitor)
                .setParameter("cardNo", o.getCardNo())
                .setParameter("idOfOrg", idOfOrg)
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
        String condition = o.getOrgOwner() == null ? " and idOfOrg in :idOfOrgs" : " and idOfOrg = :idOfOrg";
        Query query = entityManager.createQuery("update Card set "
                + " client = :client, "
                + " state = :state, "
                + " validTime = :validTime,"
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime, "
                + " visitor = null "
                + " where cardNo = :cardNo"
                + condition);

        query.setParameter("state", CardState.ISSUED.getValue());
        query.setParameter("client", client);
        query.setParameter("validTime", o.getValidDate());
        query.setParameter("issueTime", o.getOperationDate());
        query.setParameter("updateTime", new Date());
        query.setParameter("cardNo", o.getCardNo());
        if (o.getOrgOwner() == null) {
            query.setParameter("idOfOrgs", DAOUtils.findFriendlyOrgIds((Session)entityManager.getDelegate(), idOfOrg));
        } else {
            query.setParameter("idOfOrg", o.getOrgOwner());
        }

        return query.executeUpdate();
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
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg")
                .setParameter("state", CardState.TEMPISSUED.getValue())
                .setParameter("client", client)
                .setParameter("validTime", o.getValidDate())
                .setParameter("issueTime", o.getOperationDate())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", o.getCardNo())
                .setParameter("idOfOrg", idOfOrg)
                .executeUpdate();
    }

    public int blockPermanent(long cardNo, long idOfOrg) {
        return entityManager.createQuery("update Card set "
                + " state = :state, "
                + " updateTime = :updateTime "
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg")
                .setParameter("state", CardState.BLOCKED.getValue())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .setParameter("idOfOrg", idOfOrg)
                .executeUpdate();
    }

    public int returnCardToClient(long cardNo, long idOfOrg) {
        return entityManager.createQuery("update Card set "
                + " state = :state, "
                + " issueTime = :issueTime ,"
                + " updateTime = :updateTime "
                + " where cardNo = :cardNo"
                + "     and org.idOfOrg = :idOfOrg")
                .setParameter("state", CardState.ISSUED.getValue())
                .setParameter("issueTime", new Date())
                .setParameter("updateTime", new Date())
                .setParameter("cardNo", cardNo)
                .setParameter("idOfOrg", idOfOrg)
                .executeUpdate();
    }

    public List<Card> findAllByClientList(List<Client> clients) {
        StringBuilder sb = new StringBuilder();
        for (Client cl : clients) {
            sb.append(cl.getIdOfClient()).append(",");
        }
        Query q = entityManager.createNativeQuery("create temp table clients_for_cards (idOfClient bigint) on commit drop");
        q.executeUpdate();
        String str = sb.toString();
        q = entityManager.createNativeQuery(String.format("insert into clients_for_cards(idOfClient) values(unnest(cast(string_to_array('%s', ',') as bigint[])))", str.substring(0, str.length()-1)));
        q.executeUpdate();
        q = entityManager.createNativeQuery("select c.IdOfCard, c.Version, c.IdOfClient, c.IdOfVisitor, c.idoforg, c.CardNo, c.CardType, c.CreatedDate, c.LastUpdate, c.State, c.LockReason, "
                + "c.ValidDate, c.IssueDate, c.LifeState, c.CardPrintedNo, c.ExternalId, c.CardSignCertNum, c.IsLongUid, c.transitionstate, c.longCardNo "
                + "from cf_cards c inner join clients_for_cards cfc on c.idOfClient = cfc.idOfClient", Card.class);
        return q.getResultList();
    }

    public boolean updateCardSync(Long idOforg, Card card, Long changeState) {
        TypedQuery<CardSync> query = entityManager.createQuery(
                "from CardSync cs where cs.org.idOfOrg=:idOforg and cs.card=:card", CardSync.class);
        query.setParameter("idOforg",idOforg);
        query.setParameter("card", card);
        List<CardSync> resultList = query.getResultList();
        if(resultList.size()> 0){
            CardSync cardSync = query.getResultList().get(0);
            cardSync.setStatechange(changeState);
            entityManager.persist(cardSync);
            return true;
        }else
            return false;
    }

    public Card findByLongCardNo(Long longCardNo, Long idOfOrg) {
        try {
            TypedQuery<Card> query = entityManager.createQuery(
                    "from Card c left join fetch c.client inner join fetch c.org "
                            + "where c.longCardNo=:longCardNo and c.org.idOfOrg=:idOfOrg", Card.class);
            query.setParameter("longCardNo", longCardNo);
            query.setParameter("idOfOrg", idOfOrg);
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (PersistenceException e){
            return null;
        }
    }
}
