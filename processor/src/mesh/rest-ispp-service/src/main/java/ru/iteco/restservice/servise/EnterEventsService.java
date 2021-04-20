/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.db.repo.readonly.EnterEventsReadOnlyRepo;

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
}
