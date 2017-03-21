/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/***
 *
 * Класс для получения данных, используемых во взаимодействии с платежными контрагентами, ПГУ и МП
 * Отдельный слейв БД для минимизации времени отклика вызовов внешних сервисов.
 * Не использовать для получения данных в методах синхронизации и внутренних сервисов!
 *
 ***/
@Component
@Scope("singleton")
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DAOReadExternalsService {
    private final static Logger logger = LoggerFactory.getLogger(DAOReadExternalsService.class);

    @PersistenceContext(unitName = "externalServicesPU")
    private EntityManager entityManager;

    public static DAOReadExternalsService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOReadExternalsService.class);
    }

    public String getContragentPublicKeyString(Long idOfContragent) throws Exception {
        try {
            Query query = entityManager
                .createQuery("select c.publicKey from Contragent c where c.idOfContragent = :idOfContragent",
                        String.class);
            query.setParameter("idOfContragent", idOfContragent);
            return (String) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NullPointerException("Unknown contragent with id == "+idOfContragent);
        }
    }

    public Long getContractIdByCardNo(long cardId) {
        try {
            Query query = entityManager
                .createQuery("select c.client.contractId from Card c where c.cardNo = :cardNo", Long.class);
            query.setParameter("cardNo", cardId);
            return (Long) query.getSingleResult();
        } catch (Exception ignore) {
            return null;
        }
    }

    public Contragent findContragent(Long idOfContragent) {
        try {
            Query query = entityManager
                    .createQuery("select c from Contragent c where c.idOfContragent = :idOfContragent", Contragent.class)
                    .setParameter("idOfContragent", idOfContragent);
            return (Contragent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Contragent findContragentByClient(Long clientContractId) {
        try {
            Query query = entityManager
                    .createQuery("select c.org.defaultSupplier from Client c where c.contractId = :contractId", Contragent.class)
                    .setParameter("contractId", clientContractId);
            return (Contragent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean existClientPayment(Contragent contragent, PaymentRequest.PaymentRegistry.Payment payment) {
        String additionalCondition = "";
        if (payment.getAddIdOfPayment() == null || !payment.getAddIdOfPayment().startsWith(RNIPLoadPaymentsService.SERVICE_NAME)) {
            additionalCondition = String.format(" and cp.contragent.idOfContragent = %s", contragent.getIdOfContragent());
        }
        Query query = entityManager
                .createQuery("select count(cp.idOfClientPayment) from ClientPayment cp where cp.idOfPayment = :idOfPayment" + additionalCondition);
        query.setParameter("idOfPayment", payment.getIdOfPayment());
        return (Long)query.getSingleResult() > 0;
    }

    /*
    * Поиск клиента по idOfClient или contractId
    * */
    public Client findClient(Long idOfClient, Long contractId) throws Exception {
        if ((idOfClient != null && contractId != null) || (idOfClient == null && contractId == null))
            throw new Exception("Invalid arguments");
        try {
            String query_str = (idOfClient != null ? "select c from Client c where c.idOfClient = :parameter" : "select c from Client c where c.contractId = :parameter");
            Query query = entityManager.createQuery(query_str);
            query.setParameter("parameter", idOfClient != null ? idOfClient : contractId);
            return (Client) query.getSingleResult();
        } catch(Exception e) {
            return null;
        }
    }

    /*public List<Client> findListOfClientsByListOfIds(List<Long> idsOfClient) throws Exception {
        try {
            Query query = entityManager.createQuery("select c from Client c where idOfClient in :list");
            query.setParameter("list", idsOfClient);
            return query.getResultList();
        } catch(Exception e) {
            return null;
        }
    }*/

    public ContragentClientAccount findContragentClientAccount(CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount) {
        return entityManager.find(ContragentClientAccount.class, compositeIdOfContragentClientAccount);
    }

    public Org findOrg(Long idOfOrg) {
        return entityManager.find(Org.class, idOfOrg);
    }

    public Contragent getOrgDefaultSupplier(Long idOfOrg) {
        try {
            return entityManager.find(Contragent.class, findOrg(idOfOrg).getDefaultSupplier().getIdOfContragent());
        } catch (Exception e) {
            return null;
        }
    }

    public Client refreshClient(Client client) {
        return entityManager.merge(client);
    }

    public Person findPerson(Long idOfPerson) {
        return entityManager.find(Person.class, idOfPerson);
    }

    public List<Client> findGuardiansByClient(Long idOfChildren, Long idOfGuardian) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return ClientManager.findGuardiansByClient(session, idOfChildren, idOfGuardian);
    }

    public Boolean allowedGuardianshipNotification(Long guardianId, Long clientId, Long notifyType) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return ClientManager.allowedGuardianshipNotification(session, guardianId, clientId, notifyType);
    }

    /*public List<Long> extractIDFromGuardByGuardMobile(String guardMobile) {
        Set<Long> result = new HashSet<Long>();
        String query = "select client.idOfClient from Client client where client.phone=:guardMobile or client.mobile=:guardMobile"; //все клиенты с номером телефона
        Query q = entityManager.createQuery(query, Long.class);
        q.setParameter("guardMobile", guardMobile);
        List<Long> clients = q.getResultList();

        if (clients != null && !clients.isEmpty()){
            for(Long id : clients){
                Query q2 = entityManager.createQuery("select cg from ClientGuardian cg " +
                        "where cg.idOfGuardian = :idOfGuardian and cg.deletedState = false", ClientGuardian.class);  //все дети текущего клиента
                q2.setParameter("idOfGuardian", id);
                List<ClientGuardian> list = q2.getResultList();
                if (list != null && list.size() > 0) {
                    for (ClientGuardian cg : list) {
                        if (!cg.isDisabled()) {
                            result.add(cg.getIdOfChildren());
                        }
                    }
                } else {
                    result.add(id);
                }
            }
        }

        return new ArrayList<Long>(result);
    }*/
}
