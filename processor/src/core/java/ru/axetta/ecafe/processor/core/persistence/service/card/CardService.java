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
import ru.axetta.ecafe.processor.core.persistence.dao.visitor.VisitorWritableRepository;
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
    public void issueToClient(Card card, Client client){
        card.setClient(client);
        client.getCardsInternal().add(card);
        card.setState(CardState.ISSUED.getValue());
        card.setUpdateTime(new Date());
        updateClient(client);
    }

    public ResCardsOperationsRegistryItem issueToClient(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo());
        if(card == null) {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }
        Client client = clientWritableRepository.findWithCards(o.getIdOfClient());
        if(client == null) {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CLIENT_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CLIENT_NOT_FOUND_MESSAGE);
        }
        if(client.getCards().size()>0){
            for (Card card1 : client.getCards()) {
                reset(card1);
            }
        }
        card.setValidTime(o.getValidDate());
        card.setIssueTime(o.getOperationDate());

        issueToClient(card, client);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }

    //3.Выдача карты клиенту во временное пользование
    public void issueToClientTemp(Card card, Client client){
        card.setClient(client);
        client.getCardsInternal().add(card);
        card.setState(CardState.ISSUEDTEMP.getValue());
        card.setUpdateTime(new Date());
        updateClient(client);
    }

    public ResCardsOperationsRegistryItem issueToClientTemp(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo());
        if(card == null) {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }
        Client client = clientWritableRepository.findWithCards(o.getIdOfClient());
        if(client == null) {
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CLIENT_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CLIENT_NOT_FOUND_MESSAGE);
        }
        card.setIssueTime(o.getOperationDate());
        card.setValidTime(o.getValidDate());
        issueToClientTemp(card, client);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }

    //4. Выдача карты посетителю
    public Visitor issueToVisitor(Card card, Visitor visitor){
        card.setClient(null);
        card.setVisitor(visitor);
        visitor.getCardsInternal().add(card);
        card.setState(CardState.ISSUED.getValue());
        card.setUpdateTime(new Date());
        return visitor;
    }
    public ResCardsOperationsRegistryItem issueToVisitor(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo());
        if (card == null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }
        VisitorWritableRepository visitorWritableRepository = VisitorWritableRepository.getInstance();

        Visitor visitor = visitorWritableRepository.find(o.getGlobalId());
        card.setIssueTime(o.getOperationDate());
        card.setValidTime(o.getValidDate());
        visitorWritableRepository.update(issueToVisitor(card, visitor));

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }

    //5. Сброс (возврат, аннулирование) карты
    public void reset(Card card){
        card.setClient(null);
        card.setState(CardState.FREE.getValue());
        card.setValidTime(new Date());
        updateCard(card);
    }
    public void resetAllCards(long idOfClient){
        Client client = clientWritableRepository.findWithCards(idOfClient);
        if(client!= null){
            resetAllCards(client);
        }
    }

    public void resetAllCards(Client client){
        for (Card card : client.getCards()) {
            reset(card);
        }
    }

    public void reset(long cardNo){
        Card card = cardWritableRepository.findByCardNo(cardNo);
        reset(card);
    }

    public ResCardsOperationsRegistryItem reset(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo());
        if (card == null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }

        reset(card);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }
    //6.	Блокировка карты
    public void block(Card card){
        card.setState(CardState.BLOCKED.getValue());
        card.setValidTime(new Date());
        card.setIssueTime(new Date());
        card.setUpdateTime(new Date());
        updateCard(card);
        if(card.getClient() != null){
            updateClient(card.getClient());
        }
    }

    public ResCardsOperationsRegistryItem block(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo());
        if (card == null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }

        block(card);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
    }
    //7.	Блокировка карты со сбросом
    public void blockAndReset(Card card){

        //Client client = card.getClient();
        card.setClient(null);
        card.setState(CardState.BLOCKEDANDRESET.getValue());
        card.setValidTime(new Date());
        card.setIssueTime(new Date());
        card.setUpdateTime(new Date());
        updateCard(card);
    }



    public ResCardsOperationsRegistryItem blockAndReset(CardsOperationsRegistryItem o, long idOfOrg) {
        Card card = cardWritableRepository.findByCardNo(o.getCardNo());
        if (card == null){
            return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND, ResCardsOperationsRegistryItem.ERROR_CARD_NOT_FOUND_MESSAGE);
        }

        blockAndReset(card);

        return new ResCardsOperationsRegistryItem(o.getIdOfOperation(), ResCardsOperationsRegistryItem.OK, ResCardsOperationsRegistryItem.OK_MESSAGE);
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
            reset(card);
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

}
