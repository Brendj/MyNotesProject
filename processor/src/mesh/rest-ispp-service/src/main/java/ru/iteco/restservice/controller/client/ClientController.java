/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "Операции по клиентам")
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
    @ApiOperation(
            value = "Получение списка детей по номеру опекуна",
            notes = "Позволяет получить список детей по номеру телефона опекуна, если такой опекун присуствует в системе"
    )
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
    @ApiOperation(
            value = "Получение клиента по MESH-GUID",
            notes = "Позволяет получить клиента с указанным MESH-GUID"
    )
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
