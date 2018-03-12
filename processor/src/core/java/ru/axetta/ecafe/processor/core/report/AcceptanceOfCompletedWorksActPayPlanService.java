/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem1;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by anvarov on 12.03.2018.
 */
public class AcceptanceOfCompletedWorksActPayPlanService extends AbstractDAOService {

    public Set<OrderTypeEnumType> getPayPlanAndSubscriptionFeedingOrderTypes() {
        Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>();
        orderTypeEnumTypeSet.add(OrderTypeEnumType.PAY_PLAN);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.SUBSCRIPTION_FEEDING);
        return orderTypeEnumTypeSet;
    }

    /* Для акта */
    /* получаем список всех товаров по типу заказа */
    @SuppressWarnings("unchecked")
    public List<GoodItem1> findAllGoodsByOrderTypesByOrg(Long idOfOrg, Date startTime, Date endTime,
            Set<OrderTypeEnumType> orderTypeEnumTypes) {
        Set<Integer> orderTypeEnumTypeSet = new HashSet<Integer>();

        for (OrderTypeEnumType orderTypeEnumType : orderTypeEnumTypes) {
            orderTypeEnumTypeSet.add(orderTypeEnumType.ordinal());
        }

        String sql = "SELECT DISTINCT good1_.IdOfGood AS globalId, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 3) ELSE split_part(good1_.FullName, '/', 3) END AS pathPart3, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 4) ELSE split_part(good1_.FullName, '/', 4) END AS pathPart4, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 2) ELSE split_part(good1_.FullName, '/', 2) END AS pathPart2, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 1) ELSE split_part(good1_.FullName, '/', 1) END AS pathPart1, "
                + "orderdetai0_.MenuDetailName AS fullName, orderdetai0_.rPrice AS price "
                + "FROM CF_OrderDetails orderdetai0_ LEFT OUTER JOIN cf_goods good1_ ON orderdetai0_.IdOfGood=good1_.IdOfGood "
                + "LEFT OUTER JOIN CF_Orders order2_ ON orderdetai0_.IdOfOrg=order2_.IdOfOrg AND orderdetai0_.IdOfOrder=order2_.IdOfOrder "
                + "LEFT OUTER JOIN CF_Orgs org3_ ON order2_.IdOfOrg=org3_.IdOfOrg "
                + "WHERE order2_.State=0 AND orderdetai0_.State=0 AND (order2_.OrderType IN (:orderType)) "
                + "AND (orderdetai0_.IdOfGood IS NOT NULL) AND org3_.IdOfOrg=:idOfOrg "
                + "AND (order2_.CreatedDate BETWEEN :startDate AND :endDate) AND orderdetai0_.MenuType>=:mintype AND orderdetai0_.MenuType<=:maxtype ORDER BY fullName";
        SQLQuery query = getSession().createSQLQuery(sql);
        query.setParameterList("orderType", orderTypeEnumTypeSet);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", startTime.getTime());
        query.setParameter("endDate", endTime.getTime());
        query.setResultTransformer(Transformers.aliasToBean(GoodItem1.class));
        query.addScalar("globalId").addScalar("pathPart3").addScalar("pathPart4").addScalar("pathPart2")
                .addScalar("pathPart1").addScalar("fullName").addScalar("price");
        return (List<GoodItem1>) query.list();
    }

    /* Для акта */
    /* получаем список всех товаров по типу заказа */
    @SuppressWarnings("unchecked")
    public List<GoodItem1> findAllGoodsByOrderTypesByOrgs(List<Long> idOfOrgList, Date startTime, Date endTime,
            Set<OrderTypeEnumType> orderTypeEnumTypes) {
        Set<Integer> orderTypeEnumTypeSet = new HashSet<Integer>();

        for (OrderTypeEnumType orderTypeEnumType : orderTypeEnumTypes) {
            orderTypeEnumTypeSet.add(orderTypeEnumType.ordinal());
        }

        String sql = "SELECT DISTINCT good1_.IdOfGood AS globalId, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 3) ELSE split_part(good1_.FullName, '/', 3) END AS pathPart3, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 4) ELSE split_part(good1_.FullName, '/', 4) END AS pathPart4, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 2) ELSE split_part(good1_.FullName, '/', 2) END AS pathPart2, "
                + "CASE good1_.FullName WHEN '' THEN split_part(orderdetai0_.MenuDetailName, '/', 1) ELSE split_part(good1_.FullName, '/', 1) END AS pathPart1, "
                + "orderdetai0_.MenuDetailName AS fullName, orderdetai0_.rPrice AS price "
                + "FROM CF_OrderDetails orderdetai0_ LEFT OUTER JOIN cf_goods good1_ ON orderdetai0_.IdOfGood=good1_.IdOfGood "
                + "LEFT OUTER JOIN CF_Orders order2_ ON orderdetai0_.IdOfOrg=order2_.IdOfOrg AND orderdetai0_.IdOfOrder=order2_.IdOfOrder "
                + "LEFT OUTER JOIN CF_Orgs org3_ ON order2_.IdOfOrg=org3_.IdOfOrg "
                + "WHERE order2_.State=0 AND orderdetai0_.State=0 AND (order2_.OrderType IN (:orderType)) "
                + "AND (orderdetai0_.IdOfGood IS NOT NULL) AND org3_.IdOfOrg IN (:idOfOrg) "
                + "AND (order2_.CreatedDate BETWEEN :startDate AND :endDate) AND orderdetai0_.MenuType>=:mintype AND orderdetai0_.MenuType<=:maxtype ORDER BY fullName";
        SQLQuery query = getSession().createSQLQuery(sql);
        query.setParameterList("orderType", orderTypeEnumTypeSet);
        query.setParameterList("idOfOrg", idOfOrgList);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", startTime.getTime());
        query.setParameter("endDate", endTime.getTime());
        query.setResultTransformer(Transformers.aliasToBean(GoodItem1.class));
        query.addScalar("globalId").addScalar("pathPart3").addScalar("pathPart4").addScalar("pathPart2")
                .addScalar("pathPart1").addScalar("fullName").addScalar("price");
        return (List<GoodItem1>) query.list();
    }

    /*для акта*/
    @SuppressWarnings("unchecked")
    public Long buildRegisterStampBodyValueByOrderTypesByOrg(Long idOfOrg, Date start, Date end, String fullname,
            Set<OrderTypeEnumType> orderTypeEnumType) {
        Set<Integer> orderType = new HashSet<Integer>();

        for (OrderTypeEnumType orderTypeT : orderTypeEnumType) {
            orderType.add(orderTypeT.ordinal());
        }

        String sql = "select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                " and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.state=0 and orderdetail.state=0 and cforder.createddate>=:startDate and cforder.createddate<=:endDate and " +
                " cforder.idoforg=:idoforg and case good.fullname when '' then orderdetail.MenuDetailName else good.fullname end like '"
                + fullname + "' and " +
                " orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and cforder.ordertype in (:orderType) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", start.getTime());
        query.setParameter("endDate", end.getTime());
        query.setParameterList("orderType", orderType);
        List list = query.list();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0L;
        } else {
            return new Long(list.get(0).toString());
        }
    }

    /*для акта*/
    @SuppressWarnings("unchecked")
    public Long buildRegisterStampBodyValueByOrderTypesByOrgs(List<Long> idOfOrg, Date start, Date end, String fullname,
            Set<OrderTypeEnumType> orderTypeEnumType) {
        Set<Integer> orderType = new HashSet<Integer>();

        for (OrderTypeEnumType orderTypeT : orderTypeEnumType) {
            orderType.add(orderTypeT.ordinal());
        }

        String sql = "select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                " and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.state=0 and orderdetail.state=0 and cforder.createddate>=:startDate and cforder.createddate<=:endDate and"
                +
                " cforder.idoforg in (:idoforg) and case good.fullname when '' then orderdetail.MenuDetailName else good.fullname end like '"
                + fullname + "' and " +
                " orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and cforder.ordertype in (:orderType) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameterList("idoforg", idOfOrg);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", start.getTime());
        query.setParameter("endDate", end.getTime());
        query.setParameterList("orderType", orderType);
        List list = query.list();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0L;
        } else {
            return new Long(list.get(0).toString());
        }
    }
}
