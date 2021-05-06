/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.controller.client.responsedto.NotificationResponseDTO;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ClientsNotificationSettings;
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
@RequestMapping("/client")
@Tag(name = "Операции по клиентам")
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
    @Operation(
            summary = "Получение списка детей по номеру опекуна",
            description = "Позволяет получить список детей по номеру телефона опекуна, если такой опекун присуствует в системе"
    )
    public List<ClientResponseDTO> getClientByGuardian(
           @Parameter(description = "Номер телефона опекуна чере \"7\"", example = "79000000000")
           @NotNull
           @RequestParam String guardPhone) {
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
    @Operation(
            summary = "Получение клиента по MESH-GUID",
            description = "Позволяет получить клиента с указанным MESH-GUID"
    )
    public ClientResponseDTO getClientByMeshGuid(
            @Parameter(description = "Идентификатор MESH-GUID")
            @NotNull
            @RequestParam String meshGuid) {
        try {
            Client client = clientService.getClientByMeshGuid(meshGuid);
            return clientConverter.toDTO(client);
        } catch (Exception e){
            log.error("Exception in getClientByMeshGuid ", e);
            throw e;
        }
    }

    @GetMapping("/notifications")
    @ResponseBody
    @Operation(
            summary = "Получение списока типов оповещений о событиях обучающегося в ОО",
            description = "Позволяет получить список типов оповещения клиента по номеру лицевого счета"
    )
    public List<NotificationResponseDTO> getNotifications(
            @NotNull @RequestParam
            @Parameter(description = "Номер лицевого счета клиента")
            @PositiveOrZero Long contractId) {
        List<ClientsNotificationSettings> settings = clientService.getNotificationSettingsByClients(contractId);
    }
}
