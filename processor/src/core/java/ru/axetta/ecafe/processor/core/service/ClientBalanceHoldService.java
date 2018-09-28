/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.UUID;

/**
 * Created by baloun on 30.08.2018.
 */
@Component
@Scope(value = "singleton")
public class ClientBalanceHoldService {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    public ClientBalanceHold createClientBalanceHold(Session session, Client client, Org oldOrg, Org newOrg,
            Contragent oldContragent, Contragent newContragent, ClientBalanceHoldCreateStatus createStatus, ClientBalanceHoldRequestStatus requestStatus) {
        Long version = DAOUtils.nextVersionByClientBalanceHold(session);
        ClientBalanceHold clientBalanceHold = new ClientBalanceHold();
        clientBalanceHold.setGuid(UUID.randomUUID().toString());
        clientBalanceHold.setClient(client);
        clientBalanceHold.setHoldSum(client.getBalance());
        clientBalanceHold.setOldOrg(oldOrg);
        clientBalanceHold.setNewOrg(newOrg);
        clientBalanceHold.setOldContragent(oldContragent);
        clientBalanceHold.setNewContragent(newContragent);
        clientBalanceHold.setCreatedDate(new Date());
        clientBalanceHold.setCreateStatus(createStatus);
        clientBalanceHold.setRequestStatus(requestStatus);
        clientBalanceHold.setVersion(version);
        return clientBalanceHold;
    }

    public void holdClientBalance(Client client, Org oldOrg, Org newOrg, ClientBalanceHoldCreateStatus createStatus,
            ClientBalanceHoldRequestStatus requestStatus) throws Exception {
        if (oldOrg.getDefaultSupplier().equals(newOrg.getDefaultSupplier()) || client.getBalance() <= 0L) return;
        RuntimeContext.getFinancialOpsManager().holdClientBalance(client, oldOrg, newOrg, oldOrg.getDefaultSupplier(),
                newOrg.getDefaultSupplier(), createStatus, requestStatus);
    }
}
