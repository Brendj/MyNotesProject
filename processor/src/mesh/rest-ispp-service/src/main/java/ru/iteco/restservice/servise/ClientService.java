/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.guardian.responsedto.GuardianResponseDTO;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ClientsNotificationSettings;
import ru.iteco.restservice.model.enums.ClientGroupAssignment;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Validated
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

        List<Client> childs = clientReadOnlyRepo.getClientsByGuardMobile(guardPhone);
        if(CollectionUtils.isEmpty(childs)){
            throw new NotFoundException(String.format("Не найдены клиенты по номеру опекуна %s", guardPhone));
        }
        return childs;
    }

    public Client getClientByMeshGuid(@NotNull String meshGuid) {
        if(!guidPattern.matcher(meshGuid).matches()){
            throw new IllegalArgumentException("Полученный GUID имеет неверный формат");
        }
        Client client = clientReadOnlyRepo.getClientByMeshGuid(meshGuid)
                .orElseThrow(() -> new NotFoundException(String.format("Клиент с MESH-GUID %s не найден", meshGuid)));

        if(client.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }
        return client;
    }

    public Client getEmployeeByMobile(@NotNull String mobile) {
        if (!phonePattern.matcher(mobile).matches()) {
            throw new IllegalArgumentException("Номер телефона не соотвествует паттерну");
        }
        Client client = clientReadOnlyRepo.getEmployeeByMobile(mobile);
        if (client == null) {
            throw new NotFoundException(String.format("Не найден клиент по номеру %s", mobile));
        }
        return client;
    }

    public List<GuardianResponseDTO> getGuardiansByClient(@NotNull Long contractId){
        Client c = clientReadOnlyRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден клиент по л/с %d", contractId)));

        if(c.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }
        List<GuardianResponseDTO> results = clientReadOnlyRepo.getGuardiansByClient(contractId);
        if(CollectionUtils.isEmpty(results)){
            throw new NotFoundException(String.format("Не удалось найти представителей по л/с %d", contractId));
        }

        return results;
    }

    @Transactional
    public List<ClientsNotificationSettings> getNotificationSettingsByClients(@NotNull Long contractId) {
        Client c = clientReadOnlyRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден клиент по л/с %d", contractId)));

        if(c.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        return new LinkedList<>(c.getNotificationSettings());
    }
}
