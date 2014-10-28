/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientItem;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.service.clients.SubFeedingService;
import ru.axetta.ecafe.processor.core.persistence.service.enterevents.EnterEventsService;
import ru.axetta.ecafe.processor.core.persistence.service.order.OrderService;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Data;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Days;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Row;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Shamil
 * Date: 03.10.14
 */
public class FeedingAndVisitReport extends BasicReportForOrgJob {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    private static final int DEFAULT_REPORT_WIDTH = 550;

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("startDate", CalendarUtils.dateShortToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToString(endTime));

            endTime = CalendarUtils.endOfDay(endTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime));

            jasperPrint.setPageWidth(DEFAULT_REPORT_WIDTH + 70 * CalendarUtils.getDifferenceInDays(startTime,endTime));
            Date generateEndTime = new Date();
            return new AutoEnterEventV2Report(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime) throws Exception {
            Map<String, Data> dataMap = new HashMap<String, Data>();
            SubFeedingService subFeedingService = RuntimeContext.getAppContext().getBean(SubFeedingService.class);

            OrderService orderService =  RuntimeContext.getAppContext().getBean(OrderService.class);

            List<ClientItem> clientItemList = subFeedingService.getClientItems(org.getIdOfOrg());
            List<OrderItem> orderItemList = orderService
                    .findOrders(org.getIdOfOrg(), startTime, endTime);

            dataMap = fillDataPlanWithOrders(dataMap, clientItemList, startTime, endTime);

            updataDataWithOrders(dataMap, orderItemList, startTime, endTime);

            //clientItemList = subFeedingService.getClientItems(org.getIdOfOrg(),notFoundOrderItems);
            EnterEventsService enterEventsService = RuntimeContext.getAppContext().getBean(EnterEventsService.class);

            List<DAOEnterEventSummaryModel> enterEventsSummary = enterEventsService
                    .getEnterEventsSummary(org.getIdOfOrg(), startTime.getTime(), endTime.getTime());

            fillDataWithEnterEvents(dataMap, enterEventsSummary);

            List<Data> dataList = new ArrayList<Data>(dataMap.values());
            Collections.sort(dataList);
            return new JRBeanCollectionDataSource(dataList);
        }

        private void updataDataWithOrders(Map<String, Data> dataMap, List<OrderItem> orderItemList,
                Date startTime, Date endTime) {
            Data currentData;
            OrderItem notfoundItem = null;
            SubFeedingService subFeedingService = RuntimeContext.getAppContext().getBean(SubFeedingService.class);

            for (OrderItem orderItem : orderItemList) {
                if (StringUtils.isBlank(orderItem.getGroupName())) {
                    continue;
                }
                currentData = dataMap.get(orderItem.getGroupName());
                if (orderItem.getOrdertype() == OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal()) {
                    notfoundItem = updateRowListWithOrder(currentData.getPlan(), orderItem);
                    if(notfoundItem != null){
                        ClientItem clientItem = subFeedingService.getClientItem(org.getIdOfOrg(), orderItem);
                        fillRowListWithClient(currentData.getPlan(), clientItem, startTime, endTime, orderItem);
                        notfoundItem = null;
                    }
                } else if (orderItem.getOrdertype() == OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal()) {
                    notfoundItem = updateRowListWithOrder(currentData.getReserve(), orderItem);
                    if(notfoundItem != null){
                        ClientItem clientItem = subFeedingService.getClientItem(org.getIdOfOrg(), orderItem);
                        fillRowListWithClient(currentData.getReserve(), clientItem, startTime, endTime, orderItem);
                        notfoundItem = null;
                    }
                }
                updateTotalListWithOrder(currentData, orderItem, startTime, endTime);
            }
        }

        private static void updateTotalListWithOrder(Data data, OrderItem item, Date startTime, Date endTime) {
            if (data.getTotal() == null) {
                data.setTotal(new ArrayList<Row>());
            }
            int itemDay = CalendarUtils.getDayOfMonth(item.orderDate);
            boolean foundItemNameInTotal = false;
            for (Row totalRow : data.getTotal()) {
                if ((totalRow.getClientId().equals((long) item.idOfComplex)) && (totalRow.getDay().equals(itemDay))) {
                    totalRow.updateTotal(item);
                    foundItemNameInTotal = true;
                }
            }

            if (!foundItemNameInTotal) {

                for (int i : CalendarUtils.daysBetween(startTime, endTime)) {
                    data.getTotal()
                            .add(new Row((long) item.idOfComplex, item.getComplexName(), i, item.getGroupName()) {{
                                setTotalRow(true);
                            }});
                }
                updateTotalListWithOrder(data, item, startTime, endTime);
            }
        }

        private static OrderItem updateRowListWithOrder(List<Row> rowList, OrderItem item) {
            int eventDay = CalendarUtils.getDayOfMonth(item.orderDate);
            for (Row groupItem : rowList) {
                if ((groupItem.getClientId().equals(item.idOfClient)) && (groupItem.getDay().equals(eventDay))) {
                    groupItem.update(item);
                    return null;
                }
            }
            return item;
        }

        //Заполняет проходами резерв и план льготного питания
        private static void fillDataWithEnterEvents(Map<String, Data> dataMap,
                List<DAOEnterEventSummaryModel> enterEventsSummary) {
            Data data;
            for (DAOEnterEventSummaryModel enterEvent : enterEventsSummary) {
                data = dataMap.get(enterEvent.getGroupName());
                if (data == null) {
                    continue;
                }

                updateRowListWithEnterEvent(data.getPlan(), enterEvent);
                updateRowListWithEnterEvent(data.getReserve(), enterEvent);
            }
        }

        private static void updateRowListWithEnterEvent(List<Row> rowList, DAOEnterEventSummaryModel enterEvent) {
            int eventDay = CalendarUtils.getDayOfMonth(enterEvent.getEvtDateTime());
            for (Row groupItem : rowList) {
                if ((groupItem.getClientId().equals(enterEvent.getIdOfClient())) && (groupItem.getDay()
                        .equals(eventDay))) {
                    groupItem.update(enterEvent);
                }
            }
        }


        private static Map<String, Data> fillDataPlanWithOrders(Map<String, Data> dataMap, List<ClientItem> clientItems,
                Date start, Date end) {
            List<Days> days = new ArrayList<Days>();
            for (int i : CalendarUtils.daysBetween(start, end)) {
                days.add(new Days(i));
            }
            for (ClientItem item : clientItems) {
                Data dataItem = dataMap.get(item.getGroupName());
                if (dataItem == null) {
                    dataItem = new Data(item.getGroupName(), days);
                    dataItem.setPlan(new ArrayList<Row>());
                    dataItem.setReserve(new ArrayList<Row>());
                    dataMap.put(dataItem.getName(), dataItem);
                }

                if (item.getPlanType() == ClientItem.IN_PLAN_TYPE) {
                    fillRowListWithClient(dataItem.getPlan(), item, start, end,null);
                } else if (item.getPlanType() == ClientItem.IN_RESERVE_TYPE) {
                    fillRowListWithClient(dataItem.getReserve(), item, start, end,null);
                }
            }
            return dataMap;
        }
    }

    private static void fillRowListWithClient(List<Row> dataItem, ClientItem item, Date start, Date end,OrderItem orderItem) {
        Row row;
        for (int i : CalendarUtils.daysBetween(start, end)) {
            row =new Row(item.getId(), item.getFullName(), i, item.getGroupName());
            if(orderItem != null){
                if(i == CalendarUtils.getDayOfMonth(orderItem.getOrderDate())){
                    row.update(orderItem);
                }
                row.setName( row.getName() + " ! " + row.getGroupname());
            }
            dataItem.add(row);
        }
    }


    public FeedingAndVisitReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }

    private static final Logger logger = LoggerFactory.getLogger(AutoEnterEventV2Report.class);

    public FeedingAndVisitReport() {
    }

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
