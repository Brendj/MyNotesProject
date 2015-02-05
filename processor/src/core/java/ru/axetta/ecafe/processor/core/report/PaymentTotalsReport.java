/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 28.10.14
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class PaymentTotalsReport extends BasicReportForAllOrgJob {

    final public static String P_CONTRAGENT = "contragent";
    final public static String P_ORG_LIST = "orgList";
    final public static String P_HIDE_NULL_ROWS = "hideNullRows";
    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReport.class);

    public PaymentTotalsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public PaymentTotalsReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new PaymentTotalsReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + PaymentTotalsReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();

            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            JRDataSource dataSource = createDataSource(session, startTime, endTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new PaymentTotalsReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime) throws Exception {

            String idOfContragentString = reportProperties.getProperty(P_CONTRAGENT);
            Long idOfContragent = idOfContragentString == "" ? null : Long.parseLong(idOfContragentString);

            String idOfOrgsString = StringUtils.trimToEmpty(reportProperties.getProperty(P_ORG_LIST));
            List<Long> idOfOrgList = new ArrayList<Long>();
            if (idOfOrgsString.length() > 0) {
                List<String> idOfOrgListString = Arrays.asList(StringUtils.split(idOfOrgsString, ','));
                for (String idOfOrg : idOfOrgListString)
                    if (!idOfOrg.equals(""))
                        idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            boolean hideNullRows = Boolean.parseBoolean(reportProperties.getProperty(P_HIDE_NULL_ROWS, "false"));

            PaymentTotalsReportService service = new PaymentTotalsReportService(session);
            return new JRBeanCollectionDataSource(
                    service.buildReportItems(idOfContragent, idOfOrgList, startTime, endTime, hideNullRows));
        }
    }
}
