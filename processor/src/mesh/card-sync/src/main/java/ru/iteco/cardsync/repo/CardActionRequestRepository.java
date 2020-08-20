/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.models.CardActionRequest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardActionRequestRepository extends JpaRepository<CardActionRequest, Long> {
    @Query(value =
            "SELECT * "
                    + "FROM cf_cr_cardactionrequests "
                    + "WHERE requestid = :requestId and processed = :processed and \"action\" = :atype",
            nativeQuery = true)
    CardActionRequest findByRequestIdAndProcessedAndActionType(@Param("requestId") String requestId,
                                                               @Param("processed") Boolean processed,
                                                               @Param("atype") Integer type);
}
