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

    public static class Builder extends BasicReportJob.Builder {

        public static class RegisterReportItem {

             class SumArray extends ArrayList {
                 long[] vals;
                 public SumArray(int size) {
                     vals = new long[size];
                 } 
                 public void sum(int ind, Long number) {
                     vals[ind]+=number;
                 }
                 public Long get(int index) {
                     return vals[index];
                 }
             }

            public static final int CLASSES_1_4 = 0, CLASSES_5_11 = 1, CLASSES_COUNT=2;

            SumArray breakfastCount = new SumArray(CLASSES_COUNT); // завтрак
            SumArray breakfastCost = new SumArray(CLASSES_COUNT);
            SumArray breakfastSum = new SumArray(CLASSES_COUNT);
            SumArray lunchCount = new SumArray(CLASSES_COUNT); // обед
            SumArray lunchCost = new SumArray(CLASSES_COUNT);
            SumArray lunchSum = new SumArray(CLASSES_COUNT);
            SumArray afternoonSnackCount = new SumArray(CLASSES_COUNT); // полдник
            SumArray afternoonSnackCost = new SumArray(CLASSES_COUNT);
            SumArray afternoonSnackSum = new SumArray(CLASSES_COUNT);

            private Date date;

            public RegisterReportItem() {
                this.date = null;
            }

            public RegisterReportItem(Long date) {
                if (date!=null)
                    this.date = new Date(date);
            }

            public SumArray getBreakfastSum() {
                return breakfastSum;
            }

            public void setBreakfastSum(SumArray breakfastSum) {
                this.breakfastSum = breakfastSum;
            }

            public SumArray getLunchSum() {
                return lunchSum;
            }

            public void setLunchSum(SumArray lunchSum) {
                this.lunchSum = lunchSum;
            }

            public SumArray getAfternoonSnackSum() {
                return afternoonSnackSum;
            }

            public void setAfternoonSnackSum(SumArray afternoonSnackSum) {
                this.afternoonSnackSum = afternoonSnackSum;
            }

            public SumArray getBreakfastCount() {
                return breakfastCount;
            }

            public void setBreakfastCount(SumArray breakfastCount) {
                this.breakfastCount = breakfastCount;
            }

            public SumArray getBreakfastCost() {
                return breakfastCost;
            }

            public void setBreakfastCost(SumArray breakfastCost) {
                this.breakfastCost = breakfastCost;
            }

            public SumArray getLunchCount() {
                return lunchCount;
            }

            public void setLunchCount(SumArray lunchCount) {
                this.lunchCount = lunchCount;
            }

            public SumArray getLunchCost() {
                return lunchCost;
            }

            public void setLunchCost(SumArray lunchCost) {
                this.lunchCost = lunchCost;
            }

            public SumArray getAfternoonSnackCount() {
                return afternoonSnackCount;
            }

            public void setAfternoonSnackCount(SumArray afternoonSnackCount) {
                this.afternoonSnackCount = afternoonSnackCount;
            }

            public SumArray getAfternoonSnackCost() {
                return afternoonSnackCost;
            }

            public void setAfternoonSnackCost(SumArray afternoonSnackCost) {
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

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new RegisterReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            HashMap<Integer, RegisterReportItem> mapItems = new HashMap<Integer, RegisterReportItem>(31);
            List<RegisterReportItem> resultRows = new LinkedList<RegisterReportItem>();
            Calendar c = Calendar.getInstance();
            Query query = session.createSQLQuery("SELECT count(*), o.CreatedDate/(1000*60*60*24) xdt, sum(od.socDiscount) AS SUM1, sum(od.Qty*od.socDiscount) AS SUM2, od.menuDetailName, cg.groupname "+
                " FROM CF_ORDERS o, CF_ORDERDETAILS od, CF_CLIENTS c, CF_CLIENTGROUPS cg " +
                " WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg and cg.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND (o.idofclient=c.idofclient) AND (c.idofclientgroup=cg.idofclientgroup) AND "+
                " (od.MenuType>=:typeComplex1 OR od.MenuType<=:typeComplex10) AND (od.RPrice=0 AND od.Discount>0) AND " +
                " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) AND " +
                " (od.menuDetailName = 'Обед' OR od.menuDetailName = 'Завтрак' OR od.menuDetailName = 'Полдник')" +
                " group by xdt, od.menuDetailName, cg.groupname "+
                " order by xdt, od.menuDetailName;");

            query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
            query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
            query.setParameter("idOfOrg", org.getIdOfOrg());
            query.setParameter("typeComplex1", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("typeComplex10", OrderDetail.TYPE_COMPLEX_MAX);

            List resultList = query.list();

            for (Object o : resultList) {
                Object vals[]=(Object[])o;
                long count = Long.parseLong(vals[0].toString());
                long timeMs=Long.parseLong(vals[1].toString())*1000*60*60*24;
                c.setTimeInMillis(timeMs);
                int day = c.get(Calendar.DAY_OF_MONTH);
                long price = Long.parseLong(vals[2].toString());
                long sum = Long.parseLong(vals[3].toString());
                String detailName = (String)vals[4];
                String grName = (String)vals[5];

                RegisterReportItem resultRow = mapItems.get(day);
                if (resultRow == null) {
                    resultRow = new RegisterReportItem(timeMs);
                    mapItems.put(day, resultRow);
                    resultRows.add(resultRow);
                }

                int classNum = getClassNumber(grName)<5? 0 : 1;//(grName.charAt(0) >= '1' && grName.charAt(0) <= '4')? 0 : 1;
                if (detailName.equals("Завтрак")) {
                    resultRow.getBreakfastCost().sum(classNum, price);
                    resultRow.getBreakfastCount().sum(classNum, count);
                    resultRow.getBreakfastSum().sum(classNum, sum);
                } else if (detailName.equals("Обед")) {
                    resultRow.getLunchCost().sum(classNum, price);
                    resultRow.getLunchCount().sum(classNum, count);
                    //resultRow.getLunchCount().sum(classNum, count);
                    resultRow.getLunchSum().sum(classNum, sum);
                } else if (detailName.equals("Полдник")) {
                    resultRow.getAfternoonSnackCost().sum(classNum, price);
                    resultRow.getAfternoonSnackCount().sum(classNum, count);
                    resultRow.getAfternoonSnackSum().sum(classNum, sum);
                }
            }
            RegisterReportItem resultRow = new RegisterReportItem(null);
            for (RegisterReportItem row : resultRows) {
                for (int classNum = 0; classNum < 2; classNum++) {
                    resultRow.getBreakfastCost().sum(classNum, row.getBreakfastCost().get(classNum));
                    resultRow.getBreakfastCount().sum(classNum, row.getBreakfastCount().get(classNum));
                    resultRow.getBreakfastSum().sum(classNum, row.getBreakfastSum().get(classNum));
                    resultRow.getLunchCost().sum(classNum, row.getLunchCost().get(classNum));
                    resultRow.getLunchCount().sum(classNum, row.getLunchCount().get(classNum));
                    resultRow.getLunchSum().sum(classNum, row.getLunchSum().get(classNum));
                    resultRow.getAfternoonSnackCost().sum(classNum, row.getAfternoonSnackCost().get(classNum));
                    resultRow.getAfternoonSnackCount().sum(classNum, row.getAfternoonSnackCount().get(classNum));
                    resultRow.getAfternoonSnackSum().sum(classNum, row.getAfternoonSnackSum().get(classNum));
                }
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

    private static int getClassNumber(String groupName) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < groupName.length() && groupName.charAt(i) >= '0' && groupName.charAt(i) <= '9'; i++)
                sb.append(groupName.charAt(i));
            return Integer.parseInt(sb.toString());
        } catch (Exception e) {
            return 0;
        }

    }
}

