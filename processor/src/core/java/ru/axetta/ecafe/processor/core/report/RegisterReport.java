/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
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
public class RegisterReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder implements BasicReportJob.Builder {

        public static class RegisterReportItem {
            private long class14count1 = 0;
            private long class14count2 = 0;
            private long class14count3 = 0;
            private long class14price1 = 0;
            private long class14price2 = 0;
            private long class14price3 = 0;
            private long class14sum1 = 0;
            private long class14sum2 = 0;
            private long class14sum3 = 0;
            private long class511count1 = 0;
            private long class511count2 = 0;
            private long class511count3 = 0;
            private long class511price1 = 0;
            private long class511price2 = 0;
            private long class511price3 = 0;
            private long class511sum1 = 0;
            private long class511sum2 = 0;
            private long class511sum3 = 0;

            private Date date;

            public RegisterReportItem() {
                this.date = null;
            }

            public RegisterReportItem(Long date) {
                this.date = new Date(date);
            }

            public long getClass14count1() {
                return class14count1;
            }


            public long getClass14count2() {
                return class14count2;
            }

            public long getClass14count3() {
                return class14count3;
            }

            public long getClass14price1() {
                return class14price1;
            }

            public long getClass14price2() {
                return class14price2;
            }

            public long getClass14price3() {
                return class14price3;
            }

            public long getClass14sum1() {
                return class14sum1;
            }

            public long getClass14sum2() {
                return class14sum2;
            }

            public long getClass14sum3() {
                return class14sum3;
            }

            public long getClass511count1() {
                return class511count1;
            }

            public long getClass511count2() {
                return class511count2;
            }

            public long getClass511count3() {
                return class511count3;
            }

            public long getClass511price1() {
                return class511price1;
            }

            public long getClass511price2() {
                return class511price2;
            }

            public long getClass511price3() {
                return class511price3;
            }

            public long getClass511sum1() {
                return class511sum1;
            }

            public long getClass511sum2() {
                return class511sum2;
            }

            public long getClass511sum3() {
                return class511sum3;
            }

            public String getDate() {
                if (date == null)
                    return null;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
                return simpleDateFormat.format(date);
            }

            public void setDate(Date date) {
                this.date = date;
            }

            public void addClass14count1(long class14count1) {
                this.class14count1 += class14count1;
            }

            public void addClass14count2(long class14count2) {
                this.class14count2 += class14count2;
            }

            public void addClass14count3(long class14count3) {
                this.class14count3 += class14count3;
            }

            public void addClass14price1(long class14price1) {
                this.class14price1 += class14price1;
            }

            public void addClass14price2(long class14price2) {
                this.class14price2 += class14price2;
            }

            public void addClass14price3(long class14price3) {
                this.class14price3 += class14price3;
            }

            public void addClass14sum1(long class14sum1) {
                this.class14sum1 += class14sum1;
            }

            public void addClass14sum2(long class14sum2) {
                this.class14sum2 += class14sum2;
            }

            public void addClass14sum3(long class14sum3) {
                this.class14sum3 += class14sum3;
            }

            public void addClass511count1(long class511count1) {
                this.class511count1 += class511count1;
            }

            public void addClass511count2(long class511count2) {
                this.class511count2 += class511count2;
            }

            public void addClass511count3(long class511count3) {
                this.class511count3 += class511count3;
            }

            public void addClass511price1(long class511price1) {
                this.class511price1 += class511price1;
            }

            public void addClass511price2(long class511price2) {
                this.class511price2 += class511price2;
            }

            public void addClass511price3(long class511price3) {
                this.class511price3 += class511price3;
            }

            public void addClass511sum1(long class511sum1) {
                this.class511sum1 += class511sum1;
            }

            public void addClass511sum2(long class511sum2) {
                this.class511sum2 += class511sum2;
            }

            public void addClass511sum3(long class511sum3) {
                this.class511sum3 += class511sum3;
            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Org org, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<Object, Object> parameterMap = new HashMap<Object, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new RegisterReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar, Map<Object, Object> parameterMap) throws Exception {
            HashMap<Integer, RegisterReportItem> mapItems = new HashMap<Integer, RegisterReportItem>(31);
            List<RegisterReportItem> resultRows = new LinkedList<RegisterReportItem>();
            Calendar c = Calendar.getInstance();
            Query query = session.createSQLQuery("SELECT count(*), o.CreatedDate, sum(od.socDiscount) AS SUM1, sum(od.Qty*od.socDiscount) AS SUM2, od.menuDetailName, cg.groupname "+
                " FROM CF_ORDERS o, CF_ORDERDETAILS od, CF_CLIENTS c, CF_CLIENTGROUPS cg " +
                " WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND (o.idofclient=c.idofclient) AND (c.idofclientgroup=cg.idofclientgroup) AND "+
                " (od.MenuType>=:typeComplex1 OR od.MenuType<=:typeComplex10) AND (od.RPrice=0 AND od.Discount>0) AND " +
                " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) AND " +
                " (od.menuDetailName = 'Обед' OR od.menuDetailName = 'Завтрак' OR od.menuDetailName = 'Полдник')" +
                " group by o.CreatedDate, od.menuDetailName, cg.groupname "+
                " order by o.CreatedDate, od.menuDetailName;");

            query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
            query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
            query.setParameter("idOfOrg", org.getIdOfOrg());
            query.setParameter("typeComplex1", OrderDetail.TYPE_COMPLEX_0);
            query.setParameter("typeComplex10", OrderDetail.TYPE_COMPLEX_9);

            List resultList = query.list();

            for (Object o : resultList) {
                Object vals[]=(Object[])o;
                int count = Integer.parseInt(vals[0].toString());
                c.setTimeInMillis(Long.parseLong(vals[1].toString()));
                int day = c.get(Calendar.DAY_OF_MONTH);
                long price = Long.parseLong(vals[2].toString());
                long sum = Long.parseLong(vals[3].toString());
                String detailName = (String)vals[4];
                String grName = (String)vals[5];

                RegisterReportItem resultRow = mapItems.get(day);
                if (resultRow == null) {
                    resultRow = new RegisterReportItem(Long.parseLong(vals[1].toString()));
                    mapItems.put(day, resultRow);
                    resultRows.add(resultRow);
                }

                if (grName.charAt(0) >= '1' && grName.charAt(0) <= '4') {
                    if (detailName.equals("Завтрак")) {
                        resultRow.addClass14count1(count);
                        resultRow.addClass14price1(price);
                        resultRow.addClass14sum1(sum);
                    } else if (detailName.equals("Обед")) {
                        resultRow.addClass14count2(count);
                        resultRow.addClass14price2(price);
                        resultRow.addClass14sum2(sum);
                    } else if (detailName.equals("Полдник")) {
                        resultRow.addClass14count3(count);
                        resultRow.addClass14price3(price);
                        resultRow.addClass14sum3(sum);
                    }
                } else {
                    if (detailName.equals("Завтрак")) {
                        resultRow.addClass511count1(count);
                        resultRow.addClass511price1(price);
                        resultRow.addClass511sum1(sum);
                    } else if (detailName.equals("Обед")) {
                        resultRow.addClass511count2(count);
                        resultRow.addClass511price2(price);
                        resultRow.addClass511sum2(sum);
                    } else if (detailName.equals("Полдник")) {
                        resultRow.addClass511count3(count);
                        resultRow.addClass511price3(price);
                        resultRow.addClass511sum3(sum);
                    }
                }
            }
            // ИТОГО
            RegisterReportItem resultRow = new RegisterReportItem();
            for (RegisterReportItem tmp : resultRows) {
                resultRow.addClass14count1(tmp.getClass14count1());
                resultRow.addClass14price1(tmp.getClass14price1());
                resultRow.addClass14sum1(tmp.getClass14sum1());
                resultRow.addClass14count2(tmp.getClass14count2());
                resultRow.addClass14price2(tmp.getClass14price2());
                resultRow.addClass14sum2(tmp.getClass14sum2());
                resultRow.addClass14count3(tmp.getClass14count3());
                resultRow.addClass14price3(tmp.getClass14price3());
                resultRow.addClass14sum3(tmp.getClass14sum3());
                resultRow.addClass511count1(tmp.getClass511count1());
                resultRow.addClass511price1(tmp.getClass511price1());
                resultRow.addClass511sum1(tmp.getClass511sum1());
                resultRow.addClass511count2(tmp.getClass511count2());
                resultRow.addClass511price2(tmp.getClass511price2());
                resultRow.addClass511sum2(tmp.getClass511sum2());
                resultRow.addClass511count3(tmp.getClass511count3());
                resultRow.addClass511price3(tmp.getClass511price3());
                resultRow.addClass511sum3(tmp.getClass511sum3());
            }
            resultRows.add(resultRow);
            return new JRBeanCollectionDataSource(resultRows);
        }

    }


    public RegisterReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime,
            Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(RegisterReport.class);

    public RegisterReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new RegisterReport();
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

