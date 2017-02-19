/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.clients;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.DiscountChangeHistory;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDiscountChangeHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Liya on 23.03.2016.
 */

@Service
public class ClientDiscountChangeHistoryService {

    @Autowired
    ClientDiscountChangeHistoryRepository repository;

    public List<DiscountChangeHistory> findAll(Client client) {
        return repository.findAll(client);
    }


}
