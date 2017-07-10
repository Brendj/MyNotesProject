/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientItem;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.service.clients.SubFeedingService;
import ru.axetta.ecafe.processor.core.persistence.service.enterevents.EnterEventsService;
import ru.axetta.ecafe.processor.core.persistence.service.order.OrderService;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
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
 * S - mean Simplified
 * User: Shamil
 * Date: 03.10.14
 */
public class FeedingAndVisitSReport extends BasicReportForOrgJob {
    /*
   * Параметры отчета для добавления в правила и шаблоны
   *
   * При создании любого отчета необходимо добавить параметры:
   * REPORT_NAME - название отчета на русском
   * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
   * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
   * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
   * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
   *
   * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
   */
    public static final String REPORT_NAME = "Отчет по питанию и посещению";
    public static final String[] TEMPLATE_FILE_NAMES = {"FeedingAndVisitSReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3};


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
            OrgService orgService = OrgService.getInstance();
            if (org == null) throw new NullPointerException("Не выбрана организация");
            Org mainBulding = orgService.getMainBulding(org.getIdOfOrg());
            parameterMap.put("orgName", mainBulding != null? mainBulding.getShortNameInfoService() : org.getOfficialName());
            parameterMap.put("startDate", CalendarUtils.dateShortToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToString(endTime));

            startTime = CalendarUtils.roundToBeginOfDay(startTime);

            endTime = CalendarUtils.endOfDay(endTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime));

            jasperPrint.setPageWidth(DEFAULT_REPORT_WIDTH + 70 * CalendarUtils.getDifferenceInDays(startTime,endTime));
            Date generateEndTime = new Date();
            return new FeedingAndVisitSReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime) throws Exception {
            Map<String, Data> dataMap = new HashMap<String, Data>();

            List<Org> orgList = DAOUtils.findAllFriendlyOrgs(session, org.getIdOfOrg());
            String orgsIdsString = OrgUtils.extractIdsAsString(orgList);

            SubFeedingService subFeedingService = RuntimeContext.getAppContext().getBean(SubFeedingService.class);

            OrderService orderService =  RuntimeContext.getAppContext().getBean(OrderService.class);

            List<ClientItem> clientItemList = new ArrayList<ClientItem>();
            for (Org org1 : orgList) {
                clientItemList.addAll(subFeedingService.getClientItems(org1.getIdOfOrg()));
            }
            List<OrderItem> orderItemList = orderService
                    .findOrders(orgsIdsString, startTime, endTime);

            dataMap = fillDataPlanWithClients(dataMap, clientItemList, startTime, endTime, orgList);

            List<Row> overallTotal = new LinkedList<Row>();

            updataDataWithOrders(dataMap, orderItemList, startTime, endTime, orgList, orgsIdsString, overallTotal);

            //clientItemList = subFeedingService.getClientItems(org.getIdOfOrg(),notFoundOrderItems);
            EnterEventsService enterEventsService = RuntimeContext.getAppContext().getBean(EnterEventsService.class);

            List<DAOEnterEventSummaryModel> enterEventsSummary = enterEventsService
                    .getEnterEventsSummary(orgsIdsString, startTime.getTime(), endTime.getTime());

            fillDataWithEnterEvents(dataMap, enterEventsSummary, orgList, orgsIdsString);

            List<Data> dataList = new ArrayList<Data>(dataMap.values());
            Collections.sort(dataList);

            if(dataList.size() > 0){
                dataList.get(dataList.size()-1).setOverall(overallTotal);
            }
            processColors(dataList);

            return new JRBeanCollectionDataSource(dataList);
        }

        private void processColors(List<Data> dataList) {
            for (Data data : dataList) {
                processColorsRowsList(data.getPlan());
                processColorsRowsList(data.getReserve());
                processColorsRowsList(data.getTotal());
                processColorsRowsList(data.getOverall());
            }
        }
        private void processColorsRowsList(List<Row> dataList) {
            for (Row row : dataList) {
                row.setEntry(row.processEntry());
                row.setColorTo(row.processColorTo());
                row.processOrderDiff();
            }
        }


        private void updataDataWithOrders(Map<String, Data> dataMap, List<OrderItem> orderItemList, Date startTime,
                Date endTime, List<Org> orgList, String orgsIdsString, List<Row> overallTotal) {
            Data currentData;
            OrderItem notfoundItem = null;
            SubFeedingService subFeedingService = RuntimeContext.getAppContext().getBean(SubFeedingService.class);

            for (OrderItem orderItem : orderItemList) {
                if (StringUtils.isBlank(prepareGroupName(orgList, orderItem.getGroupName(), orderItem.idOfOrg))) {
                    continue;
                }
                currentData = dataMap.get(prepareGroupName(orgList, orderItem.getGroupName(), orderItem.idOfOrg));

                //находим класс в котором был заказ, если
                if (currentData == null) {
                    ClientItem clientItem = subFeedingService.getClientItem(orgsIdsString, orderItem.idOfClient);
                    if(clientItem == null ){
                        clientItem = new ClientItem(orderItem.getIdOfClient(), orderItem.getIdOfOrg(), orderItem.getOrgName(), orderItem.getFullname(),
                                ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup(), orderItem.getOrdertype());
                        if(orderItem.getOrdertype() == 4){
                            clientItem.setPlanType(ClientItem.IN_PLAN_TYPE);
                        }else {
                            clientItem.setPlanType(ClientItem.IN_RESERVE_TYPE);
                        }
                    }
                    currentData = dataMap.get(
                            prepareGroupName(orgList, clientItem.getGroupName(), clientItem.getIdOfOrg()));

                    if(currentData == null){
                        currentData = createGroup(dataMap,orgList, clientItem, startTime, endTime);
                    }

                }
                notfoundItem = updateRowListWithOrder(currentData, orderItem);
                if (notfoundItem != null) {
                    try {
                        ClientItem clientItem = subFeedingService.getClientItem(orgsIdsString, orderItem.idOfClient);
                        if(clientItem == null ){
                            clientItem = new ClientItem(orderItem.getIdOfClient(), orderItem.getIdOfOrg(), orderItem.getOrgName(), orderItem.getFullname(),
                                    ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup(), orderItem.getOrdertype());
                            if(orderItem.getOrdertype() == 4){
                                clientItem.setPlanType(ClientItem.IN_PLAN_TYPE);
                            }else {
                                clientItem.setPlanType(ClientItem.IN_RESERVE_TYPE);
                            }
                        }


                        currentData = dataMap.get(
                                prepareGroupName(orgList, clientItem.getGroupName(), clientItem.getIdOfOrg()));

                        if (currentData != null) {
                            notfoundItem = updateRowListWithOrder(currentData, orderItem);
                            if (notfoundItem != null) {
                                fillRowListWithClient(currentData, clientItem, startTime, endTime, orderItem);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Не удалось найти клиента: " + orderItem.getIdOfClient());
                    }
                }

                //todo 8 handle
                if (currentData != null) {
                    updateTotalListWithOrder(currentData, orderItem, startTime, endTime);
                    updateOverallTotalListWithOrder(overallTotal, orderItem, startTime, endTime);
                }
            }
        }

        private static List<Row> getRowListByOrderType(Data data, OrderItem orderItem){
            List<Row> rowList = null;

            switch (orderItem.getOrdertype()){
                case 4:
                    rowList = data.getPlan();
                    break;
                case 6:
                    rowList = data.getReserve();
                    break;
                default:
                    rowList = new ArrayList<Row>();
            }
            return rowList;
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
                            .add(new Row( (long) item.idOfComplex, item.getIdOfOrg(), item.getComplexName(), i, item.getGroupName()) {{
                                setTotalRow(true);
                            }});
                }
                updateTotalListWithOrder(data, item, startTime, endTime);
            }
        }

        private static void updateOverallTotalListWithOrder(List<Row> data, OrderItem item, Date startTime, Date endTime) {
            int itemDay = CalendarUtils.getDayOfMonth(item.orderDate);
            boolean foundItemNameInTotal = false;
            for (Row totalRow : data) {
                if ((totalRow.getClientId().equals((long) item.idOfComplex)) && (totalRow.getDay().equals(itemDay))) {
                    totalRow.updateTotal(item);
                    foundItemNameInTotal = true;
                }
            }

            if (!foundItemNameInTotal) {
                for (int i : CalendarUtils.daysBetween(startTime, endTime)) {
                    data.add(new Row( (long) item.idOfComplex, item.getIdOfOrg(), item.getComplexName(), i, item.getGroupName()) {{
                                setTotalRow(true);
                            }});
                }
                updateOverallTotalListWithOrder(data, item, startTime, endTime);
            }
        }

        private static OrderItem updateRowListWithOrder(Data currentData, OrderItem orderItem) {
            List<Row> rowList = getRowListByOrderType(currentData,orderItem);
            int eventDay = CalendarUtils.getDayOfMonth(orderItem.orderDate);
            for (Row groupItem : rowList) {
                if ((groupItem.getClientId().equals(orderItem.idOfClient)) && (groupItem.getDay().equals(eventDay))) {
                    groupItem.update(orderItem);
                    return null;
                }
            }
            return orderItem;
        }

        //Заполняет проходами резерв и план льготного питания
        private static void fillDataWithEnterEvents(Map<String, Data> dataMap,
                List<DAOEnterEventSummaryModel> enterEventsSummary, List<Org> orgList, String orgsIdsString) {
            Data data;
            SubFeedingService subFeedingService = RuntimeContext.getAppContext().getBean(SubFeedingService.class);
            List<Row> rowList;
            for (DAOEnterEventSummaryModel enterEvent : enterEventsSummary) {
                data = dataMap.get( prepareGroupName(orgList, enterEvent.getGroupName(), enterEvent.getClientOrgId()));
                if (data == null) {
                    ClientItem clientItem = subFeedingService.getClientItem(orgsIdsString, enterEvent.getClientOrgId());
                    if(clientItem != null && clientItem.getIdOfOrg()!= enterEvent.getIdOfOrg()){
                        data = dataMap.get(prepareGroupName(orgList, clientItem.getGroupName(), clientItem.getIdOfOrg()));

                    }
                    if (data == null){
                        continue;
                    }
                }

                boolean foundInPlan = updateRowListWithEnterEvent(data.getPlan(), enterEvent);
                boolean foundInReserve = updateRowListWithEnterEvent(data.getReserve(), enterEvent);
                if(!foundInPlan && !foundInReserve){
                    System.out.print("Не найден человек с событием прохода: " + enterEvent.getIdOfClient() );
                }
            }
        }

        private static boolean updateRowListWithEnterEvent(List<Row> rowList,
                DAOEnterEventSummaryModel enterEvent) {
            int eventDay = CalendarUtils.getDayOfMonth(enterEvent.getEvtDateTime());
            boolean flag = false;
            for (Row groupItem : rowList) {
                if ((groupItem.getClientId().equals(enterEvent.getIdOfClient())) && (groupItem.getDay().equals(eventDay)) ) {
                    groupItem.update(enterEvent);
                    flag= true;
                }
            }
            return flag;
        }


        private static Map<String, Data> fillDataPlanWithClients(Map<String, Data> dataMap,
                List<ClientItem> clientItems, Date startTime, Date endTime, List<Org> orgList) {

            for (ClientItem item : clientItems) {
                Data dataItem = dataMap.get( prepareGroupName(orgList, item.getGroupName(), item.getIdOfOrg()) );
                if (dataItem == null) {
                    dataItem = createGroup(dataMap,orgList,item,startTime,endTime);
                }
                if (item.getPlanType() == ClientItem.IN_PLAN_TYPE) {
                    fillRowListWithClient(dataItem.getPlan(), item, startTime, endTime,null);
                } else if (item.getPlanType() == ClientItem.IN_RESERVE_TYPE) {
                    fillRowListWithClient(dataItem.getReserve(), item, startTime, endTime,null);
                }
            }
            return dataMap;
        }

        private static Data createGroup(Map<String, Data> dataMap, List<Org> orgList,ClientItem item, Date startTime, Date endTime){
            List<Days> days = new ArrayList<Days>();
            for (long i : CalendarUtils.daysBetweenInMillis(startTime, endTime)) {
                days.add(new Days(i));
            }
            Data dataItem = new Data(prepareGroupName(orgList, item.getGroupName(), item.getIdOfOrg()), days);
            dataItem.setPlan(new ArrayList<Row>());
            dataItem.setReserve(new ArrayList<Row>());
            dataMap.put(dataItem.getName(), dataItem);

            return dataItem;
        }

        private static String prepareGroupName(List<Org> orgList, String groupname, long idOfOrg){
            return groupname;
        }

        private static void fillRowListWithClient(Data currentData, ClientItem item, Date start, Date end,OrderItem orderItem) {
            List<Row> dataItem = getRowListByOrderType(currentData, orderItem);
            fillRowListWithClient(dataItem, item, start, end, orderItem);
        }

        private static void fillRowListWithClient(List<Row> dataItem, ClientItem item, Date start, Date end,OrderItem orderItem) {
            Row row;
            for (int i : CalendarUtils.daysBetween(start, end)) {
                row = new Row(item.getId(), item.getIdOfOrg(), item.getFullName(), i, item.getGroupName());
                if(orderItem != null){
                    if(i == CalendarUtils.getDayOfMonth(orderItem.getOrderDate())){
                        row.update(orderItem);
                    }
                    row.setName( row.getName() + " ! ");
                }
                dataItem.add(row);
            }
        }
    }




    public FeedingAndVisitSReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }

    private static final Logger logger = LoggerFactory.getLogger(FeedingAndVisitSReport.class);

    public FeedingAndVisitSReport() {
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new FeedingAndVisitSReport();
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
