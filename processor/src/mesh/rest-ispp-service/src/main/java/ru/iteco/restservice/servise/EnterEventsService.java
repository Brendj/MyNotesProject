/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.enterevents.responsedto.EnterEventResponseDTO;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.EnterEventsReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.enums.ClientGroupAssignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

@Service
public class EnterEventsService {
    private final EnterEventsReadOnlyRepo readOnlyRepo;
    private final ClientReadOnlyRepo clientReadOnlyRepo;

    public EnterEventsService(EnterEventsReadOnlyRepo readOnlyRepo,
            ClientReadOnlyRepo clientReadOnlyRepo){
        this.readOnlyRepo = readOnlyRepo;
        this.clientReadOnlyRepo = clientReadOnlyRepo;
    }

    public Boolean clientIsInside(@NotNull Long idOfClient){
        Date today = getStartOfDay();

        return readOnlyRepo.clientIsInside(idOfClient, today.getTime()).orElse(false);
    }

    @Transactional
    public Page<EnterEventResponseDTO> getEnterEventsWitchExternalEventsByContractId(
            @NotNull Long contractId, @NotNull Long startDate,
            @NotNull Long endDate, @NotNull Pageable pageable) {
        Client c = clientReadOnlyRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден клиент по л/с %d", contractId)));

        if(c.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }
        return readOnlyRepo.getEnterEventsByClient(contractId, startDate, endDate, pageable);
    }

    private Date getStartOfDay(){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
