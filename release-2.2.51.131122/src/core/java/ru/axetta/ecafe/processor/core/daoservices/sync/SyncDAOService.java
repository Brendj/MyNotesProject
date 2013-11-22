/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.sync;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.client.items.ClientMigrationHistoryReportItem;
import ru.axetta.ecafe.processor.core.daoservices.client.items.ReportOnNutritionItem;
import ru.axetta.ecafe.processor.core.daoservices.sync.items.Sync;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.SyncHistory;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.01.13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class SyncDAOService extends AbstractDAOService {

    @SuppressWarnings("unchecked")
    public List<Sync> buildSyncReport(Date startDate, Date endDate, List<Long> idOfOrgList){
        Criteria syncCriteria = getSession().createCriteria(SyncHistory.class);
        syncCriteria.createAlias("org","organization", JoinType.LEFT_OUTER_JOIN);
        Criterion expression1 = Restrictions
                .and(Restrictions.ge("syncStartTime", startDate), Restrictions.le("syncStartTime", endDate));
        Criterion expression2 = Restrictions
                .and(Restrictions.ge("syncEndTime", startDate), Restrictions.le("syncEndTime", endDate));
        Criterion expression = Restrictions.or(expression1, expression2);
        Criterion orgInExpression = Restrictions.in("organization.idOfOrg", idOfOrgList);

        expression = Restrictions.and(orgInExpression, expression);
        syncCriteria.add(expression);
        syncCriteria.addOrder(Order.asc("organization.idOfOrg"));
        syncCriteria.addOrder(Order.desc("syncStartTime"));
        syncCriteria.setProjection(Projections.projectionList()
                .add(Projections.property("syncStartTime"), "syncStartTime")
                .add(Projections.property("syncEndTime"), "syncEndTime")
                .add(Projections.property("organization.idOfOrg"),"idOfOrg")
                .add(Projections.property("organization.officialName"),"officialName")
        );
        syncCriteria.setResultTransformer(Transformers.aliasToBean(Sync.class));
        return syncCriteria.list();
    }

}
