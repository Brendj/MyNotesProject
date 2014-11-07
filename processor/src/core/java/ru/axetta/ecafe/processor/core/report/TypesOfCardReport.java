/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.utils.TypesOfCardService;
import ru.axetta.ecafe.processor.core.card.TypesOfCardReportItem;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 03.11.14
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class TypesOfCardReport {

    public static final String PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS = "includeSummaryByDistrict";

    private final static Logger logger = LoggerFactory.getLogger(TypesOfCardReport.class);

    private String htmlReport;

    public TypesOfCardReport(Date generateTime, JasperPrint jasperPrint, Date startTime) {

    }

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public TypesOfCardReport build(Session session, Date startTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, (Calendar) calendar.clone(), parameterMap));
            return new TypesOfCardReport(generateTime, jasperPrint, startTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<TypesOfCardReportItem> result = new ArrayList<TypesOfCardReportItem>();
            TypesOfCardService service = new TypesOfCardService();
            service.setSession(session);

            String withOutSummaryByDistrictParam = (String) getReportProperties().get(PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS);
            boolean withOutSummaryByDistrict = false;
            if (withOutSummaryByDistrictParam!=null) {
                withOutSummaryByDistrict = withOutSummaryByDistrictParam.trim().equalsIgnoreCase("true");
            }

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");


            return new JRBeanCollectionDataSource(result);
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public TypesOfCardReport build(Session session, Date startTime, Calendar calendar) throws Exception {
        return doBuild(session, startTime, calendar);
    }

    public TypesOfCardReport doBuild(Session session, Date startTime, Calendar calendar)
            throws Exception {
        return null;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public TypesOfCardReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public static Logger getLogger() {
        return logger;
    }
}
