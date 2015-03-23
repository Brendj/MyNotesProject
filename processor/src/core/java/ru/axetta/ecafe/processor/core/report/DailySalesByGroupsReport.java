/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Отчет по реализации
 */

public class DailySalesByGroupsReport extends BasicReportForOrgJob {

    private static final Logger logger = LoggerFactory.getLogger(DailySalesByGroupsReport.class);

    public static final String PARAM_GROUP_BY_MENU_GROUP = "groupByMenuGroup";
    public static final String PARAM_MENU_GROUPS = "menuGroups";
    public static final String PARAM_INCLUDE_COMPLEX = "includeComplex";

    private String htmlReport;

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public static class Builder extends BasicReportJob.Builder {

        public static class MealRow implements Comparable<MealRow> {
            //private int menuOrigin = 0;
            private final String name;
            private final long count;
            private final long price, sum;
            private String originName = null;

            public MealRow(String originName, String name, long count, long price, long sum) {
                this.name = name;
                this.count = count;
                this.price = price;
                this.sum = sum;
                this.originName = originName;
            }

            public String getOriginName() {
                return originName;
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
        private boolean exportToHTML = false;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + DailySalesByGroupsReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
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
            if (!exportToHTML) {
                return new DailySalesByGroupsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, org.getIdOfOrg());
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                return new DailySalesByGroupsReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                        jasperPrint, startTime, endTime, org.getIdOfOrg()).setHtmlReport(os.toString("UTF-8"));
            }
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<MealRow> mealRows = new LinkedList<MealRow>();

            Object[] vals;

            String includeComplexParam = (String) getReportProperties().get(PARAM_INCLUDE_COMPLEX);
            boolean includeComplex = true;
            if (includeComplexParam!=null) {
                includeComplex = includeComplexParam.trim().equalsIgnoreCase("true");
            }

            LinkedList<TotalDataRow> totalDataRows = new LinkedList<TotalDataRow>();

            LinkedList<SubReportDataRow> subReportDataRowsPay = new LinkedList<SubReportDataRow>();

            List mealsList = null;
            MealRow mealRow;

            long totalCount=0, totalSum=0;
            long totalBuffetCount=0, totalBuffetSum=0;

            // буфет

            String groupByField = "MenuOrigin";
            boolean groupByMenuOrigin = true;
            String menuGroupsCondition = null;
            String typeConditionsValue = (String)getReportProperties().get(PARAM_GROUP_BY_MENU_GROUP);
            if (typeConditionsValue != null && typeConditionsValue.replace(",", "").trim().equalsIgnoreCase("true")) {
                groupByField = "MenuGroup";
                groupByMenuOrigin = false;

                String menuGroups = (String)getReportProperties().get(PARAM_MENU_GROUPS);
                if (menuGroups!=null && menuGroups.length()>0) {
                    menuGroupsCondition = "";
                    String[] g = menuGroups.split(",");
                    for (String s : g) {
                        if (s.length()>0) menuGroupsCondition+="'"+s.replaceAll("'", "")+"',";
                    }
                    if (menuGroupsCondition.length()==0) menuGroupsCondition=null;
                    else menuGroupsCondition=menuGroupsCondition.substring(0, menuGroupsCondition.length()-1);
                }
            }

            Query mealsQuery = session.createSQLQuery(String.format("SELECT od.%s, od.MenuDetailName, SUM(od.qty) as qtySum, od.RPrice, SUM(od.Qty*od.RPrice)" +
                    " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                    "(od.MenuType=:typeDish) and o.state=0 and od.state=0 AND " +
                    "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) %s"
                    + "GROUP BY od.%s, od.MenuDetailName, od.RPrice "
                    + "ORDER BY od.%s, od.MenuDetailName", groupByField, menuGroupsCondition==null?"":"AND od.MenuGroup IN ("+menuGroupsCondition+") ", groupByField, groupByField));

            mealsQuery.setParameter("idOfOrg", org.getIdOfOrg());
            mealsQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            mealsQuery.setParameter("startTime", startTime.getTime());
            mealsQuery.setParameter("endTime", endTime.getTime());

            mealsList = mealsQuery.list();

            List<SubReportDataRow> subReportDataRowsBuffet = new LinkedList<SubReportDataRow>();

            int menuOrigin = 0;
            String menuGroup = null;
            String menuName;
            String currentTotalGroup="";

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
                if (groupByMenuOrigin)
                    mealRow = new MealRow(OrderDetail.getMenuOriginAsString(menuOrigin), menuName, count, rPrice, sum);
                else
                    mealRow = new MealRow(menuGroup , menuName, count, rPrice, sum);
                mealRows.add(mealRow);
                ////
                if (!currentTotalGroup.equals(mealRow.getOriginName())) {
                    if (!currentTotalGroup.equals("")) {
                        subReportDataRowsBuffet.add(new SubReportDataRow("   Буфет: "+currentTotalGroup, totalCount, totalSum));
                    }
                    totalCount=0; totalSum=0;
                    currentTotalGroup = mealRow.getOriginName();
                }
                totalBuffetCount+=count;
                totalCount+=count;
                totalBuffetSum+=sum;
                totalSum+=sum;
            }
            if (!currentTotalGroup.equals("")) {
                subReportDataRowsBuffet.add(new SubReportDataRow("   Буфет: "+currentTotalGroup, totalCount, totalSum));
            }

            totalDataRows.add(new TotalDataRow("Буфет ВСЕГО: ", totalBuffetCount, totalBuffetSum, subReportDataRowsBuffet));

            long totalUnPaidCount = 0, totalUnPaidSum = 0;
            long totalPayCount=0, totalPaySum=0;

            totalCount=0;
            totalSum=0;

            if (includeComplex) {
                Query complexQuery_1 = session.createSQLQuery("SELECT od.MenuType, SUM(od.Qty) as qtySum, od.RPrice, SUM(od.Qty*od.RPrice), od.menuDetailName, od.discount, od.socdiscount, o.grantsum" +
                        " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND" +
                        " (od.MenuType>=:typeComplexMin AND od.MenuType<=:typeComplexMax) AND (od.rPrice>0) AND " +
                        " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0 "
                        + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName, od.menuDetailName, od.discount, od.socdiscount, o.grantsum");

                complexQuery_1.setParameter("idOfOrg", org.getIdOfOrg());
                complexQuery_1.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
                complexQuery_1.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
                //complexQuery_1.setParameter("typeComplex1", OrderDetail.TYPE_COMPLEX_0); // централизованный 11-18
                //complexQuery_1.setParameter("typeComplex2", OrderDetail.TYPE_COMPLEX_1); // централизованный 7-10
                //complexQuery_1.setParameter("typeComplex4", OrderDetail.TYPE_COMPLEX_4); // локальный 11-18
                //complexQuery_1.setParameter("typeComplex5", OrderDetail.TYPE_COMPLEX_5); // локальный 7-10
                //complexQuery_1.setParameter("typeComplex10", OrderDetail.TYPE_COMPLEX_9); // свободный выбоh
                complexQuery_1.setParameter("startTime", startTime.getTime());
                complexQuery_1.setParameter("endTime", endTime.getTime());

                mealsList = complexQuery_1.list();

                List<MealRow> payMealRows = new LinkedList<MealRow>();
                String menuGroupPay = "Платное комплексное питание";
                for (Object o : mealsList) {
                    vals=(Object[])o;
                    //int menuOrigin = Integer.parseInt(vals[0].toString());
                    String menuNamePay = vals[4].toString(); // od.MenuType
                    long count = Long.parseLong(vals[1].toString());
                    long rPrice = vals[2]==null?0:Long.parseLong(vals[2].toString());
                    long sum = vals[3]==null?0:Long.parseLong(vals[3].toString());
                    long discount = vals[5]==null?0:Long.parseLong(vals[5].toString());
                    long socdiscount = vals[6]==null?0:Long.parseLong(vals[6].toString());
                    long grant = vals[7]==null?0:Long.parseLong(vals[7].toString());
                    long tradeDiscount = (long)( ((double)(discount - socdiscount)/(double)(discount + socdiscount + rPrice + grant))*100 );
                    if (tradeDiscount > 0)
                        menuNamePay = String.format("%s (скидка %d%%)", menuNamePay, tradeDiscount);
                    //MealRow mealRow = new MealRow(menuGroup, menuName, count, rPrice, sum);
                    totalCount+=count;
                    totalPayCount+=count;
                    totalSum+=sum;
                    totalPaySum+=sum;
                    mealRow = new MealRow(menuGroupPay, menuNamePay, count, rPrice, sum);
                    payMealRows.add(mealRow);
                }
                subReportDataRowsPay.add(new SubReportDataRow(menuGroupPay, totalCount, totalSum));
                
                Collections.sort(payMealRows);
                mealRows.addAll(payMealRows);

                long totalPayAndBuffetCount = totalBuffetCount + totalPayCount;
                long totalPayAndBuffetSum = totalBuffetSum + totalPaySum;

                totalDataRows.add(new TotalDataRow("Платное комплексное питание + Буфет ВСЕГО: ", totalPayAndBuffetCount, totalPayAndBuffetSum, subReportDataRowsPay));

                //// бесплатное питание

                List<SubReportDataRow> subReportDataRowsUnPaid = new LinkedList<SubReportDataRow>();

                Query freeComplexQuery1 = session.createSQLQuery("SELECT od.MenuType, SUM(od.Qty) as qtySum, od.RPrice, SUM(od.Qty*(od.RPrice+od.socdiscount)), od.menuDetailName, od.socdiscount " +
                        " FROM CF_ORDERS o,CF_ORDERDETAILS od WHERE (o.idOfOrg=:idOfOrg AND od.idOfOrg=:idOfOrg) AND (o.IdOfOrder=od.IdOfOrder) AND " +
                        " (od.MenuType>=:typeComplexMin OR od.MenuType<=:typeComplexMax) AND (od.RPrice=0 AND od.Discount>0) AND " +
                        " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) and o.state=0 and od.state=0"
                        + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName, od.socdiscount");

                freeComplexQuery1.setParameter("idOfOrg", org.getIdOfOrg());
                freeComplexQuery1.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
                freeComplexQuery1.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
                freeComplexQuery1.setParameter("startTime", startTime.getTime());
                freeComplexQuery1.setParameter("endTime", endTime.getTime());

                mealsList = freeComplexQuery1.list();

                totalCount=0;
                totalSum=0;

                String menuGroupUnPaid = "Бесплатное комплексное питание";
                for (Object o : mealsList) {
                    vals=(Object[])o;
                    //int menuOrigin = Integer.parseInt(vals[0].toString());
                    String menuNameUnPaid = vals[4].toString(); // od.MenuType
                    long count = Long.parseLong(vals[1].toString());
                    long rPrice = vals[2]==null?0:Long.parseLong(vals[2].toString());
                    long sum = vals[3]==null?0:Long.parseLong(vals[3].toString());
                    long socdiscount = vals[5]==null?0:Long.parseLong(vals[5].toString());
                    totalCount+=count;
                    totalUnPaidCount+=count;
                    totalSum+=sum;
                    totalUnPaidSum+=sum;
                    mealRow = new MealRow(menuGroupUnPaid, menuNameUnPaid, count, rPrice+socdiscount, sum);
                    mealRows.add(mealRow);
                }
                subReportDataRowsUnPaid.add(new SubReportDataRow(menuGroupUnPaid, totalCount, totalSum));

                long totalByAllCount = totalBuffetCount + totalPayCount + totalUnPaidCount;
                long totalByAllSum = totalBuffetSum + totalPaySum +totalUnPaidSum;

                totalDataRows.add(new TotalDataRow("ОБЩЕЕ: ", totalByAllCount, totalByAllSum, subReportDataRowsUnPaid));
            }
            
            ///
            parameterMap.put("totalsData", new JRBeanCollectionDataSource(totalDataRows));
            parameterMap.put("SUBREPORT_DIR", RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());

            return new JRBeanCollectionDataSource(mealRows);
        }

    }
    
    public static class TotalDataRow {
        String totalOriginName;
        long totalCount;
        long totalSum;
        List<SubReportDataRow> subReportDataRows;
        String subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();

        public TotalDataRow(String totalOriginName, long totalCount, long totalSum, List<SubReportDataRow> subReportDataRows) {
            this.totalOriginName = totalOriginName;
            this.totalCount = totalCount;
            this.totalSum = totalSum;
            this.subReportDataRows = subReportDataRows;
        }

        public String getTotalOriginName() {
            return totalOriginName;
        }

        public void setTotalOriginName(String totalOriginName) {
            this.totalOriginName = totalOriginName;
        }

        public long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        public long getTotalSum() {
            return totalSum;
        }

        public void setTotalSum(long totalSum) {
            this.totalSum = totalSum;
        }

        public List<SubReportDataRow> getSubReportDataRows() {
            return subReportDataRows;
        }

        public void setSubReportDataRows(List<SubReportDataRow> subReportDataRows) {
            this.subReportDataRows = subReportDataRows;
        }

        public String getSubReportDir() {
            return subReportDir;
        }

        public void setSubReportDir(String subReportDir) {
            this.subReportDir = subReportDir;
        }
    }

    public static class SubReportDataRow {
        String originName;
        Long count;
        Long sum;

        public SubReportDataRow(String originName, Long count, Long sum) {
            this.originName = originName;
            this.count = count;
            this.sum = sum;
        }

        public String getOriginName() {
            return originName;
        }

        public void setOriginName(String originName) {
            this.originName = originName;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public Long getSum() {
            return sum;
        }

        public void setSum(Long sum) {
            this.sum = sum;
        }
    }

    public DailySalesByGroupsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public DailySalesByGroupsReport() {}

    public DailySalesByGroupsReport setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

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