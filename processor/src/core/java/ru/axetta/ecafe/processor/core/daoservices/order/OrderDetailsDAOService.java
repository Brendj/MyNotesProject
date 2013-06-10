/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.order;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.ClientReportItem;
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

    @SuppressWarnings("unchecked")
    public Long findNotNullGoodsFullNameByOrgByDayAndGoodEq(Long idOfOrg, Date start, String fullname) {
        String sql ="select sum(orderdetail.qty) from cf_orders cforder" +
                " left join cf_orderdetails orderdetail on orderdetail.idoforg = cforder.idoforg " +
                "   and orderdetail.idoforder = cforder.idoforder" +
                " left join cf_goods good on good.idofgood = orderdetail.idofgood" +
                " where cforder.createddate>=:start and cforder.createddate<:end and orderdetail.socdiscount>0 and" +
                " cforder.idoforg=:idoforg and good.fullname like '"+fullname+"' and " +
                " orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and " +
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
                " orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype and " +
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
                + " details.menuType >= :mintype and details.menuType <=:maxtype ";
        Query query = getSession().createQuery(sql);
        query.setParameterList("orderType",orderTypeEnumTypeSet);
        query.setParameter("idOfOrg",idOfOrg);
        query.setParameter("mintype",OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        query.setResultTransformer(Transformers.aliasToBean(GoodItem.class));
        return  (List<GoodItem>) query.list();
    }

    public List<ClientReportItem> fetchClientReportItem(Date startDate, Date endDate){

        String sql = "select client.contractid, "
                + " peroson.firstname || ' ' || peroson.secondname || ' ' || peroson.surname, "
                + " detail.menuorigin, detail.menudetailname, "
                + " (ord.sumbycard - detail.discount) * detail.qty as subsum, ord.sumbycard, "
                + " detail.discount, detail.qty  FROM  cf_orderdetails as detail"
                + " left join cf_orders as ord on ord.idoforg=detail.idoforg AND ord.idoforder = detail.idoforder"
                + " left join cf_clients as client on ord.idofclient = client.idofclient   "
                + " left join cf_persons as peroson on peroson.idofperson = client.idofperson WHERE "
                + " ord.createddate>=:startTime AND ord.createddate<=:endTime AND "
                + " ord.sumbycard>0 and detail.menutype>=:mintype and detail.menutype<=:maxtype "
                + " and (ord.sumbycard - detail.discount) * detail.qty>0";
        Query query = getSession().createSQLQuery(sql);
        //Query query = getSession().createSQLQuery("SELECT cf_orderdetails.idoforderdetail, cf_clients.contractid, cf_persons.firstname || ' ' || cf_persons.secondname || ' ' || cf_persons.surname, "
        //        + " cf_orderdetails.menuorigin, cf_orderdetails.menudetailname, cf_orders.sumbycard, cf_orderdetails.discount, cf_orderdetails.qty "
        //        + " FROM  public.cf_clients, public.cf_persons, public.cf_orders, public.cf_orderdetails "
        //        + " WHERE (cf_orders.createddate>=:startTime AND cf_orders.createddate<=:endTime AND cf_orders.idoforg=cf_orderdetails.idoforg  AND "
        //        + " cf_orders.idoforder = cf_orderdetails.idoforder AND cf_orders.idofclient = cf_clients.idofclient AND cf_persons.idofperson = cf_clients.idofperson "
        //        + "and cf_orders.sumbycard>0 and orderdetail.menutype>=:mintype and orderdetail.menutype<=:maxtype);");
        query.setParameter("startTime", startDate.getTime());
        query.setParameter("endTime", endDate.getTime());
        query.setParameter("mintype", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxtype",OrderDetail.TYPE_COMPLEX_MAX);
        List list = query.list();
        List<ClientReportItem> clientReportItems = new LinkedList<ClientReportItem>();
        for (Object row : list) {
            Object[] sale = (Object[]) row;
            Long idOfOrderDetail = Long.parseLong(sale[0].toString());
            Long contractId = Long.parseLong(sale[1].toString());
            String fullName = sale[2].toString();
            Integer menuOrigin = (Integer) sale[3];
            String menuName = sale[4].toString();
            Float price = Float.parseFloat(sale[5].toString()) / 100;
            Float discount = Float.parseFloat(sale[6].toString()) / 100;
            Integer quantity = (Integer) sale[7];
            Float totalDetailSum = (price - discount) * quantity;
            clientReportItems.add(new ClientReportItem(idOfOrderDetail, contractId, fullName, menuName, OrderDetail.getMenuOriginAsString(menuOrigin), totalDetailSum));
        }
        return clientReportItems;
    }

}
