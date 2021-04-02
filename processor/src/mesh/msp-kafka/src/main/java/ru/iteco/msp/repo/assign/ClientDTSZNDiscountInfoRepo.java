/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.assign;

import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDTSZNDiscountInfoRepo extends JpaRepository<ClientDTSZNDiscountInfo, Long> {
    ClientDTSZNDiscountInfo findFirstByDTISZNCodeAndClientOrderByLastUpdateDesc(Long code, Client client);
}
