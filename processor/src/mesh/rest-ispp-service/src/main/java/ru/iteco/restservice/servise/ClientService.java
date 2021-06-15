/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.client.request.NotificationUpdateRequest;
import ru.iteco.restservice.controller.guardian.request.SetRelationRequest;
import ru.iteco.restservice.controller.guardian.responsedto.GuardianResponseDTO;
import ru.iteco.restservice.db.repo.readonly.ClientGuardianNotificationSettingsReadonlyRepo;
import ru.iteco.restservice.db.repo.readonly.ClientGuardianReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.*;
import ru.iteco.restservice.model.enums.ClientGroupAssignment;
import ru.iteco.restservice.model.enums.ClientGuardianRelationType;
import ru.iteco.restservice.model.enums.ClientGuardianRepresentType;
import ru.iteco.restservice.model.enums.ClientNotificationSettingType;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Date;
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
    private final ClientGuardianReadOnlyRepo guardianReadOnlyRepo;
    private final ClientGuardianNotificationSettingsReadonlyRepo guardianNotificationSettingsReadonlyRepo;

    @PersistenceContext(name = "readonlyEntityManager", unitName = "readonlyPU")
    EntityManager readonlyEntityManager;

    @PersistenceContext(name = "writableEntityManager", unitName = "writablePU")
    EntityManager writableEntityManager;

    public ClientService(ClientReadOnlyRepo clientReadOnlyRepo,
            ClientGuardianReadOnlyRepo clientGuardianReadOnlyRepo,
            ClientGuardianNotificationSettingsReadonlyRepo guardianNotificationSettingsReadonlyRepo){
        this.clientReadOnlyRepo = clientReadOnlyRepo;
        this.guardianReadOnlyRepo = clientGuardianReadOnlyRepo;
        this.guardianNotificationSettingsReadonlyRepo = guardianNotificationSettingsReadonlyRepo;
    }

    public List<Client> getClientsByGuardianPhone(@NotNull String guardPhone) {
        if(!phonePattern.matcher(guardPhone).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
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
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
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

    @Transactional
    public void setLimit(@NotNull Long contractId, @NotNull Long limit) {
        Client c = clientReadOnlyRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден клиент по л/с %d", contractId)));

        if(c.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        ClientRegistry registry = writableEntityManager.find(ClientRegistry.class, ClientRegistry.THE_ONLY_INSTANCE_ID,
                LockModeType.PESSIMISTIC_WRITE);

        Long version = registry.getClientRegistryVersion() + 1;
        registry.setClientRegistryVersion(version);
        writableEntityManager.merge(registry);

        c.setExpenditureLimit(limit);
        c.setClientRegistryVersion(version);
        writableEntityManager.merge(c);
    }

    @Transactional
    public void setBalanceNotification(@NotNull Long contractId, @NotNull Long balanceNotification) {
        Client c = clientReadOnlyRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден клиент по л/с %d", contractId)));

        if(c.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        ClientRegistry registry = writableEntityManager.find(ClientRegistry.class, ClientRegistry.THE_ONLY_INSTANCE_ID,
                LockModeType.PESSIMISTIC_WRITE);

        Long version = registry.getClientRegistryVersion() + 1;
        registry.setClientRegistryVersion(version);
        writableEntityManager.merge(registry);

        c.setBalanceToNotify(balanceNotification);
        c.setClientRegistryVersion(version);
        writableEntityManager.merge(c);
    }

    @Transactional
    public void setRelations(@NotNull SetRelationRequest relations) {
        if(relations.getContractId() == null){
            throw new IllegalArgumentException("Не указан л/с клиента");
        } else if(!phonePattern.matcher(relations.getGuardianMobile()).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
        } else if(relations.getRepContractId() == null){
            throw new IllegalArgumentException("Не указан л/с представителя");
        } else if(relations.getRelation() == null){
            throw new IllegalArgumentException("Не указана степень родства");
        } else if(relations.getIsLegalRepresent() == null){
            throw new IllegalArgumentException("Не указана роль представителя");
        }

        ClientGuardianRelationType relationType = ClientGuardianRelationType.of(relations.getRelation());
        ClientGuardianRepresentType representType = ClientGuardianRepresentType.of(relations.getIsLegalRepresent());
        if(!(representType.equals(ClientGuardianRepresentType.IN_LAW) || representType.equals(ClientGuardianRepresentType.NOT_IN_LAW))){
            throw new IllegalArgumentException("Операция доступна только для законного представителя");
        }

        Client child = clientReadOnlyRepo.getClientByContractId(relations.getContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден клиент по л/с %d", relations.getContractId()))
                );
        if(child.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        Client guardian = clientReadOnlyRepo
                .getClientByMobileAndContractId(relations.getGuardianMobile(), relations.getRepContractId())
                .orElseThrow(() -> new NotFoundException(
                String.format("Не найден представитель по л/с %d и телефону %s", relations.getContractId(), relations.getGuardianMobile()))
        );
        Long groupIdOfGuardian = guardian.getClientGroup().getClientGroupId().getIdOfClientGroup();
        if(ClientGroupAssignment.CLIENT_PARENTS.getId() > groupIdOfGuardian && groupIdOfGuardian > ClientGroupAssignment.CLIENT_VISITORS.getId() -1L){
            throw new IllegalArgumentException("Указанный представитель не пренадлежит группе \"Родители\"");
        }

        ClientGuardian clientGuardian = guardianReadOnlyRepo
                .getClientGuardianByChildrenAndGuardianAndDeletedStateIsFalse(child, guardian).orElse(null);
        if(clientGuardian != null){
            throw new IllegalArgumentException("Связь между клиентом и представителем уже существует");
        }
        Long nextVersion = guardianReadOnlyRepo.getVersion() + 1L;

        clientGuardian = new ClientGuardian(guardian, child, representType, relationType, nextVersion);
        clientGuardian = writableEntityManager.merge(clientGuardian);
    }

    @Transactional
    public void updateNotifications(NotificationUpdateRequest req) {
        if(req.getContractId() == null){
            throw new IllegalArgumentException("Не указан л/с");
        } else if(StringUtils.isEmpty(req.getGuardianMobile())){
            throw new IllegalArgumentException("Не указан номер телефона представителя");
        } else if(!phonePattern.matcher(req.getGuardianMobile()).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
        } else if(req.getNotificationType() == null){
            throw new IllegalArgumentException("Не указан тип оповещения");
        } else if(req.getActivity() == null){
            throw new IllegalArgumentException("Не указан флаг включения оповещения");
        }

        ClientNotificationSettingType type = ClientNotificationSettingType.of(req.getNotificationType());
        if(type == null){
            throw new NotFoundException("Не найден тип оповещения " + req.getNotificationType());
        }

        Client child = clientReadOnlyRepo.getClientByContractId(req.getContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден клиент по л/с %d", req.getContractId()))
                );
        if(child.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        List<Client> guardians = clientReadOnlyRepo.getGuardianByChild(child.getContractId(), req.getGuardianMobile(),
                PageRequest.of(0,1));
        if(CollectionUtils.isEmpty(guardians)){
            throw new NotFoundException(String.format("У указанного клиента л/с %d не найдены представители по номеру %s",
                    child.getContractId(), req.getGuardianMobile()));
        }
        Client guardian = guardians.get(0);
        Long groupIdOfGuardian = guardian.getClientGroup().getClientGroupId().getIdOfClientGroup();
        if(ClientGroupAssignment.CLIENT_PARENTS.getId() > groupIdOfGuardian
                && groupIdOfGuardian > ClientGroupAssignment.CLIENT_VISITORS.getId() -1L){
            throw new IllegalArgumentException("Указанный представитель не пренадлежит группе \"Родители\"");
        }

        ClientGuardian clientGuardianRelations = guardianReadOnlyRepo
                .getClientGuardianByChildrenAndGuardianAndDeletedStateIsFalse(child, guardian)
                .orElseThrow(() -> new NotFoundException("Не найдена активная связь между клиентом и представителем"));

        ClientGuardianNotificationSettings guardianNotificationSettings = guardianNotificationSettingsReadonlyRepo
                .getClientGuardianNotificationSettingsByClientGuardianAndType(clientGuardianRelations, type);

        if(req.getActivity()){
            if(guardianNotificationSettings != null){
                throw new IllegalArgumentException("Оповещение уже активированно");
            }
            guardianNotificationSettings = new ClientGuardianNotificationSettings();
            guardianNotificationSettings.setClientGuardian(clientGuardianRelations);
            guardianNotificationSettings.setCreatedDate(new Date().getTime());
            guardianNotificationSettings.setType(type);
            writableEntityManager.merge(guardianNotificationSettings);
        } else {
            if(guardianNotificationSettings == null){
                throw new IllegalArgumentException("Оповещение уже отключено");
            }
            guardianNotificationSettings = writableEntityManager.merge(guardianNotificationSettings);
            writableEntityManager.remove(guardianNotificationSettings);
        }
    }
}
