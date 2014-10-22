/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientItem;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.persistence.service.clients.SubFeedingService;
import ru.axetta.ecafe.processor.core.persistence.service.enterevents.EnterEventsService;
import ru.axetta.ecafe.processor.core.persistence.utils.ClientsEntereventsService;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Data;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Days;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Row;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Shamil
 * Date: 03.10.14
 */
public class FeedingAndVisitReport  extends BasicReportForOrgJob {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    private static final int DEFAULT_REPORT_WIDTH =  225;

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {
        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("date", CalendarUtils.dateMMMMYYYYToString(startTime));

            //startTime = CalendarUtils.getFirstDayOfMonth(startTime);
            //endTime = CalendarUtils.getLastDayOfMonth(startTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));

            jasperPrint.setPageWidth(DEFAULT_REPORT_WIDTH + 80*CalendarUtils.getDifferenceInDays(startTime));
            Date generateEndTime = new Date();
            return new AutoEnterEventV2Report(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {

            Map<String,Data> dataMap = new HashMap<String, Data>();


            SubFeedingService subFeedingService = (SubFeedingService) RuntimeContext.getAppContext()
                    .getBean(SubFeedingService.class);

            List<ClientItem> clientItemList = subFeedingService.getClientItems(org.getIdOfOrg());



            List<PlanOrderItem> ordersInPlan = ClientsEntereventsService.loadPlanOrderItemToPay(session, startTime, org.getIdOfOrg());
            List<PlanOrderItem> ordersPlanPaid = ClientsEntereventsService.loadPaidPlanOrders(session, "4,8,9",
                    "" + org.getIdOfOrg(), startTime, endTime);
            List<PlanOrderItem> ordersReservePaid = ClientsEntereventsService.loadPaidPlanOrders(session,ClientsEntereventsService.REDUCED_PRICE_PLAN_RESERVE,""+org.getIdOfOrg(),startTime, endTime);

            dataMap = fillDataPlanWithOrdersV(dataMap, clientItemList, startTime, endTime);

            fillDataPlanWithPaidOrders(dataMap, ordersPlanPaid);
            fillDataTotalWithPaidOrders(dataMap,ordersPlanPaid,startTime,endTime);
            fillDataReserveWithPaidOrders(dataMap,ordersReservePaid,startTime,endTime);
            fillDataTotalWithPaidOrders(dataMap,ordersReservePaid,startTime,endTime);


            EnterEventsService enterEventsService = (EnterEventsService) RuntimeContext.getAppContext()
                    .getBean(EnterEventsService.class);

            List<DAOEnterEventSummaryModel> enterEventsSummary = enterEventsService
                    .getEnterEventsSummary(org.getIdOfOrg(), startTime.getTime(), endTime.getTime());

            fillDataWithEnterEvents(dataMap, enterEventsSummary);

            List<Data> dataList = new ArrayList<Data>(dataMap.values());
            Collections.sort(dataList);
            return new JRBeanCollectionDataSource(dataList);
        }

        private void fillDataReserveWithPaidOrders(Map<String, Data> dataMap, List<PlanOrderItem> ordersReservePaid, Date startTime, Date endTime) {
            Data data;
            for (PlanOrderItem item : ordersReservePaid) {
                if(item.idOfClient == null){
                    continue;
                }
                data = dataMap.get(item.getGroupName());
                if(data == null){
                    continue;
                }
                updateReserveListWithOrder(data.getReserve(), item);
            }
        }

        private void updateReserveListWithOrder(List<Row> reserveList, PlanOrderItem item) {
            int eventDay = CalendarUtils.getDayOfMonth(item.orderDate);
            for (Row groupItem : reserveList) {
                if( ( groupItem.getClientId().equals(item.idOfClient) )&& (groupItem.getDay().equals(eventDay)) ){
                    groupItem.update(item);
                }
            }
        }

        private static void fillDataTotalWithPaidOrders(Map<String, Data> dataMap, List<PlanOrderItem> orderItemList, Date startTime, Date endTime) {
            Data data;
            for (PlanOrderItem item : orderItemList) {
                data = dataMap.get(item.getGroupName());
                if(data == null){
                    continue;
                }
                updateTotalListWithOrder(data, item, startTime, endTime);
            }
        }

        private static void updateTotalListWithOrder(Data data, PlanOrderItem item, Date startTime, Date endTime) {
            if(data.getTotal() == null){
                data.setTotal(new ArrayList<Row>());
            }
            int itemDay = CalendarUtils.getDayOfMonth(item.orderDate);
            boolean foundItemNameInTotal = false;
            for (Row totalRow : data.getTotal()) {
                if( (totalRow.getClientId().equals((long)item.idOfComplex)) &&(totalRow.getDay().equals(itemDay)) ){
                    totalRow.updateTotal(item);
                    foundItemNameInTotal = true;
                }
            }

            if (!foundItemNameInTotal){

                for(int i : CalendarUtils.daysBetween(startTime,endTime)){
                    data.getTotal().add(new Row((long)item.idOfComplex, item.getComplexName(), i, item.getGroupName()){{setTotalRow(true);}});
                }
                updateTotalListWithOrder(data, item, startTime, endTime);
            }
        }

        private static void fillDataPlanWithPaidOrders(Map<String,Data> dataMap, List<PlanOrderItem> ordersPlanPaid){
            Data data;
            for (PlanOrderItem item : ordersPlanPaid) {
                data = dataMap.get(item.getGroupName());
                if(data == null){
                    continue;
                }
                updateRowListWithOrder(data.getPlan(), item);
            }
        }

        private static void updateRowListWithOrder(List<Row> rowList, PlanOrderItem item) {
            int eventDay = CalendarUtils.getDayOfMonth(item.orderDate);
            for (Row groupItem : rowList) {
                if( ( groupItem.getClientId().equals(item.idOfClient) )&& (groupItem.getDay().equals(eventDay)) ){
                    groupItem.update(item);
                }
            }
        }

        //Заполняет проходами резерв и план льготного питания
        private static void fillDataWithEnterEvents(Map<String,Data> dataMap, List<DAOEnterEventSummaryModel> enterEventsSummary){
            Data data;
            for (DAOEnterEventSummaryModel enterEvent : enterEventsSummary) {
                data = dataMap.get(enterEvent.getGroupName());
                if(data == null){
                    continue;
                }

                updateRowListWithEnterEvent(data.getPlan(),enterEvent);
                updateRowListWithEnterEvent(data.getReserve(),enterEvent);
            }
        }
        private static void updateRowListWithEnterEvent(List<Row> rowList, DAOEnterEventSummaryModel enterEvent ){
            int eventDay = CalendarUtils.getDayOfMonth(enterEvent.getEvtDateTime());
            for (Row groupItem : rowList) {
                if( ( groupItem.getClientId().equals(enterEvent.getIdOfClient()) )&& (groupItem.getDay().equals(eventDay)) ){
                    groupItem.update(enterEvent);
                }
            }
        }

        private static  Map<String,Data> fillDataPlanWithOrders(Map<String, Data> dataMap,List<PlanOrderItem> ordersInPlan, Date start, Date end){
            List<Days> days = new ArrayList<Days>();
            for(int i : CalendarUtils.daysBetween(start,end)){
                days.add(new Days(i));
            }
            List<Long> idsInList = new LinkedList<Long>();

            for(PlanOrderItem item : ordersInPlan){
                if(idsInList.contains(item.idOfClient)){
                    continue;
                }
                Data dataItem = dataMap.get(item.getGroupName());
                if(dataItem == null) {
                    dataItem = new Data(item.getGroupName(),days);
                    dataItem.setPlan(new ArrayList<Row>());
                    dataMap.put(dataItem.getName(),dataItem);
                }
                for(int i : CalendarUtils.daysBetween(start,end)){
                    dataItem.getPlan().add(new Row(item.idOfClient, item.getClientName(), i, item.getGroupName()));
                }

                idsInList.add(item.idOfClient);
            }

            return dataMap;
        }
        private static  Map<String,Data> fillDataPlanWithOrdersV(Map<String, Data> dataMap,List<ClientItem> clientItems,Date start, Date end){
            List<Days> days = new ArrayList<Days>();
            for(int i : CalendarUtils.daysBetween(start,end)){
                days.add(new Days(i));
            }
            List<Long> idsInList = new LinkedList<Long>();

            for(ClientItem item : clientItems){
                if(idsInList.contains(item.getId())){
                    continue;
                }
                Data dataItem = dataMap.get(item.getGroupName());
                if(dataItem == null) {
                    dataItem = new Data(item.getGroupName(),days);
                    dataItem.setPlan(new ArrayList<Row>());
                    dataItem.setReserve(new ArrayList<Row>());
                    dataMap.put(dataItem.getName(),dataItem);
                }
                for(int i : CalendarUtils.daysBetween(start,end)){
                    if(item.getPlanType() == ClientItem.IN_PLAN_TYPE){
                        dataItem.getPlan().add(new Row(item.getId(), item.getFullName(), i, item.getGroupName()));
                    }else if(item.getPlanType() == ClientItem.IN_RESERVE_TYPE){
                        dataItem.getReserve().add(new Row(item.getId(), item.getFullName(), i, item.getGroupName()));
                    }
                }

                idsInList.add(item.getId());
            }

            return dataMap;
        }
    }


    public FeedingAndVisitReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }
    private static final Logger logger = LoggerFactory.getLogger(AutoEnterEventV2Report.class);

    public FeedingAndVisitReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new AutoEnterEventV2Report();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_CURRENT_MONTH;
    }
}
