/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AtolCompany;
import ru.axetta.ecafe.processor.core.persistence.AtolPacket;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by nuc on 27.08.2019.
 */
@Component
@Scope("singleton")
public class AtolDAOService {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public static AtolDAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(AtolDAOService.class);
    }

    public AtolCompany getAtolCompany(Session session) {
        Criteria criteria = session.createCriteria(AtolCompany.class);
        return (AtolCompany)criteria.uniqueResult();
    }

    public AtolCompany getAtolCompany() {
        Query query = entityManager.createQuery("select ac from AtolCompany ac");
        query.setMaxResults(1);
        return (AtolCompany)query.getSingleResult();
    }

    public List<ClientPaymentAddon> getPaymentsToSend() {
        Query query = entityManager.createQuery("select cpa from ClientPaymentAddon cpa join fetch cpa.clientPayment cp "
                + "join fetch cp.transaction t "
                + "join fetch t.client c "
                + "where cpa.atolStatus = :status order by cpa.createdDate");
        query.setParameter("status", AtolPaymentNotificator.ATOL_NEW);
        query.setMaxResults(AtolPaymentNotificator.LIMIT_PER_RUN);
        return query.getResultList();
    }

    @Transactional
    public void saveAtolPacket(ClientPaymentAddon clientPaymentAddon, String packet) {
        AtolPacket atolPacket = new AtolPacket(clientPaymentAddon, packet);
        entityManager.persist(atolPacket);

    }

    private AtolPacket getLastAtolPacket(ClientPaymentAddon clientPaymentAddon) {
        Query query = entityManager.createQuery("select ap from AtolPacket ap where ap.clientPaymentAddon.idOfClientPaymentAddon = :clientPaymentAddon order by ap.createdDate desc");
        query.setParameter("clientPaymentAddon", clientPaymentAddon.getIdOfClientPaymentAddon());
        query.setMaxResults(1);
        return (AtolPacket)query.getSingleResult();
    }

    @Transactional
    public void saveWithSuccess(ClientPaymentAddon clientPaymentAddon, String uuid, String response) {
        clientPaymentAddon.setAtolStatus(AtolPaymentNotificator.ATOL_SENT);
        clientPaymentAddon.setAtolUpdate(new Date());
        entityManager.merge(clientPaymentAddon);

        AtolPacket atolPacket = getLastAtolPacket(clientPaymentAddon);
        atolPacket.setLastUpdate(new Date());
        atolPacket.setAtolUUid(uuid);
        atolPacket.setResponse(response);
        entityManager.merge(atolPacket);
    }

    @Transactional
    public void saveWithError(ClientPaymentAddon clientPaymentAddon, int status) {
        AtolPacket atolPacket = getLastAtolPacket(clientPaymentAddon);
        atolPacket.setLastUpdate(new Date());
        atolPacket.setResponse(Integer.toString(status));
        entityManager.merge(atolPacket);
    }
}
