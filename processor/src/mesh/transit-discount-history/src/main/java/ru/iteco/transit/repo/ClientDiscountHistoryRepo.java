/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.repo;

import ru.iteco.transit.models.ClientDiscountHistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDiscountHistoryRepo extends JpaRepository<ClientDiscountHistory, Long> {
}
