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
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.SentSmsItem;
import ru.axetta.ecafe.processor.core.report.msc.DiscrepanciesOnOrdersAndAttendanceJasperReport;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

import static org.hibernate.criterion.Order.asc;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.02.14
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
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


        if (StringUtils.isEmpty(getReportProperties().getProperty("idOfMenuSourceOrg"))) {
            throw new Exception("Не указана организация-поставщик меню.");
        }
        String sourceMenuOrgId = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfMenuSourceOrg"));
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

    public DiscrepanciesOnOrdersAndAttendanceReport build(Session session, List<Long> idOfSupplier, List<Long> idOfOrgs,
            Calendar calendar, Date startTime, Date endTime) throws  Exception{

        Criteria catCriteria = session.createCriteria(Org.class)
                .createAlias("categoriesInternal", "cat", JoinType.LEFT_OUTER_JOIN)
                .createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
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


        String goodSQL = "select sum(this_.TotalCount)/1000, this_.OrgOwner, gr1_.DoneDate "
                + " from cf_goods_requests_positions this_ "
                + " inner join cf_goods_requests gr1_ on this_.IdOfGoodsRequest=gr1_.IdOfGoodsRequest "
                + "  join cf_menuexchangerules on cf_menuexchangerules.idofdestorg=this_.OrgOwner "
                + " where gr1_.DoneDate>=:startDate and gr1_.DoneDate<:endDate "
                + " and cf_menuexchangerules.idofsourceorg in (:idOfSupplier)"
                + " and gr1_.State=1 and this_.DeletedState=false group by this_.OrgOwner, gr1_.DoneDate "
                + " order by this_.OrgOwner asc ";
        Query goodQuery = session.createSQLQuery(goodSQL);
        goodQuery.setLong("startDate", startTime.getTime());
        goodQuery.setLong("endDate", endTime.getTime());
        goodQuery.setParameterList("idOfSupplier", idOfSupplier);
        List<Object[]> goodRes = (List<Object[]>) goodQuery.list();

        Map<Long, Map<Date, RequestCountItem>> requestCountMap = new HashMap<Long, Map<Date, RequestCountItem>>();
        for (Object[] row : goodRes) {
            //Long idOfOrg = (Long) row[1];
            Long idOfOrg = ((BigInteger) row[1]).longValue();
            //Long totalCount = row[0] == null ? 0L : ((Long) row[0])/1000;
            Long totalCount = ((BigDecimal) row[0]).longValue();
            //Date date = CalendarUtils.truncateToDayOfMonth((Date) row[2]);
            Date date  = CalendarUtils.truncateToDayOfMonth(new Date(((BigInteger) row[2]).longValue()));
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


        String sql = "SELECT count(client), EXTRACT(EPOCH FROM sms_data.d) * 1000, sms_data.org "
                + "FROM ("
                + "  SELECT idofclient AS client, date_trunc('day', to_timestamp(evtdatetime/1000)) AS d, idoforg AS org"
                + "  FROM cf_enterevents "
                + "  join cf_menuexchangerules on cf_menuexchangerules.idofdestorg=cf_enterevents.idoforg "
                + "  WHERE evtdatetime >= :startDate AND evtdatetime <= :endDate AND passdirection = 0 and cf_menuexchangerules.idofsourceorg in (:idOfSupplier)) AS sms_data "
                + "  GROUP BY sms_data.d, sms_data.org";

        Query query = session.createSQLQuery(sql);
        query.setLong("startDate", startTime.getTime());
        query.setLong("endDate", endTime.getTime());
        query.setParameterList("idOfSupplier", idOfSupplier);
        List<Object[]> res = query.list();

        Map<Long, Map<Date, EnterEventCountItem>> eventCountMap = new HashMap<Long, Map<Date, EnterEventCountItem>>();
        for (Object[] row : res) {
            Long idOfOrg = ((BigInteger) row[2]).longValue();// (Long) row[2];
            Long totalCount = ((BigInteger) row[0]).longValue();
            //Long totalCount = row[0] == null ? 0L : ((Long) row[0]);
            //Date date = CalendarUtils.truncateToDayOfMonth((Date) row[1]);
            long d = ((Double) row[1]).longValue();
            calendar.setTimeInMillis(d);
            Date date = calendar.getTime();
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
            //Map<Date, EnterEventCountItem> dateForecastEventCountItemMap = eventForecastCountMap.get(id);
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

                //if(dateForecastEventCountItemMap!=null){
                //    EnterEventCountItem item = dateForecastEventCountItemMap.get(beginDate);
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

        return new DiscrepanciesOnOrdersAndAttendanceReport(items);
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

//Map<Long, Map<Date, EnterEventCountItem>> eventForecastCountMap = new HashMap<Long, Map<Date, EnterEventCountItem>>();
//for (Object[] row: enterEventForecastRes){
//    Long idOfOrg = (Long) row[1];
//    Long totalCount = row[0] == null ? 0L : ((Long) row[0]);
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
//}

//Map<Long, Map<Date, EnterEventCountItem>> eventForecastCountMap = new HashMap<Long, Map<Date, EnterEventCountItem>>();
//for (Long idOfOrg: eventForecastCountMap.keySet()){
//    Map<Date, EnterEventCountItem> dateOrderCountItemMap = eventForecastCountMap.get(idOfOrg);
//    Date beginDate = CalendarUtils.truncateToDayOfMonth(startTime);
//    Date endDate = CalendarUtils.truncateToDayOfMonth(endTime);
//    orderBeginDate = CalendarUtils.addDays(beginDate, -21);
//    orderEndDate = CalendarUtils.addDays(endDate, -21);
//    Map<Date, EnterEventCountItem> dateOrderForecastCountItemMap = new HashMap<Date, EnterEventCountItem>();
//
//    while (orderBeginDate.getTime() <= orderEndDate.getTime()) {
//        EnterEventCountItem m = dateOrderCountItemMap.get(orderBeginDate);
//        long total =0L;
//        if(m!=null) total = m.getTotalCount();
//        Date forecastBeginDate = CalendarUtils.addDays(orderBeginDate, 21);
//        //Date forecastEndDate = CalendarUtils.addDays(orderEndDate, 21);
//        while (forecastBeginDate.getTime() <= endDate.getTime() ) {
//            EnterEventCountItem forecastItem = dateOrderForecastCountItemMap.get(forecastBeginDate);
//            if(forecastItem==null){
//                dateOrderForecastCountItemMap.put(forecastBeginDate, new EnterEventCountItem(total, forecastBeginDate));
//            } else {
//                forecastItem.setTotalCountAndCount(forecastItem.getTotalCount() + total);
//            }
//            forecastBeginDate = CalendarUtils.addDays(forecastBeginDate, 1);
//        }
//
//        orderBeginDate = CalendarUtils.addDays(orderBeginDate, 1);
//    }
//    for (Date d: dateOrderForecastCountItemMap.keySet()){
//        EnterEventCountItem item = dateOrderForecastCountItemMap.get(d);
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
