/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.AbstractDAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by anvarov on 19.02.2018.
 */
public class AcceptanceOfCompletedWorksAct extends BasicReportForOrgJob {

    public static final String ACT_NAME = "Акт сдачи-приёмки оказанных услуг";
    public static final String TEMPLATE_FILE_NAME = "AcceptanceOfCompletedWorksAct.jasper";

    private final static Logger logger = LoggerFactory.getLogger(AcceptanceOfCompletedWorksAct.class);

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;
        private AcceptanceOfCompletedWorksActDAOService daoService;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Boolean showAllOrgs = Boolean.valueOf(reportProperties.getProperty("showAllOrgs"));
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("idOfOrg", org.getIdOfOrg());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime) + "г.");
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime) + "г.");

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap, showAllOrgs));
            Date generateEndTime = new Date();
            return new AcceptanceOfCompletedWorksAct(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap, Boolean showAllOrgs) throws Exception {

            daoService = new AcceptanceOfCompletedWorksActDAOService();
            daoService.setSession(session);

            List<AcceptanceOfCompletedWorksActItem> result = daoService.findAllItemsForAct(org, showAllOrgs);
            calendar.setTime(startTime);

            return new JRBeanCollectionDataSource(result);
        }
    }

    public AcceptanceOfCompletedWorksAct(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }

    public AcceptanceOfCompletedWorksAct() {

    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new AcceptanceOfCompletedWorksAct();
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
        return REPORT_PERIOD_PREV_MONTH;
    }
}
