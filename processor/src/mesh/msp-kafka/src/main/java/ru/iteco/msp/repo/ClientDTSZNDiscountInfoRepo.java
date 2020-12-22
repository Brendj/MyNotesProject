/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo;

import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientDTSZNDiscountInfoRepo extends JpaRepository<ClientDTSZNDiscountInfo, Long> {
    @Query("SELECT cdi FROM ClientDTSZNDiscountInfo cdi WHERE cdi.client= :client AND cdi.lastUpdate BETWEEN :begin AND :end")
    List<ClientDTSZNDiscountInfo> findAllByClientAndLastUpdate(@Param("client") Client client,
                                                               @Param("begin") Long begin,
                                                               @Param("end") Long end);

    ClientDTSZNDiscountInfo findFirstByDTISZNCodeAndClientOrderByLastUpdateDesc(Long code, Client client);
}
