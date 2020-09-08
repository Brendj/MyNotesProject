/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.cardsync.models.CardActionClient;
import ru.iteco.cardsync.models.CardActionRequest;

import java.util.List;

public interface CardActionClientRepository extends JpaRepository<CardActionClient, Long> {

}
