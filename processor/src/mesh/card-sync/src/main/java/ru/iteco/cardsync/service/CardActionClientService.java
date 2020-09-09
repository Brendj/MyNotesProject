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

    public void writeRecord(CardActionRequest cardActionRequest, CardActionClient cardActionClient, String comment) {
        CardActionClient cardActionClientnew = new CardActionClient();
        cardActionClientnew.setComment(comment);
        cardActionClientnew.setCardActionRequest(cardActionRequest);
        cardActionClientnew.setCard(cardActionClient.getCard());
        cardActionClientnew.setClient(cardActionClient.getClient());
        cardActionClientnew.setClientChild(cardActionClient.getClientChild());
        cardActionClientRepository.save(cardActionClientnew);
    }

}
