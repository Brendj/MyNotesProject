/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItemAct;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by anvarov on 12.03.2018.
 */
public class AcceptanceOfCompletedWorksActReducePricePlanService extends AbstractDAOService {

    public Set<OrderTypeEnumType> getReducedPricePlanOrderTypes() {
        Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>();
        orderTypeEnumTypeSet.add(OrderTypeEnumType.REDUCED_PRICE_PLAN);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.DAILY_SAMPLE);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.CORRECTION_TYPE);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.WATER_ACCOUNTING);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.DISCOUNT_PLAN_CHANGE);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.RECYCLING_RETIONS);
        return orderTypeEnumTypeSet;
    }

    /* получаем список всех товаров для льготного питания по одной организации */
    @SuppressWarnings("unchecked")
    public List<GoodItemAct> findAllGoodsByTypesByOrg(Long idOfOrg, Date startTime, Date endTime, Set orderTypes) {
        String sql = "select distinct good.globalId as globalId, good.parts as parts, "
                + "     good.fullName as fullName, ord.orderType as orderType "
                + " from OrderDetail details left join details.good good "
                + "     left join details.order ord left join ord.org o where ord.state=0 "
                + "     and details.state=0 and ord.orderType in :orderType "
                + "     and details.good is not null and o.idOfOrg = :idOfOrg "
                + "     and ord.createTime between :startDate and :endDate and details.menuType >= :mintype "
                + "     and details.menuType <=:maxtype order by fullName";
        Query query = getSession().createQuery(sql);
        query.setParameterList("orderType", orderTypes);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", startTime);
        query.setParameter("endDate", endTime);
        query.setResultTransformer(Transformers.aliasToBean(GoodItemAct.class));
        return (List<GoodItemAct>) query.list();
    }

    /* получаем список всех товаров для льготного питания по списку организаций */
    @SuppressWarnings("unchecked")
    public List<GoodItemAct> findAllGoodsByTypesByOrgs(List<Long> idOfOrgList, Date startTime, Date endTime,
            Set orderTypes) {
        String sql = "select distinct good.globalId as globalId, good.parts as parts, "
                + "     good.fullName as fullName, ord.orderType as orderType "
                + " from OrderDetail details left join details.good good "
                + "     left join details.order ord left join ord.org o where ord.state=0 "
                + "     and details.state=0 and ord.orderType in :orderType "
                + "     and details.good is not null and o.idOfOrg in (:idOfOrg) "
                + "     and ord.createTime between :startDate and :endDate and details.menuType >= :mintype "
                + "     and details.menuType <=:maxtype order by fullName";
        Query query = getSession().createQuery(sql);
        query.setParameterList("orderType", orderTypes);
        query.setParameterList("idOfOrg", idOfOrgList);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", startTime);
        query.setParameter("endDate", endTime);
        query.setResultTransformer(Transformers.aliasToBean(GoodItemAct.class));
        return (List<GoodItemAct>) query.list();
    }

    @SuppressWarnings("unchecked")
    public SumQtyAndPriceItem buildRegisterStampBodyValueByOrg(Long idOfOrg, Date start, Date end, String fullname,
            Set<OrderTypeEnumType> orderTypes) {
        // todo привести к единому виду способ отнесения заказа к льготной или платной группе

        Set<Integer> orderType = new HashSet<Integer>();

        for (OrderTypeEnumType orderTypeEnumType : orderTypes) {
            orderType.add(orderTypeEnumType.ordinal());
        }

        String sql =
                "select sum(orderdetail.qty), (((orderdetail.rprice - orderdetail.discount) * orderdetail.qty)*-1) as sumPrice "
                        + " from cf_orders cforder "
                        + "     left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg and orderdetail.idoforder = cforder.idoforder"
                        + "     left join cf_goods good on good.idofgood = orderdetail.idofgood"
                        + " where cforder.state=0 and orderdetail.state=0 "
                        + "     and cforder.createddate>=:startDate and cforder.createddate<=:endDate "
                        + "     and cforder.idoforg=:idoforg "
                        + " and case good.fullname when '' then orderdetail.MenuDetailName else good.fullname end like '"
                        + fullname + "' and orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and " +
                        " (cforder.ordertype in (:orderType) or (cforder.ordertype=8  and orderdetail.qty>=0)) "
                        + " group by orderdetail.qty, (((orderdetail.rprice - orderdetail.discount) * orderdetail.qty)) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", start.getTime());
        query.setParameter("endDate", end.getTime());
        query.setParameterList("orderType", orderType);
        List list = query.list();

        SumQtyAndPriceItem sumQtyAndPriceItem = null;

        if (list == null || list.isEmpty() || list.get(0) == null) {
            return new SumQtyAndPriceItem(0L, 0L);
        } else {
            for (Object o : list) {
                Object[] objList = (Object[]) o;
                sumQtyAndPriceItem = new SumQtyAndPriceItem(((BigInteger) objList[0]).longValue(),
                        ((BigInteger) objList[1]).longValue());
            }
            return sumQtyAndPriceItem;
        }
    }

    @SuppressWarnings("unchecked")
    public SumQtyAndPriceItem buildRegisterStampBodyValueByOrgs(List<Long> idOfOrgList, Date start, Date end,
            String fullname, Set<OrderTypeEnumType> orderTypes) {
        // todo привести к единому виду способ отнесения заказа к льготной или платной группе

        Set<Integer> orderType = new HashSet<Integer>();

        for (OrderTypeEnumType orderTypeEnumType : orderTypes) {
            orderType.add(orderTypeEnumType.ordinal());
        }

        String sql =
                "select sum(orderdetail.qty), (((orderdetail.rprice - orderdetail.discount) * orderdetail.qty)*-1) as sumPrice "
                        + " from cf_orders cforder "
                        + "     left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg and orderdetail.idoforder = cforder.idoforder"
                        + "     left join cf_goods good on good.idofgood = orderdetail.idofgood"
                        + " where cforder.state=0 and orderdetail.state=0 "
                        + "     and cforder.createddate>=:startDate and cforder.createddate<=:endDate "
                        + "     and cforder.idoforg in (:idoforg) "
                        + " and case good.fullname when '' then orderdetail.MenuDetailName else good.fullname end like '"
                        + fullname + "' and orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and "
                        + " (cforder.ordertype in (:orderType) or (cforder.ordertype=8  and orderdetail.qty>=0)) "
                        + " group by orderdetail.qty, (((orderdetail.rprice - orderdetail.discount) * orderdetail.qty)) ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameterList("idoforg", idOfOrgList);
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("startDate", start.getTime());
        query.setParameter("endDate", end.getTime());
        query.setParameterList("orderType", orderType);
        List list = query.list();

        SumQtyAndPriceItem sumQtyAndPriceItem = null;

        if (list == null || list.isEmpty() || list.get(0) == null) {
            return new SumQtyAndPriceItem(0L, 0L);
        } else {
            for (Object o : list) {
                Object[] objList = (Object[]) o;
                sumQtyAndPriceItem = new SumQtyAndPriceItem(((BigInteger) objList[0]).longValue(),
                        ((BigInteger) objList[1]).longValue());
            }
            return sumQtyAndPriceItem;
        }
    }
}
