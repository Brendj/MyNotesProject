/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import ru.iteco.restservice.controller.base.BaseConverter;
import ru.iteco.restservice.controller.client.responsedto.NotificationResponseDTO;
import ru.iteco.restservice.controller.client.responsedto.NotificationResponseErrorDTO;
import ru.iteco.restservice.model.ClientGuardianNotificationSettings;
import ru.iteco.restservice.model.enums.ClientNotificationSettingType;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class NotificationSettingsGuardiansConverter extends BaseConverter<NotificationResponseDTO, ClientGuardianNotificationSettings> {

    @Override
    public NotificationResponseDTO toDTO(ClientGuardianNotificationSettings item) {
        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.setTypeOfNotification(item.getType().getCode());
        dto.setNameOfNotification(item.getType().toString());

        return dto;
    }

    @Override
    public List<NotificationResponseDTO> toDTOs(Iterable<ClientGuardianNotificationSettings> items) {
        List<NotificationResponseDTO> res = new LinkedList<>();
        for(ClientGuardianNotificationSettings s : items){
            if(s.getType().equals(ClientNotificationSettingType.SMS_SETTING_CHANGED)){
                continue;
            }
            NotificationResponseDTO dto = toDTO(s);
            res.add(dto);
        }
        return res;
    }

    @Override
    public Set<NotificationResponseDTO> toDTOs(Set<ClientGuardianNotificationSettings> items) {
        Set<NotificationResponseDTO> res = new HashSet<>();
        for(ClientGuardianNotificationSettings s : items){
            if(s.getType().equals(ClientNotificationSettingType.SMS_SETTING_CHANGED)){
                continue;
            }
            NotificationResponseDTO dto = toDTO(s);
            res.add(dto);
        }
        return res;
    }

    public List<NotificationResponseDTO> toDTOs(@NotNull List<ClientNotificationSettingType> settings) {
        List<NotificationResponseDTO> result = new LinkedList<>();
        for (ClientNotificationSettingType type: settings)
        {
            if(type.equals(ClientNotificationSettingType.SMS_SETTING_CHANGED)){
                continue;
            }
            NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
            notificationResponseDTO.setNameOfNotification(type.toString());
            notificationResponseDTO.setTypeOfNotification(type.getCode());
            result.add(notificationResponseDTO);
        }
        return result;
    }
}
