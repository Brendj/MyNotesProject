/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.client;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.client.items.ClientMigrationHistoryReportItem;
import ru.axetta.ecafe.processor.core.daoservices.client.items.ReportOnNutritionItem;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
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
public class ClientDAOService extends AbstractDAOService {

    @SuppressWarnings("unchecked")
    public List<ClientMigrationHistoryReportItem> generate(Long idOfOrg, Date startDate, Date endDate) {
        Criteria criteria = getSession().createCriteria(ClientMigration.class);
        criteria.createAlias("client", "cl", JoinType.LEFT_OUTER_JOIN)
                .createAlias("cl.org", "o", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("o.idOfOrg", idOfOrg));
        criteria.createAlias("cl.person","person", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("org","organization", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.between("registrationDate", startDate, endDate));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("cl.idOfClient"), "idOfClient")
                .add(Projections.property("cl.contractId"), "contractId")
                .add(Projections.property("person.firstName"), "firstName")
                .add(Projections.property("person.surname"), "surname")
                .add(Projections.property("person.secondName"), "secondName")
                .add(Projections.property("cl.clientGUID"), "guid")
                .add(Projections.property("organization.idOfOrg"),"idOfOrg")
                .add(Projections.property("organization.shortName"),"shortName")
                .add(Projections.property("registrationDate"),"registrationDate")
        );
        criteria.setResultTransformer(Transformers.aliasToBean(ClientMigrationHistoryReportItem.class));
        return (List<ClientMigrationHistoryReportItem>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<ReportOnNutritionItem> generateReportOnNutritionByWeekReport(Long idOfOrg, Date startDate, Date endDate){
        Criteria criteria = getSession().createCriteria(OrderDetail.class,"details");
        criteria.createAlias("details.order","ord", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("ord.org","org", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("ord.client","client", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("client.person","person", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("client.clientGroup","clientgroup", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("org.id", idOfOrg));
        criteria.add(Restrictions.eq("details.state",0));
        criteria.add(Restrictions.eq("ord.state",0));
        //criteria.add(Restrictions.eq("ord.org", org));
        //criteria.add(Restrictions.eq("client.clientGroup", clientGroup));
        criteria.add(Restrictions.between("ord.createTime", startDate, endDate));
        criteria.addOrder(Order.desc("ord.createTime"));
        criteria.addOrder(Order.asc("person.surname"));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.sqlGroupProjection(
                        "(case when {alias}.menuType>=50 then 1 when {alias}.menuType=0 then 0 else -1 end) as menuType",
                        "{alias}.menuType", new String[]{"menuType"}, new Type[]{new IntegerType()}))
                .add(Projections.groupProperty("client.id"))
                .add(Projections.property("client.balance"),"balance")
                .add(Projections.groupProperty("person.id"))
                .add(Projections.property("person.surname"), "surname")
                .add(Projections.property("person.firstName"), "firstName")
                .add(Projections.property("person.secondName"), "secondName")
                .add(Projections.groupProperty("clientgroup.groupName"), "groupName")
                .add(Projections.groupProperty("ord.createTime"),"createTime")
                .add(Projections.sum("RPrice"),"price")
        );
        criteria.setResultTransformer(Transformers.aliasToBean(ReportOnNutritionItem.class));
        return (List<ReportOnNutritionItem>) criteria.list();
    }
}
