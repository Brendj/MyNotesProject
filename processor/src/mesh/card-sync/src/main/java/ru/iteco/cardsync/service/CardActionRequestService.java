/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.CardActionClient;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.models.Client;
import ru.iteco.cardsync.repo.CardActionRequestRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardActionRequestService {
    private final CardActionRequestRepository cardActionRequestRepository;

    public CardActionRequestService(CardActionRequestRepository cardActionRequestRepository) {
        this.cardActionRequestRepository = cardActionRequestRepository;
    }

    public CardActionRequest findRequestBlockByRequestId(String id) {
        return cardActionRequestRepository.findByRequestIdAndProcessedAndActionType(id, true, 0);
    }

    public List<CardActionRequest> findRequestBlockByRequestIdFull(String id) {
        return cardActionRequestRepository.findByRequestIdAndProcessedAndActionTypeFull(id, 0);
    }

    public List<CardActionRequest> findRequestUnblockByRequestIdFull(String id) {
        return cardActionRequestRepository.findByRequestIdAndProcessedAndActionTypeFull(id, 1);
    }

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed) {
        CardActionRequest request = CardActionRequest.buildCardActionRequest(blockPersonEntranceRequest);
        request.setComment(message);
        request.setProcessed(processed);
        cardActionRequestRepository.save(request);
    }

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed,
                            CardActionRequest cardActionRequest) {
        CardActionRequest request = CardActionRequest.buildCardActionRequest(blockPersonEntranceRequest);
        request.setComment(message);
        request.setProcessed(processed);
        request.setCardActionRequest(cardActionRequest);
        cardActionRequestRepository.save(request);
    }


    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed, Client client) {
        CardActionRequest request = CardActionRequest.buildCardActionRequest(blockPersonEntranceRequest);
        request.setComment(message);
        request.setProcessed(processed);
        if (client != null) {
            CardActionClient cardActionClient = new CardActionClient();
            cardActionClient.setClient(client);
            cardActionClient.setCardActionRequest(request);
            List<CardActionClient> cardActionClients = request.getCardActionClients();
            if (cardActionClients == null)
            {
               cardActionClients = new ArrayList<>();
            }
            cardActionClients.add(cardActionClient);
            request.setCardActionClients(cardActionClients);
        }
        cardActionRequestRepository.save(request);
    }

    public void writeRecord(CardActionRequest request, Client client, Card card, Client clientChild, String comment) {
        CardActionClient cardActionClient = new CardActionClient();
        cardActionClient.setClient(client);
        cardActionClient.setCard(card);
        cardActionClient.setClientChild(clientChild);
        cardActionClient.setComment(comment);
       // request.getCardActionClients().add(cardActionClient);
        cardActionRequestRepository.save(request);
    }

    public void writeRecord(CardActionRequest request, String comment, boolean processed) {
        request.setProcessed(processed);
        request.setComment(comment);
        cardActionRequestRepository.save(request);
    }
}
