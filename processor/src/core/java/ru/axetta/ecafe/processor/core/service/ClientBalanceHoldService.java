/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            ClientBalanceHoldRequestStatus requestStatus, Client declarer, String phoneOfDeclarer,
            String declarerInn, String declarerAccount, String declarerBank, String declarerBik, String declarerCorrAccount) {
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
        clientBalanceHold.setDeclarerInn(declarerInn);
        clientBalanceHold.setDeclarerAccount(declarerAccount);
        clientBalanceHold.setDeclarerBank(declarerBank);
        clientBalanceHold.setDeclarerBik(declarerBik);
        clientBalanceHold.setDeclarerCorrAccount(declarerCorrAccount);
        clientBalanceHold.setVersion(version);
        return clientBalanceHold;
    }

    public void holdClientBalance(String guid, Client client, Client declarer, Org oldOrg, Org newOrg, Contragent oldContragent, Contragent newContragent,
            ClientBalanceHoldCreateStatus createStatus, ClientBalanceHoldRequestStatus requestStatus, String phoneOfDeclarer,
            String declarerInn, String declarerAccount, String declarerBank, String declarerBik, String declarerCorrAccount) throws Exception {
        if (client.getBalance() <= 0L) return;
        RuntimeContext.getFinancialOpsManager().holdClientBalance(guid, client, declarer, oldOrg, newOrg, oldContragent,
                newContragent, createStatus, requestStatus, phoneOfDeclarer, declarerInn, declarerAccount, declarerBank, declarerBik, declarerCorrAccount);
    }

    public List<ClientBalanceHold> getClientBalanceHoldForOrgSinceVersion(Session session,
            long orgOwner, long version) throws Exception {
        Query query = session.createQuery("select cbh from ClientBalanceHold cbh "
                + "where cbh.version > :version and cbh.oldOrg.idOfOrg = :idOfOrg");
        query.setParameter("version", version);
        query.setParameter("idOfOrg", orgOwner);
        return query.list();
    }

    public List<ClientBalanceHold> getClientBalanceHolds(Session session, Long idOfOrg, List<Long> idOfClientList, Integer requestStatus) {
        String condition = "";
        if (idOfOrg != null) condition += " and cbh.oldOrg.idOfOrg = :idOfOrg";
        if (idOfClientList.size() > 0) condition += " and cbh.client.idOfClient in (:idOfClientList)";
        if (!requestStatus.equals(100)) condition += " and cbh.requestStatus = :requestStatus";
        Query query = session.createQuery("select cbh from ClientBalanceHold cbh where cbh.version > -1 " + condition + " order by cbh.createdDate");
        if (idOfOrg != null) query.setParameter("idOfOrg", idOfOrg);
        if (idOfClientList.size() > 0) query.setParameterList("idOfClientList", idOfClientList);
        if (!requestStatus.equals(100)) query.setParameter("requestStatus", ClientBalanceHoldRequestStatus.fromInteger(requestStatus));
        return query.list();
    }

    @Transactional
    public void setStatusAsRefunded(Long idOfClientBalanceHold) {
        javax.persistence.Query query = em.createQuery("update ClientBalanceHold set requestStatus = :status where idOfClientBalanceHold = :id");
        query.setParameter("status", ClientBalanceHoldRequestStatus.REFUNDED);
        query.setParameter("id", idOfClientBalanceHold);
        query.executeUpdate();
    }

    //todo Сделать запуск по расписанию. Просмотр таблицы истории перемещений клиентов и запуск удалений предзаказов +
    //todo блокировка баланса по этой таблице
    public void run() {

    }

    public String getBalanceHoldListAsString(Session session, Long idOfClient) {
        List clients = new ArrayList<Long>();
        clients.add(idOfClient);
        List<ClientBalanceHold> list = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                .getClientBalanceHolds(session, null, clients, 100);
        if (list.size() == 0) return "Нет";

        String balanceHold = "";
        for (ClientBalanceHold cbh : list) {
            balanceHold += String.format("Заблокированная сумма: %s р., создание: %s, дата: %s, статус: %s", CurrencyStringUtils
                            .copecksToRubles(cbh.getHoldSum()),
                    cbh.getCreateStatus().toString(), CalendarUtils.dateTimeToString(cbh.getCreatedDate()), cbh.getRequestStatus().toString()) + "<br/>";
        }
        return balanceHold;
    }
}
