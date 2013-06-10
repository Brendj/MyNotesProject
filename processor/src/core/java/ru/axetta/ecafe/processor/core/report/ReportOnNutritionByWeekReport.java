/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.daoservices.client.ClientDAOService;
import ru.axetta.ecafe.processor.core.daoservices.client.items.DayInfo;
import ru.axetta.ecafe.processor.core.daoservices.client.items.ReportOnNutritionItem;
import ru.axetta.ecafe.processor.core.daoservices.client.items.ReportOnNutritionResultItem;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.03.12
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public class ReportOnNutritionByWeekReport extends BasicReportForOrgJob {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    public static SimpleDateFormat dayInWeekFormat = new SimpleDateFormat("EE", new Locale("ru"));



    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            List<String> daysWeek = new ArrayList<String>(6); // 1 Вс	2 Пн	3 Вт	4 Ср ...
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            parameterMap.put("days", daysWeek);
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("monthName", calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru")));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap, daysWeek));
            Date generateEndTime = new Date();
            return new ReportOnNutritionByWeekReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap, List<String> daysOfMonth) throws Exception {
            ClientDAOService clientDAOService = new ClientDAOService();
            clientDAOService.setSession(session);
            List<ReportOnNutritionResultItem> returnItems = new ArrayList<ReportOnNutritionResultItem>();
            List<ReportOnNutritionItem> items = clientDAOService.generateReportOnNutritionByWeekReport(org.getIdOfOrg(), startTime, endTime);
            List<ReportOnNutritionResultItem> resultItems = new ArrayList<ReportOnNutritionResultItem>();
            for (ReportOnNutritionItem clientsGroupInWeekItem: items){
                ReportOnNutritionResultItem temp = new ReportOnNutritionResultItem(clientsGroupInWeekItem,startTime,endTime);
                DayInfo dayInfo = new DayInfo();
                if(clientsGroupInWeekItem.getMenuType()==0){
                    dayInfo.setPriceType0(clientsGroupInWeekItem.getPrice());
                }
                if(clientsGroupInWeekItem.getMenuType()==1){
                    dayInfo.setPriceType1(clientsGroupInWeekItem.getPrice());
                }
                if(resultItems.contains(temp)){
                    int position = resultItems.indexOf(temp);
                    ReportOnNutritionResultItem other = resultItems.get(position);
                    other.addDateInfoByDate(temp.getCreateDate(),dayInfo);
                    resultItems.set(position,other);
                } else {
                    temp.addDateInfoByDate(temp.getCreateDate(),dayInfo);
                    resultItems.add(temp);
                }
            }
            for (ReportOnNutritionResultItem resultItem : resultItems){
                resultItem.checkCount();
                returnItems.add(resultItem);
            }
            return new JRBeanCollectionDataSource(returnItems);
        }

    }


    public ReportOnNutritionByWeekReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(ReportOnNutritionByWeekReport.class);

    public ReportOnNutritionByWeekReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ReportOnNutritionByWeekReport();
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
        return REPORT_PERIOD_LAST_WEEK;
    }
}


