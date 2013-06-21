/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentCompletionItem;
import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentCompletionReportItem;
import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentDAOService;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.01.13
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class ContragentCompletionReport extends BasicReportForContragentJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public static class Builder extends BasicReportForContragentJob.Builder{

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("contragentName", contragent.getContragentName());
            JRDataSource dataSource = createDataSource(session, contragent, startTime, endTime,
                    (Calendar) calendar.clone(), parameterMap);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();
            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, contragent.getIdOfContragent());
        }

        private JRDataSource createDataSource(Session session, Contragent contragent, Date startTime, Date endTime,
                Calendar clone, Map<String, Object> parameterMap) {
            ContragentDAOService contragentDAOService = new ContragentDAOService();
            contragentDAOService.setSession(session);
            List<ContragentCompletionReportItem> contragentCompletionReportItems = contragentDAOService.generateContragentCompletionReportItems(contragent.getIdOfContragent(),startTime,endTime);
            return new JRBeanCollectionDataSource(contragentCompletionReportItems);
        }
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    public ContragentCompletionReport() {}

    public ContragentCompletionReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ContragentCompletionReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_TODAY;
    }

    private static final Logger logger = LoggerFactory.getLogger(ContragentCompletionReport.class);
}
