/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ClientService {
    private final Logger log = LoggerFactory.getLogger(ClientService.class);
    private static final Pattern phonePattern = Pattern.compile("^7\\d{10}$");

    private final ClientReadOnlyRepo clientReadOnlyRepo;

    @PersistenceContext(name = "readonlyEntityManager", unitName = "readonlyPU")
    EntityManager readonlyEntityManager;

    @PersistenceContext(name = "writableEntityManager", unitName = "writablePU")
    EntityManager writableEntityManager;

    public ClientService(ClientReadOnlyRepo clientReadOnlyRepo){
        this.clientReadOnlyRepo = clientReadOnlyRepo;
    }

    public List<ClientResponseDTO> getClientsByGuardianPhone(String guardPhone) {
        if(!phonePattern.matcher(guardPhone).matches()){
            throw new IllegalArgumentException("Номер телефона не соотвествует паттерну");
        }
        return clientReadOnlyRepo.getClientsByGuardMobile(guardPhone);
    }
}
