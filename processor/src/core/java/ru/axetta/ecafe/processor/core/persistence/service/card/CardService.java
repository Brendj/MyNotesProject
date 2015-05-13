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
import ru.axetta.ecafe.processor.core.sync.handlers.registry.cards.CardsOperationsRegistryItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.ResCardsOperationsRegistryItem;

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
        card.setUpdateTime(new Date());
        cardWritableRepository.update(card);
        return card;
    }

    private Card updateCard(Card card, long l){
        card.setIssueTime(new Date());
        card.setValidTime(new Date(System.currentTimeMillis() + l));

        return updateCard(card);
    }


    //1. Регистрация карты
    public Card registerNew(long idOfOrg, long cardNo, long cardPrintedNo, int type){
        Org org = orgRepository.findOne(idOfOrg);
        return cardWritableRepository.createCard(org, cardNo, cardPrintedNo, type);
    }

    //1. Регистрация карты
    public ResCardsOperationsRegistryItem registerNew(CardsOperationsRegistryItem o, long idOfOrg){
        Card card = cardReadOnlyRepository.find(o.getIdOfCard());
        if(card != null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
        }else {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR, ResCardsOperationsRegistryItem.CARD_NOT_FOUND_MESSAGE);
        }
    }

    //2. Выдача карты клиенту
    public void issueToClient(Card card, Client client){
        card.setClient(client);
        client.getCards().add(card);
        card.setState(CardState.ISSUED.getValue());
        updateCard(card, Card.DEFAULT_CARD_VALID_TIME);
    }

    public ResCardsOperationsRegistryItem issueToClient(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findOne(o.getIdOfCard());
        Client client = clientWritableRepository.find(o.getIdOfClient());

        issueToClient(card, client);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }

    //3.Выдача карты клиенту во временное пользование
    public void issueToClientTemp(Card card, Client client){
        card.setClient(client);
        client.getCards().add(card);
        card.setState(CardState.ISSUEDTEMP.getValue());

        updateCard(card, Card.DEFAULT_TEMP_CARD_VALID_TIME);
    }

    public ResCardsOperationsRegistryItem issueToClientTemp(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findOne(o.getIdOfCard());
        Client client = clientWritableRepository.find(o.getIdOfClient());

        issueToClientTemp(card, client);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }

    //4. Выдача карты посетителю
    public void issueToVisitor(Card card, Visitor visitor){
        //card.//todo
    }
    public ResCardsOperationsRegistryItem issueToVisitor(CardsOperationsRegistryItem o, long idOfOrg) {
        //Card card = cardWritableRepository.findOne(o.getIdOfCard());
        //Client client = clientWritableRepository.find(o.getIdOfClient());
        //
        //issueToClientTemp(card, client);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR, ResCardsOperationsRegistryItem.ERROR_MESSAGE);
    }

    //5. Сброс (возврат, аннулирование) карты
    public void reset(Card card){
        card.setClient(null);
        card.setState(CardState.FREE.getValue());
        card.setValidTime(new Date());

        updateCard(card);
    }

    public ResCardsOperationsRegistryItem reset(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findOne(o.getIdOfCard());


        reset(card);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }
    //6.	Блокировка карты
    public void block(Card card){
        card.setState(CardState.BLOCKED.getValue());

        updateCard(card);
    }

    public ResCardsOperationsRegistryItem block(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findOne(o.getIdOfCard());


        block(card);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }
    //7.	Блокировка карты со сбросом
    public void blockAndReset(Card card){
        card.setState(CardState.BLOCKEDANDRESET.getValue());

        updateCard(card);
    }

    public ResCardsOperationsRegistryItem blockAndReset(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findOne(o.getIdOfCard());


        blockAndReset(card);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }
    //8.	Разблокировка карты
    public void unblock(Card card){
        if(CardState.BLOCKED.getValue() == card.getState()){
            if(card.getValidTime().getTime() > (System.currentTimeMillis() + Card.DEFAULT_TEMP_CARD_VALID_TIME)){
                card.setState(CardState.ISSUED.getValue());
            }else{
                card.setState(CardState.ISSUEDTEMP.getValue());
            }

            updateCard(card);
        }else if (CardState.BLOCKEDANDRESET.getValue() == card.getState()){
            reset(card);
        }
    }

    public ResCardsOperationsRegistryItem unblock(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findOne(o.getIdOfCard());


        unblock(card);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }

}
