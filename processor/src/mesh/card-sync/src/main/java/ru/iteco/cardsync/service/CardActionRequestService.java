/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.repo.CardActionRequestRepository;

import org.springframework.stereotype.Service;

@Service
public class CardActionRequestService {
    private final CardActionRequestRepository cardActionRequestRepository;

    public CardActionRequestService(CardActionRequestRepository cardActionRequestRepository) {
        this.cardActionRequestRepository = cardActionRequestRepository;
    }

    public CardActionRequest findByRequestId(String id) {
        return cardActionRequestRepository.findByRequestId(id);
    }

    public void writeRecord(BlockPersonEntranceRequest blockPersonEntranceRequest, String message, boolean processed){
        CardActionRequest request = CardActionRequest.buildCardActionRequest(blockPersonEntranceRequest);
        request.setComment(message);
        request.setProcessed(processed);

        cardActionRequestRepository.save(request);
    }
}
