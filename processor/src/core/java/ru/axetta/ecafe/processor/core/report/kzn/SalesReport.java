/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 14.03.11
 * Time: 0:14
 * Отчет по продажам
 */
public class SalesReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public static class Builder extends BasicReportJob.Builder {

        public static class MealRow {
            private final int menuOrigin;
            private final String name;
            private final long count;
            private final long price, sum;

            public MealRow(int menuOrigin, String name, long count, long price, long sum) {
                this.name = name;
                this.count = count;
                this.price = price;
                this.sum = sum;
                this.menuOrigin = menuOrigin;
            }

            public int getMenuOrigin() {
                return menuOrigin;
            }

            public String getOriginName() {
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
            parameterMap.put("idOfOrg", getOrg().getIdOfOrg());
            parameterMap.put("orgName", getOrg().getOfficialName());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, getOrg(), startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new SalesReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, getOrg().getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<MealRow> mealRows = new LinkedList<MealRow>();

            //// горячее питание
            Query complexQuery_0 = session.createSQLQuery("SELECT COUNT(*), SUM(od.Qty*od.RPrice) as SUM1, SUM(od.Qty*od.Discount) as SUM2" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeComplex1 OR od.MenuType=:typeComplex2 OR od.MenuType=:typeComplex4 OR od.MenuType=:typeComplex5 OR od.MenuType=:typeComplex10) AND (od.rPrice>0) AND " +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0 ");

            complexQuery_0.setParameter("idOfOrg", org.getIdOfOrg());
            complexQuery_0.setParameter("typeComplex1", OrderDetail.TYPE_COMPLEX_0); // централизованный 11-18
            complexQuery_0.setParameter("typeComplex2", OrderDetail.TYPE_COMPLEX_1); // централизованный 7-10
            complexQuery_0.setParameter("typeComplex4", OrderDetail.TYPE_COMPLEX_4); // локальный 11-18
            complexQuery_0.setParameter("typeComplex5", OrderDetail.TYPE_COMPLEX_5); // локальный 7-10
            complexQuery_0.setParameter("typeComplex10", OrderDetail.TYPE_COMPLEX_9); // свободный выбоh
            complexQuery_0.setParameter("startTime", startTime.getTime());
            complexQuery_0.setParameter("endTime", endTime.getTime());

            Object[] vals=(Object[])complexQuery_0.uniqueResult();
            parameterMap.put("complexCount_0", Long.parseLong(vals[0].toString()));
            parameterMap.put("complexSum_0", vals[1]==null?0:Long.parseLong(vals[1].toString()));
            parameterMap.put("complexDiscount_0", vals[2]==null?0:Long.parseLong(vals[2].toString()));

            //// бесплатное питание
            Query freeComplexQuery = session.createSQLQuery("SELECT COUNT(*), SUM(od.Qty*od.Discount)" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType>=:typeComplex1 OR od.MenuType<=:typeComplex10) AND (od.RPrice=0 AND od.Discount>0) AND " +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0 ");

            freeComplexQuery.setParameter("idOfOrg", org.getIdOfOrg());
            freeComplexQuery.setParameter("typeComplex1", OrderDetail.TYPE_COMPLEX_MIN);
            freeComplexQuery.setParameter("typeComplex10", OrderDetail.TYPE_COMPLEX_MAX);
            freeComplexQuery.setParameter("startTime", startTime.getTime());
            freeComplexQuery.setParameter("endTime", endTime.getTime());

            vals=(Object[])freeComplexQuery.uniqueResult();
            parameterMap.put("freeComplexCount", Long.parseLong(vals[0].toString()));
            parameterMap.put("freeComplexSum", vals[1]==null?0:Long.parseLong(vals[1].toString()));

            //// ГПД
            Query complexQuery_1 = session.createSQLQuery("SELECT COUNT(*), SUM(od.Qty*od.RPrice) as SUM1, SUM(od.Qty*od.Discount) as SUM2" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeComplex3 OR od.MenuType=:typeComplex4) AND (od.rPrice<>od.Discount) AND " +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0 ");

            complexQuery_1.setParameter("idOfOrg", org.getIdOfOrg());
            complexQuery_1.setParameter("typeComplex3", OrderDetail.TYPE_COMPLEX_2); // ГПД централизованный
            complexQuery_1.setParameter("typeComplex4", OrderDetail.TYPE_COMPLEX_3); // ГПД локальный
            complexQuery_1.setParameter("startTime", startTime.getTime());
            complexQuery_1.setParameter("endTime", endTime.getTime());

            vals=(Object[])complexQuery_1.uniqueResult();
            parameterMap.put("complexCount_1", Long.parseLong(vals[0].toString()));
            parameterMap.put("complexSum_1", vals[1]==null?0:Long.parseLong(vals[1].toString()));
            parameterMap.put("complexDiscount_1", vals[2]==null?0:Long.parseLong(vals[2].toString()));

            //// Буфет собственное
            Query mealsSelfProductionQuery = session.createSQLQuery("SELECT COUNT(*), SUM(od.Qty*od.RPrice) as SUM1, SUM(od.Qty*od.Discount) as SUM2" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeDish) AND (od.MenuOrigin=:menuOrigin0 OR od.MenuOrigin=:menuOrigin1 OR od.MenuOrigin=:menuOrigin2) AND" +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0 ");

            mealsSelfProductionQuery.setParameter("idOfOrg", org.getIdOfOrg());
            mealsSelfProductionQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            mealsSelfProductionQuery.setParameter("menuOrigin0", OrderDetail.PRODUCT_OWN);
            mealsSelfProductionQuery.setParameter("menuOrigin1", OrderDetail.PRODUCT_CENTRALIZE);
            mealsSelfProductionQuery.setParameter("menuOrigin2", OrderDetail.PRODUCT_CENTRALIZE_COOK);
            mealsSelfProductionQuery.setParameter("startTime", startTime.getTime());
            mealsSelfProductionQuery.setParameter("endTime", endTime.getTime());
            vals=(Object[])mealsSelfProductionQuery.uniqueResult();
            parameterMap.put("buffetSelfProductionCount", Long.parseLong(vals[0].toString()));
            parameterMap.put("buffetSelfProductionSum", vals[1]==null?0:Long.parseLong(vals[1].toString()));
            parameterMap.put("buffetSelfProductionDiscount", vals[2]==null?0:Long.parseLong(vals[2].toString()));

            //// Буфет закупочный
            Query mealsBoughtQuery = session.createSQLQuery("SELECT COUNT(*), SUM(od.Qty*od.RPrice) as SUM1, SUM(od.Qty*od.Discount) as SUM2" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeDish) AND (od.MenuOrigin=:menuOrigin) AND" +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0 ");

            mealsBoughtQuery.setParameter("idOfOrg", org.getIdOfOrg());
            mealsBoughtQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            mealsBoughtQuery.setParameter("menuOrigin", OrderDetail.PRODUCT_PURCHASE);
            mealsBoughtQuery.setParameter("startTime", startTime.getTime());
            mealsBoughtQuery.setParameter("endTime", endTime.getTime());
            vals=(Object[])mealsBoughtQuery.uniqueResult();
            parameterMap.put("buffetBoughtCount", Long.parseLong(vals[0].toString()));
            parameterMap.put("buffetBoughtSum", vals[1]==null?0:Long.parseLong(vals[1].toString()));
            parameterMap.put("buffetBoughtDiscount", vals[2]==null?0:Long.parseLong(vals[2].toString()));

            //// ИТОГО (без бесплатных)
            Query totalQuery = session.createSQLQuery("SELECT COUNT(*), SUM(od.Qty*od.RPrice) as SUM1, SUM(od.Qty*od.Discount) as SUM2" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeDish OR (od.MenuType>=:complexType0 AND od.MenuType<=:complexType9)) AND (od.rPrice>0) AND" +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0 ");

            totalQuery.setParameter("idOfOrg", org.getIdOfOrg());
            totalQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            totalQuery.setParameter("complexType0", OrderDetail.TYPE_COMPLEX_MIN);
            totalQuery.setParameter("complexType9", OrderDetail.TYPE_COMPLEX_MAX);
            totalQuery.setParameter("startTime", startTime.getTime());
            totalQuery.setParameter("endTime", endTime.getTime());
            vals=(Object[])totalQuery.uniqueResult();
            parameterMap.put("totalCount", Long.parseLong(vals[0].toString()));
            parameterMap.put("totalSum", vals[1]==null?0:Long.parseLong(vals[1].toString()));
            parameterMap.put("totalDiscount", vals[2]==null?0:Long.parseLong(vals[2].toString()));

            ////
            Query mealsQuery = session.createSQLQuery("SELECT od.MenuOrigin, od.MenuDetailName, COUNT(*), od.RPrice, SUM(od.Qty*od.RPrice)" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeDish)  and o.state=0 and od.state=0 AND " +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) GROUP BY od.MenuOrigin, od.MenuDetailName, od.RPrice ORDER BY od.MenuOrigin, od.MenuDetailName");

            mealsQuery.setParameter("idOfOrg", org.getIdOfOrg());
            mealsQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            mealsQuery.setParameter("startTime", startTime.getTime());
            mealsQuery.setParameter("endTime", endTime.getTime());

            List mealsList = mealsQuery.list();

            for (Object o : mealsList) {
                vals=(Object[])o;
                int menuOrigin = Integer.parseInt(vals[0].toString());
                String menuName = (String)vals[1];
                long count = Long.parseLong(vals[2].toString());
                long rPrice = vals[3]==null?0:Long.parseLong(vals[3].toString());
                long sum = vals[4]==null?0:Long.parseLong(vals[4].toString());
                MealRow mealRow = new MealRow(menuOrigin, menuName, count, rPrice, sum);
                mealRows.add(mealRow);
            }

            return new JRBeanCollectionDataSource(mealRows);
        }

    }

    public SalesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime, Date endTime,
            Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(SalesReport.class);

    public SalesReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new SalesReport();
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

