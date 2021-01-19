/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo;

import ru.iteco.msp.models.ClientDiscountHistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDiscountHistoryRepo extends JpaRepository<ClientDiscountHistory, Long> {
}
