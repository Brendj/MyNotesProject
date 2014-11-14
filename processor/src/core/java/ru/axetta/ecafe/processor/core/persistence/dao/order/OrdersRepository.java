/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.order;

import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;

import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: shamil
 * Date: 10.10.14
 * Time: 10:57
 */
@Repository
public class OrdersRepository extends BaseJpaDao {

    private List<OrderItem> getOrderItemBySQL(String query) {
        List<Object[]> temp = entityManager.createNativeQuery(query).getResultList();
        return parse(temp);
    }

    private List<OrderItem> parse(List<Object[]> temList) {
        List<OrderItem> resultList = new ArrayList<OrderItem>();
        for (Object[] result : temList) {
            resultList.add(new OrderItem(((BigInteger) result[0]).longValue(), (String) result[1],((BigInteger) result[2]).longValue(),
                    ((BigInteger) result[3]).longValue(),
                    (Integer) result[4], (Integer) result[5], (String) result[6], (String) result[7],
                    (Integer) result[8]));
        }
        return resultList;
    }

    public List<OrderItem> findOrdersByClientIds(String orgsIdsString, String clientIds, Date startTime, Date endTime) {
        String sql =
                " SELECT o.idoforg, og.shortname, o.idofclient, o.createddate, o.ordertype, od.menutype, od.menudetailname, g.groupname , od.qty "
                        + " FROM cf_orders o "
                        + " INNER JOIN cf_orderdetails od ON o.idoforder= od.idoforder AND o.idoforg = od.idoforg "
                        + " INNER JOIN cf_clients c ON c.idofclient = o.idofclient and o.idoforg = c.idoforg "
                        + " INNER JOIN cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND g.idoforg = c.idoforg "
                        + " INNER JOIN cf_orgs og ON o.idoforg =og.idoforg "
                        + " WHERE o.idoforg in ( " + orgsIdsString + " ) AND od.menutype >= 50 AND od.menutype<=99 ";
        if (clientIds != null) {
            sql += " AND o.idofclient in ( " + clientIds + ") ";

        }
        sql += " AND o.socdiscount > 0 " + " AND o.createddate between " + startTime.getTime()
                + " AND " + endTime.getTime() + " " + " AND o.state = 0 "
                + " AND o.ordertype in (4,6,8) AND c.idofclientgroup < 1100000000 "
                + " ORDER BY g.groupname,  c.idofclient, o.createddate, o.ordertype, od.menudetailname ";
        return getOrderItemBySQL(sql);
    }

    public List<OrderItem> findOrdersByClientIds(long idOfOrg, Date startTime, Date endTime) {
        return findOrdersByClientIds("" + idOfOrg, null, startTime, endTime);
    }

    public List<OrderItem> findOrdersByClientIds(String orgsIdsString, Date startTime, Date endTime) {
        return findOrdersByClientIds(orgsIdsString, null, startTime, endTime);
    }

    public List<OrderItem> findOrdersByClientIds(long idOfOrg, String clientIds, Date startTime, Date endTime) {
        return findOrdersByClientIds("" + idOfOrg, clientIds, startTime, endTime);
    }
}
