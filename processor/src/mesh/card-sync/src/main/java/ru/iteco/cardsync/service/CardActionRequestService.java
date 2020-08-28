/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
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

    public List<CardActionRequest> findRequestBlockByRequestId(String id) {
        return cardActionRequestRepository.findByRequestIdAndProcessedAndActionType(id, true, 0);
    }

    public List<CardActionRequest> findRequestBlockByRequestIdFull(String id) {
        return cardActionRequestRepository.findByRequestIdAndProcessedAndActionTypeFull(id, 0);
    }

    public List<CardActionRequest> findRequestUnblockByRequestIdFull(String id) {
        return cardActionRequestRepository.findByRequestIdAndProcessedAndActionTypeFull(id, 1);
    }

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed){
        writeRecord(blockPersonEntranceRequest, message, processed, null);
    }

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed, Client client){
        CardActionRequest request = CardActionRequest.buildCardActionRequest(blockPersonEntranceRequest);
        request.setComment(message);
        request.setProcessed(processed);
        request.setClient(client);

        cardActionRequestRepository.save(request);
    }
}
