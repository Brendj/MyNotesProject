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

            public static final int CLASSES_1_4 = 0, CLASSES_5_11 = 1;

            List<Object> breakfastCount = new ArrayList<Object>(2); // завтрак
            List<Object> breakfastCost = new ArrayList<Object>(2);
            List<Object> lunchCount = new ArrayList<Object>(2); // обед
            List<Object> lunchCost = new ArrayList<Object>(2);
            List<Object> afternoonSnackCount = new ArrayList<Object>(2); // полдник
            List<Object> afternoonSnackCost = new ArrayList<Object>(2);

            private Date date;

            public RegisterReportItem() {
                this.date = null;
            }

            public RegisterReportItem(Long date) {
                this.date = new Date(date);
            }

            public List<Object> getBreakfastCount() {
                return breakfastCount;
            }

            public void setBreakfastCount(List<Object> breakfastCount) {
                this.breakfastCount = breakfastCount;
            }

            public List<Object> getBreakfastCost() {
                return breakfastCost;
            }

            public void setBreakfastCost(List<Object> breakfastCost) {
                this.breakfastCost = breakfastCost;
            }

            public List<Object> getLunchCount() {
                return lunchCount;
            }

            public void setLunchCount(List<Object> lunchCount) {
                this.lunchCount = lunchCount;
            }

            public List<Object> getLunchCost() {
                return lunchCost;
            }

            public void setLunchCost(List<Object> lunchCost) {
                this.lunchCost = lunchCost;
            }

            public List<Object> getAfternoonSnackCount() {
                return afternoonSnackCount;
            }

            public void setAfternoonSnackCount(List<Object> afternoonSnackCount) {
                this.afternoonSnackCount = afternoonSnackCount;
            }

            public List<Object> getAfternoonSnackCost() {
                return afternoonSnackCost;
            }

            public void setAfternoonSnackCost(List<Object> afternoonSnackCost) {
                this.afternoonSnackCost = afternoonSnackCost;
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
                    for (int classNum = 0; classNum < 2; classNum++) {
                        resultRow.getAfternoonSnackCost().add(classNum, 0F);
                        resultRow.getAfternoonSnackCount().add(classNum, 0);
                        resultRow.getBreakfastCost().add(classNum, 0F);
                        resultRow.getBreakfastCount().add(classNum, 0);
                        resultRow.getLunchCost().add(classNum, 0F);
                        resultRow.getLunchCount().add(classNum, 0);
                    }
                    mapItems.put(day, resultRow);
                    resultRows.add(resultRow);
                }

                int classNum = (grName.charAt(0) >= '1' && grName.charAt(0) <= '4')? 0 : 1;
                if (detailName.equals("Завтрак")) {
                    resultRow.getBreakfastCost().add(classNum, Float.valueOf(String.valueOf(price/100.0)));
                    resultRow.getBreakfastCount().add(classNum, count);
                } else if (detailName.equals("Обед")) {
                    resultRow.getLunchCost().add(classNum, Float.valueOf(String.valueOf(price/100.0)));
                    resultRow.getLunchCount().add(classNum, count);
                } else if (detailName.equals("Полдник")) {
                    resultRow.getAfternoonSnackCost().add(classNum, Float.valueOf(String.valueOf(price/100.0)));
                    resultRow.getAfternoonSnackCount().add(classNum, count);
                }
            }
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

