/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.assign;

import ru.iteco.msp.models.ClientDiscountHistory;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientDiscountHistoryRepo extends CrudRepository<ClientDiscountHistory, Long> {
    List<ClientDiscountHistory> getAllByRegistryDateGreaterThanEqual(Long time);
}
