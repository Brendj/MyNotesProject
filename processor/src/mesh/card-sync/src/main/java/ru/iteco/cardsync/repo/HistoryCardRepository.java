/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import ru.iteco.cardsync.models.HistoryCard;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryCardRepository extends JpaRepository<HistoryCard, Long> {

}
