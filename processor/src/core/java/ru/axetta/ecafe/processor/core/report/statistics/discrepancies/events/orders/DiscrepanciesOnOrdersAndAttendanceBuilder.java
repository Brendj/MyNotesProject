/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.02.14
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class DiscrepanciesOnOrdersAndAttendanceBuilder {

    public DiscrepanciesOnOrdersAndAttendanceReport build(Session session, /*Contragent contragent,*/ List<Long> idOfOrgs,
            Calendar calendar, Date startTime, Date endTime) throws  Exception{

        Criteria catCriteria = session.createCriteria(Org.class)
                .createAlias("categoriesInternal", "cat", JoinType.LEFT_OUTER_JOIN)
                //.add(Restrictions.eq("defaultSupplier", contragent))
                .setProjection(Projections.projectionList()
                        .add(Projections.property("idOfOrg"))
                        .add(Projections.property("type"))
                        .add(Projections.property("shortName"))
                        .add(Projections.property("address"))
                        .add(Projections.property("cat.idOfCategoryOrg"))
                        .add(Projections.property("cat.categoryName"))
                        .add(Projections.property("district"))
                )
                .addOrder(Order.asc("idOfOrg"));
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

        Criteria goodRequestPosCrit = session.createCriteria(GoodRequestPosition.class);
        goodRequestPosCrit.createCriteria("goodRequest", "gr");
        goodRequestPosCrit.add(Restrictions.ge("gr.doneDate", startTime));
        goodRequestPosCrit.add(Restrictions.lt("gr.doneDate", endTime));
        goodRequestPosCrit.add(Restrictions.eq("gr.state", DocumentState.FOLLOW));
        goodRequestPosCrit.add(Restrictions.eq("deletedState", false));
        goodRequestPosCrit.setProjection(Projections.projectionList()
                .add(Projections.sum("totalCount"))
                .add(Projections.groupProperty("orgOwner"))
                .add(Projections.groupProperty("gr.doneDate"))
        );
        goodRequestPosCrit.addOrder(Order.asc("orgOwner"));
        goodRequestPosCrit.add(Restrictions.in("orgOwner", orgItems.keySet()));


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
                    HashMap<Date, RequestCountItem> value = new HashMap<Date, RequestCountItem>();
                    value.put(date, new RequestCountItem(totalCount, date));
                    requestCountMap.put(idOfOrg, value);
                } else {
                    item.setTotalCount(item.getTotalCount()+totalCount);
                }
            }
        }

        Criteria enterEventCrit = session.createCriteria(EnterEvent.class);
        enterEventCrit.add(Restrictions.ge("evtDateTime", startTime));
        enterEventCrit.add(Restrictions.lt("evtDateTime", endTime));
        enterEventCrit.setProjection(Projections.projectionList()
                .add(Projections.count("client"))
                .add(Projections.groupProperty("compositeIdOfEnterEvent.idOfOrg"))
                .add(Projections.groupProperty("evtDateTime"))
        );
        enterEventCrit.add(Restrictions.in("compositeIdOfEnterEvent.idOfOrg", orgItems.keySet()));

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
                    HashMap<Date, EnterEventCountItem> value = new HashMap<Date, EnterEventCountItem>();
                    value.put(date, new EnterEventCountItem(totalCount, date));
                    eventCountMap.put(idOfOrg, value);
                } else {
                    item.setTotalCount(item.getTotalCount()+totalCount);
                }
            }
        }

        List<Item> items = new ArrayList<Item>();
        Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date endDate = CalendarUtils.truncateToDayOfMonth(endTime);
        for (Long id: orgItems.keySet()){
            OrgItem orgItem = orgItems.get(id);
            Map<Date, RequestCountItem> dateRequestCountItemMap = requestCountMap.get(id);
            Map<Date, EnterEventCountItem> dateEventCountItemMap = eventCountMap.get(id);
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
                e.setCurrentDate(beginDate);
                items.add(e);
                beginDate = CalendarUtils.addDays(beginDate, 1);
            }
        }

        return new DiscrepanciesOnOrdersAndAttendanceReport(items);
    }

}
