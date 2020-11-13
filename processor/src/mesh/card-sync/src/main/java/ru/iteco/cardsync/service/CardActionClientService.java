/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import org.springframework.stereotype.Service;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.CardActionClient;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.models.Client;
import ru.iteco.cardsync.repo.CardActionClientRepository;

import java.util.List;

@Service
public class CardActionClientService {
    private final CardActionClientRepository cardActionClientRepository;

    public CardActionClientService(CardActionClientRepository cardActionClientRepository) {
        this.cardActionClientRepository = cardActionClientRepository;
    }

    public void writeRecord(CardActionRequest cardActionRequest, String message, Client client) {
        CardActionClient cardActionClient = new CardActionClient();
        cardActionClient.setClient(client);
        cardActionClient.setCardActionRequest(cardActionRequest);
        cardActionClient.setComment(message);
        cardActionClientRepository.save(cardActionClient);
    }

    public void writeRecord(CardActionRequest request, Client client, Card card, Client clientChild, String comment, Integer oldState) {
        CardActionClient cardActionClient = new CardActionClient();
        cardActionClient.setClient(client);
        cardActionClient.setCard(card);
        cardActionClient.setClientChild(clientChild);
        cardActionClient.setComment(comment);
        cardActionClient.setCardActionRequest(request);
        cardActionClient.setOldcardstate(oldState);
        cardActionClientRepository.save(cardActionClient);
    }

    public void writeRecord(CardActionRequest cardActionRequest, CardActionClient cardActionClient,
                            List<Card> cardsActive, String comment) {
        CardActionClient cardActionClientnew = new CardActionClient();
        cardActionClientnew.setComment(comment);
        cardActionClientnew.setCardActionRequest(cardActionRequest);
        cardActionClientnew.setCard(cardActionClient.getCard());
        cardActionClientnew.setClient(cardActionClient.getClient());
        cardActionClientnew.setClientChild(cardActionClient.getClientChild());
        if (cardsActive != null) {
            String actionCards = "";
            for (Card card : cardsActive) {
                actionCards = actionCards + card.getIdOfCard() + ",";
            }
            if (actionCards.length() > 1)
                actionCards = actionCards.substring(0, actionCards.length()-1);
            cardActionClientnew.setIdOldCards(actionCards);
        }
        cardActionClientRepository.save(cardActionClientnew);
    }
}
