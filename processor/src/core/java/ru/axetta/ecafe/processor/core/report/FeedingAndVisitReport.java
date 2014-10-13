/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
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

            startTime = CalendarUtils.getFirstDayOfMonth(startTime);
            endTime = CalendarUtils.getLastDayOfMonth(startTime);



            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));

            jasperPrint.setPageWidth(DEFAULT_REPORT_WIDTH + 80*CalendarUtils.getDifferenceInDays(startTime));
            Date generateEndTime = new Date();
            return new AutoEnterEventV2Report(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            final int daysCount = CalendarUtils.getDifferenceInDays(startTime);

            List<Data> dataList = prepareBaseData(startTime);


            List<PlanOrderItem> data = findData(session, startTime, org.getIdOfOrg());


            List<Long> clientsInsideBuilding = ClientsEntereventsService
                    .getClientsInsideBuilding(session, org.getIdOfOrg(), startTime, endTime);



            EnterEventsService enterEventsService = (EnterEventsService) RuntimeContext.getAppContext()
                    .getBean(EnterEventsService.class);

            List<DAOEnterEventSummaryModel> enterEventsSummary = enterEventsService
                    .getEnterEventsSummary(org.getIdOfOrg(), startTime.getTime(), endTime.getTime());

            Map<Long, List<Row>> rowMap = prepareBaseData(enterEventsSummary, daysCount ); // проходы учеников

            dataList = updateClientsData(rowMap, data, daysCount);
            //питание
            return new JRBeanCollectionDataSource(dataList);
        }

        private List<Data> updateClientsData(Map<Long, List<Row>> rowMap, List<PlanOrderItem> data, int daysCount) {

            Map<String, Data> dataMap = fillDataMap(rowMap,daysCount);
            List<PlanOrderItem> lost = new ArrayList<PlanOrderItem>();
            for (PlanOrderItem item : data){
                if(rowMap.containsKey(item.idOfClient)){
                    List<Row> client = rowMap.get(item.idOfClient);
                    int itemDay = CalendarUtils.getDayOfMonth(item.orderDate);
                    for(Row row :dataMap.get(client.get(0).getGroupname()).getPlan()){
                        if(row.getDay().equals(itemDay) &&row.getClientId().equals(item.idOfClient) ){
                            row.setColor(Row.COLOR_PAID);
                        }
                    }


                    boolean foundInTotal = false;
                    for (Row row : dataMap.get(client.get(0).getGroupname()).getTotal() ){
                        if(row.getClientId().equals(item.idOfComplex)&&row.getDay().equals(itemDay)){
                            row.incrementcount();
                            foundInTotal = true;
                            break;
                        }
                    }
                    if (!foundInTotal){
                        dataMap.get(client.get(0).getGroupname()).getTotal().addAll(fillTotalDataMap(item,daysCount));
                        for (Row row : dataMap.get(client.get(0).getGroupname()).getTotal() ){
                            if(row.getClientId().equals(item.idOfComplex)&&row.getDay().equals(itemDay)){
                                row.incrementcount();
                            }
                        }
                    }

                } else {
                    lost.add(item);
                }
            }
            return new ArrayList<Data>(dataMap.values());
        }

        private  static List<Row> fillTotalDataMap(PlanOrderItem item, int daysCount){
            List<Row> rowList = new ArrayList<Row>();
            for( int i = 1; i <= daysCount; i++){
                rowList.add(new Row((long)item.idOfComplex,""+item.idOfComplex,i,""){{setTotalRow(true);}}); //todo replace with name
            }
            return  rowList;
        }



            private  static Map<String, Data> fillDataMap(Map<Long, List<Row>> rowMap, int daysCount){
            List<Days> days = new ArrayList<Days>();
            for( int i = 1; i <= daysCount; i++){
                days.add(new Days(i));
            }
            Map<String, Data> result = new HashMap<String, Data>();

            for (List<Row> rowList : rowMap.values()) {
                for (Row row : rowList) {
                    if (!result.containsKey(row.getGroupname())) {
                        result.put(row.getGroupname(), new Data(row.getGroupname(), days));
                    }

                    result.get(row.getGroupname()).getPlan().add(row);
                }
            }

            return result;
        }


        private static Map<Long, List<Row> > prepareBaseData(List<DAOEnterEventSummaryModel> summaryModelList,  int daysCount){
            Map<Long, List<Row> > rowMap = new HashMap<Long, List<Row>>();

            Row row ;
            for (final DAOEnterEventSummaryModel model : summaryModelList){
                if(!rowMap.containsKey(model.getIdOfClient())){
                    rowMap.put(model.getIdOfClient(), fillClientForAllDays(model, daysCount));
                    continue;
                }
                int modelDay = CalendarUtils.getDayOfMonth(model.getEvtdatetime());
                row = null;
                for ( Row row1 : rowMap.get(model.getIdOfClient())){
                    if(row1.getDay().equals(modelDay) ){
                        row = row1;
                    }
                }
                if (row == null){
                    row = new Row(model);
                    rowMap.get(model.getIdOfClient()).add(row);
                }else{
                    row.update(model);
                }
            }
            return rowMap;
        }

        private static List<Row> fillClientForAllDays(DAOEnterEventSummaryModel model,  int daysCount){
            List<Row> rowList = new ArrayList<Row>();
            for( int i = 1; i <= daysCount; i++){
                rowList.add(new Row(model.getIdOfClient(), model.getVisitorFullName(), i, model.getGroupname()));
            }
            return rowList;
        }

        private static Map<String, List<Data>> fillClasses(List<Data> dataList, Date startTime,int daysCount ){
            List<Days> days = new ArrayList<Days>();
            for( int i = 1; i <= daysCount; i++){
                days.add(new Days(i));
            }

            return null;
        }

        private static List<Data> prepareBaseData(Date startTime){
            List<Days> days = new ArrayList<Days>();
            final int daysCount = CalendarUtils.getDifferenceInDays(startTime);
            for( int i = 1; i <= daysCount; i++){
                days.add(new Days(i));
            }

            List<Data> dataList = new ArrayList<Data>();
            dataList.add( new Data("1А",days));
            dataList.add( new Data("2Б",days));
            dataList.add( new Data("3В",days));

            dataList.get(0).setReserve(new ArrayList<Row>() {{
                for(int i = 1; i <= daysCount; i++){
                    add(new Row("Ivan", i));
                }
            }});
            dataList.get(0).setPlan(new ArrayList<Row>() {{
                for(int i = 1; i <= daysCount; i++){
                    add(new Row("Ivan", i));
                }
            }});
            dataList.get(0).setTotal(new ArrayList<Row>() {{
                for(int i = 1; i <= daysCount; i++){
                    add(new Row("Завтрак", i));
                }
            }});



            dataList.get(1).setReserve(new ArrayList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 3,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 4,"10:45 - 15:56",1));
            }});
            dataList.get(1).setPlan(new ArrayList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 2,"10:45 - 15:56",1));
            }});
            dataList.get(1).setTotal(new ArrayList<Row>(){{
                add(new Row(1L,"Ivan", 1," - 15:56",1));
                add(new Row(1L,"Ivan", 2,"10:45 - 15:56",1));
            }});

            dataList.get(2).setReserve(new ArrayList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 3,"10:45 - ",1));
                add(new Row(1L,"Ivan", 4,"10:45 - 15:56",1));
            }});
            dataList.get(2).setPlan(new ArrayList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 2,"X",1));
            }});
            dataList.get(2).setTotal(new ArrayList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 2,"H",1));
            }});

            return dataList;
        }

    }

    private static List<PlanOrderItem> findData(Session session, Date startTime, Long idOfOrg){
        List<PlanOrderItem> list =  ClientsEntereventsService.loadPlanOrderItemToPay(session, startTime, idOfOrg);
        return list;
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
