/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.msc.DiscrepanciesOnOrdersAndAttendanceJasperReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

import static org.hibernate.criterion.Order.asc;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.02.14
 * Time: 14:03
 * Онлайн отчеты -> Статистика по расхождениям данных -> Статистика расхождения данных по заказам и оплате
 */
public class DiscrepanciesOnOrdersAndAttendanceBuilder extends BasicReportForAllOrgJob.Builder{

    private final String templateFilename;
    private boolean exportToHTML = false;

    public DiscrepanciesOnOrdersAndAttendanceBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public DiscrepanciesOnOrdersAndAttendanceBuilder() {
        templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + DiscrepanciesOnOrdersAndAttendanceJasperReport.class.getSimpleName() + ".jasper";
        exportToHTML = true;
    }


    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        Date generateTime = new Date();
            /* Строим параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        calendar.setTime(startTime);
        int month = calendar.get(Calendar.MONTH);
        parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        parameterMap.put("month", month + 1);
        parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
        parameterMap.put("year", calendar.get(Calendar.YEAR));
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));


      /*  if (StringUtils.isEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG))) {
            throw new Exception("Не указана организация-поставщик меню.");
        }*/
        String sourceMenuOrgId = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG));
        List<Long> sourceMenuList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(sourceMenuOrgId, ','))) {
            sourceMenuList.add(Long.parseLong(idOfOrg));
        }

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        List<Long> idOfOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }
        Date generateEndTime = new Date();
        DiscrepanciesOnOrdersAndAttendanceReport report = build(session, sourceMenuList, idOfOrgList, calendar, startTime, endTime);
        JRDataSource dataSource = new JRBeanCollectionDataSource(report.getItems());
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        //  Если имя шаблона присутствует, значит строится для джаспера
        if (!exportToHTML) {
            final long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new DiscrepanciesOnOrdersAndAttendanceJasperReport(generateTime, generateDuration, jasperPrint,startTime, endTime);
        } else {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            JRHtmlExporter exporter = new JRHtmlExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
            exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
            exporter.exportReport();
            final long l = generateEndTime.getTime() - generateTime.getTime();
            return new DiscrepanciesOnOrdersAndAttendanceJasperReport(generateTime, l,jasperPrint, startTime, endTime);
        }
    }

    public BasicReportJob build2(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        Date generateTime = new Date();
            /* Строим параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        calendar.setTime(startTime);
        int month = calendar.get(Calendar.MONTH);
        parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        parameterMap.put("month", month + 1);
        parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
        parameterMap.put("year", calendar.get(Calendar.YEAR));
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));


      /*  if (StringUtils.isEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG))) {
            throw new Exception("Не указана организация-поставщик меню.");
        }*/
        String sourceMenuOrgId = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG));
        List<Long> sourceMenuList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(sourceMenuOrgId, ','))) {
            sourceMenuList.add(Long.parseLong(idOfOrg));
        }

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        List<Long> idOfOrgList = new ArrayList<Long>();
        for (String idOfOrg : Arrays.asList(StringUtils.split(idOfOrgs, ','))) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }
        Date generateEndTime = new Date();
        DiscrepanciesOnOrdersAndAttendanceReport report = build(session, sourceMenuList, idOfOrgList, calendar, startTime, endTime);
        JRDataSource dataSource = new JRBeanCollectionDataSource(report.getItemTotals());

        String summaryTemplateFileName = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + DiscrepanciesOnOrdersAndAttendanceJasperReport.class.getSimpleName() + "_summary.jasper";

        JasperPrint jasperPrint = JasperFillManager.fillReport(summaryTemplateFileName, parameterMap, dataSource);
        //  Если имя шаблона присутствует, значит строится для джаспера
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            JRHtmlExporter exporter = new JRHtmlExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
            exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
            exporter.exportReport();
            final long l = generateEndTime.getTime() - generateTime.getTime();
            return new DiscrepanciesOnOrdersAndAttendanceJasperReport(generateTime, l,jasperPrint, startTime, endTime);
    }

    public DiscrepanciesOnOrdersAndAttendanceReport build(Session session, List<Long> idOfSupplier, List<Long> idOfOrgs,
            Calendar calendar, Date startTime, Date endTime) throws  Exception{

        Criteria catCriteria = session.createCriteria(Org.class)
                /*.createAlias("categoriesInternal", "cat", JoinType.LEFT_OUTER_JOIN)*/
                //.createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
                //.add(Restrictions.in("sm.idOfOrg", idOfSupplier))
                .setProjection(Projections.projectionList().add(Projections.property("idOfOrg"))
                        .add(Projections.property("type")).add(Projections.property("shortName"))
                        .add(Projections.property("address"))/*.add(Projections.property("cat.idOfCategoryOrg"))*/
                        /*.add(Projections.property("cat.categoryName"))*/.add(Projections.property("district")))
                .addOrder(asc("idOfOrg"));
        //if (!idOfOrgs.isEmpty()) {
        //    catCriteria.add(Restrictions.in("idOfOrg", idOfOrgs));
        //}
        if (!CollectionUtils.isEmpty(idOfOrgs)) {
            catCriteria.add(Restrictions.in("idOfOrg", idOfOrgs));
        }
        if (!CollectionUtils.isEmpty(idOfSupplier)) {
            catCriteria.createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN);
            catCriteria.add(Restrictions.in("sm.idOfOrg", idOfSupplier));
        }
        catCriteria.add(Restrictions.ne("type", OrganizationType.SUPPLIER));
        List<Object[]> catRes = (List<Object[]>) catCriteria.list();
        Map<Long, OrgItem> orgItems = new HashMap<Long, OrgItem>();

        for (Object[] row : catRes) {
            Long idOfOrg = (Long) row[0];
            OrganizationType orgType = (OrganizationType) row[1];
            String shortName = StringUtils.defaultString((String) row[2]);
            String address = StringUtils.defaultString((String) row[3]);
            /*Long idOfCategory = (Long) row[4];
            String categoryName = (String) row[5];*/
            String district = (String) row[4];
            String category = /*idOfCategory == null ? */(orgType == null ? "" : orgType.toString())
                    /*: StringUtils.defaultString(categoryName)*/;
            OrgItem orgItem = orgItems.get(idOfOrg);
            if (orgItem == null) {
                orgItems.put(idOfOrg, new OrgItem(idOfOrg, district, shortName, address, category));
            } else {
                orgItem.setOrgTypeCategory(orgItem.getOrgTypeCategory() + ", " + category);
            }
        }

        Criteria goodRequestPositionCriteria = session.createCriteria(GoodRequestPosition.class);
        goodRequestPositionCriteria.createAlias("goodRequest", "gr");
        goodRequestPositionCriteria.add(Restrictions.between("gr.doneDate", startTime, endTime));
        goodRequestPositionCriteria.add(Restrictions.in("gr.orgOwner", orgItems.keySet()));
        goodRequestPositionCriteria.add(Restrictions.isNotNull("good"));
        goodRequestPositionCriteria.add(Restrictions.isNotNull("totalCount"));
        goodRequestPositionCriteria.add(Restrictions.eq("deletedState", false));
        goodRequestPositionCriteria.add(Restrictions.eq("gr.deletedState", false));
        goodRequestPositionCriteria.createAlias("good", "g");
        Disjunction fullNameDisjunction = Restrictions.disjunction();
        fullNameDisjunction.add(Restrictions.ilike("g.fullName", "завтрак", MatchMode.ANYWHERE));
        fullNameDisjunction.add(Restrictions.ilike("g.fullName", "обед", MatchMode.ANYWHERE));
        fullNameDisjunction.add(Restrictions.ilike("g.fullName", "полдник", MatchMode.ANYWHERE));
        goodRequestPositionCriteria.add(fullNameDisjunction);
        goodRequestPositionCriteria.add(
                Restrictions.not(Restrictions.ilike("g.fullName", "сотрудник", MatchMode.ANYWHERE))
        );
        List goodRequestPositionList = goodRequestPositionCriteria.list();
        Date  beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        Date  endDate = CalendarUtils.truncateToDayOfMonth(endTime);
        List<GoodRequestItem> goodRequestItems = new ArrayList<GoodRequestItem>();
        //Map<GoodRequestItem, GoodRequestItem> goodRequestItemHashMap = new HashMap<GoodRequestItem, GoodRequestItem>();
        for (Object obj: goodRequestPositionList){
            GoodRequestPosition position = (GoodRequestPosition) obj;
            Date doneDate = CalendarUtils.truncateToDayOfMonth(position.getGoodRequest().getDoneDate());
            long idoforg = position.getOrgOwner();
            long totalCount = (position.getTotalCount()==null?0L:position.getTotalCount());
            long dailySampleCount = (position.getDailySampleCount()==null?0L:position.getDailySampleCount());
            long count = (totalCount-dailySampleCount)/1000L;
            String fullname = position.getGood().getFullName();
            String part3 = position.getGood().getPathPart3();
            //GoodRequestItem item = goodRequestItemHashMap.get(new GoodRequestItem(idoforg, doneDate, fullname));
            GoodRequestItem requestItem = new GoodRequestItem(idoforg, doneDate, part3);
            int index = goodRequestItems.indexOf(requestItem);
            if(index>=0){
                requestItem = goodRequestItems.get(index);
                requestItem.addGoodCount(fullname, count);
                goodRequestItems.set(index, requestItem);
            } else {
                requestItem.addGoodCount(fullname, count);
                goodRequestItems.add(requestItem);
            }
            //OrgRequestCountItem item = orgRequestCountItemMap.get(new OrgRequestCountItem(position.getOrgOwner(), doneDate));
            //String pathPart4 = position.getGood().getPathPart4();
            //item.addCount(position.getTotalCount()/1000L, pathPart4);

        }


        Map<OrgRequestCountItem, OrgRequestCountItem> orgRequestCountItemMap = new HashMap<OrgRequestCountItem, OrgRequestCountItem>();
        for (long id: orgItems.keySet()){
            while (beginDate.getTime() <= endDate.getTime()) {
                OrgRequestCountItem item = new OrgRequestCountItem(id, beginDate);
                orgRequestCountItemMap.put(item, item);
                beginDate = CalendarUtils.addDays(beginDate, 1);
            }
            beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
        }

        for (GoodRequestItem position: goodRequestItems){
            //GoodRequestPosition position = (GoodRequestPosition) obj;
            OrgRequestCountItem item = orgRequestCountItemMap.get(new OrgRequestCountItem(position.getIdOfOrg(), position.getDoneDate()));
            item.setTotalCount(item.getTotalCount()+position.getMaxValue());
            //String pathPart4 = position.getGood().getPathPart4();
            //item.addCount(position.getTotalCount()/1000L, pathPart4);
        }

        //for (Object obj: g+oodRequestPositionList){
        //    GoodRequestPosition position = (GoodRequestPosition) obj;
        //    Date doneDate = CalendarUtils.truncateToDayOfMonth(position.getGoodRequest().getDoneDate());
        //    OrgRequestCountItem item = orgRequestCountItemMap.get(new OrgRequestCountItem(position.getOrgOwner(), doneDate));
        //    //String pathPart4 = position.getGood().getPathPart4();
        //    //item.addCount(position.getTotalCount()/1000L, pathPart4);
        //}


        String str;
        if (!CollectionUtils.isEmpty(idOfSupplier)) {
           str = " and cf_menuexchangerules.idofsourceorg in (:idOfSupplier)) AS order_data GROUP BY order_data.d, order_data.org";
        } else {
            str = ") AS order_data GROUP BY order_data.d, order_data.org";
        }

        final ArrayList<Long> orgs = new ArrayList<Long>(orgItems.keySet());
        String sql = "SELECT count(distinct client), order_data.d, order_data.org "
                + "FROM ("
                + " SELECT cf_orders.idofclient AS client, cf_orders.createddate AS d, "
                + "  cf_orders.idoforg AS org "
                + " FROM cf_orders "
                + " join cf_clients on cf_clients.idofclient=cf_orders.idofclient "
                + " join cf_menuexchangerules on cf_menuexchangerules.idofdestorg=cf_orders.idoforg "
                + " WHERE cf_orders.createddate >= :startDate AND cf_orders.createddate <= :endDate and "
                + " cf_clients.idofclientgroup<1100000000 and "  /* берем только детей */
                //+ " cf_clients.discountmode = 3 and "  /* берем только льготников */
                + " cf_orders.ordertype in (4, 6, 11) and cf_orders.state=0 and "/* смотрим плану льготного питания */
                + " cf_orders.state = 0" + str; /* Учитываем только пробитые заказы */
        Query query = session.createSQLQuery(sql);
        query.setLong("startDate", startTime.getTime());
        query.setLong("endDate", endTime.getTime());
        if (!CollectionUtils.isEmpty(idOfSupplier)) {
            query.setParameterList("idOfSupplier", idOfSupplier);
        }
        List<Object[]> res = query.list();

        Map<Long, Map<Date, OrderCountItem>> orderCountMap = new HashMap<Long, Map<Date, OrderCountItem>>();
        for (Object[] row : res) {
            Long idOfOrg = ((BigInteger) row[2]).longValue();
            Long totalCount = ((BigInteger) row[0]).longValue();
            long d = ((BigInteger) row[1]).longValue();
            calendar.setTimeInMillis(d);
            CalendarUtils.truncateToDayOfMonth(calendar);
            Date date = calendar.getTime();
            Map<Date, OrderCountItem> dateOrderCountItemMap = orderCountMap.get(idOfOrg);
            if(dateOrderCountItemMap == null){
                HashMap<Date, OrderCountItem> value = new HashMap<Date, OrderCountItem>();
                value.put(date, new OrderCountItem(totalCount, date));
                orderCountMap.put(idOfOrg, value);
            } else {
                OrderCountItem item = dateOrderCountItemMap.get(date);
                if(item==null){
                    dateOrderCountItemMap.put(date, new OrderCountItem(totalCount, date));
                } else {
                    item.setTotalCount(item.getTotalCount()+totalCount);
                }
            }
        }


        sql = "SELECT count(distinct client), order_data.d, order_data.org "
                + "FROM ("
                + " SELECT cf_orders.idofclient AS client, cf_orders.createddate AS d, "
                + "  cf_orders.idoforg AS org "
                + " FROM cf_orders "
                + " join cf_clients on cf_clients.idofclient=cf_orders.idofclient "
                //+ " join cf_orgs on cf_orgs.idoforg=cf_orders.idoforg "
                + " join cf_menuexchangerules on cf_menuexchangerules.idofdestorg=cf_orders.idoforg "
                + " WHERE cf_orders.createddate >= :startDate AND cf_orders.createddate <= :endDate and "
                + " cf_clients.idofclientgroup<1100000010 and "  /* берем только детей + админов */
                //+ " cf_orders.ordertype = 6 and "/* План льготного питания, резерв */
                + " cf_orders.ordertype = :orderType and "/* Утилизация */
                + " cf_orders.state = 0" + str;/* Учитываем только пробитые заказы */

        query = session.createSQLQuery(sql);
        query.setLong("startDate", startTime.getTime());
        query.setLong("endDate", endTime.getTime());
        query.setInteger("orderType", OrderTypeEnumType.RECYCLING_RETIONS.ordinal());
        if (!CollectionUtils.isEmpty(idOfSupplier)) {
            query.setParameterList("idOfSupplier", idOfSupplier);
        }
        res = query.list();

        Map<Long, Map<Date, OrderCountItem>> orderReserveCountMap = new HashMap<Long, Map<Date, OrderCountItem>>();
        for (Object[] row : res) {
            Long idOfOrg = ((BigInteger) row[2]).longValue();
            Long totalCount = ((BigInteger) row[0]).longValue();
            long d = ((BigInteger) row[1]).longValue();
            calendar.setTimeInMillis(d);
            CalendarUtils.truncateToDayOfMonth(calendar);
            Date date = calendar.getTime();
            Map<Date, OrderCountItem> dateOrderCountItemMap = orderReserveCountMap.get(idOfOrg);
            if(dateOrderCountItemMap == null){
                HashMap<Date, OrderCountItem> value = new HashMap<Date, OrderCountItem>();
                value.put(date, new OrderCountItem(totalCount, date));
                orderReserveCountMap.put(idOfOrg, value);
            } else {
                OrderCountItem item = dateOrderCountItemMap.get(date);
                if(item==null){
                    dateOrderCountItemMap.put(date, new OrderCountItem(totalCount, date));
                } else {
                    item.setTotalCount(item.getTotalCount()+totalCount);
                }
            }
        }

        List<Item> items = new ArrayList<Item>();
        for (Long id: orgs){
            OrgItem orgItem = orgItems.get(id);
            Map<Date, OrderCountItem> dateOrderCountItemMap = orderCountMap.get(id);
            Map<Date, OrderCountItem> dateOrderReserveCountItemMap = orderReserveCountMap.get(id);
            beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
            endDate = CalendarUtils.truncateToDayOfMonth(endTime);
            while (beginDate.getTime() <= endDate.getTime()) {
                final Item e = new Item();
                e.fillOrgInfo(orgItem);
                OrgRequestCountItem orgRequestCountItem =orgRequestCountItemMap.get(new OrgRequestCountItem(id, beginDate));
                if(orgRequestCountItem==null /*|| orgRequestCountItem.isEmptyComplex()*/){
                    e.setRequestCount(0L);
                } else {
                    //e.setRequestCount(orgRequestCountItem.getRequestCount());
                    e.setRequestCount(orgRequestCountItem.getTotalCount());
                }
                if(dateOrderCountItemMap!=null){
                    OrderCountItem item = dateOrderCountItemMap.get(beginDate);
                    if(item!=null){
                        e.setOrderCount(item.getTotalCount());
                    } else e.setOrderCount(0L);
                } else e.setOrderCount(0L);

                if(dateOrderCountItemMap!=null){
                    if(dateOrderReserveCountItemMap==null || dateOrderReserveCountItemMap.isEmpty()){
                        e.setOrderReserveCount(0L);
                    } else {
                        OrderCountItem item = dateOrderReserveCountItemMap.get(beginDate);
                        if(item!=null){
                            e.setOrderReserveCount(item.getTotalCount());
                        } else e.setOrderReserveCount(0L);
                    }
                } else e.setOrderReserveCount(0L);

                //if(dateForecastEventCountItemMap!=null){
                //    OrderCountItem item = dateForecastEventCountItemMap.get(beginDate);
                //    if(item!=null){
                //        //e.setForecastQty(item.getTotalCount()*103/2100);
                //        e.setForecastQty((item.getTotalCount()*103)/100);
                //    } else e.setForecastQty(0L);
                //} else e.setForecastQty(0L);

                e.setCurrentDate(beginDate);
                items.add(e);
                beginDate = CalendarUtils.addDays(beginDate, 1);
            }
        }

        List<ItemTotal> itemTotals = new ArrayList<ItemTotal>();
        List<Long> orgIds = new ArrayList<Long>();
        Iterator iterator = items.iterator();
        Item itm = items.get(0);
        orgIds.add(itm.getIdOfOrg());

        Long totalRequestCount = 0L;
        Long totalForecastQty = 0L;
        Long totalOrderCount = 0L;
        Long totalOrderReserveCount = 0L;

        Map<Long, ItemTotal> itemTotalMap = new HashMap<Long, ItemTotal>();

        while (iterator.hasNext()) {
            Item itemTo = (Item) iterator.next();
            if (itm.getIdOfOrg().equals(itemTo.getIdOfOrg())) {
                totalRequestCount += itemTo.getRequestCount();
                if (itemTo.getForecastQty() != null) {
                    totalForecastQty += itemTo.getForecastQty();
                }
                totalOrderCount += itemTo.getOrderCount();
                totalOrderReserveCount += itemTo.getOrderReserveCount();
            } else {
                ItemTotal itemTotal = new ItemTotal(itm.getDistrict(), itm.getOrgTypeCategory(), itm.getShortName(),
                        itm.getNumber(), itm.getAddress(), totalRequestCount, totalForecastQty, totalOrderCount,
                        totalOrderReserveCount, itm.getIdOfOrg());

                itemTotalMap.put(itm.getIdOfOrg(), itemTotal);

                totalRequestCount = 0L;
                totalForecastQty = 0L;
                totalOrderCount = 0L;
                totalOrderReserveCount = 0L;

                itm = itemTo;
                orgIds.add(itemTo.getIdOfOrg());
                itemTotals.add(itemTotal);
            }
        }

        totalRequestCount = 0L;
        totalForecastQty = 0L;
        totalOrderCount = 0L;
        totalOrderReserveCount = 0L;

        Iterator itr = items.iterator();
        int n = orgIds.size();
        Item itemOld = new Item();
        while (itr.hasNext()) {
            Item itemTo = (Item) itr.next();
            if (orgIds.get(n - 1).equals(itemTo.getIdOfOrg())) {
                totalRequestCount += itemTo.getRequestCount();
                if (itemTo.getForecastQty() != null) {
                    totalForecastQty += itemTo.getForecastQty();
                }
                totalOrderCount += itemTo.getOrderCount();
                totalOrderReserveCount += itemTo.getOrderReserveCount();
                itemOld = itemTo;
            }
        }

        ItemTotal itemTotal = new ItemTotal(itemOld.getDistrict(), itemOld.getOrgTypeCategory(), itemOld.getShortName(),
                itemOld.getNumber(), itemOld.getAddress(), totalRequestCount, totalForecastQty, totalOrderCount,
                totalOrderReserveCount, itemOld.getIdOfOrg());

        itemTotalMap.put(itemOld.getIdOfOrg(), itemTotal);
        itemTotals.add(itemTotal);

        return new DiscrepanciesOnOrdersAndAttendanceReport(items, itemTotals);
    }

    protected static class GoodRequestItem{
        private long idOfOrg;
        private Date doneDate;
        private String pathPart3;
        private Map<String, Long> nameCountMap = new HashMap<String, Long>();

        public GoodRequestItem(long idOfOrg, Date doneDate, String pathPart3) {
            this.idOfOrg = idOfOrg;
            this.doneDate = doneDate;
            this.pathPart3 = pathPart3;
        }

        public void addGoodCount(String fullName, Long count){
            Long totalCount = nameCountMap.get(fullName);
            if(totalCount!=null){
                totalCount+=count;
            } else {
                nameCountMap.put(fullName, count);
            }
        }

        public long getMaxValue(){
            long max = 0L;
            for (long count: nameCountMap.values()){
                max = Math.max(max, count);
            }
            return max;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Date getDoneDate() {
            return doneDate;
        }

        public void setDoneDate(Date doneDate) {
            this.doneDate = doneDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GoodRequestItem item = (GoodRequestItem) o;

            if (idOfOrg != item.idOfOrg) {
                return false;
            }
            if (!doneDate.equals(item.doneDate)) {
                return false;
            }
            if (!pathPart3.equals(item.pathPart3)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (idOfOrg ^ (idOfOrg >>> 32));
            result = 31 * result + doneDate.hashCode();
            result = 31 * result + pathPart3.hashCode();
            return result;
        }
    }

    protected static class OrgGoodRequestCountItem{
        private long idOfOrg;
        private Date doneDate;
        private String pathPart3;

        public OrgGoodRequestCountItem(long idOfOrg, Date doneDate) {
            this.idOfOrg = idOfOrg;
            this.doneDate = doneDate;
        }

        public OrgGoodRequestCountItem(long idOfOrg, Date doneDate, String pathPart3) {
            this.idOfOrg = idOfOrg;
            this.doneDate = doneDate;
            this.pathPart3 = pathPart3;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Date getDoneDate() {
            return doneDate;
        }

        public void setDoneDate(Date doneDate) {
            this.doneDate = doneDate;
        }

        public String getPathPart3() {
            return pathPart3;
        }

        public void setPathPart3(String pathPart3) {
            this.pathPart3 = pathPart3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            OrgGoodRequestCountItem that = (OrgGoodRequestCountItem) o;

            if (idOfOrg != that.idOfOrg) {
                return false;
            }
            if (!doneDate.equals(that.doneDate)) {
                return false;
            }
            if (!pathPart3.equals(that.pathPart3)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (idOfOrg ^ (idOfOrg >>> 32));
            result = 31 * result + doneDate.hashCode();
            result = 31 * result + pathPart3.hashCode();
            return result;
        }
    }


    protected static class OrgRequestCountItem {
        private long idOfOrg;
        private Date doneDate;
        private long totalCount = 0L;
        private TreeSet<String> pathPart4 = new TreeSet<String>();;

        public OrgRequestCountItem(long idOfOrg, Date doneDate) {
            this.idOfOrg = idOfOrg;
            this.doneDate = doneDate;
        }

        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        public void addCount(long count, String pathPart4){
            totalCount+=count;
            this.pathPart4.add(pathPart4);
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public Date getDoneDate() {
            return doneDate;
        }

        public long getTotalCount() {
            return totalCount;
        }

        long getRequestCount(){
            return getTotalCount()/complexCount();
        }

        public boolean isEmptyComplex(){
            return pathPart4.isEmpty();
        }



        public int complexCount(){
            return pathPart4.size();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            OrgRequestCountItem item = (OrgRequestCountItem) o;

            if (idOfOrg != item.idOfOrg) {
                return false;
            }
            if (!doneDate.equals(item.doneDate)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (idOfOrg ^ (idOfOrg >>> 32));
            result = 31 * result + doneDate.hashCode();
            return result;
        }
    }

}


//Date orderBeginDate = CalendarUtils.truncateToDayOfMonth(startTime);
//orderBeginDate = CalendarUtils.addDays(orderBeginDate, -21);
//Date orderEndDate = CalendarUtils.truncateToDayOfMonth(endTime);
//orderEndDate = CalendarUtils.addDays(orderEndDate, -21);
//
//Criteria enterEventForecastCrit = session.createCriteria(EnterEvent.class);
//enterEventForecastCrit.createAlias("org", "o")
//        .createAlias("o.sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
//        .add(Restrictions.in("sm.idOfOrg", idOfSupplier));
//if (!idOfOrgs.isEmpty()) {
//    enterEventForecastCrit.add(Restrictions.in("compositeIdOfEnterEvent.idOfOrg", idOfOrgs));
//}
//enterEventForecastCrit.add(Restrictions.ge("evtDateTime", orderBeginDate));
//enterEventForecastCrit.add(Restrictions.lt("evtDateTime", orderEndDate));
//enterEventForecastCrit.setProjection(
//        Projections.projectionList().add(Projections.distinct(Projections.count("client")))
//                .add(Projections.groupProperty("compositeIdOfEnterEvent.idOfOrg"))
//        //.add(Projections.groupProperty("evtDateTime"))
//);
//
//enterEventForecastCrit.addOrder(asc("compositeIdOfEnterEvent.idOfOrg"));
//
//List<Object[]>  enterEventForecastRes = (List<Object[]>) enterEventForecastCrit.list();

//Map<Long, Map<Date, OrderCountItem>> eventForecastCountMap = new HashMap<Long, Map<Date, OrderCountItem>>();
//for (Object[] row: enterEventForecastRes){
//    Long idOfOrg = (Long) row[1];
//    Long totalCount = row[0] == null ? 0L : ((Long) row[0]);
//Date date = CalendarUtils.truncateToDayOfMonth((Date) row[2]);
//Map<Date, OrderCountItem> dateEventCountItemMap = eventForecastCountMap.get(idOfOrg);


//if(dateEventCountItemMap == null){
//    HashMap<Date, OrderCountItem> value = new HashMap<Date, OrderCountItem>();
//    value.put(date, new OrderCountItem(totalCount, date));
//    eventForecastCountMap.put(idOfOrg, value);
//} else {
//    OrderCountItem item = dateEventCountItemMap.get(date);
//    if(item==null){
//        //HashMap<Date, OrderCountItem> value = new HashMap<Date, OrderCountItem>();
//        //value.put(date, new OrderCountItem(totalCount, date));
//        //eventForecastCountMap.put(idOfOrg, value);
//        dateEventCountItemMap.put(date, new OrderCountItem(totalCount, date));
//    } else {
//        item.setTotalCount(item.getTotalCount()+totalCount);
//    }
//}
//}

//Map<Long, Map<Date, OrderCountItem>> eventForecastCountMap = new HashMap<Long, Map<Date, OrderCountItem>>();
//for (Long idOfOrg: eventForecastCountMap.keySet()){
//    Map<Date, OrderCountItem> dateOrderCountItemMap = eventForecastCountMap.get(idOfOrg);
//    Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
//    Date endDate = CalendarUtils.truncateToDayOfMonth(endTime);
//    orderBeginDate = CalendarUtils.addDays(beginDate, -21);
//    orderEndDate = CalendarUtils.addDays(endDate, -21);
//    Map<Date, OrderCountItem> dateOrderForecastCountItemMap = new HashMap<Date, OrderCountItem>();
//
//    while (orderBeginDate.getTime() <= orderEndDate.getTime()) {
//        OrderCountItem m = dateOrderCountItemMap.get(orderBeginDate);
//        long total =0L;
//        if(m!=null) total = m.getTotalCount();
//        Date forecastBeginDate = CalendarUtils.addDays(orderBeginDate, 21);
//        //Date forecastEndDate = CalendarUtils.addDays(orderEndDate, 21);
//        while (forecastBeginDate.getTime() <= endDate.getTime() ) {
//            OrderCountItem forecastItem = dateOrderForecastCountItemMap.get(forecastBeginDate);
//            if(forecastItem==null){
//                dateOrderForecastCountItemMap.put(forecastBeginDate, new OrderCountItem(total, forecastBeginDate));
//            } else {
//                forecastItem.setTotalCountAndCount(forecastItem.getTotalCount() + total);
//            }
//            forecastBeginDate = CalendarUtils.addDays(forecastBeginDate, 1);
//        }
//
//        orderBeginDate = CalendarUtils.addDays(orderBeginDate, 1);
//    }
//    for (Date d: dateOrderForecastCountItemMap.keySet()){
//        OrderCountItem item = dateOrderForecastCountItemMap.get(d);
//        if(item.getCount()!=null && item.getCount()>0) item.setTotalCount(item.getTotalCount()/item.getCount());
//    }
//    eventForecastCountMap.put(idOfOrg, dateOrderForecastCountItemMap);
//}

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


//String sql =
//        "select sms_data.org, substring(sms_data.org from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
//                + "     EXTRACT(EPOCH FROM sms_data.d) * 1000, count(sms) "
//                + "from ("
//                + "select IdOfSms as sms, date_trunc('day', to_timestamp(servicesenddate/1000)) as d, cf_orgs.shortname as org "
//                + "from CF_ClientSms "
//                + "join cf_clients on CF_ClientSms.IdOfClient=cf_clients.idofclient "
//                + "join cf_orgs on cf_clients.idoforg=cf_orgs.idoforg "
//                + "where servicesenddate >= :startDate and "
//                + "      servicesenddate <= :endDate and "
//                + "      DeliveryStatus in (:sentStatus, :sendStatus, :deliveredStatus) " + orgRestrict
//                + ") as sms_data "
//                + "group by sms_data.d, sms_data.org order by 1";

//Query query = session.createSQLQuery();



//Criteria enterEventCrit = session.createCriteria(EnterEvent.class);
//enterEventCrit.createAlias("org", "o")
//        .createAlias("o.sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
//        .add(Restrictions.in("sm.idOfOrg", idOfSupplier));
//if (!idOfOrgs.isEmpty()) {
//    enterEventCrit.add(Restrictions.in("compositeIdOfEnterEvent.idOfOrg", idOfOrgs));
//}
//enterEventCrit.add(Restrictions.ge("evtDateTime", startTime));
//enterEventCrit.add(Restrictions.lt("evtDateTime", endTime));
//enterEventCrit.setProjection(Projections.projectionList()
//        .add(Projections.distinct(Projections.count("client")))
//        .add(Projections.groupProperty("compositeIdOfEnterEvent.idOfOrg"))
//        .add(Projections.groupProperty("evtDateTime"))
//);
//
//enterEventCrit.addOrder(asc("compositeIdOfEnterEvent.idOfOrg"));

//List<Object[]> eventRes = (List<Object[]>) enterEventCrit.list();

//Criteria goodRequestPosCrit = session.createCriteria(GoodRequestPosition.class);
//goodRequestPosCrit.createCriteria("goodRequest", "gr");
//goodRequestPosCrit.add(Restrictions.in("orgOwner", orgs));
//goodRequestPosCrit.add(Restrictions.ge("gr.doneDate", startTime));
//goodRequestPosCrit.add(Restrictions.lt("gr.doneDate", endTime));
//goodRequestPosCrit.add(Restrictions.eq("gr.state", DocumentState.FOLLOW));
//goodRequestPosCrit.add(Restrictions.eq("deletedState", false));
//goodRequestPosCrit.setProjection(Projections.projectionList()
//        .add(Projections.sum("totalCount"))
//        .add(Projections.groupProperty("orgOwner"))
//        .add(Projections.groupProperty("gr.doneDate"))
//);
//goodRequestPosCrit.addOrder(asc("orgOwner"));
//
//
//List<Object[]> goodRes = (List<Object[]>) goodRequestPosCrit.list();


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


//String orgCriteria ="";
//if (idOfOrgs!=null && !idOfOrgs.isEmpty() && idOfOrgs.get(0)!=null) {
//    orgCriteria =" cf_goods_requests.orgowner IN (:idOfOrgs) ";
//} else {
//    orgCriteria =" cf_goods_requests.orgowner IN (SELECT idofdestorg FROM cf_menuexchangerules WHERE cf_menuexchangerules.idofsourceorg IN (:idOfSupplier)) ";
//}
//String goodSQL = "SELECT resultdata.dd, resultdata.orgo AS org, int8(sum(resultdata.tc)*2/sum(resultdata.cg)) FROM ( "
//        + " SELECT ddate AS dd, sum(tcount) AS tc, orgo, count(good) AS cg, sum(tcount)/count(good), int8(sum(tcount)/count(good)) FROM "
//        + " ( SELECT int8(sum(totalcount) / 1000) AS tcount, cf_goods_requests.DoneDate AS ddate, cf_goods.fullname AS good, "
//        + "  cf_goods_requests.orgowner orgo FROM cf_goods_requests_positions "
//        + "  LEFT JOIN cf_goods_requests ON cf_goods_requests_positions.IdOfGoodsRequest=cf_goods_requests.IdOfGoodsRequest "
//        + "  LEFT JOIN cf_goods ON cf_goods.idofgood=cf_goods_requests_positions.idofgood WHERE "
//        + orgCriteria
//        + "  AND cf_goods_requests.DoneDate>=:startDate AND cf_goods_requests.DoneDate<=:endDate "
//        + "  AND (cf_goods.fullname ILIKE '%завтрак%' OR cf_goods.fullname ILIKE '%обед%' OR cf_goods.fullname ILIKE '%полдник%') "
//        + "  AND NOT(cf_goods.fullname ILIKE '%сотрудник%') AND cf_goods.orgowner in (:idOfSupplier) "
//        + "  GROUP BY cf_goods_requests.orgowner, cf_goods_requests.DoneDate, cf_goods.fullname "
//        + " ) AS rdata GROUP BY rdata.ddate, rdata.orgo) AS resultdata "
//        + " GROUP BY resultdata.dd, resultdata.orgo ORDER BY resultdata.dd ";
//Query goodQuery = session.createSQLQuery(goodSQL);
//goodQuery.setLong("startDate", startTime.getTime());
//goodQuery.setLong("endDate", endTime.getTime());
//if (idOfOrgs!=null && !idOfOrgs.isEmpty() && idOfOrgs.get(0)!=null) {
//    goodQuery.setParameterList("idOfOrgs", idOfOrgs);
//}
//goodQuery.setParameterList("idOfSupplier", idOfSupplier);
//List<Object[]> goodRes = (List<Object[]>) goodQuery.list();
//
//Map<Long, Map<Date, RequestCountItem>> requestCountMap = new HashMap<Long, Map<Date, RequestCountItem>>();
//for (Object[] row : goodRes) {
//    Long idOfOrg = ((BigInteger) row[1]).longValue();
//    Long totalCount = ((BigInteger) row[2]).longValue();
//    Date date  = CalendarUtils.truncateToDayOfMonth(new Date(((BigInteger) row[0]).longValue()));
//    Map<Date, RequestCountItem> dateRequestCountItemMap = requestCountMap.get(idOfOrg);
//    if(dateRequestCountItemMap == null){
//        HashMap<Date, RequestCountItem> value = new HashMap<Date, RequestCountItem>();
//        value.put(date, new RequestCountItem(totalCount, date));
//        requestCountMap.put(idOfOrg, value);
//    } else {
//        RequestCountItem item = dateRequestCountItemMap.get(date);
//        if(item==null){
//            dateRequestCountItemMap.put(date, new RequestCountItem(totalCount, date));
//        } else {
//            item.setTotalCount(item.getTotalCount()+totalCount);
//        }
//    }
//}
