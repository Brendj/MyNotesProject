/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo;

import ru.iteco.restservice.model.Client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepo extends CrudRepository<Client, Long> {

    @Query("SELECT child FROM Client AS child "
         + "JOIN FETCH child.guardians AS guardians "
         + "JOIN guardians.guardian as guardian "
         + "WHERE guardian.mobile = :mobilPhone ")
    List<Client> getChildsByPhone(@Param("mobilPhone") String mobilPhone);
}
