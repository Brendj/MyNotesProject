/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo;

import ru.iteco.msp.models.DiscountChangeHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountChangeHistoryRepo extends JpaRepository<DiscountChangeHistory, Long> {
    List<DiscountChangeHistory> getAll(Pageable pageable);
}
