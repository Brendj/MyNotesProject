/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.03.12
 * Time: 13:46
 * Продажи за месяц
 */
@Deprecated // Отчет устарел и работает кривовато. На проде МСК не используется
public class OrgOrderByDaysReport extends BasicReportForOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Продажи за месяц";
    public static final String[] TEMPLATE_FILE_NAMES = {"OrgOrderByDaysReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{28, 29, 3, 22, 23};


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public static class ReportItem {
            private Integer id = null; // порядковый номер строки в отчете
            private String orderName = null; // наименование блюда
            private List<Integer> countRow; // лист количесва блюд
            private List<Long> sumRow; // лист стоимости

            public ReportItem() {
                countRow = new LinkedList<>();
                sumRow = new LinkedList<>();
                for (float f = 0; f < 32; f++) {
                    countRow.add(0);
                    sumRow.add(0L);
                }
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getOrderName() {
                return orderName;
            }

            public void setOrderName(String orderName) {
                this.orderName = orderName;
            }

            public List<Integer> getCountRow() {
                return countRow;
            }

            public List<Long> getSumRow() {
                return sumRow;
            }

            public void setSumRow(List<Long> sumRow) {
                this.sumRow = sumRow;
            }

            public void setCountRow(List<Integer> countRow) {
                this.countRow = countRow;
            }

            public void addSum(int ind, Long val) {
                this.sumRow.set(ind, this.sumRow.get(ind) + val);
                this.sumRow.set(31, this.sumRow.get(31) + val);
            }

            public void addSum(ReportItem arg) {
                for (int i = 0; i < arg.getSumRow().size()-1; i++) {
                    this.addSum(i, arg.getSumRow().get(i));
                }
            }

            public void addCount(int ind, Integer val) {
                this.countRow.set(ind, this.countRow.get(ind) + val);
                this.countRow.set(31, this.countRow.get(31) + val);
            }

            public void addCount(ReportItem arg) {
                for (int i = 0; i < arg.getCountRow().size()-1; i++) {
                    this.addCount(i, arg.getCountRow().get(i));
                }
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new OrgOrderByDaysReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            HashMap<Integer, ReportItem> mapItems = new HashMap<Integer, ReportItem>(31);
            List<ReportItem> resultRows = new LinkedList<ReportItem>();
            Calendar c = Calendar.getInstance();
            Query query = session.createSQLQuery(
                    "SELECT o.CreatedDate, SUM(od.Qty*od.RPrice) as SUM, SUM(od.Qty) as COUNT, od.menudetailname "
                + "FROM CF_ORDERS o "
                + "JOIN CF_ORDERDETAILS od ON od.idoforder = o.idoforder AND o.idoforg = od.idoforg "
                + "WHERE (o.idOfOrg=:idOfOrg) AND (od.RPrice > 0) "
                + "AND o.CreatedDate BETWEEN :startTime AND :endTime and o.state=0 and od.state=0 "
                + "group by o.CreatedDate, od.menudetailname "
                + "order by od.menudetailname;");

            query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
            query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
            query.setParameter("idOfOrg", org.getIdOfOrg());

            List<Object[]> resultList = query.list();
            ReportItem tmp;
            int i = 1;
            for (Object[] vals : resultList) {
                String orderName = (String)vals[3];
                Long sum = Long.parseLong(vals[1].toString());
                Integer count = Integer.parseInt(vals[2].toString());
                long time = Long.parseLong(vals[0].toString());
                tmp = mapItems.get(orderName.hashCode());
                if (tmp == null) {
                    tmp = new ReportItem();
                    tmp.setId(i++);
                    tmp.setOrderName(orderName);
                    mapItems.put(orderName.hashCode(), tmp);
                    resultRows.add(tmp);
                }
                c.setTimeInMillis(time);
                int day = c.get(Calendar.DAY_OF_MONTH);
                tmp.addCount(day, count);
                tmp.addSum(day, sum);
            }
            // ИТОГО
            ReportItem sum = new ReportItem();
            for (ReportItem reportItem : resultRows) {
                sum.addCount(reportItem);
                sum.addSum(reportItem);
            }
            resultRows.add(sum);
            return new JRBeanCollectionDataSource(resultRows);
        }
    }


    public OrgOrderByDaysReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(OrgOrderByDaysReport.class);

    public OrgOrderByDaysReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new OrgOrderByDaysReport();
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
        return REPORT_PERIOD_PREV_PREV_PREV_DAY;
    }
}


