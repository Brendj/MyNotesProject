/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.client.exceptionhandler.ClientControllerExceptionHandler;
import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.servise.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/client")
@ClientControllerExceptionHandler
@Tag(name = "Client-controller", description = "Операции по клиентам")
public class ClientController {
    private final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;
    private final ClientConverter clientConverter;

    public ClientController(ClientService clientService,
            ClientConverter clientConverter) {
        this.clientService = clientService;
        this.clientConverter = clientConverter;
    }

    @GetMapping("/getByGuardMobile")
    @ResponseBody
    public List<ClientResponseDTO> getClientByGuardian(@NotNull @RequestParam String guardPhone) {
        try {
            List<Client> childs = clientService.getClientsByGuardianPhone(guardPhone);
            return clientConverter.toDTOs(childs);
        } catch (Exception e){
            log.error("Exception in getClientByGuardian ", e);
            throw e;
        }
    }

    @GetMapping("/getByMeshGuid")
    @ResponseBody
    public ClientResponseDTO getClientByMeshGuid(@NotNull @RequestParam String meshGuid) {
        try {
            Client client = clientService.getClientByMeshGuid(meshGuid);
            return clientConverter.toDTO(client);
        } catch (Exception e){
            log.error("Exception in getClientByMeshGuid ", e);
            throw e;
        }
    }
}
