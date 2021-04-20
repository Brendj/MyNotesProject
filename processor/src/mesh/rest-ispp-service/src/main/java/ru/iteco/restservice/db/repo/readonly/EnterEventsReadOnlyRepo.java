/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.EnterEvent;
import ru.iteco.restservice.model.compositid.EnterEventId;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EnterEventsReadOnlyRepo extends CrudRepository<EnterEvent, EnterEventId> {

    @Query(value = "SELECT CASE WHEN ee.eventcode IN (0, 6, 100, 101, 102, 112) THEN TRUE ELSE FALSE END "
            + "FROM cf_enterevents ee "
            + "WHERE ee.idofclient = :idOfClient "
            + "ORDER BY evtdatetime DESC "
            + "LIMIT 1", nativeQuery = true)
    Optional<Boolean> clientIsInside(@Param("idOfClient") Long idOfClient);
}
