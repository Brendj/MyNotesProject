/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.order;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.dao.BaseJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
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

    private static final Logger logger = LoggerFactory.getLogger(OrdersRepository.class);

    public static OrdersRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(OrdersRepository.class);
    }

    private List<OrderItem> getOrderItemBySQL(String query) {
        List<Object[]> temp = entityManager.createNativeQuery(query).getResultList();
        return parse(temp);
    }

    private List<OrderItem> parse(List<Object[]> temList) {
        List<OrderItem> resultList = new ArrayList<OrderItem>();
        for (Object[] result : temList) {
            resultList.add(
                    new OrderItem(((BigInteger) result[0]).longValue(), (String) result[1],((BigInteger) result[2]).longValue(),
                    ((BigInteger) result[3]).longValue(),
                    (Integer) result[4], (Integer) result[5], (String) result[6], (String) result[7],
                    (Integer) result[8],((BigInteger) result[9]).longValue(),
                    (String)result[10],(String)result[11],((BigInteger) result[12]).longValue()) );
        }
        return resultList;
    }

    public List<OrderItem> findOrdersByClientIds(String orgsIdsString, String clientIds, Date startTime, Date endTime) {
        String sql =
                " SELECT o.idoforg, og.shortname, o.idofclient, o.createddate, o.ordertype, od.menutype, od.menudetailname, g.groupname , od.qty, cog.idoforg as idofclientorg, cog.shortname as cogshort, (p.surname || ' ' || p.firstname || ' ' || p.secondname) as fullname, (o.createddate - o.orderdate) as datediff "
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
                + " AND o.ordertype in (4,6,8,11,12) AND c.idofclientgroup < 1100000000 "
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


    public List<OrderItem> findAllOrders(List<Long> idOfOrgsList, Date startDate, Date endDate) {
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    connection.prepareStatement("SET enable_seqscan TO OFF").execute();
                }
            });

            org.hibernate.Query nativeQuery = session.createSQLQuery(
                    "SELECT (o.idoforg) AS name, o.createdDate, ((od.rprice + od.discount) *od.qty)AS sum, od.socDiscount, "
                            + "od.menutype, od.menuOrigin, o.idofclient, g.groupname, o.idofclientgroup, od.rprice*od.qty as sumPay, "
                            + "od.socdiscount*od.qty as sumDiscount, od.discount*od.qty as kazanDiscount "
                            + "FROM CF_Orders o "
                            + "INNER JOIN cf_orderdetails od ON o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg "
                            + "LEFT JOIN cf_clientgroups g ON o.idofclientgroup = g.idofclientgroup AND o.idoforg = g.idoforg "
                            + "WHERE o.idoforg IN (:idOfOrgs) "
                            + "AND o.createdDate >= :startDate AND o.createdDate <= :endDate "
                            + "AND (od.menuType = 0 OR (od.menuType >= 50 AND od.menuType <= 99)) "
                            + "AND o.state=0 AND od.state=0 ORDER BY o.idoforg");
            nativeQuery.setParameterList("idOfOrgs", idOfOrgsList);
            nativeQuery.setParameter("startDate", startDate.getTime());
            nativeQuery.setParameter("endDate", endDate.getTime());

            List temp = nativeQuery.list();
            for (Object entry : temp) {
                Object o[] = (Object[]) entry;

                orderItemList.add(new OrderItem(((BigInteger) o[0]).longValue(), ((BigInteger) o[1]).longValue(),
                        ((BigInteger) o[2]).longValue(), ((BigInteger) o[3]).longValue(), (Integer) o[4],
                        (Integer) o[5], o[6] == null ? null : ((BigInteger) o[6]).longValue(), (String) o[7], o[8] == null ? null : ((BigInteger) o[8]).longValue(),
                        ((BigInteger) o[9]).longValue(), ((BigInteger) o[10]).longValue(), ((BigInteger) o[11]).longValue()));
            }

            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    connection.prepareStatement("SET enable_seqscan TO ON").execute();
                }
            });

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return orderItemList;
    }

    public List<OrderItem> findAllOrdersPaid(List<Long> idOfOrgsList, Date startDate, Date endDate) {
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    connection.prepareStatement("SET enable_seqscan TO OFF").execute();
                }
            });

            org.hibernate.Query nativeQuery = session.createSQLQuery(
                    "SELECT (o.idoforg) AS name, o.createdDate, ((od.rprice + od.discount) *od.qty)AS sum, od.socDiscount, "
                            + "od.menutype, od.menuOrigin, o.idofclient, g.groupname, o.idofclientgroup, "
                            + "od.rprice*od.qty as sumPay, od.socdiscount*od.qty as sumDiscount, od.discount*od.qty as kazanDiscount "
                            + "FROM CF_Orders o "
                            + "INNER JOIN cf_orderdetails od ON o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg "
                            + "LEFT JOIN cf_clientgroups g ON o.idofclientgroup = g.idofclientgroup AND o.idoforg = g.idoforg "
                            + "WHERE o.idoforg IN (:idOfOrgs) "
                            + "AND o.createdDate >= :startDate AND o.createdDate <= :endDate AND od.rprice > 0 AND (od.menuType >= 50 AND od.menuType <= 99) "
                            + "AND o.state=0 AND od.state=0 ORDER BY o.idoforg");
            nativeQuery.setParameterList("idOfOrgs", idOfOrgsList);
            nativeQuery.setParameter("startDate", startDate.getTime());
            nativeQuery.setParameter("endDate", endDate.getTime());

            List temp = nativeQuery.list();
            for (Object entry : temp) {
                Object o[] = (Object[]) entry;

                orderItemList.add(new OrderItem(((BigInteger) o[0]).longValue(), ((BigInteger) o[1]).longValue(),
                        ((BigInteger) o[2]).longValue(), ((BigInteger) o[3]).longValue(), (Integer) o[4],
                        (Integer) o[5], o[6] == null ? null : ((BigInteger) o[6]).longValue(), (String) o[7],
                        o[8] == null ? null : ((BigInteger) o[8]).longValue(),
                        ((BigInteger) o[9]).longValue(), ((BigInteger) o[10]).longValue(), ((BigInteger) o[11]).longValue()));
            }

            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    connection.prepareStatement("SET enable_seqscan TO ON").execute();
                }
            });

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return orderItemList;
    }

    public List<String> findAllManufacturers(List<Long> idOfOrgsList, Date startDate, Date endDate) {
        List<String> manufacturerList = new ArrayList<String>();

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            org.hibernate.Query nativeQuery = session.createSQLQuery(
                    "SELECT DISTINCT od.manufacturer "
                            + "FROM CF_Orders o "
                            + "INNER JOIN cf_orderdetails od ON o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg "
                            + "WHERE o.idoforg IN (:idOfOrgs) "
                            + "AND o.createdDate >= :startDate AND o.createdDate <= :endDate "
                            + "AND od.manufacturer IS NOT NULL "
                            + "AND o.state=0 AND od.state=0");
            nativeQuery.setParameterList("idOfOrgs", idOfOrgsList);
            nativeQuery.setParameter("startDate", startDate.getTime());
            nativeQuery.setParameter("endDate", endDate.getTime());

            List temp = nativeQuery.list();
            for (Object entry : temp) {
                manufacturerList.add((String) entry);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return manufacturerList;
    }

    public List<OrderItem> findAllOrdersByManufacturer(List<Long> idOfOrgsList, Date startDate, Date endDate) {
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            org.hibernate.Query nativeQuery = session.createSQLQuery(
                    "SELECT o.idoforg, o.createdDate, ((od.rprice + od.discount) *od.qty)AS sum, od.manufacturer "
                            + "FROM CF_Orders o "
                            + "INNER JOIN cf_orderdetails od ON o.idOfOrder = od.idOfOrder AND o.idOfOrg = od.idOfOrg "
                            + "WHERE o.idoforg IN (:idOfOrgs) "
                            + "AND o.createdDate >= :startDate AND o.createdDate <= :endDate "
                            + "AND od.manufacturer IS NOT NULL "
                            + "AND o.state=0 AND od.state=0");
            nativeQuery.setParameterList("idOfOrgs", idOfOrgsList);
            nativeQuery.setParameter("startDate", startDate.getTime());
            nativeQuery.setParameter("endDate", endDate.getTime());

            List temp = nativeQuery.list();
            for (Object entry : temp) {
                Object o[] = (Object[]) entry;

                orderItemList.add(new OrderItem(((BigInteger) o[0]).longValue(), ((BigInteger) o[1]).longValue(),
                        ((BigInteger) o[2]).longValue(), (String) o[3]));
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed export report: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return orderItemList;
    }

    public List<OrderItem> findAllBeneficiaryComplexes(Date startTime, Date endTime){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT distinct on (o.idofclient) o.idoforg,o.ordertype, o.idofclient   FROM cf_orders o "
                        + " LEFT JOIN cf_clients c on o.idofclient =c.idofclient "
                        + " WHERE o.socdiscount > 0 "
                        + " AND o.ordertype IN (4,6,8)"
                        + " AND o.state=0 "
                        + " AND o.createddate BETWEEN  :startTime AND :endTime "
                        + " AND c.discountmode=3 "
                        + " ORDER BY o.idofclient")
                .setParameter("startTime",startTime.getTime())
                .setParameter("endTime",endTime.getTime());
        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            orderItemList.add(new OrderItem(((BigInteger)o[0]).longValue(),(Integer)o[1],0L,((BigInteger)o[2]).longValue()));
        }
        return orderItemList;

    }

    public List<OrderItem> findAllBeneficiaryOrders(Date startTime, Date endTime){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT o.idoforg,o.ordertype, o.idofclient, o.createddate, o.socdiscount  FROM cf_orders o "
                        + " INNER JOIN cf_orderdetails od on o.idoforg = od.idoforg and o.idoforder=od.idoforder "
                        + " WHERE o.socdiscount > 0 "
                        + " AND o.ordertype IN (4,6,8)"
                        + " AND o.state=0 "
                        + " AND o.createddate BETWEEN  :startTime AND :endTime "
                        + " AND od.menutype between 50 and 99 ")
                .setParameter("startTime",startTime.getTime())
                .setParameter("endTime",endTime.getTime());
        List<Object[]> temp = nativeQuery.getResultList();
        for(Object[] o : temp){
            OrderItem orderItem = new OrderItem(((BigInteger) o[0]).longValue(), (Integer) o[1], 0L,
                    ((BigInteger) o[2]).longValue());
            orderItem.setOrderDate(((BigInteger) o[3]).longValue());
            orderItem.setSum(((BigInteger) o[4]).longValue());
            orderItemList.add(orderItem);
        }
        return orderItemList;

    }
}
