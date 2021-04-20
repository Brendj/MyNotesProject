/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ClientService {
    private final Logger log = LoggerFactory.getLogger(ClientService.class);
    private static final Pattern phonePattern = Pattern.compile("^7\\d{10}$");
    private static final Pattern guidPattern = Pattern.compile("^[A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{12}$");

    private final ClientReadOnlyRepo clientReadOnlyRepo;

    @PersistenceContext(name = "readonlyEntityManager", unitName = "readonlyPU")
    EntityManager readonlyEntityManager;

    @PersistenceContext(name = "writableEntityManager", unitName = "writablePU")
    EntityManager writableEntityManager;

    public ClientService(ClientReadOnlyRepo clientReadOnlyRepo){
        this.clientReadOnlyRepo = clientReadOnlyRepo;
    }

    public List<Client> getClientsByGuardianPhone(@NotNull String guardPhone) {
        if(!phonePattern.matcher(guardPhone).matches()){
            throw new IllegalArgumentException("Номер телефона не соотвествует паттерну");
        }
        if(!clientReadOnlyRepo.existsByMobile(guardPhone)){
            throw new NotFoundException(String.format("Не найден клиент по номеру %s", guardPhone));
        }
        return clientReadOnlyRepo.getClientsByGuardMobile(guardPhone);
    }

    public Client getClientByMeshGuid(String meshGuid) {
        if(!guidPattern.matcher(meshGuid).matches()){
            throw new IllegalArgumentException("Полученный GUID имеет неверный формат");
        }
        Client client = clientReadOnlyRepo.getClientByMeshGuid(meshGuid);
        if(client == null){
            throw new NotFoundException(String.format("Клиент с MESH-GUID %s не найден", meshGuid));
        }
        return client;
    }
}
