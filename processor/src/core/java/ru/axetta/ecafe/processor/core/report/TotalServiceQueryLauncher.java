/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.06.14
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class TotalServiceQueryLauncher {
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;


    @Transactional
    public void loadOrgs(String orgCondition, Map<Long, TotalServicesReport.TotalEntry> entries) {
        String preparedQuery =
                "select cf_orgs.idoforg, cf_orgs.officialname, count(distinct cf_clients.idofclient) " +
                        "from cf_orgs " +
                        "left join cf_clients on cf_clients.idoforg = cf_orgs.idoforg " +
                        (orgCondition.length() > 0 ? "where " + orgCondition + " AND " : "where ") +
                        "cf_orgs.state=1 and  cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES
                        .getValue() + " " +
                        "group by cf_orgs.idoforg";
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createSQLQuery(preparedQuery);
        List resultList = query.list();

        for (Object result : resultList) {
            Object e[] = (Object[]) result;
            long id = ((BigInteger) e[0]).longValue();
            String officialName = ((String) e[1]).trim();

            TotalServicesReport.TotalEntry item = new TotalServicesReport.TotalEntry(officialName);
            item.put("totalClientsCount", e[2]);
            entries.put(id, item);
        }
    }

    @Transactional
    public void loadValue(Map<Long, TotalServicesReport.TotalEntry> entries, String valueKey, String preparedQuery) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createSQLQuery(preparedQuery);
        List resultList = query.list();

        for (Object result : resultList) {
            Object e[] = (Object[]) result;
            long id = ((BigInteger) e[0]).longValue();

            try {
                TotalServicesReport.TotalEntry item = entries.get(id);
                item.put(valueKey, e[1]);
            } catch (Exception e1) {
            }
        }
    }
}
