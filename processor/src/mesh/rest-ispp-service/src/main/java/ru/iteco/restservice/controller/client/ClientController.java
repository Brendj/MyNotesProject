/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.client.exceptionhandler.ClientControllerExceptionHandler;
import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.servise.ClientService;

import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/client")
@ClientControllerExceptionHandler
@Tag(name = "Client-controller", description = "Операции по клиентам")
public class ClientController {

    private final ClientService clientService;
    private final ClientConverter clientConverter;

    public ClientController(ClientService clientService,
            ClientConverter clientConverter) {
        this.clientService = clientService;
        this.clientConverter = clientConverter;
    }

    @GetMapping
    @ResponseBody
    public List<ClientResponseDTO> getClientByGuardian(@NotNull @RequestParam String guardPhone) {
        return clientService.getClientsByGuardianPhone(guardPhone);
    }
}
