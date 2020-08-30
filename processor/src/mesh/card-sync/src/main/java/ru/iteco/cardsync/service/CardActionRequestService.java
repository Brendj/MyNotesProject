/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.CardActionClient;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.models.Client;
import ru.iteco.cardsync.repo.CardActionRequestRepository;

import org.springframework.stereotype.Service;

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

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed){
        writeRecord(blockPersonEntranceRequest, message, processed, null, null, null);
    }

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed, Client client){
        writeRecord(blockPersonEntranceRequest, message, processed, client, null, null);
    }

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed,
                            Client client, Card card, Client clientChild){
        CardActionRequest request = CardActionRequest.buildCardActionRequest(blockPersonEntranceRequest);
        request.setComment(message);
        request.setProcessed(processed);

        if (client != null || card != null || clientChild != null) {
            CardActionClient cardActionClient = new CardActionClient();
//            cardActionClient.setRequestId(blockPersonEntranceRequest.getId());
            cardActionClient.setClient(client);
            cardActionClient.setCard(card);
            cardActionClient.setClientChild(clientChild);
            request.getCardactionclient().add(cardActionClient);
        }

        cardActionRequestRepository.save(request);
    }
}
