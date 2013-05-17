/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.PartGroupItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampItem;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.04.13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class OrderDetailsDAOService extends AbstractDAOService {

    //@SuppressWarnings("unchecked")
    //@Deprecated
    //public List<RegisterStampItem> findNotNullGoodsFullNameByOrg(Long idOfOrg, Date start, Date end){
    //    String sql;
    //    sql = "select g.pathPart3 as level1, g.pathPart4 as level2, sum(details.qty) as qty, ord.createTime as date, g.fullName as name" +
    //            " from OrderDetail details left join details.good g left join details.order ord " +
    //            " where g is not null and details.org.idOfOrg=:idOfOrg and ord.createTime between :begin and :end and " +
    //            " details.socDiscount>0 and ord.orderType in :orderTypes" +
    //            " group by ord.createTime, g.fullName, details.qty";
    //    Query query = getSession().createQuery(sql);
    //    query.setParameter("idOfOrg",idOfOrg);
    //    query.setParameter("begin",start);
    //    query.setParameter("end",end);
    //    Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>(3);
    //    orderTypeEnumTypeSet.add(OrderTypeEnumType.DEFAULT);
    //    orderTypeEnumTypeSet.add(OrderTypeEnumType.UNKNOWN);
    //    orderTypeEnumTypeSet.add(OrderTypeEnumType.REDUCED_PRICE_PLAN);
    //    query.setParameterList("orderTypes", orderTypeEnumTypeSet);
    //    query.setResultTransformer(Transformers.aliasToBean(RegisterStampItem.class));
    //    return (List<RegisterStampItem>) query.list();
    //}

    @SuppressWarnings("unchecked")
    public Long findNotNullGoodsFullNameByOrgByDayAndGoodEq(Long idOfOrg, Date start, String fullname) {
        String sql ="select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                "   and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.createddate>=:start and cforder.createddate<:end and orderdetail.socdiscount>0 and" +
                " cforder.idoforg=:idoforg and good.fullname like '"+fullname+"' and " +
                " orderdetail.menutype between :mintype and :maxtype and " +
                " cforder.ordertype in (0,1,4) " +
                " group by orderdetail.qty ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg",idOfOrg);
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("start",start.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 1);
        query.setParameter("end",calendar.getTimeInMillis()-1);
        List list = query.list();
        if(list==null || list.isEmpty()){
            return  0L;
        } else {
            return new Long(list.get(0).toString());
        }
    }

    @SuppressWarnings("unchecked")
    public Long findNotNullGoodsFullNameByOrgByDailySampleAndGoodEq(Long idOfOrg, Date start, Date end, String fullname) {
        String sql ="select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                "   and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.createddate between :start and :end and orderdetail.socdiscount>0 and" +
                //" cforder.idoforg=:idoforg and split_part(good.fullname, '/', 4) like '"+part4+"'" +
                " orderdetail.menutype between :mintype and :maxtype and " +
                " cforder.idoforg=:idoforg and good.fullname like '"+fullname+"' and" +
                " cforder.ordertype in (5) "+
                " group by orderdetail.qty ";
        Query query = getSession().createSQLQuery(sql);
        query.setParameter("idoforg",idOfOrg);
        query.setParameter("start",start.getTime());
        query.setParameter("end",end.getTime());
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        List list = query.list();
        if(list==null || list.isEmpty()){
            return  0L;
        } else {
            return new Long(list.get(0).toString());
        }
    }

    /* получаем список всех  */
    @SuppressWarnings("unchecked")
    public List<GoodItem> findAllGoods(Long idOfOrg){
        Set<OrderTypeEnumType> orderTypeEnumTypeSet = new HashSet<OrderTypeEnumType>(3);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.DEFAULT);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.UNKNOWN);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.REDUCED_PRICE_PLAN);
        orderTypeEnumTypeSet.add(OrderTypeEnumType.DAILY_SAMPLE);
        String sql = "select distinct good.globalId as globalId, good.pathPart3 as pathPart3, "
                + "good.pathPart4 as pathPart4,good.pathPart2 as pathPart2, good.fullName as fullName "
                + " from OrderDetail details "
                + " left join details.good good left join details.order ord left join ord.org o "
                + " where ord.orderType in :orderType and details.good is not null and o.idOfOrg=:idOfOrg and "
                + " details.menuType between :mintype and :maxtype ";
        Query query = getSession().createQuery(sql);
        query.setParameterList("orderType",orderTypeEnumTypeSet);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        query.setResultTransformer(Transformers.aliasToBean(GoodItem.class));
        return  (List<GoodItem>) query.list();
    }

}
