/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.order.responseDTO.OrderResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping("/order")
@Tag(name = "Операции заказам и покупкам")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/getList")
    @ResponseBody
    @Operation(
            summary = "Получение списка обытий предоставления питания",
            description = "Позволяет получить события предоставления питания обучающемуся в ОО")
    public OrderResponseDTO getList(
            @Parameter(description = "Номер лицевого счета клиента") @NotNull @PositiveOrZero @RequestParam Long contractId,
            @NotNull @RequestParam @Parameter(description = "Дата начала выборки в Timestamp (ms)") @PositiveOrZero Long startDate,
            @NotNull @RequestParam @Parameter(description = "Дата конца выборки в Timestamp (ms)") @PositiveOrZero Long endDate){
        return null;
    }

}
