/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

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

    public ClientBalanceHold createClientBalanceHold(Session session, String guid, Client client, Long holdSum, Org oldOrg, Org newOrg,
            Contragent oldContragent, Contragent newContragent, ClientBalanceHoldCreateStatus createStatus,
            ClientBalanceHoldRequestStatus requestStatus, Client declarer, String phoneOfDeclarer,
            String declarerInn, String declarerAccount, String declarerBank, String declarerBik, String declarerCorrAccount, Long version,
            Long idOfOrgLastChange, ClientBalanceHoldLastChangeStatus lastChangeStatus) {
        if (version == null) version = DAOUtils.nextVersionByClientBalanceHold(session);
        ClientBalanceHold clientBalanceHold = new ClientBalanceHold();
        clientBalanceHold.setGuid(guid == null ? UUID.randomUUID().toString() : guid);
        clientBalanceHold.setClient(client);
        clientBalanceHold.setHoldSum(holdSum);
        clientBalanceHold.setOldOrg(oldOrg);
        clientBalanceHold.setNewOrg(newOrg);
        clientBalanceHold.setOldContragent(oldContragent);
        clientBalanceHold.setNewContragent(newContragent);
        clientBalanceHold.setCreatedDate(new Date());
        clientBalanceHold.setLastUpdate(new Date());
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
        clientBalanceHold.setIdOfOrgLastChange(idOfOrgLastChange);
        clientBalanceHold.setLastChangeStatus(lastChangeStatus);
        return clientBalanceHold;
    }

    public static BalanceHoldTransaction processClientBalanceHoldTransaction(Session session, String guid, Long summ, Boolean isCommit) throws Exception {
        Query query = session.createQuery("select cbh from ClientBalanceHold cbh where cbh.guid = :guid");
        query.setParameter("guid", guid);
        ClientBalanceHold clientBalanceHold = (ClientBalanceHold)query.uniqueResult();
        if (clientBalanceHold == null) throw new Exception("Client Balance Hold not found by guid");
        Long nextVersion = DAOUtils.nextVersionByClientBalanceHold(session);
        clientBalanceHold.setHoldSum(clientBalanceHold.getHoldSum() - (isCommit ? summ : -summ));
        clientBalanceHold.setVersion(nextVersion);
        session.update(clientBalanceHold);
        return new BalanceHoldTransaction(clientBalanceHold, isCommit ? summ : -summ, new Date());
    }

    public void holdClientBalance(String guid, Client client, Long holdSum, Client declarer, Org oldOrg, Org newOrg, Contragent oldContragent, Contragent newContragent,
            ClientBalanceHoldCreateStatus createStatus, ClientBalanceHoldRequestStatus requestStatus, String phoneOfDeclarer,
            String declarerInn, String declarerAccount, String declarerBank, String declarerBik, String declarerCorrAccount, Long version, Long idOfOrgLastChange,
            ClientBalanceHoldLastChangeStatus lastChangeStatus) throws Exception {
        if (client.getBalance() - holdSum < 0L) throw new Exception("Not enough balance");
        RuntimeContext.getFinancialOpsManager().holdClientBalance(guid, client, holdSum, declarer, oldOrg, newOrg, oldContragent,
                newContragent, createStatus, requestStatus, phoneOfDeclarer, declarerInn, declarerAccount, declarerBank, declarerBik, declarerCorrAccount, version,
                idOfOrgLastChange, lastChangeStatus);
    }

    public void declineClientBalance(Long idOfClientBalanceHold, ClientBalanceHoldRequestStatus status, ClientBalanceHoldLastChangeStatus lastChangeStatus) throws Exception {
        RuntimeContext.getFinancialOpsManager().declineClientBalance(idOfClientBalanceHold, status, lastChangeStatus);
    }

    public List<ClientBalanceHold> getClientBalanceHoldForOrgSinceVersion(Session session,
            long orgOwner, long version) throws Exception {
        List<Long> listOfOrgs = DAOReadonlyService.getInstance().findFriendlyOrgsIds(orgOwner);
        Query query = session.createQuery("select cbh from ClientBalanceHold cbh "
                + "where cbh.version > :version and cbh.oldOrg.idOfOrg in (:idOfOrgList)");
        query.setParameter("version", version);
        query.setParameterList("idOfOrgList", listOfOrgs);
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void setStatusWithValue(Long idOfClientBalanceHold, ClientBalanceHoldRequestStatus status, ClientBalanceHoldLastChangeStatus lastChangeStatus) {
        Session session = (Session)em.getDelegate();
        Long nextVersion = DAOUtils.nextVersionByClientBalanceHold(session);
        javax.persistence.Query query = em.createQuery("update ClientBalanceHold set requestStatus = :status, version = :version, "
                + "lastUpdate = :lastUpdate, lastChangeStatus = :lastChangeStatus, idOfOrgLastChange = null where idOfClientBalanceHold = :id");
        query.setParameter("status", status);
        query.setParameter("id", idOfClientBalanceHold);
        query.setParameter("version", nextVersion);
        query.setParameter("lastUpdate", new Date());
        query.setParameter("lastChangeStatus", lastChangeStatus);
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
                    cbh.getCreateStatus().toString(), CalendarUtils.dateTimeToString(cbh.getCreatedDate()), cbh.getRequestStatus().toString());
            if (!StringUtils.isEmpty(cbh.getPhoneOfDeclarer())) balanceHold += ", телефон представителя: " + cbh.getPhoneOfDeclarer();
            balanceHold += "<br/>";
        }
        return balanceHold;
    }

    public long getClientBalanceHoldSum(Client client) {
        return client.getBalance();
    }

    public ClientBalanceHold getClientBalanceHoldByGuid(String guid) {
        javax.persistence.Query query = em.createQuery("select cbh from ClientBalanceHold cbh where cbh.guid = :guid");
        query.setParameter("guid", guid);
        try {
            return (ClientBalanceHold) query.getSingleResult();
        } catch(Exception e) {
            return null;
        }
    }

    public List<ClientBalanceHold> getClientBalanceHoldListByClient(Session session, Client client) {
        Query query = session.createQuery("select cbh from ClientBalanceHold cbh "
                + "where cbh.client = :client and cbh.requestStatus <> :status order by cbh.createdDate");
        query.setParameter("client", client);
        query.setParameter("status", ClientBalanceHoldRequestStatus.ANNULLED);
        return query.list();
    }
}
