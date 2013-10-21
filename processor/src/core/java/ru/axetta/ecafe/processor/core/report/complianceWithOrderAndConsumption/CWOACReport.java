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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
            Map<Long, CWOACItem> itemsByOrg = new HashMap<Long, CWOACItem>();
            Query query = session.createSQLQuery("SELECT \n" +
                    "o.idoforg, \n" +
                    "o.shortname, \n" +
                    "o.district, \n" +
                    "sum(coalesce(grp.totalcount / 1000, 0)) AS requestCount \n" +
                    "FROM cf_orgs o LEFT JOIN cf_goods_requests gr on (gr.orgOwner = o.idoforg) \n" +
                    "LEFT JOIN cf_goods_requests_positions grp ON (gr.IdOfGoodsRequest = grp.IdOfGoodsRequest) \n" +
                    "LEFT JOIN cf_goods g ON (g.IdOfGood = grp.IdOfGood) \n" +
                    "WHERE gr.doneDate BETWEEN :startDate AND :endDate \n" +
                    "GROUP BY o.idoforg, o.shortname, o.district")
                    .setParameter("startDate", startTime.getTime())
                    .setParameter("endDate", endTime.getTime());
            List<Object[]> res = (List<Object[]>) query.list();
            for (Object[] record : res) {
                CWOACItem item = new CWOACItem(record[1].toString(), StringUtils.defaultString((String) record[2]),
                        ((BigDecimal) record[3]).longValue());
                itemsByOrg.put(((BigInteger) record[0]).longValue(), item);
            }
            query = session.createSQLQuery("SELECT \n" +
                    "ord.idoforg, \n" +
                    "sum(CASE WHEN ord.ordertype = 4 OR ord.ordertype = 6 THEN det.qty ELSE 0 END) AS consumedCount, \n" +
                    "sum(CASE WHEN ord.ordertype = 6 THEN det.qty ELSE 0 END) AS writtenOffCount \n" +
                    "FROM cf_orders ord JOIN CF_OrderDetails det ON (ord.idoforder = det.idoforder AND ord.idoforg = det.idoforg) \n" +
                    "JOIN cf_goods g ON (g.IdOfGood = det.IdOfGood) \n" +
                    "WHERE ord.createddate >= :beginDate AND ord.createddate <= :endDate \n" +
                    "AND g.IdOfGood IN (SELECT g2.idofgood \n" +
                    "                   FROM cf_goods_requests gr JOIN cf_goods_requests_positions grp ON (gr.IdOfGoodsRequest = grp.IdOfGoodsRequest) \n" +
                    "                                             JOIN cf_goods g2 ON (g2.IdOfGood = grp.IdOfGood) \n" +
                    "                   WHERE gr.doneDate BETWEEN :startDate AND :endDate2 AND gr.orgOwner = ord.idoforg)" +
                    "GROUP BY ord.idoforg \n" +
                    "ORDER BY ord.idoforg")
                    .setParameter("beginDate", startTime.getTime())
                    .setParameter("endDate", endTime.getTime())
                    .setParameter("startDate", startTime.getTime())
                    .setParameter("endDate2", endTime.getTime());
            res = (List<Object[]>) query.list();
            for (Object[] record : res) {
                CWOACItem item = itemsByOrg.get(((BigInteger) record[0]).longValue());
                if (item != null) {
                    item.setConsumedCount(((BigInteger) record[1]).longValue());
                    item.setWrittenOffCount(((BigInteger) record[2]).longValue());
                }
            }
            SortedSet<CWOACItem> beanColl = new TreeSet<CWOACItem>(new Comparator<CWOACItem>() {
                @Override
                public int compare(CWOACItem o1, CWOACItem o2) {
                    int res = o1.getDistrict().compareTo(o2.getDistrict());
                    if (res != 0)
                        return res;
                    return o1.getOrgName().compareTo(o2.getOrgName());
                }
            });
            beanColl.addAll(itemsByOrg.values());
            return new JRBeanCollectionDataSource(beanColl);
        }
    }
}
