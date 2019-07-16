/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ClientData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class OkuDAOService {

    private static final Logger logger = LoggerFactory.getLogger(OkuDAOService.class);

    private static List<Long> clientGroupList = new ArrayList<>();

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager emReport;

    @PostConstruct
    private void init() {
        clientGroupList.add(ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
    }

    @Transactional(readOnly = true)
    public ClientData checkClient(Long contractId, String surname) {
        Query query = em.createQuery(
                "select c from Client c join c.person p where c.contractId = :contractId and lower(p.surname) = :surname");
        query.setParameter("contractId", contractId);
        query.setParameter("surname", surname.toLowerCase());
        query.setMaxResults(1);
        try {
            Client client = (Client) query.getSingleResult();
            return new ClientData(client.getOrg().getIdOfOrg());
        } catch (Exception e) {
            logger.info(String.format("Unable to find client with contractId=%s, surname=%s", contractId, surname));
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setClientAsUserOP(Long contractId) throws Exception {
        Query query = em.createQuery("update Client c set c.userOP = true where c.contractId = :contractId");
        query.setParameter("contractId", contractId);
        int res = query.executeUpdate();
        if (res != 1) {
            throw new Exception("Client not found");
        }
    }

    public static List<Long> getClientGroupList() {
        return clientGroupList;
    }
}
