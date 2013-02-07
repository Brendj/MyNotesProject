/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.client;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.persistence.ClientMigration;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

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

}
