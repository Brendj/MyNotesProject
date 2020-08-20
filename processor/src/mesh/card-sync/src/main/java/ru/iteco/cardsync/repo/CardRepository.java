/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.cardsync.models.Card;
import ru.iteco.cardsync.models.Client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query(value =
            "SELECT * "
                    + "FROM cf_cards "
                    + "WHERE idofclient = :idofclient"
                    + "  AND state = :state and lockreason = :lockReason ", // Только активные карты
            nativeQuery = true)
    List<Card> findAllByClientAndLockReasonLikeAndState(@Param("idofclient") Long idofclient,
                                                        @Param("lockReason") String lockReason,
                                                        @Param("state") Integer state);

    @Query(value =
            "SELECT * "
                    + "FROM cf_cards "
                    + "WHERE idofclient = :idofclient"
                    + "  AND state in (0,1,4); ", // Только активные карты
            nativeQuery = true)
    List<Card> findAllActiveCardByClient(@Param("idofclient") Long idofclient);
}
