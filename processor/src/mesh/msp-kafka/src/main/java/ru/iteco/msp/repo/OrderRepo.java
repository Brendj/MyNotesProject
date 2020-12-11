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

    @Query(" from Order o " +
            "         join o.orderDetailSet od " +
            "         left join od.rule dr  " +
            "         left join od.wtRule wdr  " +
            "         join dr.categoryDiscounts cd  " +
            "         join wdr.categoryDiscountSet wcd " +
            "         left join cd.categoryDiscountDTSZN cd_dszn  " +
            "         join o.client c " +
            "         left join dr.codeMSP ccm " +
            " where o.state = 0 " +
            " and c.meshGuid is not null " +
            "  and o.createdDate between :begin and :end " +
            "  and o.orderType in :types " +
            "  and od.menuType between 50 and 99 " +
            "  and od.rule is not null " +
            "  and (cd.idOfCategoryDiscount >= 0 or cd.idOfCategoryDiscount = -90) " +
            "  and (wcd.idOfCategoryDiscount >= 0 or wcd.idOfCategoryDiscount = -90) ")
    List<Order> findAllByCreatedDateBetweenAndAndOrderTypeIn(@Param("begin")Long begin,
                                                             @Param("end") Long end,
                                                             @Param("types") List<Integer> types,
                                                             Pageable pageable);
    @Query(name = "Order.fullInfo",
    nativeQuery = true)
    List<SupplyMSPOrders> fullInfo(@Param("begin")Long begin,
                                   @Param("end") Long end,
                                   @Param("types") List<Integer> types,
                                   Pageable pageable);

    Integer countAllByCreatedDateBetweenAndOrderTypeIn(Long begin, Long end, List<Integer> types);
}
