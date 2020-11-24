/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.repo;

import ru.iteco.dtszn.models.Order;
import ru.iteco.dtszn.models.compositeId.OrderCompositeId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, OrderCompositeId> {
    List<Order> findAllByCreatedDateBetweenAndAndOrderTypeIn(Date begin, Date end, List<Integer> types, Pageable pageable);

    Integer countAllByCreatedDateAndOrderTypeIn(Date begin, Date end, List<Integer> types);
}
