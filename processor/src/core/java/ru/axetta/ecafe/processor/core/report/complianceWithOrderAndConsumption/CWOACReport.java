/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.complianceWithOrderAndConsumption;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 12.09.13
 * Time: 14:24
 * "Отчет по соответствию заказа и потребления резервной группе"
 */

public class CWOACReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(CWOACReport.class);

    public CWOACReport() {
    }

    public CWOACReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new CWOACReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private String templateFilename;

        private Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            Date generateTime = new Date();
            parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
            parameterMap.put("IS_IGNORE_PAGINATION", true);
            JasperPrint jp = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, startTime, endTime));
            Date generateEndTime = new Date();
            return new CWOACReport(generateTime,
                    generateEndTime.getTime() - generateTime.getTime(), jp, startTime, endTime);
        }

        @SuppressWarnings("unchecked")
        private JRDataSource createDataSource(Session session, Date startTime, Date endTime) {
            List<CWOACItem> items = new ArrayList<CWOACItem>();
            Query query = session.createSQLQuery(
                    "SELECT \n" +
                    "o.idoforg, \n" +
                    "o.shortname, \n" +
                    "sum(CASE WHEN ord.ordertype = 4 THEN 1 ELSE 0 END) AS consumedCount, \n" +
                    "sum(CASE WHEN ord.ordertype = 6 THEN 1 ELSE 0 END) AS writtenOffCount \n" +
                    "FROM cf_orgs o JOIN cf_orders ord ON (o.idoforg = ord.idoforg) \n" +
                    "WHERE ord.createddate >= :beginDate AND ord.createddate <= :endDate \n" +
                    "GROUP BY o.idoforg, o.shortname \n" +
                    "ORDER BY o.shortname")
                    .setParameter("beginDate", startTime.getTime())
                    .setParameter("endDate", endTime.getTime());
            List<Object[]> res = (List<Object[]>) query.list();
            for (Object[] record : res) {
                CWOACItem item = new CWOACItem(record[1].toString(), 0L, ((BigInteger) record[2]).longValue(),
                        ((BigInteger) record[3]).longValue());
                items.add(item);
            }
            return new JRBeanCollectionDataSource(items);
        }
    }
}
