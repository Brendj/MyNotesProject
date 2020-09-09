/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.*;
import ru.iteco.cardsync.repo.CardSyncRepository;
import ru.iteco.cardsync.repo.ClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CardSyncService {

    private final Logger log = LoggerFactory.getLogger(CardSyncService.class);
    private final CardSyncRepository cardSyncRepository;

    public CardSyncService(CardSyncRepository cardSyncRepository) {
        this.cardSyncRepository = cardSyncRepository;
    }

    public void savechangeforCard(Card card, Long idoforg)
    {
        List<Long> friendlyorgs = cardSyncRepository.findAllFiendlyOrgs(idoforg);
        List<CardSync> cardSyncList = cardSyncRepository.findCardSyncbyCardandOrgs(card.getIdOfCard(), friendlyorgs);
        boolean find;
        for (Long idOrg: friendlyorgs)
        {
            find = false;
            for (CardSync cardSync: cardSyncList)
            {
                if (cardSync.getIdoforg().equals(idOrg))
                {
                    cardSync.setStatechange(1L);
                    cardSyncRepository.save(cardSync);
                    find = true;
                    break;
                }
            }
            if (!find)
            {
                CardSync cardSync = new CardSync();
                cardSync.setIdoforg(idOrg);
                cardSync.setCard(card);
                cardSync.setStatechange(1L);
                cardSyncRepository.save(cardSync);
            }
        }
    }
}
