/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 20.02.2018.
 */
@Service
@Transactional(readOnly = true)
public class AcceptanceOfCompletedWorksActDAOService extends AbstractDAOService {

    private final static Logger logger = LoggerFactory.getLogger(AcceptanceOfCompletedWorksActDAOService.class);

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public static AcceptanceOfCompletedWorksActDAOService getInstance() {
        return RuntimeContext.getAppContext().getBean(AcceptanceOfCompletedWorksActDAOService.class);
    }

    public List<AcceptanceOfCompletedWorksActItem> findAllItemsForAct(BasicReportJob.OrgShortItem org, Date startTime,
            Date endTime) {
        List<AcceptanceOfCompletedWorksActItem> result = new ArrayList<AcceptanceOfCompletedWorksActItem>();

        List res;

        Query query = getSession().createSQLQuery("SELECT * from cf_orhs where idoforg = :idOfOrg");
        query.setParameter("idOfOrg", org.getIdOfOrg());
        res = query.list();

        AcceptanceOfCompletedWorksActItem acceptanceOfCompletedWorksActItem = new AcceptanceOfCompletedWorksActItem();

        result.add(acceptanceOfCompletedWorksActItem);
        return result;
    }




}
