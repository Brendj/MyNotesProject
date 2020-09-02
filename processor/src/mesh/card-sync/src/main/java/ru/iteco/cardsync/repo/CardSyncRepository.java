/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.CardSync;
import ru.iteco.cardsync.models.HistoryCard;

import java.util.List;

public interface CardSyncRepository extends JpaRepository<CardSync, Long> {
    @Query(value =
            "select friendlyorg from cf_friendly_organization "
                    + "WHERE currentorg = :currentorg",
            nativeQuery = true)
    List<Long> findAllFiendlyOrgs(@Param("currentorg") Long currentorg);

    @Query(value =
            "SELECT * "
                    + "FROM cf_card_sync "
                    + "WHERE idofcard = :cardId and idoforg in (:orgIds)",
            nativeQuery = true)
    List<CardSync> findCardSyncbyCardandOrgs (@Param("card") Long cardId,
                                          @Param("orgIds") List<Long> orgIds);
}
