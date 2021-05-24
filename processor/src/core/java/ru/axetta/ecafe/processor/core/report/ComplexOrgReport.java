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
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ComplexOrgReport extends BasicReportForContragentJob{

    private Logger logger = LoggerFactory.getLogger(ComplexOrgReport.class);

    public static class Builder extends BasicReportForContragentJob.Builder {
        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance()
                    .getAutoReportGenerator().getReportsTemplateFilePath()
                    + ComplexOrgReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = startTime == null ? null : CalendarUtils.startOfDay(startTime);
            parameterMap.put("startDate", startTime);
            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("orgList"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> orgList = new ArrayList<>();
            for (String org: stringOrgList)
                orgList.add(Long.valueOf(org));
            JRDataSource dataSource = new JRBeanCollectionDataSource(createDataSource(session, orgList));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<ComplexOrgItem> createDataSource(Session session, List<Long> orgList) throws Exception {
            List<ComplexOrgItem> orgItem = new LinkedList<>();
            String idOfOrgs = CollectionUtils.isEmpty(orgList) ? "" : " and co.idoforg in (:orgList) " ;
            String getOrg = " select co.idoforg, co.shortnameinfoservice, co.address, co.shortname, co.district "
                    + " from cf_orgs co "
                    + "where co.idoforg >= 0 "
                    + idOfOrgs ;
            Query queryOrg = session.createSQLQuery(getOrg);
            if(!CollectionUtils.isEmpty(orgList))
                queryOrg.setParameterList("orgList", orgList);
            List<Object[]> orgData = queryOrg.list();
            if (CollectionUtils.isEmpty(orgData))
                throw new Exception("Нет данных для построения отчета");

            for (Object[] data: orgData)
                orgItem.add(new ComplexOrgItem(data[0].toString(), data[1].toString(), data[2].toString(),
                        data[3].toString(), data[4].toString()));
            return orgItem;
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
        return new ComplexOrgReport();
    }

    @Override
    public ComplexOrgReport.Builder createBuilder(String templateFilename) {
        return new ComplexOrgReport.Builder(templateFilename);
    }

}
