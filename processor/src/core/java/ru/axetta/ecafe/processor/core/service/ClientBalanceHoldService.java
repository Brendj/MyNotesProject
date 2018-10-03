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

    public ClientBalanceHold createClientBalanceHold(Session session, String guid, Client client, Org oldOrg, Org newOrg,
            Contragent oldContragent, Contragent newContragent, ClientBalanceHoldCreateStatus createStatus,
            ClientBalanceHoldRequestStatus requestStatus, Client declarer, String phoneOfDeclarer) {
        Long version = DAOUtils.nextVersionByClientBalanceHold(session);
        ClientBalanceHold clientBalanceHold = new ClientBalanceHold();
        clientBalanceHold.setGuid(guid == null ? UUID.randomUUID().toString() : guid);
        clientBalanceHold.setClient(client);
        clientBalanceHold.setHoldSum(client.getBalance());
        clientBalanceHold.setOldOrg(oldOrg);
        clientBalanceHold.setNewOrg(newOrg);
        clientBalanceHold.setOldContragent(oldContragent);
        clientBalanceHold.setNewContragent(newContragent);
        clientBalanceHold.setCreatedDate(new Date());
        clientBalanceHold.setCreateStatus(createStatus);
        clientBalanceHold.setRequestStatus(requestStatus);
        clientBalanceHold.setDeclarer(declarer);
        clientBalanceHold.setPhoneOfDeclarer(phoneOfDeclarer);
        clientBalanceHold.setVersion(version);
        return clientBalanceHold;
    }

    public void holdClientBalance(String guid, Client client, Client declarer, Org oldOrg, Org newOrg, Contragent oldContragent, Contragent newContragent,
            ClientBalanceHoldCreateStatus createStatus, ClientBalanceHoldRequestStatus requestStatus, String phoneOfDeclarer) throws Exception {
        if (client.getBalance() <= 0L) return;
        RuntimeContext.getFinancialOpsManager().holdClientBalance(guid, client, declarer, oldOrg, newOrg, oldContragent,
                newContragent, createStatus, requestStatus, phoneOfDeclarer);
    }

    //todo Сделать запуск по расписанию. Просмотр таблицы истории перемещений клиентов и запуск удалений предзаказов +
    //todo блокировка баланса по этой таблице
    public void run() {

    }
}
