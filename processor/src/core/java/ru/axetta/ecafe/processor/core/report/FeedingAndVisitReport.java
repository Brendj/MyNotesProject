/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
            findData(session, startTime,org.getIdOfOrg());


            return new JRBeanCollectionDataSource(prepareBaseData(startTime));
        }

        private static List<Data> prepareBaseData(Date startTime){
            List<Days> days = new LinkedList<Days>();
            final int daysCount = CalendarUtils.getDifferenceInDays(startTime);
            for( int i = 1; i <= daysCount; i++){
                days.add(new Days(i));
            }

            List<Data> dataList = new LinkedList<Data>();
            dataList.add( new Data("1А",days));
            dataList.add( new Data("2Б",days));
            dataList.add( new Data("3В",days));

            dataList.get(0).setReserve(new LinkedList<Row>() {{
                for(int i = 1; i <= daysCount; i++){
                    add(new Row("Ivan", i));
                }
            }});
            dataList.get(0).setPlan(new LinkedList<Row>() {{
                for(int i = 1; i <= daysCount; i++){
                    add(new Row("Ivan", i));
                }
            }});
            dataList.get(0).setTotal(new LinkedList<Row>() {{
                for(int i = 1; i <= daysCount; i++){
                    add(new Row("Завтрак", i));
                }
            }});



            dataList.get(1).setReserve(new LinkedList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 3,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 4,"10:45 - 15:56",1));
            }});
            dataList.get(1).setPlan(new LinkedList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 2,"10:45 - 15:56",1));
            }});
            dataList.get(1).setTotal(new LinkedList<Row>(){{
                add(new Row(1L,"Ivan", 1," - 15:56",1));
                add(new Row(1L,"Ivan", 2,"10:45 - 15:56",1));
            }});

            dataList.get(2).setReserve(new LinkedList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 3,"10:45 - ",1));
                add(new Row(1L,"Ivan", 4,"10:45 - 15:56",1));
            }});
            dataList.get(2).setPlan(new LinkedList<Row>(){{
                add(new Row(1L,"Ivan", 1,"10:45 - 15:56",1));
                add(new Row(1L,"Ivan", 2,"X",1));
            }});
            dataList.get(2).setTotal(new LinkedList<Row>(){{
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
        return REPORT_PERIOD_PREV_DAY;
    }


}
