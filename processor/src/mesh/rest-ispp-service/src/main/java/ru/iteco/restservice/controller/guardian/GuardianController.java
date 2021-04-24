/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.guardian;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.guardian.responsedto.GuardianResponseDTO;
import ru.iteco.restservice.servise.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/guardian")
@Tag(name = "Операции по представителям клиента")
public class GuardianController {

    private final Logger log = LoggerFactory.getLogger(GuardianController.class);

    private final ClientService clientService;

    public GuardianController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/getGuardiansByClient")
    @ResponseBody
    @Operation(
            summary = "Получения списка представителей по лицевому счету клиента",
            description = "Позволяет получить список всех привязанных представителей по лицевому счету клиента")
    public List<GuardianResponseDTO> getGuardiansByClient(
            @Parameter(description = "Номер лицевого счета клиента")
            @NotNull @PositiveOrZero
            @RequestParam Long contractId) {
        try {
            return clientService.getGuardiansByClient(contractId);
        } catch (Exception e) {
            log.error("Exception in getGuardiansByClientList ", e);
            throw e;
        }
    }
}