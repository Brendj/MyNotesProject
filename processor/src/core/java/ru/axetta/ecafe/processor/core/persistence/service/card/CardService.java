/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistryItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.cards.CardsOperationsRegistryItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


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
    private CardReadOnlyRepository cardReadOnlyRepository;

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
    public Card registerNew(long idOfOrg, long cardNo, long cardPrintedNo, int type){
        Org org = orgRepository.findOne(idOfOrg);
        return cardWritableRepository.createCard(org, cardNo, cardPrintedNo, type);
    }

    //1. Регистрация карты
    public ResCardsOperationsRegistryItem registerNew(CardsOperationsRegistryItem o, long idOfOrg){
        Card card = cardReadOnlyRepository.findByCardNo(o.getCardNo());
        if(card != null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
        }else {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }
    }

    //2. Выдача карты клиенту
    public int issueToClient(long cardNo, long idOfClient, long idOfOrg){
        return cardWritableRepository.issueToClient(cardNo, idOfClient, idOfOrg);
    }

    public ResCardsOperationsRegistryItem issueToClient(CardsOperationsRegistryItem o, long idOfOrg) {
        return cardUpdateResult(o, issueToClient(o.getCardNo(), o.getIdOfClient(), idOfOrg));

    }

    //3.Выдача карты клиенту во временное пользование
    public int issueToClientTemp(long cardNo, long idOfClient, long idOfOrg){
        return cardWritableRepository.issueToClientTemp(cardNo, idOfClient, idOfOrg);
    }

    public ResCardsOperationsRegistryItem issueToClientTemp(CardsOperationsRegistryItem o, long idOfOrg) {
        return cardUpdateResult(o, issueToClientTemp(o.getCardNo(), o.getIdOfClient(), idOfOrg));
    }

    //4. Выдача карты посетителю
    public int issueToVisitor(long cardNo, long idOfVisitor, long idOfOrg){
        return cardWritableRepository.issueToVisitor(cardNo, idOfVisitor, idOfOrg);

    }
    public ResCardsOperationsRegistryItem issueToVisitor(CardsOperationsRegistryItem o, long idOfOrg) {
        return cardUpdateResult(o, issueToVisitor(o.getCardNo(), o.getGlobalId(), idOfOrg));
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
    //6.	Блокировка карты
    public int block(long cardNo, long idOfOrg){
        return cardWritableRepository.block(cardNo, idOfOrg);
    }

    public ResCardsOperationsRegistryItem block(CardsOperationsRegistryItem o, long idOfOrg) {
        int block = block(o.getCardNo(), idOfOrg);

        return cardUpdateResult(o, block);
    }


    //7.	Блокировка карты со сбросом
    public int blockAndReset(long cardNo, long idOfOrg){
        return cardWritableRepository.blockAndReset(cardNo, idOfOrg);
    }

    public ResCardsOperationsRegistryItem blockAndReset(CardsOperationsRegistryItem o, long idOfOrg) {
        int result = blockAndReset(o.getCardNo(), idOfOrg);
        return cardUpdateResult(o, result);
    }
    //8.	Разблокировка карты
    public void unblock(Card card, CardsOperationsRegistryItem o){
        if(CardState.BLOCKED.getValue() == card.getState()){
            if (card.getClient()!= null){
                if(o.getTemp() != null && o.getTemp() ){
                    card.setState(CardState.ISSUEDTEMP.getValue());
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
        }else if (CardState.BLOCKEDANDRESET.getValue() == card.getState()){
            reset(o, card.getOrg().getIdOfOrg());
        }
    }

    public ResCardsOperationsRegistryItem unblock(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo());
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
}
