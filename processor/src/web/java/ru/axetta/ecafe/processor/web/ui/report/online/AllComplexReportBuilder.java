/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.report.AllComplexReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by i.semenov on 18.10.2017.
 */
public class AllComplexReportBuilder {
    public AllComplexReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
            throws Exception {
        startDate = CalendarUtils.truncateToDayOfMonth(startDate);
        endDate = CalendarUtils.endOfDay(endDate);
        Date generateTime = new Date();
        List<AllComplexReport.ComplexItem> complexItems = new LinkedList<AllComplexReport.ComplexItem>();
        if (!idOfOrgList.isEmpty()) {
            // Обработать лист с организациями
            String orgCondition = "";
            orgCondition = "and (";
            for (Long idOfOrg : idOfOrgList) {
                orgCondition = orgCondition.concat("o.idOfOrg = " + idOfOrg + " or ");
            }
            orgCondition = orgCondition.substring(0, orgCondition.length() - 4) + ") ";

            List resultList = getResultList(session, orgCondition, startDate, endDate, false);
            complexItems = getComplexItems(resultList);

            resultList = getResultList(session, orgCondition, startDate, endDate, true);
            if (resultList != null && resultList.size() > 0) {
                List<AllComplexReport.ComplexItem> complexItems2 = getComplexItems(resultList);
                boolean found = false;
                for (AllComplexReport.ComplexItem item2 : complexItems2) {
                    for (AllComplexReport.ComplexItem item : complexItems) {
                        if (item.equals(item2)) {
                            transformComplexItem(item, item2, false);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        complexItems.add(item2);
                        transformComplexItem(item2, item2, true);
                    }
                }
            }

        } else {
            throw new Exception("Укажите список организаций");
        }
        return new AllComplexReport(generateTime, new Date().getTime() - generateTime.getTime(), complexItems);
    }

    protected void transformComplexItem(AllComplexReport.ComplexItem item, AllComplexReport.ComplexItem item2, boolean clearOld) {
        item.setQtyTemp(item2.getQty());
        item.setSumPriceDiscountTemp(item2.getSumPriceDiscount());
        item.setSumPriceTemp(item2.getSumPrice());
        item.setTotalTemp(item2.getTotal());
        if (clearOld) {
            item.setQty(null);
            item.setSumPriceDiscount("");
            item.setSumPrice("");
            item.setTotal("");
        }
    }

    protected List<AllComplexReport.ComplexItem> getComplexItems(List resultList) {
        List<AllComplexReport.ComplexItem> complexItems = new LinkedList<AllComplexReport.ComplexItem>();
        for (Object result : resultList) {
            Object[] complex = (Object[]) result;
            String officialName = (String) complex[0];
            String menuDetailName = (String) complex[1];
            Long rPrice = ((BigInteger) complex[2]).longValue();
            Long discount = ((BigInteger) complex[3]).longValue();
            Long qty = ((BigInteger) complex[4]).longValue();
            Date firstTimeSale = new Date(((BigInteger) complex[5]).longValue());
            Date lastTimeSale = new Date(((BigInteger) complex[6]).longValue());
            AllComplexReport.ComplexItem complexItem = new AllComplexReport.ComplexItem(officialName, menuDetailName, rPrice, discount, qty,
                    firstTimeSale, lastTimeSale);
            complexItems.add(complexItem);
        }
        return complexItems;
    }

    protected List getResultList(Session session, String orgCondition, Date startDate, Date endDate, boolean tempClients) {
        String preparedQuery = "select org.officialName, od.menuDetailName, od.rPrice, od.discount, "
                + "sum(od.qty) as quantity, min(o.createdDate), max(o.createdDate) "
                + "  from CF_Orders o, CF_OrderDetails od, CF_Orgs org, cf_clients c "
                + " where o.idOfOrder = od.idOfOrder and o.state=0 and od.state=0 "
                + "   and o.idOfOrg = od.idOfOrg and org.idOfOrg = od.idOfOrg "
                + "   and o.idofclient = c.idofclient and c.idoforg" + (tempClients ? " not " : "")
                + " in (select friendlyOrg from cf_friendly_organization where currentorg = o.idoforg) "
                + "   and o.createdDate >= :fromCreatedDate and o.createdDate <= :toCreatedDate"
                + "   and (od.menuType >= :fromMenuType and od.menuType <= :toMenuType) "
                + orgCondition
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
