/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.clients;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientMigrationHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: shamil
 * Date: 04.12.14
 * Time: 11:15
 */
@Service
public class ClientMigrationHistoryService {

    @Autowired
    ClientMigrationHistoryRepository repository;

    public List<ClientMigration> findAll(Org org, Client client){
        return repository.findAll(org,client);
    }

}
