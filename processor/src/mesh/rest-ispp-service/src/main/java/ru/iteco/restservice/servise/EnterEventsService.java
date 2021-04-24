/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.enterevents.responsedto.EnterEventResponseDTO;
import ru.iteco.restservice.db.repo.readonly.EnterEventsReadOnlyRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class EnterEventsService {
    private final EnterEventsReadOnlyRepo readOnlyRepo;

    public EnterEventsService(EnterEventsReadOnlyRepo readOnlyRepo){
        this.readOnlyRepo = readOnlyRepo;
    }

    public Boolean clientIsInside(@NotNull Long idOfClient){
        return readOnlyRepo.clientIsInside(idOfClient).orElse(false);
    }

    public Page<EnterEventResponseDTO> getEnterEventsWitchExternalEventsByContractId(
            @NotNull Long contractId, @NotNull Long startDate,
            @NotNull Long endDate, @NotNull Pageable pageable) {
        return readOnlyRepo.getEnterEventsByClient(contractId, startDate, endDate, pageable);
    }
}
