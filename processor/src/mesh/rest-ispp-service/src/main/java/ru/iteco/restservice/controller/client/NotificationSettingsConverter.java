/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import ru.iteco.restservice.controller.base.BaseConverter;
import ru.iteco.restservice.controller.client.responsedto.NotificationResponseDTO;
import ru.iteco.restservice.model.ClientsNotificationSettings;
import ru.iteco.restservice.model.enums.ClientNotificationSettingType;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class NotificationSettingsConverter extends BaseConverter<NotificationResponseDTO, ClientsNotificationSettings> {

    @Override
    public NotificationResponseDTO toDTO(ClientsNotificationSettings item) {
        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.setSettingsCode(item.getType().getCode());
        dto.setSettingsName(item.getType().toString());

        return dto;
    }

    @Override
    public List<NotificationResponseDTO> toDTOs(Iterable<ClientsNotificationSettings> items) {
        List<NotificationResponseDTO> res = new LinkedList<>();
        for(ClientsNotificationSettings s : items){
            if(s.getType().equals(ClientNotificationSettingType.SMS_SETTING_CHANGED)){
                continue;
            }
            NotificationResponseDTO dto = toDTO(s);
            res.add(dto);
        }
        return res;
    }

    @Override
    public Set<NotificationResponseDTO> toDTOs(Set<ClientsNotificationSettings> items) {
        Set<NotificationResponseDTO> res = new HashSet<>();
        for(ClientsNotificationSettings s : items){
            if(s.getType().equals(ClientNotificationSettingType.SMS_SETTING_CHANGED)){
                continue;
            }
            NotificationResponseDTO dto = toDTO(s);
            res.add(dto);
        }
        return res;
    }
}
