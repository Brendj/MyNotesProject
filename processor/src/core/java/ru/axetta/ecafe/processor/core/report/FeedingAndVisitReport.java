/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Data;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Days;
import ru.axetta.ecafe.processor.core.report.model.feedingandvisit.Row;

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
    private static final int DEFAULT_REPORT_WIRTH =  550;

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
            Calendar date = Calendar.getInstance();
            List<Days> days = new LinkedList<Days>();
            for( int i = 1; i < date.get(Calendar.DAY_OF_MONTH); i++){  //ставить максимально кол-во дней в месяце
                days.add(new Days(i));
            }
            parameterMap.put("days", days);

            calendar.setTime(startTime);
            calendar.set(Calendar.DAY_OF_MONTH,0);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            //int orgCount = getFriendlyOrgs(session,org.getIdOfOrg()).size() - 1;
            //jasperPrint.setPageWidth(DEFAULT_REPORT_WIRTH + 400*orgCount );
            Date generateEndTime = new Date();
            return new AutoEnterEventV2Report(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {



            return new JRBeanCollectionDataSource(prepareBaseData(calendar));
        }

        private static List<Data> prepareBaseData(Calendar calendar){
            List<Days> days = new LinkedList<Days>();
            for( int i = 1; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++){  //ставить максимально кол-во дней в месяце
                days.add(new Days(i));
            }

            List<Data> dataList = new LinkedList<Data>();
            dataList.add( new Data("1А",days));
            dataList.add( new Data("2Б",days));
            dataList.add( new Data("3В",days));

            dataList.get(0).setReserve(new LinkedList<Row>() {{
                add(new Row(1L, "Ivan1", 1, "10:45 - 15:56", 1));
                add(new Row(1L, "Ivan2", 3, "10:45 - 15:56", 1));
                add(new Row(1L, "Ivan2", 4, "10:45 - 15:56", 1));
            }});
            dataList.get(0).setPlan(new LinkedList<Row>() {{
                add(new Row(1L, "Ivan41", 1, "10:45 - 15:56", 1));
                add(new Row(1L, "Ivan52", 2, "10:45 - 15:56", 1));
            }});
            dataList.get(0).setTotal(new LinkedList<Row>() {{
                add(new Row(1L, "Ivan6", 1, "10:45 - 15:56", 1));
                add(new Row(1L, "Ivan7", 2, "10:45 - 15:56", 1));
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
