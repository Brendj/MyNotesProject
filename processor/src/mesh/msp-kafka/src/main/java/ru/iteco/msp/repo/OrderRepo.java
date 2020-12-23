/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo;

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

    @Query(value = "select count(distinct o.idoforder)\n"
            + " from cf_orders o\n"
            + " join cf_clients c on o.idofclient = c.idofclient\n"
            + " join cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder = od.idoforder\n"
            + " where o.state = 0\n"
            + "  and (c.idofclientgroup < 1100000000 or c.idofclientgroup in (1100000120, 1100000080))\n"
            + "  and o.createddate between :begin and :end\n"
            + "  and o.ordertype in (:types)\n"
            + "  and c.meshguid is not null\n",
            nativeQuery = true)
    Integer countDistinctByCreatedDateBetweenAndOrderTypeIn(@Param("begin")Long begin,
                                                            @Param("end") Long end,
                                                            @Param("types") List<Integer> types);
}
