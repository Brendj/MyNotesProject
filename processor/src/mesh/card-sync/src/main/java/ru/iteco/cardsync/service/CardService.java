/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.CardActionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CardService {
    private Logger log = LoggerFactory.getLogger(CardService.class);

    private final CardActionRequestService cardActionRequestService;

    public CardService(CardActionRequestService cardActionRequestService){
        this.cardActionRequestService = cardActionRequestService;
    }

    public void unblockCardForClient(BlockPersonEntranceRequest request) {
        if(request == null){
            return;
        }

        CardActionRequest fromDB = cardActionRequestService.findByRequestId(request.getId());
        if(fromDB == null){
            cardActionRequestService.writeRecord(request,);
        }
    }

    public void blockCardForClient(BlockPersonEntranceRequest request) {

    }
}
