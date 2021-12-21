/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.client.request.NotificationUpdateRequest;
import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.controller.client.responsedto.NotificationResponseDTO;
import ru.iteco.restservice.controller.client.responsedto.NotificationResponseErrorDTO;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ClientGuardianNotificationSettings;
import ru.iteco.restservice.servise.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
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
    private final NotificationSettingsGuardiansConverter notificationSettingsGuardiansConverter;

    public ClientController(ClientService clientService,
            ClientConverter clientConverter,
            NotificationSettingsGuardiansConverter notificationSettingsGuardiansConverter) {
        this.clientService = clientService;
        this.clientConverter = clientConverter;
        this.notificationSettingsGuardiansConverter = notificationSettingsGuardiansConverter;
    }

    @GetMapping("/getByGuardMobile")
    @ResponseBody
    @Operation(
            summary = "Получение списка детей по номеру опекуна",
            description = "Позволяет получить список детей по номеру телефона опекуна, если такой опекун присуствует в системе"
    )
    public List<ClientResponseDTO> getClientByGuardian(
           @Parameter(description = "Номер телефона опекуна через \"7\"", example = "79000000000")
           @NotNull
           @RequestParam String guardPhone) {
        try {
            List<Client> childs = clientService.getClientsByGuardianPhone(guardPhone);
            return clientConverter.toDTOs(childs);
        } catch (Exception e){
            log.error("Exception in getClientByGuardian:", e);
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
            log.error("Exception in getClientByMeshGuid:", e);
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
            @PositiveOrZero Long contractId,
            @NotNull @RequestParam
            @Parameter(description = "Номер телефона представителя", example = "79000000000")
            String guardPhone) {
        List<ClientGuardianNotificationSettings> settings = clientService.getNotificationSettingsByClients(contractId, guardPhone);
        return notificationSettingsGuardiansConverter.toDTOs(settings);
    }

    @PutMapping("/notifications")
    @ResponseBody
    @Operation(
            summary = "Изменения настроек оповещения для опекунов",
            description = "Позволяет изменить настройки оповещения для опекунов"
    )
    public List<NotificationResponseErrorDTO> updateNotifications(@NotNull @RequestBody NotificationUpdateRequest req) {
        List<Long> errors = clientService.updateNotifications(req);
        if (!errors.isEmpty())
            return clientConverter.toDTOs(errors);;
        return null;
    }

    @PutMapping("/setLimit")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Установка размера ограничения на покупку в буфете",
            description = "Устанавливает лимит на покупки в буфете по указангому л/с."
    )
    public void setLimit(@NotNull @RequestParam @Parameter(description = "Номер лицевого счета клиента")
    @PositiveOrZero Long contractId,
    @NotNull @RequestParam @Parameter(description = "Лимит в копейках") @Max(100000)
    @PositiveOrZero Long limit){
        try {
            clientService.setLimit(contractId, limit);
        } catch (Exception e) {
            log.error("Exception in setLimit:", e);
            throw e;
        }
    }

    @PutMapping("/setThreshold")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Установка размера порога снижения баланса лицевого счета",
            description = "Устанавливает размер порога снижения баланса клиента по л/с."
    )
    public void setThreshold(
            @NotNull @RequestParam @Parameter(description = "Номер лицевого счета клиента")
            @PositiveOrZero Long contractId,
            @NotNull @RequestParam @Parameter(description = "Лимит в копейках") @Max(100000)
            @PositiveOrZero Long threshold){
        try{
            clientService.setBalanceNotification(contractId, threshold);
        } catch (Exception e) {
            log.error("Exception in setLimit:", e);
            throw e;
        }
    }
}
