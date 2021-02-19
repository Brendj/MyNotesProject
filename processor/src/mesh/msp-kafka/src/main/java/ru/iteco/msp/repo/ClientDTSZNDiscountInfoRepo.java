/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo;

import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDTSZNDiscountInfoRepo extends JpaRepository<ClientDTSZNDiscountInfo, Long> {
    ClientDTSZNDiscountInfo findFirstByDTISZNCodeAndClientAndLastUpdateBetweenOrderByLastUpdateDesc(Long code,
            Client client, Long begin,  Long end);

    ClientDTSZNDiscountInfo findFirstByDTISZNCodeAndClientOrderByLastUpdateDesc(Long code, Client client);
}
