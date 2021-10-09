/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.assign;

import ru.iteco.msp.models.Client;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepo extends JpaRepository<Client, Long> {

    @EntityGraph(value = "only_client", type = EntityGraph.EntityGraphType.LOAD)
    List<Client> findDistinctByMeshGuidIsNotNullAndDiscountsNotNull(Pageable pageable);

    @EntityGraph(value = "only_client", type = EntityGraph.EntityGraphType.LOAD)
    List<Client> findDistinctByMeshGuidIsNotNullAndDiscountsNotNullAndIdOfClientGreaterThan(Long idOfClient, Pageable pageable);
}