/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.models.CardActionRequest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardActionRequestRepository extends JpaRepository<CardActionRequest, Long> {
    CardActionRequest findByRequestIdAndProcessedAndActionType(String requestId, Boolean processed, ActionType type);
}
