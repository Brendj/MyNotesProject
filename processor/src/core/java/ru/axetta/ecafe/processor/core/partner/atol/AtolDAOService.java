/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.AtolCompany;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
}
