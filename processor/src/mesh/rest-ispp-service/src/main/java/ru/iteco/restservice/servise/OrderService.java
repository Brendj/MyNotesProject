/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.servise;

import ru.iteco.restservice.db.repo.readonly.OrderReadOnlyRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderReadOnlyRepo readOnlyRepo;

    public OrderService(OrderReadOnlyRepo readOnlyRepo) {
        this.readOnlyRepo = readOnlyRepo;
    }
}
