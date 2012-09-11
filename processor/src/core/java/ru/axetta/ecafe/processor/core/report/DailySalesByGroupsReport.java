/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 14.03.11
 * Time: 0:14
 * To change this template use File | Settings | File Templates.
 */
public class DailySalesByGroupsReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder implements BasicReportJob.Builder {

        public static class MealRow implements Comparable<MealRow> {
            private int menuOrigin = 0;
            private final String name;
            private final long count;
            private final long price, sum;
            private String originName = null;

            public MealRow(int menuOrigin, String name, long count, long price, long sum) {
                this.name = name;
                this.count = count;
                this.price = price;
                this.sum = sum;
                this.menuOrigin = menuOrigin;
            }

            public MealRow(String originName, String name, long count, long price, long sum) {
                this.name = name;
                this.count = count;
                this.price = price;
                this.sum = sum;
                this.originName = originName;
            }

            public int getMenuOrigin() {
                return menuOrigin;
            }

            public String getOriginName() {
                if (originName!=null)
                    return originName;
                return OrderDetail.getMenuOriginAsString(menuOrigin);
            }

            public String getName() {
                return name;
            }

            public long getCount() {
                return count;
            }

            public long getPrice() {
                return price;
            }

            public long getSum() {
                return sum;
            }

            @Override
            public int compareTo(MealRow mealRow) {
                return this.getName().compareTo(mealRow.getName());
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
            parameterMap.put("idOfOrg", org.getIdOfOrg());
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new DailySalesByGroupsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar, Map<Object, Object> parameterMap) throws Exception {
            List<MealRow> mealRows = new LinkedList<MealRow>();

            Object[] vals;

            Query complexQuery_1 = session.createSQLQuery("SELECT od.MenuType, COUNT(*), od.RPrice, SUM(od.Qty*od.RPrice), od.menuDetailName, od.discount, od.socdiscount, o.grantsum" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    " (od.MenuType=:typeComplex1 OR od.MenuType=:typeComplex2 OR od.MenuType=:typeComplex4 OR od.MenuType=:typeComplex5 OR od.MenuType=:typeComplex10) AND (od.rPrice>0) AND " +
                    " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) "
                    + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName, od.menuDetailName, od.discount, od.socdiscount, o.grantsum");

            complexQuery_1.setParameter("idOfOrg", org.getIdOfOrg());
            complexQuery_1.setParameter("typeComplex1", OrderDetail.TYPE_COMPLEX_0); // централизованный 11-18
            complexQuery_1.setParameter("typeComplex2", OrderDetail.TYPE_COMPLEX_1); // централизованный 7-10
            complexQuery_1.setParameter("typeComplex4", OrderDetail.TYPE_COMPLEX_4); // локальный 11-18
            complexQuery_1.setParameter("typeComplex5", OrderDetail.TYPE_COMPLEX_5); // локальный 7-10
            complexQuery_1.setParameter("typeComplex10", OrderDetail.TYPE_COMPLEX_9); // свободный выбор
            complexQuery_1.setParameter("startTime", startTime.getTime());
            complexQuery_1.setParameter("endTime", endTime.getTime());

            List mealsList = complexQuery_1.list();

            MealRow mealRow;
            List<MealRow> payMealRows = new LinkedList<MealRow>();
            for (Object o : mealsList) {
                vals=(Object[])o;
                //int menuOrigin = Integer.parseInt(vals[0].toString());
                String menuGroup = "Платное комплексное питание";
                String menuName = vals[4].toString(); // od.MenuType
                long count = Long.parseLong(vals[1].toString());
                long rPrice = vals[2]==null?0:Long.parseLong(vals[2].toString());
                long sum = vals[3]==null?0:Long.parseLong(vals[3].toString());
                long discount = vals[5]==null?0:Long.parseLong(vals[5].toString());
                long socdiscount = vals[6]==null?0:Long.parseLong(vals[6].toString());
                long grant = vals[7]==null?0:Long.parseLong(vals[7].toString());
                long tradeDiscount = (long)( ((double)(discount - socdiscount)/(double)(discount + socdiscount + rPrice + grant))*100 );
                if (tradeDiscount > 0)
                    menuName = String.format("%s (скидка %d%%)", menuName, tradeDiscount);
                //MealRow mealRow = new MealRow(menuGroup, menuName, count, rPrice, sum);
                mealRow = new MealRow(menuGroup, menuName, count, rPrice, sum);
                payMealRows.add(mealRow);
            }
            Collections.sort(payMealRows);
            mealRows.addAll(payMealRows);

            //// бесплатное питание
            Query freeComplexQuery1 = session.createSQLQuery("SELECT od.MenuType, COUNT(*), od.RPrice, SUM(od.Qty*od.RPrice), od.menuDetailName " +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND " +
                    " (od.MenuType>=:typeComplex1 OR od.MenuType<=:typeComplex10) AND (od.RPrice=0 AND od.Discount>0) AND " +
                    " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) "
                    + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName");

            freeComplexQuery1.setParameter("idOfOrg", org.getIdOfOrg());
            freeComplexQuery1.setParameter("typeComplex1", OrderDetail.TYPE_COMPLEX_0);
            freeComplexQuery1.setParameter("typeComplex10", OrderDetail.TYPE_COMPLEX_9);
            freeComplexQuery1.setParameter("startTime", startTime.getTime());
            freeComplexQuery1.setParameter("endTime", endTime.getTime());

            mealsList = freeComplexQuery1.list();

            for (Object o : mealsList) {
                vals=(Object[])o;
                //int menuOrigin = Integer.parseInt(vals[0].toString());
                String menuGroup = "Бесплатное комплексное питание";
                String menuName = vals[4].toString(); // od.MenuType
                long count = Long.parseLong(vals[1].toString());
                long rPrice = vals[2]==null?0:Long.parseLong(vals[2].toString());
                long sum = vals[3]==null?0:Long.parseLong(vals[3].toString());
                mealRow = new MealRow(menuGroup, menuName, count, rPrice, sum);
                mealRows.add(mealRow);
            }

            String groupByField = "MenuOrigin";
            boolean groupByMenuOrigin = true;
            String typeConditionsValue = (String) RuleProcessor.getReportProperties().get("groupByMenuGroup");
            if (typeConditionsValue != null && typeConditionsValue.replace(",", "").trim().equals("true")) {
                groupByField = "MenuGroup";
                groupByMenuOrigin = false;
            }

            Query mealsQuery = session.createSQLQuery(String.format("SELECT od.%s, od.MenuDetailName, COUNT(*), od.RPrice, SUM(od.Qty*od.RPrice)" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeDish) AND " +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) "
                    + "GROUP BY od.%s, od.MenuDetailName, od.RPrice "
                    + "ORDER BY od.%s, od.MenuDetailName", groupByField, groupByField, groupByField));

            mealsQuery.setParameter("idOfOrg", org.getIdOfOrg());
            mealsQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            mealsQuery.setParameter("startTime", startTime.getTime());
            mealsQuery.setParameter("endTime", endTime.getTime());

            mealsList = mealsQuery.list();

            int menuOrigin = 0;
            String menuGroup = null;
            String menuName;
            for (Object o : mealsList) {
                vals=(Object[])o;
                if (groupByMenuOrigin)
                    menuOrigin = Integer.parseInt(vals[0].toString());
                else
                    menuGroup = vals[0].toString();
                menuName = (String)vals[1];
                long count = Long.parseLong(vals[2].toString());
                long rPrice = vals[3]==null?0:Long.parseLong(vals[3].toString());
                long sum = vals[4]==null?0:Long.parseLong(vals[4].toString());
                if (groupByMenuOrigin==true)
                    mealRow = new MealRow(menuOrigin, menuName, count, rPrice, sum);
                else
                    mealRow = new MealRow(menuGroup , menuName, count, rPrice, sum);
                mealRows.add(mealRow);
            }

            return new JRBeanCollectionDataSource(mealRows);
        }

    }


    public DailySalesByGroupsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private static final Logger logger = LoggerFactory.getLogger(DailySalesByGroupsReport.class);

    public DailySalesByGroupsReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new DailySalesByGroupsReport();
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

