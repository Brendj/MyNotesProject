/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.repo;

import ru.iteco.transit.models.DiscountChangeHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscountChangeHistoryRepo extends JpaRepository<DiscountChangeHistory, Long> {
    @Query(value = "SELECT dch FROM DiscountChangeHistory dch")
    List<DiscountChangeHistory> getAll(Pageable pageable);

    List<DiscountChangeHistory> getAllByRegistrationDateGreaterThan(Pageable pageable, Long registrationDate);
}
