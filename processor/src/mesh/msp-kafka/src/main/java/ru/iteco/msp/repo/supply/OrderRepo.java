/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.supply;

import ru.iteco.msp.models.Order;
import ru.iteco.msp.models.compositeId.OrderCompositeId;
import ru.iteco.msp.models.dto.SupplyMSPOrders;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, OrderCompositeId> {

    @Query(name = "Order.fullInfo",
    nativeQuery = true)
    List<SupplyMSPOrders> fullInfo(@Param("begin")Long begin,
                                   @Param("end") Long end,
                                   Pageable pageable);
}
