/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 18.10.2017.
 */
public class FreeComplexReportBuilder extends AllComplexReportBuilder {

    @Override
    protected List getResultList(Session session, String orgCondition, Date startDate, Date endDate, boolean tempClients) {
        String preparedQuery = "select org.officialName, od.menuDetailName, od.rPrice, od.discount, "
                + "sum(od.qty) as quantity, " + " min(o.createdDate), max(o.createdDate) "
                + "  from CF_Orders o, CF_OrderDetails od, CF_Orgs org, cf_clients c "
                + " where o.idOfOrder = od.idOfOrder and o.state=0 and od.state=0 "
                + "   and o.idOfOrg = od.idOfOrg" + "   and org.idOfOrg = od.idOfOrg "
                + "   and o.idofclient = c.idofclient and c.idoforg" + (tempClients ? " not " : "")
                + " in (select friendlyOrg from cf_friendly_organization where currentorg = o.idoforg) "
                + "   and o.createdDate >= :fromCreatedDate " + "   and o.createdDate <= :toCreatedDate"
                + "   and (od.menuType >= :fromMenuType and od.menuType <= :toMenuType) " + orgCondition
                + "   and (od.socDiscount > 0 and od.rprice = 0) "
                + " group by org.officialName, od.menuDetailName, od.rPrice, od.discount "
                + " order by org.officialName, od.menuDetailName";
        Query query = session.createSQLQuery(preparedQuery);

        long startDateLong = startDate.getTime();
        long endDateLong = endDate.getTime();
        query.setLong("fromCreatedDate", startDateLong);
        query.setLong("toCreatedDate", endDateLong);
        query.setInteger("fromMenuType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setInteger("toMenuType", OrderDetail.TYPE_COMPLEX_MAX);

        return query.list();
    }
}
