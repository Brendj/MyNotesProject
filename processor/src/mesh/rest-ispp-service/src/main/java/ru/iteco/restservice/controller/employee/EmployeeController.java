/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.employee.responsedto.EmployeeResponseDTO;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.servise.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/employee")
@Tag(name = "Операции по сотрудникам")
public class EmployeeController {
    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final ClientService clientService;
    private final EmployeeConverter employeeConverter;

    public EmployeeController(ClientService clientService,
            EmployeeConverter employeeConverter) {
        this.clientService = clientService;
        this.employeeConverter = employeeConverter;
    }

    @GetMapping("/getByMobile")
    @ResponseBody
    @Operation(
            summary = "Получение сотрудника по номеру телефона",
            description = "Позволяет получить сотрудника по номеру телефона"
    )
    public EmployeeResponseDTO getByMobile(
            @Parameter(description = "Номер телефона опекуна чере \"7\"", example = "79000000000")
            @NotNull
            @RequestParam String mobile){
        try {
            Client client = clientService.getEmployeeByMobile(mobile);
            return employeeConverter.toDTO(client);
        } catch (Exception e){
            log.error("Exception in GetByMobile ", e);
            throw e;
        }
    }
}
