/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistryItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistryItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 23.04.15
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    @Autowired
    private CardWritableRepository cardWritableRepository;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private ClientWritableRepository clientWritableRepository;

    public static CardService getInstance(){
        return RuntimeContext.getAppContext().getBean(CardService.class);
    }

    private Card updateCard(Card card){
        card.setIssueTime(new Date());
        card.setUpdateTime(new Date());
        cardWritableRepository.update(card);
        return card;
    }

    private void updateClient(Client client){
        client.setUpdateTime(new Date());
        clientWritableRepository.update(client);
    }


    //1. Регистрация карты
    public Card registerNew(Org org, long cardNo, long cardPrintedNo, int type, Long longCardNo,
            Integer cardSignVerifyRes, Integer cardSignCertNum, Boolean isLongUid) throws Exception {
        return cardWritableRepository.createCard(org, cardNo, cardPrintedNo, type, longCardNo,
                cardSignVerifyRes, cardSignCertNum, isLongUid);
    }

    public Card registerNew(Org org, long cardNo, long cardPrintedNo, int type, Long longCardNo, Integer cardSignVerifyRes,
            Integer cardSignCertNum, Boolean isLongUid, Integer cardTransitionState) throws Exception {
        return cardWritableRepository.createCard(org, cardNo, cardPrintedNo, type, longCardNo,
                cardSignVerifyRes, cardSignCertNum, isLongUid, cardTransitionState);
    }

    public void updateTransitionState(Card card, Integer transitionState) {
        card.setTransitionState(transitionState);
        card.setUpdateTime(new Date());
    }

    public Card registerNewSpecial(long idOfOrg, long cardNo, long cardPrintedNo, int type, Long longCardNo,
            Integer cardSignCertNum) throws Exception {
        Org org = orgRepository.findOne(idOfOrg);
        if (org == null) throw new Exception("Org not found");
        return cardWritableRepository.createCardSpecial(org, cardNo, cardPrintedNo, type, longCardNo, cardSignCertNum);
    }

    //1. Регистрация карты
    public ResCardsOperationsRegistryItem registerNew(CardsOperationsRegistryItem o, long idOfOrg){
        Card card = cardWritableRepository.findByCardNoWithoutClient(o.getCardNo(), idOfOrg);
        if(card != null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
        }else {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }
    }

    //2. Выдача карты клиенту
    public int issueToClient(CardsOperationsRegistryItem o, long cardNo, long idOfClient, long idOfOrg){
        return cardWritableRepository.issueToClient(o, idOfOrg);
    }

    public ResCardsOperationsRegistryItem issueToClient(CardsOperationsRegistryItem o, long idOfOrg) {
        return cardUpdateResult(o, issueToClient(o, o.getCardNo(), o.getIdOfClient(), idOfOrg));

    }

    //3.Выдача карты клиенту во временное пользование
    public int issueToClientTemp(CardsOperationsRegistryItem o, long cardNo, long idOfClient, long idOfOrg){
        return cardWritableRepository.issueToClientTemp(o, idOfOrg);
    }

    public ResCardsOperationsRegistryItem issueToClientTemp(CardsOperationsRegistryItem o, long idOfOrg) {
        return cardUpdateResult(o, issueToClientTemp(o, o.getCardNo(), o.getIdOfClient(), idOfOrg));
    }

    //4. Выдача карты посетителю
    public int issueToVisitor(CardsOperationsRegistryItem o,long cardNo,  long idOfOrg){
        return cardWritableRepository.issueToVisitor(o, idOfOrg);

    }
    public ResCardsOperationsRegistryItem issueToVisitor(CardsOperationsRegistryItem o, long idOfOrg) {
        return cardUpdateResult(o, issueToVisitor(o, o.getGlobalId(), idOfOrg));
    }

    //5. Сброс (возврат, аннулирование) карты
    public int reset(long cardNo, long idOfOrg){
        return cardWritableRepository.reset(cardNo, idOfOrg);
    }
    public void resetAllCards(long idOfClient){
        Client client = clientWritableRepository.findWithCards(idOfClient);
        if(client!= null){
            resetAllCards(client);
        }
    }
    public int reset(long cardNo, long idOfOrg, Long idOfClient, Boolean isOldArm) {
        return cardWritableRepository.reset(cardNo, idOfOrg, idOfClient, isOldArm);
    }

    public void resetAllCards(Client client){
        for (Card card : client.getCards()) {
            reset(card.getCardNo(), card.getOrg().getIdOfOrg());
        }
    }

    public void reset(long cardNo){
        cardWritableRepository.findOne(cardNo);

    }
    public void reset(Card card){
        reset(card.getCardNo(), card.getOrg().getIdOfOrg());
    }

    public ResCardsOperationsRegistryItem reset(CardsOperationsRegistryItem o, long idOfOrg) {
        return cardUpdateResult(o, reset(o.getCardNo(), idOfOrg));
    }

    public ResCardsOperationsRegistryItem reset(CardsOperationsRegistryItem o, long idOfOrg, Boolean isOldArm) {
        return cardUpdateResult(o, reset(o.getCardNo(), idOfOrg, o.getIdOfClient(), isOldArm));
    }
    //6.	Блокировка карты
    public int block(long cardNo, long idOfOrg, long idOfClient, Boolean isOldArm, String lockReason, CardState blockState) {
        return cardWritableRepository.block(cardNo, idOfOrg, idOfClient, isOldArm, lockReason, blockState);
    }

    public ResCardsOperationsRegistryItem tempblock(CardsOperationsRegistryItem o, long idOfOrg, Boolean isOldArm) {
        return cardUpdateResult(o, block(o.getCardNo(), idOfOrg, o.getIdOfClient(), isOldArm, "", CardState.TEMPBLOCKED));
    }


    //7.	Блокировка карты со сбросом
    public int blockAndReset(long cardNo, long idOfOrg, Long idOfClient, Boolean isOldArm, String lockReason,
            Integer transitionState) {
        return cardWritableRepository.blockAndReset(cardNo, idOfOrg, idOfClient, isOldArm, lockReason, transitionState);
    }

    public ResCardsOperationsRegistryItem block(CardsOperationsRegistryItem o, long idOfOrg, Boolean isOldArm) {
        return cardUpdateResult(o, blockAndReset(o.getCardNo(), idOfOrg, o.getIdOfClient(), isOldArm, "", null));
    }
    //8.	Разблокировка карты
    public void unblock(Card card, CardsOperationsRegistryItem o){
        if(CardState.TEMPBLOCKED.getValue() == card.getState()){
            if (card.getClient()!= null){
                if(o.getTemp() != null && o.getTemp() ){
                    card.setState(CardState.TEMPISSUED.getValue());
                }else{
                    card.setState(CardState.ISSUED.getValue());
                }
            } else {
                card.setState(CardState.FREE.getValue());
            }
            card.setUpdateTime(new Date());
            updateCard(card);
            if(card.getClient() != null){
                updateClient(card.getClient());
            }
        }else if (CardState.BLOCKED.getValue() == card.getState()){
            reset(o, card.getOrg().getIdOfOrg());
        }
    }

    public ResCardsOperationsRegistryItem unblock(CardsOperationsRegistryItem o, long idOfOrg, Boolean isOldArm) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo(), idOfOrg);
        if (card == null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }
        if(o.getValidDate() != null){
            card.setValidTime(o.getValidDate());
        }
        card.setIssueTime(o.getOperationDate());
        unblock(card, o);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }


    private ResCardsOperationsRegistryItem cardUpdateResult(CardsOperationsRegistryItem o, int result){
        if (result == 0){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }else {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
        }
    }

    public Card unblockOrReturnCard(Long cardNo, Long longCardNo, Long idOfOrg) throws Exception {
        Card card = null;
        if(longCardNo == null){
            card = cardWritableRepository.findByCardNo(cardNo, idOfOrg);
        } else {
            card = cardWritableRepository.findByLongCardNo(longCardNo, idOfOrg);
        }
        if (null == card) {
            throw new CardNotFoundException(
                    String.format("UnblockOrReturnCard error: unable to find card with cardNo=%d and idOfOrg=%d", cardNo, idOfOrg)
            );
        }

        if (CardState.TEMPBLOCKED.getValue() == card.getState()) {
            unblock(card);
        } else if ((CardState.BLOCKED.getValue() == card.getState()) || (CardState.TEMPISSUED.getValue() == card.getState()) ||
                (CardState.ISSUED.getValue() == card.getState())) {
            returnCard(card);
        } else {
            throw new CardWrongStateException(String.format("UnblockOrReturnCard error: wrong card state was found - state=%d", card.getState()));
        }
        return card;
    }

    private void unblock(Card card) throws Exception {
        CardState newState;

        if (CardState.TEMPBLOCKED.getValue() == card.getState()) {
            if (null != card.getClient()) {
                newState = CardState.ISSUED;
            } else {
                newState = CardState.FREE;
            }

            List<Card> cardList = CardReadOnlyRepository.getInstance().findAllByClient(card.getClient());
            for (Card c : cardList) {
                if (CardState.TEMPISSUED.getValue() == c.getState()) {
                    if (0 != cardWritableRepository.blockPermanent(c.getCardNo(), c.getOrg().getIdOfOrg())) {
                        throw new Exception(String.format("Unblock card error: unable to block temp card with cardNo=%d and idOfOrg=%d",
                                c.getCardNo(), c.getOrg().getIdOfOrg()));
                    }
                } else if (CardState.ISSUED.getValue() == c.getState()) {
                    throw new CardWrongStateException(String.format("Unblock card error: wrong card state was found - state=%d", c.getState()));
                }
            }

            card.setState(newState.getValue());
            card.setIssueTime(new Date());
            card.setUpdateTime(new Date());
            cardWritableRepository.saveEntity(card);
        } else {
            throw new CardWrongStateException(String.format("Unblock card error: wrong card state was found - state=%d", card.getState()));
        }
    }

    private void returnCard(Card card) throws Exception {
        if (CardTransitionState.GIVEN_AWAY.getCode().equals(card.getTransitionState())) {
            throw new CardUidGivenAwayException("Return card error: card's uid was given away");
        }
        if (CardState.TEMPISSUED.getValue() == card.getState()) {
            if (0 == cardWritableRepository.reset(card.getCardNo(), card.getOrg().getIdOfOrg(), "")) {
                throw new Exception(
                        String.format("Return card error: unable to return card with cardNo=%d and idOfOrg=%d",
                                card.getCardNo(), card.getOrg().getIdOfOrg()));
            }
            List<Card> cardList = CardReadOnlyRepository.getInstance().findAllByClient(card.getClient());
            for (Card c : cardList) {
                if (CardState.TEMPBLOCKED.getValue() == c.getState()) {
                    if (0 == cardWritableRepository.returnCardToClient(card.getCardNo(), card.getOrg().getIdOfOrg())) {
                        throw new Exception(String.format("Return card error: unable to unblock card with cardNo=%d and idOfOrg=%d",
                                c.getCardNo(), c.getOrg().getIdOfOrg()));
                    }
                    break;
                }
            }
        } else if (CardState.BLOCKED.getValue() == card.getState() || CardState.ISSUED.getValue() == card.getState()) {
            if (0 == cardWritableRepository.reset(card.getCardNo(), card.getOrg().getIdOfOrg(),"")) {
                throw new Exception(
                        String.format("Return card error: unable to return card with cardNo=%d and idOfOrg=%d",
                                card.getCardNo(), card.getOrg().getIdOfOrg()));
            }
        } else {
            throw new CardWrongStateException(String.format("Return card error: wrong card state was found - state=%d", card.getState()));
        }
    }

    public void updateSyncStatus(Card card, Long idOfOrg, Long changeState, boolean fiendlyOrgs) throws Exception {
        if (!fiendlyOrgs) {
            CardWritableRepository.getInstance().updateCardSync(idOfOrg, card, changeState);
        }
        else
        {
            for (Long friendlyOrgid : DAOService.getInstance().findFriendlyOrgsIds(idOfOrg)) {
                CardWritableRepository.getInstance().updateCardSync(friendlyOrgid, card, changeState);
            }
        }
    }
}
