/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.Client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByClientAndLockReasonLikeAndState(Client client, String lockReason, Integer state);

    List<Card> findAllByClientAndStateNot(Client client, Integer state);
}
