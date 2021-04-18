/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client;

import ru.iteco.restservice.controller.base.BaseConverter;
import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.model.Client;

import org.springframework.stereotype.Component;

@Component
public class ClientConverter extends BaseConverter<ClientResponseDTO, Client> {
    @Override
    public ClientResponseDTO toDTO(Client c){
        return null;
    }
}
