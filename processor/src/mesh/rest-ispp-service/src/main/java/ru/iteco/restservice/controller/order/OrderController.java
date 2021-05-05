/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.order.responseDTO.OrderResponseDTO;
import ru.iteco.restservice.servise.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping("/order")
@Tag(name = "Операции заказам и покупкам")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/getList")
    @ResponseBody
    @Operation(
            summary = "Получение списка обытий предоставления питания",
            description = "Позволяет получить события предоставления питания обучающемуся в ОО")
    public Page<OrderResponseDTO> getList(
            @Parameter(description = "Номер лицевого счета клиента") @NotNull @PositiveOrZero @RequestParam Long contractId,
            @NotNull @RequestParam @Parameter(description = "Дата начала выборки в Timestamp (ms)") @PositiveOrZero Long startDate,
            @NotNull @RequestParam @Parameter(description = "Дата конца выборки в Timestamp (ms)") @PositiveOrZero Long endDate,
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10")
            @Max(value = 100L, message = "Размер выборки не должен привышать 100 записей") @PositiveOrZero Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        return orderService.getOrdersList(contractId, startDate, endDate, pageable);
    }
}
