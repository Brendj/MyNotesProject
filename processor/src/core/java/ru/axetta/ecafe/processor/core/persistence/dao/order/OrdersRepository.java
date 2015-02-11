/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.order;

import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;

import org.springframework.stereotype.Repository;

import javax.persistence.Query;
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
                    (Integer) result[8],
                    ((BigInteger) result[9]).longValue(),(String)result[10],(String)result[11]));
        }
        return resultList;
    }

    public List<OrderItem> findOrdersByClientIds(String orgsIdsString, String clientIds, Date startTime, Date endTime) {
        String sql =
                " SELECT o.idoforg, og.shortname, o.idofclient, o.createddate, o.ordertype, od.menutype, od.menudetailname, g.groupname , od.qty, cog.idoforg as idofclientorg, cog.shortname as cogshort, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname "
                        + " FROM cf_orders o "
                        + " INNER JOIN cf_orderdetails od ON o.idoforder= od.idoforder AND o.idoforg = od.idoforg "
                        + " INNER JOIN cf_clients c ON c.idofclient = o.idofclient and o.idoforg in (" + orgsIdsString+ ") "
                        + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                        + " INNER JOIN cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND g.idoforg = c.idoforg "
                        + " INNER JOIN cf_orgs og ON o.idoforg =og.idoforg "
                        + " INNER JOIN cf_orgs cog ON c.idoforg=cog.idoforg "
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


    /*
    * return
    * orgName - org ShortName
    * orderDate - orderDate
    * sum - sum
    *
    * */
    public List<OrderItem> findAllBuffetOrders(Date startDate, Date endDate){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery("SELECT org.ShortName AS name, o.createdDate, (od.rPrice *od.qty)AS sum "
                + "FROM CF_Orders o "
                + "JOIN CF_OrderDetails od ON (o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg) "
                + "JOIN CF_Orgs org ON (org.idOfOrg = od.idOfOrg) "
                + "WHERE o.createdDate >= :startDate AND o.createdDate <= :endDate "
                + "AND od.menuType = 0  AND o.state=0 AND od.state=0 "
                + "ORDER BY org.officialName")
                .setParameter("startDate",startDate.getTime())
                .setParameter("endDate",endDate.getTime());

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orderItemList.add(new OrderItem((String)o[0],((BigInteger)o[1]).longValue(),((BigInteger)o[2]).longValue()));
        }
        return orderItemList;
    }

    /*
    * return
    * orgName - org ShortName
    * orderDate - orderDate
    * sum - sum
    *
    * */
    public List<OrderItem> findAllFreeComplex(Date startDate, Date endDate){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery("SELECT org.ShortName AS name, o.createdDate, (od.discount*od.qty)AS sum "
                + "from CF_Orders o, CF_OrderDetails od, CF_Orgs org "
                + "where o.idOfOrder = od.idOfOrder and o.state=0 and od.state=0 "
                + "and o.idOfOrg = od.idOfOrg   and org.idOfOrg = od.idOfOrg " + "and o.createdDate >= :startDate "
                + " and o.createdDate <= :endDate  and (od.menuType >= 50 and od.menuType <= 99) "
                + " and (od.socDiscount > 0) "
                + "order by org.officialName")
                .setParameter("startDate", startDate.getTime())
                .setParameter("endDate",endDate.getTime());

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orderItemList.add(new OrderItem((String)o[0],((BigInteger)o[1]).longValue(),((BigInteger)o[2]).longValue()));
        }
        return orderItemList;
    }

    /*
    * return
    * orgName - org ShortName
    * orderDate - orderDate
    * sum - sum
    *
    * */
    public List<OrderItem> findAllPayComplex(Date startDate, Date endDate){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery("SELECT org.shortname,o.createdDate , (od.rPrice * od.qty) "
                + " FROM CF_Orders o, CF_OrderDetails od, CF_Orgs org  WHERE o.idOfOrder = od.idOfOrder   "
                + " AND o.idOfOrg = od.idOfOrg AND org.idOfOrg = od.idOfOrg  "
                + " AND o.createdDate >= :startDate AND o.createdDate <= :endDate   AND (od.menuType >= 50 AND od.menuType <= 99) "
                + "  AND (od.socDiscount = 0) AND o.state=0 AND od.state=0  "
                + "   ORDER BY org.officialName")
                .setParameter("startDate", startDate.getTime())
                .setParameter("endDate",endDate.getTime());

        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orderItemList.add(new OrderItem((String)o[0],((BigInteger)o[1]).longValue(),((BigInteger)o[2]).longValue()));
        }
        return orderItemList;
    }


    public List<OrderItem> findAllBeneficiaryComplexes(){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT  o.idoforg,o.ordertype, count(*)   FROM cf_orders o "
                        + "WHERE o.socdiscount > 0 AND o.ordertype IN (4,6,8) AND o.state=0 "
                        + "AND o.createddate BETWEEN  1409877287000 AND 1409963687000 "
                        + "GROUP BY o.idoforg, o.ordertype " + "ORDER BY o.idoforg");
        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orderItemList.add(new OrderItem(((BigInteger)o[0]).longValue(),(Integer)o[1],((BigInteger)o[2]).longValue()));
        }
        return orderItemList;

    }


    public List<OrderItem> findAllWithEnterEventCount(){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT w.idoforg,w.ordertype, w.col, count(*) "
                        + " FROM (SELECT distinct on (o.idofclient )"
                        + "o.idoforg,o.ordertype, q.col, o.idofclient   FROM cf_orders o "
                        + " INNER JOIN (SELECT c.idofclient, e.idofenterevent, CASE WHEN e.idofenterevent IS NOT null THEN 1 ELSE 0 END AS col   "
                        + "      FROM cf_clients c  "
                        + "      LEFT JOIN cf_enterevents e ON c.idofclient=e.idofclient AND e.evtdatetime BETWEEN  1409877287000 AND 1409963687000 AND e.passdirection IN (0,1,6,7) "
                        + "      WHERE c.DiscountMode > 0 AND c.idofclientgroup <  1100000000 ) q ON o.idofclient = q.idofclient "
                        + "  WHERE o.socdiscount > 0   AND o.ordertype IN (4,8)   AND o.state=0 "
                        + "  AND o.createddate BETWEEN  1409877287000 AND 1409963687000  "
                        + "  GROUP BY o.idoforg, o.idofclient, o.ordertype, q.col   ORDER BY o.idofclient) w "
                        + "GROUP BY w.idoforg, w.ordertype, w.col ORDER BY w.idoforg");
        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orderItemList.add(new OrderItem(((BigInteger)o[0]).longValue(),(Integer)o[1],(Integer)o[2],((BigInteger)o[3]).longValue()));
        }
        return orderItemList;
    }

    public List<OrderItem> findAllWithNoEnterEventCount(){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery(
                "select e1.idoforg, count(*) from( select        e.idoforg, count(*)      "
                        + "from cf_enterevents e     left join cf_clients c on e.idofclient = c.idofclient      "
                        + "        where      e.evtdatetime between  1409877287000 and 1409963687000  "
                        + "and e.idofclient is not null and c.discountmode = 3   "
                        + "and e.idofclient not in (select     distinct o.idofclient    from cf_orders o  "
                        + "left join cf_orderdetails od  on o.idoforder=od.idoforder      "
                        + "where o.socdiscount > 0 and (od.menutype >=  50 and od.menutype <=99)  "
                        + "and o.createddate  between  1409877287000 and 1409963687000)      "
                        + "group by e.idoforg, e.idofclient     ) e1        group by e1.idoforg      ");
        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orderItemList.add(new OrderItem(((BigInteger)o[0]).longValue(),((BigInteger)o[1]).longValue()));
        }
        return orderItemList;
    }
}
