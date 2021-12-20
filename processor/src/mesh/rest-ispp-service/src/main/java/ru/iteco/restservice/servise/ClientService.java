/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.client.request.NotificationUpdateRequest;
import ru.iteco.restservice.controller.guardian.request.DeleteRelationRequest;
import ru.iteco.restservice.controller.guardian.request.RelationRequest;
import ru.iteco.restservice.controller.guardian.responsedto.GuardianResponseDTO;
import ru.iteco.restservice.db.repo.readonly.ClientGuardianNotificationSettingsReadonlyRepo;
import ru.iteco.restservice.db.repo.readonly.ClientGuardianReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ClientGuardian;
import ru.iteco.restservice.model.ClientGuardianNotificationSettings;
import ru.iteco.restservice.model.ClientRegistry;
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
    public List<ClientGuardianNotificationSettings> getNotificationSettingsByClients(@NotNull Long contractId,
            @NotNull String guardPhone) {
        if(!phonePattern.matcher(guardPhone).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
        }
        Client child = clientReadOnlyRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден клиент по л/с %d", contractId))
                );
        if(child.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        List<Client> guardians = clientReadOnlyRepo.getGuardianByChild(child.getContractId(), guardPhone,
                PageRequest.of(0,1));
        if(CollectionUtils.isEmpty(guardians)){
            throw new NotFoundException(String.format("У указанного клиента л/с %d не найдены представители по номеру %s",
                    child.getContractId(), guardPhone));
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

        return guardianNotificationSettingsReadonlyRepo
                .getClientGuardianNotificationSettingsByClientGuardian(clientGuardianRelations);
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
    public void setRelations(@NotNull RelationRequest req) {
        if(req.getContractId() == null){
            throw new IllegalArgumentException("Не указан л/с клиента");
        } else if(!phonePattern.matcher(req.getGuardianMobile()).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
        } else if(req.getRepContractId() == null){
            throw new IllegalArgumentException("Не указан л/с представителя");
        } else if(req.getRelation() == null){
            throw new IllegalArgumentException("Не указана степень родства");
        } else if(req.getIsLegalRepresent() == null){
            throw new IllegalArgumentException("Не указана роль представителя");
        }

        Client child = clientReadOnlyRepo.getClientByContractId(req.getContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден клиент по л/с %d", req.getContractId()))
                );
        if(child.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        ClientGuardian changingClientGuardian = guardianReadOnlyRepo
                .getActiveGuardiansByMobileAndChild(req.getContractId(), req.getGuardianMobile())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден представитель по номеру телефону %s", req.getGuardianMobile()))
                ).get(0);
        Client changingGuardian = changingClientGuardian.getGuardian();

        Long groupIdOfGuardian = changingGuardian.getClientGroup().getClientGroupId().getIdOfClientGroup();
        if(ClientGroupAssignment.CLIENT_PARENTS.getId() > groupIdOfGuardian && groupIdOfGuardian > ClientGroupAssignment.CLIENT_VISITORS.getId() -1L){
            throw new IllegalArgumentException("Инициатор операции не пренадлежит группе \"Родители\"");
        }

        if(!changingClientGuardian.getRepresentType().equals(ClientGuardianRepresentType.IN_LAW)){
            throw new IllegalArgumentException("Операция доступна только для законного представителя");
        }

        changingClientGuardian = null;

        Client targetGuardian = clientReadOnlyRepo
                .getClientByContractId(req.getRepContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден целевой представитель по л/с %d", req.getRepContractId()))
                );

        ClientGuardian targetClientGuardian = guardianReadOnlyRepo
                .getClientGuardianByChildrenAndGuardianAndDeletedStateIsFalse(child, targetGuardian)
                .orElse(null);

        if(targetClientGuardian != null){
            throw new IllegalArgumentException("Связь между клиентом и целевым представителем уже существует");
        }

        Long nextVersion = guardianReadOnlyRepo.getVersion() + 1L;

        ClientGuardianRelationType relationType = ClientGuardianRelationType.of(req.getRelation());
        ClientGuardianRepresentType representType = ClientGuardianRepresentType.of(req.getIsLegalRepresent());

        targetClientGuardian = new ClientGuardian(targetGuardian, child, representType, relationType, nextVersion);
        targetClientGuardian = writableEntityManager.merge(targetClientGuardian);
    }

    @Transactional
    public void updateNotifications(NotificationUpdateRequest req) {
        if(req.getContractId() == null){
            throw new IllegalArgumentException("Не указан л/с");
        } else if(StringUtils.isEmpty(req.getGuardianMobile())){
            throw new IllegalArgumentException("Не указан номер телефона представителя");
        } else if(!phonePattern.matcher(req.getGuardianMobile()).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
        } else if(req.getTypeOfNotification() == null){
            throw new IllegalArgumentException("Не указан тип оповещения");
        } else if(req.getActivity() == null){
            throw new IllegalArgumentException("Не указан флаг включения оповещения");
        }

        List<ClientNotificationSettingType> types = ClientNotificationSettingType.of(req.getTypeOfNotification());
//        if(type == null){
//            throw new NotFoundException("Не найден тип оповещения " + req.getTypeOfNotification());
//        }

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
        if (types.isEmpty())
        {
            //Если перадали пустой массив, то делаем все виды оповещений неактивными
            List<ClientGuardianNotificationSettings> settings = getNotificationSettingsByClients(req.getContractId(), req.getGuardianMobile());
            for (ClientGuardianNotificationSettings setting: settings)
            {
                setting = writableEntityManager.merge(setting);
                writableEntityManager.remove(setting);
            }
        }
        for (ClientNotificationSettingType type:  types) {
            ClientGuardianNotificationSettings guardianNotificationSettings = guardianNotificationSettingsReadonlyRepo
                    .getClientGuardianNotificationSettingsByClientGuardianAndType(clientGuardianRelations, type);
            if (req.getActivity()) {
                if (guardianNotificationSettings != null) {
                    continue;
//                    throw new IllegalArgumentException("Оповещение уже активированно");
                }
                guardianNotificationSettings = new ClientGuardianNotificationSettings();
                guardianNotificationSettings.setClientGuardian(clientGuardianRelations);
                guardianNotificationSettings.setCreatedDate(new Date().getTime());
                guardianNotificationSettings.setType(type);
                writableEntityManager.merge(guardianNotificationSettings);
            } else {
                if (guardianNotificationSettings == null) {
                    continue;
//                    throw new IllegalArgumentException("Оповещение уже отключено");
                }
                guardianNotificationSettings = writableEntityManager.merge(guardianNotificationSettings);
                writableEntityManager.remove(guardianNotificationSettings);
            }
        }
    }

    @Transactional
    public void changeRelations(RelationRequest req) {
        if(req.getContractId() == null){
            throw new IllegalArgumentException("Не указан л/с клиента");
        } else if(!phonePattern.matcher(req.getGuardianMobile()).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
        } else if(req.getRepContractId() == null){
            throw new IllegalArgumentException("Не указан л/с представителя");
        } else if(req.getRelation() == null){
            throw new IllegalArgumentException("Не указана степень родства");
        } else if(req.getIsLegalRepresent() == null){
            throw new IllegalArgumentException("Не указана роль представителя");
        }

        Client child = clientReadOnlyRepo.getClientByContractId(req.getContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден клиент по л/с %d", req.getContractId()))
                );
        if(child.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        ClientGuardian changingClientGuardian = guardianReadOnlyRepo
                .getActiveGuardiansByMobileAndChild(req.getContractId(), req.getGuardianMobile())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден представитель по номеру телефону %s", req.getGuardianMobile()))
                ).get(0);
        Client changingGuardian = changingClientGuardian.getGuardian();

        Long groupIdOfGuardian = changingGuardian.getClientGroup().getClientGroupId().getIdOfClientGroup();
        if(ClientGroupAssignment.CLIENT_PARENTS.getId() > groupIdOfGuardian && groupIdOfGuardian > ClientGroupAssignment.CLIENT_VISITORS.getId() -1L){
            throw new IllegalArgumentException("Инициатор операции не пренадлежит группе \"Родители\"");
        }

        if(!changingClientGuardian.getRepresentType().equals(ClientGuardianRepresentType.IN_LAW)){
            throw new IllegalArgumentException("Операция доступна только для законного представителя");
        }

        changingClientGuardian = null;

        Client targetGuardian = clientReadOnlyRepo
                .getClientByContractId(req.getRepContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден целевой представитель по л/с %d", req.getRepContractId()))
                );

        ClientGuardian targetClientGuardian = guardianReadOnlyRepo
                .getClientGuardianByChildrenAndGuardianAndDeletedStateIsFalse(child, targetGuardian)
                .orElseThrow(() -> new NotFoundException("Нет связи между клиентом и целевым представителем"));

        Long nextVersion = guardianReadOnlyRepo.getVersion() + 1L;
        ClientGuardianRelationType relationType = ClientGuardianRelationType.of(req.getRelation());
        ClientGuardianRepresentType representType = ClientGuardianRepresentType.of(req.getIsLegalRepresent());

        targetClientGuardian.setVersion(nextVersion);
        targetClientGuardian.setRelationType(relationType);
        targetClientGuardian.setRepresentType(representType);

        targetClientGuardian = writableEntityManager.merge(targetClientGuardian);
    }

    @Transactional
    public void archiveRelations(DeleteRelationRequest req) {
        if(req.getContractId() == null){
            throw new IllegalArgumentException("Не указан л/с клиента");
        } else if(!phonePattern.matcher(req.getGuardianMobile()).matches()){
            throw new IllegalArgumentException("Номер телефона не соответствует паттерну");
        } else if(req.getRepContractId() == null){
            throw new IllegalArgumentException("Не указан л/с представителя");
        }

        Client child = clientReadOnlyRepo.getClientByContractId(req.getContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден клиент по л/с %d", req.getContractId()))
                );
        if(child.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }

        ClientGuardian changingClientGuardian = guardianReadOnlyRepo
                .getActiveGuardiansByMobileAndChild(req.getContractId(), req.getGuardianMobile())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден представитель по номеру телефону %s", req.getGuardianMobile()))
                ).get(0);
        Client changingGuardian = changingClientGuardian.getGuardian();

        Long groupIdOfGuardian = changingGuardian.getClientGroup().getClientGroupId().getIdOfClientGroup();
        if(ClientGroupAssignment.CLIENT_PARENTS.getId() > groupIdOfGuardian && groupIdOfGuardian > ClientGroupAssignment.CLIENT_VISITORS.getId() -1L){
            throw new IllegalArgumentException("Инициатор операции не пренадлежит группе \"Родители\"");
        }

        if(!changingClientGuardian.getRepresentType().equals(ClientGuardianRepresentType.IN_LAW)){
            throw new IllegalArgumentException("Операция доступна только для законного представителя");
        }

        changingClientGuardian = null;

        Client targetGuardian = clientReadOnlyRepo
                .getClientByContractId(req.getRepContractId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найден целевой представитель по л/с %d", req.getRepContractId()))
                );

        ClientGuardian targetClientGuardian = guardianReadOnlyRepo
                .getClientGuardianByChildrenAndGuardianAndDeletedStateIsFalse(child, targetGuardian)
                .orElseThrow(() -> new NotFoundException("Нет связи между клиентом и целевым представителем"));

        Long nextVersion = guardianReadOnlyRepo.getVersion() + 1L;

        targetClientGuardian.setVersion(nextVersion);
        targetClientGuardian.setDisabled(1);
        targetClientGuardian.setDeletedState(true);

        targetClientGuardian = writableEntityManager.merge(targetClientGuardian);
    }
}
