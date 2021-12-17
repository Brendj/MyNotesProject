/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.enterevents;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.enterevents.responsedto.EnterEventResponseDTO;
import ru.iteco.restservice.servise.EnterEventsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping("/enterevent")
@Tag(name = "Операции по проходам")
public class EnterEventsController {
    private final Logger log = LoggerFactory.getLogger(EnterEventsController.class);
    private final EnterEventsService enterEventsService;

    public EnterEventsController(EnterEventsService enterEventsService) {
        this.enterEventsService = enterEventsService;
    }

    @GetMapping("/getEnterEvents")
    @ResponseBody
    @Operation(
            summary = "Получения списка событий проходов",
            description = "Получение списка событий проходов на территорию ОО или здания Минкультуры РФ. "
                    + " По умолчанию сортировка по полю \"dateTime\", размер страницы 10")
    public Page<EnterEventResponseDTO> getEnterEvents(
            @NotNull @RequestParam @Parameter(description = "Номер лицевого счета клиента") @PositiveOrZero Long contractId,
            @NotNull @RequestParam @Parameter(description = "Дата начала выборки в Timestamp (ms)") @PositiveOrZero Long startDate,
            @NotNull @RequestParam @Parameter(description = "Дата конца выборки в Timestamp (ms)") @PositiveOrZero Long endDate,
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10")
            @Max(value = 100L, message = "Размер выборки не должен привышать 100 записей") @PositiveOrZero Integer size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
        return enterEventsService
                    .getEnterEventsWitchExternalEventsByContractId(contractId, startDate, endDate, pageable);
        } catch (Exception e) {
            log.error("Exception in getEnterEvents", e);
            throw e;
        }
    }
}
