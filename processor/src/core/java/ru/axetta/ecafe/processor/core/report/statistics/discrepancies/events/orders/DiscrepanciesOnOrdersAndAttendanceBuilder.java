/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.util.*;

import static org.hibernate.criterion.Order.asc;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.02.14
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesOnOrdersAndAttendanceBuilder {

    public DiscrepanciesOnOrdersAndAttendanceReport build(Session session, List<Long> idOfSupplier, List<Long> idOfOrgs,
            Calendar calendar, Date startTime, Date endTime) throws  Exception{

        Criteria catCriteria = session.createCriteria(Org.class)
                .createAlias("categoriesInternal", "cat", JoinType.LEFT_OUTER_JOIN)
                .createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
                //.add(Restrictions.eq("defaultSupplier", contragent))
                .add(Restrictions.in("sm.idOfOrg", idOfSupplier))
                .setProjection(Projections.projectionList().add(Projections.property("idOfOrg"))
                        .add(Projections.property("type")).add(Projections.property("shortName"))
                        .add(Projections.property("address")).add(Projections.property("cat.idOfCategoryOrg"))
                        .add(Projections.property("cat.categoryName")).add(Projections.property("district")))
                .addOrder(asc("idOfOrg"));
        if (!idOfOrgs.isEmpty()) {
            catCriteria.add(Restrictions.in("idOfOrg", idOfOrgs));
        }
        catCriteria.add(Restrictions.ne("type", OrganizationType.SUPPLIER));
        List<Object[]> catRes = (List<Object[]>) catCriteria.list();
        Map<Long, OrgItem> orgItems = new HashMap<Long, OrgItem>();

        for (Object[] row : catRes) {
            Long idOfOrg = (Long) row[0];
            OrganizationType orgType = (OrganizationType) row[1];
            String shortName = StringUtils.defaultString((String) row[2]);
            String address = StringUtils.defaultString((String) row[3]);
            Long idOfCategory = (Long) row[4];
            String categoryName = (String) row[5];
            String district = (String) row[6];
            String category = idOfCategory == null ? (orgType == null ? "" : orgType.toString())
                    : StringUtils.defaultString(categoryName);
            OrgItem orgItem = orgItems.get(idOfOrg);
            if (orgItem == null) {
                orgItems.put(idOfOrg, new OrgItem(idOfOrg, district, shortName, address, category));
            } else {
                orgItem.setOrgTypeCategory(orgItem.getOrgTypeCategory() + ", " + category);
            }
        }

        final ArrayList<Long> orgs = new ArrayList<Long>(orgItems.keySet());

        Criteria goodRequestPosCrit = session.createCriteria(GoodRequestPosition.class);
        goodRequestPosCrit.createCriteria("goodRequest", "gr");
        goodRequestPosCrit.add(Restrictions.in("orgOwner", orgs));
        goodRequestPosCrit.add(Restrictions.ge("gr.doneDate", startTime));
        goodRequestPosCrit.add(Restrictions.lt("gr.doneDate", endTime));
        goodRequestPosCrit.add(Restrictions.eq("gr.state", DocumentState.FOLLOW));
        goodRequestPosCrit.add(Restrictions.eq("deletedState", false));
        goodRequestPosCrit.setProjection(Projections.projectionList()
                .add(Projections.sum("totalCount"))
                .add(Projections.groupProperty("orgOwner"))
                .add(Projections.groupProperty("gr.doneDate"))
        );
        goodRequestPosCrit.addOrder(asc("orgOwner"));


        List<Object[]> goodRes = (List<Object[]>) goodRequestPosCrit.list();

        Map<Long, Map<Date, RequestCountItem>> requestCountMap = new HashMap<Long, Map<Date, RequestCountItem>>();
        for (Object[] row : goodRes) {
            Long idOfOrg = (Long) row[1];
            Long totalCount = row[0] == null ? 0L : ((Long) row[0])/1000;
            Date date = CalendarUtils.truncateToDayOfMonth((Date) row[2]);
            Map<Date, RequestCountItem> dateRequestCountItemMap = requestCountMap.get(idOfOrg);
            if(dateRequestCountItemMap == null){
                HashMap<Date, RequestCountItem> value = new HashMap<Date, RequestCountItem>();
                value.put(date, new RequestCountItem(totalCount, date));
                requestCountMap.put(idOfOrg, value);
            } else {
                RequestCountItem item = dateRequestCountItemMap.get(date);
                if(item==null){
                    //HashMap<Date, RequestCountItem> value = new HashMap<Date, RequestCountItem>();
                    //value.put(date, new RequestCountItem(totalCount, date));
                    //requestCountMap.put(idOfOrg, value);
                    dateRequestCountItemMap.put(date, new RequestCountItem(totalCount, date));
                } else {
                    item.setTotalCount(item.getTotalCount()+totalCount);
                }
            }
        }

        Date orderBeginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        orderBeginDate = CalendarUtils.addDays(orderBeginDate, -21);
        Date orderEndDate = CalendarUtils.truncateToDayOfMonth(endTime);
        orderEndDate = CalendarUtils.addDays(orderEndDate, -21);

        Criteria enterEventForecastCrit = session.createCriteria(EnterEvent.class);
        enterEventForecastCrit.createAlias("org", "o")
                .createAlias("o.sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.in("sm.idOfOrg", idOfSupplier));
        if (!idOfOrgs.isEmpty()) {
            enterEventForecastCrit.add(Restrictions.in("compositeIdOfEnterEvent.idOfOrg", idOfOrgs));
        }
        enterEventForecastCrit.add(Restrictions.ge("evtDateTime", orderBeginDate));
        enterEventForecastCrit.add(Restrictions.lt("evtDateTime", orderEndDate));
        enterEventForecastCrit.setProjection(
                Projections.projectionList().add(Projections.distinct(Projections.count("client")))
                        .add(Projections.groupProperty("compositeIdOfEnterEvent.idOfOrg"))
                //.add(Projections.groupProperty("evtDateTime"))
        );

        enterEventForecastCrit.addOrder(asc("compositeIdOfEnterEvent.idOfOrg"));

        List<Object[]>  enterEventForecastRes = (List<Object[]>) enterEventForecastCrit.list();

        //Map<Long, Map<Date, EnterEventCountItem>> eventForecastCountMap = new HashMap<Long, Map<Date, EnterEventCountItem>>();
        for (Object[] row: enterEventForecastRes){
            Long idOfOrg = (Long) row[1];
            Long totalCount = row[0] == null ? 0L : ((Long) row[0]);
            //Date date = CalendarUtils.truncateToDayOfMonth((Date) row[2]);
            //Map<Date, EnterEventCountItem> dateEventCountItemMap = eventForecastCountMap.get(idOfOrg);


            //if(dateEventCountItemMap == null){
            //    HashMap<Date, EnterEventCountItem> value = new HashMap<Date, EnterEventCountItem>();
            //    value.put(date, new EnterEventCountItem(totalCount, date));
            //    eventForecastCountMap.put(idOfOrg, value);
            //} else {
            //    EnterEventCountItem item = dateEventCountItemMap.get(date);
            //    if(item==null){
            //        //HashMap<Date, EnterEventCountItem> value = new HashMap<Date, EnterEventCountItem>();
            //        //value.put(date, new EnterEventCountItem(totalCount, date));
            //        //eventForecastCountMap.put(idOfOrg, value);
            //        dateEventCountItemMap.put(date, new EnterEventCountItem(totalCount, date));
            //    } else {
            //        item.setTotalCount(item.getTotalCount()+totalCount);
            //    }
            //}
        }

        Map<Long, Map<Date, EnterEventCountItem>> eventForecastCountMap = new HashMap<Long, Map<Date, EnterEventCountItem>>();
        for (Long idOfOrg: eventForecastCountMap.keySet()){
            Map<Date, EnterEventCountItem> dateOrderCountItemMap = eventForecastCountMap.get(idOfOrg);
            Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
            Date endDate = CalendarUtils.truncateToDayOfMonth(endTime);
            orderBeginDate = CalendarUtils.addDays(beginDate, -21);
            orderEndDate = CalendarUtils.addDays(endDate, -21);
            Map<Date, EnterEventCountItem> dateOrderForecastCountItemMap = new HashMap<Date, EnterEventCountItem>();

            while (orderBeginDate.getTime() <= orderEndDate.getTime()) {
                EnterEventCountItem m = dateOrderCountItemMap.get(orderBeginDate);
                long total =0L;
                if(m!=null) total = m.getTotalCount();
                Date forecastBeginDate = CalendarUtils.addDays(orderBeginDate, 21);
                //Date forecastEndDate = CalendarUtils.addDays(orderEndDate, 21);
                while (forecastBeginDate.getTime() <= endDate.getTime() ) {
                    EnterEventCountItem forecastItem = dateOrderForecastCountItemMap.get(forecastBeginDate);
                    if(forecastItem==null){
                        dateOrderForecastCountItemMap.put(forecastBeginDate, new EnterEventCountItem(total, forecastBeginDate));
                    } else {
                        forecastItem.setTotalCountAndCount(forecastItem.getTotalCount() + total);
                    }
                    forecastBeginDate = CalendarUtils.addDays(forecastBeginDate, 1);
                }

                orderBeginDate = CalendarUtils.addDays(orderBeginDate, 1);
            }
            for (Date d: dateOrderForecastCountItemMap.keySet()){
                EnterEventCountItem item = dateOrderForecastCountItemMap.get(d);
                if(item.getCount()!=null && item.getCount()>0) item.setTotalCount(item.getTotalCount()/item.getCount());
            }
            eventForecastCountMap.put(idOfOrg, dateOrderForecastCountItemMap);
        }

        //for (Object[] row: orderRes){
        //    Long idOfOrg = (Long) row[1];
        //    Long totalCount = row[0] == null ? 0L : ((Long) row[0]);
        //    Date date = CalendarUtils.truncateToDayOfMonth((Date) row[2]);
        //    Map<Date, OrderDetailQtyCountItem> dateOrderCountItemMap = eventForecastCountMap.get(idOfOrg);
        //    if(dateOrderCountItemMap == null){
        //        HashMap<Date, OrderDetailQtyCountItem> value = new HashMap<Date, OrderDetailQtyCountItem>();
        //        value.put(date, new OrderDetailQtyCountItem(totalCount, date));
        //        eventForecastCountMap.put(idOfOrg, value);
        //    } else {
        //        OrderDetailQtyCountItem item = dateOrderCountItemMap.get(date);
        //        if(item==null){
        //            HashMap<Date, OrderDetailQtyCountItem> value = new HashMap<Date, OrderDetailQtyCountItem>();
        //            value.put(date, new OrderDetailQtyCountItem(totalCount, date));
        //            eventForecastCountMap.put(idOfOrg, value);
        //        } else {
        //            item.setTotalCount(item.getTotalCount()+totalCount);
        //        }
        //    }
        //}



        Criteria enterEventCrit = session.createCriteria(EnterEvent.class);
        enterEventCrit.createAlias("org", "o")
                .createAlias("o.sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.in("sm.idOfOrg", idOfSupplier));
        if (!idOfOrgs.isEmpty()) {
            enterEventCrit.add(Restrictions.in("compositeIdOfEnterEvent.idOfOrg", idOfOrgs));
        }
        enterEventCrit.add(Restrictions.ge("evtDateTime", startTime));
        enterEventCrit.add(Restrictions.lt("evtDateTime", endTime));
        enterEventCrit.setProjection(Projections.projectionList()
                .add(Projections.distinct(Projections.count("client")))
                .add(Projections.groupProperty("compositeIdOfEnterEvent.idOfOrg"))
                .add(Projections.groupProperty("evtDateTime"))
        );

        enterEventCrit.addOrder(asc("compositeIdOfEnterEvent.idOfOrg"));

        List<Object[]> eventRes = (List<Object[]>) enterEventCrit.list();

        Map<Long, Map<Date, EnterEventCountItem>> eventCountMap = new HashMap<Long, Map<Date, EnterEventCountItem>>();
        for (Object[] row : eventRes) {
            Long idOfOrg = (Long) row[1];
            Long totalCount = row[0] == null ? 0L : ((Long) row[0]);
            Date date = CalendarUtils.truncateToDayOfMonth((Date) row[2]);
            Map<Date, EnterEventCountItem> dateEventCountItemMap = eventCountMap.get(idOfOrg);
            if(dateEventCountItemMap == null){
                HashMap<Date, EnterEventCountItem> value = new HashMap<Date, EnterEventCountItem>();
                value.put(date, new EnterEventCountItem(totalCount, date));
                eventCountMap.put(idOfOrg, value);
            } else {
                EnterEventCountItem item = dateEventCountItemMap.get(date);
                if(item==null){
                    //HashMap<Date, EnterEventCountItem> value = new HashMap<Date, EnterEventCountItem>();
                    //value.put(date, new EnterEventCountItem(totalCount, date));
                    //eventCountMap.put(idOfOrg, value);
                    dateEventCountItemMap.put(date, new EnterEventCountItem(totalCount, date));
                } else {
                    item.setTotalCount(item.getTotalCount()+totalCount);
                }
            }
        }

        List<Item> items = new ArrayList<Item>();
        for (Long id: orgs){
            OrgItem orgItem = orgItems.get(id);
            Map<Date, RequestCountItem> dateRequestCountItemMap = requestCountMap.get(id);
            Map<Date, EnterEventCountItem> dateEventCountItemMap = eventCountMap.get(id);
            //Map<Date, OrderDetailQtyCountItem> orderDetailQtyCountItemMap = orderQtyForecastCountMap.get(id);
            Map<Date, EnterEventCountItem> dateForecastEventCountItemMap = eventForecastCountMap.get(id);
            Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
            Date endDate = CalendarUtils.truncateToDayOfMonth(endTime);
            while (beginDate.getTime() <= endDate.getTime()) {
                final Item e = new Item();
                e.fillOrgInfo(orgItem);
                if(dateRequestCountItemMap!=null){
                    RequestCountItem item = dateRequestCountItemMap.get(beginDate);
                    if(item!=null){
                        e.setRequestCount(item.getTotalCount());
                    } else e.setRequestCount(0L);
                } else e.setRequestCount(0L);
                if(dateEventCountItemMap!=null){
                    EnterEventCountItem item = dateEventCountItemMap.get(beginDate);
                    if(item!=null){
                        e.setEnterEventCount(item.getTotalCount());
                    } else e.setEnterEventCount(0L);
                } else e.setEnterEventCount(0L);

                if(dateForecastEventCountItemMap!=null){
                    EnterEventCountItem item = dateForecastEventCountItemMap.get(beginDate);
                    if(item!=null){
                        //e.setForecastQty(item.getTotalCount()*103/2100);
                        e.setForecastQty((item.getTotalCount()*103)/100);
                    } else e.setForecastQty(0L);
                } else e.setForecastQty(0L);

                e.setCurrentDate(beginDate);
                items.add(e);
                beginDate = CalendarUtils.addDays(beginDate, 1);
            }
        }

        return new DiscrepanciesOnOrdersAndAttendanceReport(items);
    }

}


//Criteria orderDetailCrit = session.createCriteria(OrderDetail.class)
//    .createAlias("org", "o")
//        .createAlias("o.sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
//            .add(Restrictions.in("sm.idOfOrg", idOfSupplier))
//    //.add(Restrictions.in("compositeIdOfOrderDetail.idOfOrg", orgs))
//    .createAlias("order", "ord")
//        .add(Restrictions.eq("ord.orderType", OrderTypeEnumType.PAY_PLAN))
//        .add(Restrictions.between("ord.createTime", orderBeginDate, orderEndDate))
//    .setProjection(Projections.projectionList()
//            .add(Projections.sum("qty"))
//            .add(Projections.groupProperty("compositeIdOfOrderDetail.idOfOrg"))
//            .add(Projections.groupProperty("ord.createTime"))
//    )
//    .addOrder(asc("compositeIdOfOrderDetail.idOfOrg"));
//if (!idOfOrgs.isEmpty()) {
//    orderDetailCrit.add(Restrictions.in("compositeIdOfOrderDetail.idOfOrg", idOfOrgs));
//}
