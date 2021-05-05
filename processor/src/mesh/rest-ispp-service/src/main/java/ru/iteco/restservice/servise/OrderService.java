/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.order.OrderConverter;
import ru.iteco.restservice.controller.order.responseDTO.OrderResponseDTO;
import ru.iteco.restservice.db.repo.readonly.OrderReadOnlyRepo;
import ru.iteco.restservice.model.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderReadOnlyRepo readOnlyRepo;
    private final OrderConverter orderConverter;

    public OrderService(OrderReadOnlyRepo readOnlyRepo,
                        OrderConverter orderConverter) {
        this.readOnlyRepo = readOnlyRepo;
        this.orderConverter = orderConverter;
    }

    @Transactional
    public Page<OrderResponseDTO> getOrdersList(@NotNull Long contractId, @NotNull Long startDate, @NotNull Long endDate,
            @NotNull Pageable pageable) {
        if(startDate > endDate){
            throw new IllegalArgumentException("Дата начала выборки больше даты окончания");
        }

        Page<Order> orders = readOnlyRepo.findByDateAndContractId(contractId, startDate, endDate, pageable);
        List<OrderResponseDTO> res = orderConverter.toDTOs(orders);
        return new PageImpl<>(res, pageable, orders.getTotalElements());
    }
}
