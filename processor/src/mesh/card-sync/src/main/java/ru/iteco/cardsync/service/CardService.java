/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.service;

import ru.iteco.cardsync.enums.CardState;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.Client;
import ru.iteco.cardsync.models.HistoryCard;
import ru.iteco.cardsync.repo.CardRepository;
import ru.iteco.cardsync.repo.HistoryCardRepository;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CardService {
    public static final String UNBLOCK_COMMENT = "Режим самоизоляции снят";
    public static final String BLOCK_COMMENT = "Для владельца идентификатора действует режим самоизоляции";

    private final CardRepository cardRepository;
    private final HistoryCardRepository historyCardRepository;

    public CardService(
            CardRepository cardRepository,
            HistoryCardRepository historyCardRepository){
        this.cardRepository = cardRepository;
        this.historyCardRepository = historyCardRepository;
    }

    public List<Card> getBlockedCard(Client client){
        return cardRepository.findAllByClientAndLockReasonLikeAndState(client.getIdOfClient(), BLOCK_COMMENT,
                CardState.BLOCKED.getValue());
    }

    public List<Card> getActiveCard(Client client){
        return cardRepository.findAllActiveCardByClient(client.getIdOfClient());
    }

    public void unblockCard(Card c) {
        Date date = new Date();
        c.setLastUpdate(date.getTime());
        c.setState(CardState.ISSUED.getValue());
        c.setLockReason(null);
        cardRepository.save(c);

        HistoryCard historyCard = HistoryCard.buildHistoryCard(c);
        historyCard.setUpdateTime(date.getTime());
        historyCard.setInfoAboutCard(UNBLOCK_COMMENT);
        historyCardRepository.save(historyCard);
    }

    public void blockCard(Card c) {
        Date date = new Date();
        c.setLastUpdate(date.getTime());
        c.setState(CardState.BLOCKED.getValue());
        c.setLockReason(BLOCK_COMMENT);
        cardRepository.save(c);

        HistoryCard historyCard = HistoryCard.buildHistoryCard(c);
        historyCard.setUpdateTime(date.getTime());
        historyCard.setInfoAboutCard(BLOCK_COMMENT);
        historyCardRepository.save(historyCard);
    }
}
