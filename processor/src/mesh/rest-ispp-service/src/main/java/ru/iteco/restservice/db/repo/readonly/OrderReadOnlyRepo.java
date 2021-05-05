/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import io.swagger.v3.oas.annotations.Parameter;
import ru.iteco.restservice.model.Order;
import ru.iteco.restservice.model.compositid.OrderCompositeId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderReadOnlyRepo extends CrudRepository<Order, OrderCompositeId> {

    @Query(value = "SELECT DISTINCT o FROM Order AS o "
                 + " INNER JOIN o.orderDetailSet AS od "
                 + " LEFT JOIN od.dish AS d "
                 + " LEFT JOIN od.menu AS m "
                 + " LEFT JOIN m.menuDetails AS md "
                 + " INNER JOIN o.client AS c "
                 + " WHERE c.contractId = :contractId "
                 + " AND o.createdDate BETWEEN :startDate AND :endDate ")
    Page<Order> findByDateAndContractId(@Parameter(name = "contractId") Long contractId,
            @Parameter(name = "startDate") Long startDate, @Parameter(name = "endDate") Long endDate, Pageable pageable);
}
