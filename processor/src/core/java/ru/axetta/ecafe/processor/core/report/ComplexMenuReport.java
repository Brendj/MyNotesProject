/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("singleton")
public class ComplexMenuReport extends BasicReportForContragentJob {

    private Logger logger = LoggerFactory.getLogger(ComplexMenuReport.class);

    public static class Builder extends BasicReportForContragentJob.Builder {
        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance()
                    .getAutoReportGenerator().getReportsTemplateFilePath()
                    + ComplexMenuReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = CalendarUtils.startOfDay(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            Long idOfContragent = (contragent == null ? null : contragent.getIdOfContragent());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }
            Boolean showOnlyUnpaidItems = Boolean.valueOf(getReportProperties().getProperty("showOnlyUnpaidItems"));

            JRDataSource dataSource = createDataSource(session, contragent, startTime, endTime,
                    idOfOrgList);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, idOfContragent);
        }

        private JRDataSource createDataSource(Session session, Contragent contragent, Date startTime, Date endTime,
                List<Long> idOfOrgList) throws Exception {
            List<ContragentPreordersReportItem> result = new LinkedList<ContragentPreordersReportItem>();




            return new JRBeanCollectionDataSource(result);
        }


    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ComplexMenuReport();
    }

    @Override
    public ComplexMenuReport.Builder createBuilder(String templateFilename) {
        return new ComplexMenuReport.Builder(templateFilename);
    }
}
