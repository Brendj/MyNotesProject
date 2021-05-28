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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class EnterEventsService {
    private final EnterEventsReadOnlyRepo readOnlyRepo;
    private final ClientReadOnlyRepo clientReadOnlyRepo;

    @PersistenceContext(name = "readonlyEntityManager", unitName = "readonlyPU")
    private EntityManager readonlyEntityManager;

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
        if(startDate > endDate){
            throw new IllegalArgumentException("Дата начала выборки больше даты окончания");
        }

        Client c = clientReadOnlyRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден клиент по л/с %d", contractId)));

        if(c.getClientGroup().getClientGroupId().getIdOfClientGroup() >= ClientGroupAssignment.CLIENT_EMPLOYEES.getId()){
            throw new IllegalArgumentException("Клиент из предопределенной группы");
        }
        Page<Object[]> fromDB = readOnlyRepo.getEnterEventsByClient(contractId, startDate, endDate, pageable);
        List<EnterEventResponseDTO> result = new LinkedList<>();

        for(Object[] o : fromDB){
            EnterEventResponseDTO dto = EnterEventResponseDTO.build(o);
            result.add(dto);
        }

        return new PageImpl<>(result, pageable, fromDB.getTotalElements());
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
