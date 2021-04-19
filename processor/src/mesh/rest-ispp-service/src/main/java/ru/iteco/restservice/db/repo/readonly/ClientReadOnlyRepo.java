/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.controller.client.responsedto.ClientResponseDTO;
import ru.iteco.restservice.model.Client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientReadOnlyRepo extends CrudRepository<Client, Long> {

    @Query(name = "getClientByGuardPhone", nativeQuery = true)
    List<ClientResponseDTO> getClientsByGuardMobile(@Param("guardPhone") String guardMobile);
}
