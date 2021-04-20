/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.Client;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientReadOnlyRepo extends CrudRepository<Client, Long> {
    @Query(value = "FROM Client childs "
            + "JOIN FETCH childs.org AS org "
            + "JOIN FETCH childs.person AS person "
            + "JOIN FETCH childs.clientGroup AS cg "
            + "LEFT JOIN FETCH childs.discounts AS discounts "
            + "LEFT JOIN FETCH childs.preorderFlag AS pf "
            + "JOIN childs.guardians AS guardians "
            + "JOIN guardians.guardian AS guardian "
            + "WHERE guardian.mobile = :guardPhone ")
    List<Client> getClientsByGuardMobile(@Param("guardPhone") String guardMobile);

    Boolean existsByMobile(String mobile);

    @EntityGraph("forClientResponseDTO")
    Client getClientByMeshGuid(String meshGuid);
}
