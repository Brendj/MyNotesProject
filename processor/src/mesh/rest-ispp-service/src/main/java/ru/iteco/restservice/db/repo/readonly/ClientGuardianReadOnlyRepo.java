/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ClientGuardian;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientGuardianReadOnlyRepo extends CrudRepository<ClientGuardian, Long> {

    Optional<ClientGuardian> getClientGuardianByChildrenAndGuardianAndDeletedStateIsFalse(Client children, Client guardian);

    @Query(value = "SELECT MAX(version) FROM cf_client_guardian ", nativeQuery = true)
    Long getVersion();
}
