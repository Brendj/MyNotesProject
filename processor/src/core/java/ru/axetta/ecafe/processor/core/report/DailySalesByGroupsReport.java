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
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Отчет по реализации
 */

public class DailySalesByGroupsReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Отчет по реализации";
    public static final String[] TEMPLATE_FILE_NAMES = {"DailySalesByGroupsReport.jasper",
    "DailySalesByGroupsReport_subreport_items.jasper", "DailySalesByGroupsReport_subreport_main.jasper",
    "DailySalesByGroupsReport_subreport_totals.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 23, 29, 25, 26, 27, 32};


    private static final Logger logger = LoggerFactory.getLogger(DailySalesByGroupsReport.class);

    public static final String PARAM_GROUP_BY_MENU_GROUP = "groupByMenuGroup";
    public static final String PARAM_MENU_GROUPS = "menuGroups";
    public static final String PARAM_INCLUDE_COMPLEX = "includeComplex";

    private String htmlReport;

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    public static class Builder extends BasicReportJob.Builder {

        public static class MealRow {

            //private int menuOrigin = 0;

            private String originName;

            private long mealCountGroupTotal;
            private long mealSumGroupTotal;

            private Long mealCountSumByCashTotal;
            private Long mealSumSumByCashTotal;

            private Long mealCountSumByCardTotal;
            private Long mealSumSumByCardTotal;

            private Long mealCountSumByCashAndSumByCardTotal;
            private Long mealSumSumByCardAndSumByCashTotal;

            private List<SubReportMealRow> subReportMealRowList;

            public MealRow() {
            }

            public MealRow(String originName, long mealCountGroupTotal, long mealSumGroupTotal,
                    Long mealCountSumByCashTotal, Long mealSumSumByCashTotal, Long mealCountSumByCardTotal,
                    Long mealSumSumByCardTotal, Long mealCountSumByCashAndSumByCardTotal,
                    Long mealSumSumByCardAndSumByCashTotal, List<SubReportMealRow> subReportMealRowList) {
                this.originName = originName;
                this.mealCountGroupTotal = mealCountGroupTotal;
                this.mealSumGroupTotal = mealSumGroupTotal;
                this.mealCountSumByCashTotal = mealCountSumByCashTotal;
                this.mealSumSumByCashTotal = mealSumSumByCashTotal;
                this.mealCountSumByCardTotal = mealCountSumByCardTotal;
                this.mealSumSumByCardTotal = mealSumSumByCardTotal;
                this.mealCountSumByCashAndSumByCardTotal = mealCountSumByCashAndSumByCardTotal;
                this.mealSumSumByCardAndSumByCashTotal = mealSumSumByCardAndSumByCashTotal;
                this.subReportMealRowList = subReportMealRowList;
            }

            public MealRow(String menuOriginAsString, List<SubReportMealRow> subReportMealRowList) {
                this.originName = menuOriginAsString;
                this.subReportMealRowList = subReportMealRowList;
            }

            public String getOriginName() {
                return originName;
            }

            public void setOriginName(String originName) {
                this.originName = originName;
            }

            public long getMealCountGroupTotal() {
                return mealCountGroupTotal;
            }

            public void setMealCountGroupTotal(long mealCountGroupTotal) {
                this.mealCountGroupTotal = mealCountGroupTotal;
            }

            public long getMealSumGroupTotal() {
                return mealSumGroupTotal;
            }

            public void setMealSumGroupTotal(long mealSumGroupTotal) {
                this.mealSumGroupTotal = mealSumGroupTotal;
            }

            public Long getMealCountSumByCashTotal() {
                return mealCountSumByCashTotal;
            }

            public void setMealCountSumByCashTotal(Long mealCountSumByCashTotal) {
                this.mealCountSumByCashTotal = mealCountSumByCashTotal;
            }

            public Long getMealSumSumByCashTotal() {
                return mealSumSumByCashTotal;
            }

            public void setMealSumSumByCashTotal(Long mealSumSumByCashTotal) {
                this.mealSumSumByCashTotal = mealSumSumByCashTotal;
            }

            public Long getMealCountSumByCardTotal() {
                return mealCountSumByCardTotal;
            }

            public void setMealCountSumByCardTotal(Long mealCountSumByCardTotal) {
                this.mealCountSumByCardTotal = mealCountSumByCardTotal;
            }

            public Long getMealSumSumByCardTotal() {
                return mealSumSumByCardTotal;
            }

            public void setMealSumSumByCardTotal(Long mealSumSumByCardTotal) {
                this.mealSumSumByCardTotal = mealSumSumByCardTotal;
            }

            public Long getMealCountSumByCashAndSumByCardTotal() {
                return mealCountSumByCashAndSumByCardTotal;
            }

            public void setMealCountSumByCashAndSumByCardTotal(Long mealCountSumByCashAndSumByCardTotal) {
                this.mealCountSumByCashAndSumByCardTotal = mealCountSumByCashAndSumByCardTotal;
            }

            public Long getMealSumSumByCardAndSumByCashTotal() {
                return mealSumSumByCardAndSumByCashTotal;
            }

            public void setMealSumSumByCardAndSumByCashTotal(Long mealSumSumByCardAndSumByCashTotal) {
                this.mealSumSumByCardAndSumByCashTotal = mealSumSumByCardAndSumByCashTotal;
            }

            public List<SubReportMealRow> getSubReportMealRowList() {
                return subReportMealRowList;
            }

            public void setSubReportMealRowList(List<SubReportMealRow> subReportMealRowList) {
                this.subReportMealRowList = subReportMealRowList;
            }
        }

        public class SubReportMealRow implements Comparable<SubReportMealRow> {

            private String name;
            private Integer count;
            private Long price;
            private Long sum;
            private Long countCash;
            private Long countCard;
            private Long sumCash;
            private Long sumCard;
            private Long countCashAndCard;
            private Long sumCashAndCard;
            private HashMap<String, SubReportMealRow> dishesList;

            public SubReportMealRow(String name, Integer count, Long price, Long sum,
                    Long countCash, Long countCard, Long sumCash, Long sumCard, Long countCashAndCard, Long sumCashAndCard) {
                this.name = name;
                this.count = count;
                this.price = price;
                this.sum = sum;
                this.setCountCash(countCash);
                this.setCountCard(countCard);
                this.setSumCash(sumCash);
                this.setSumCard(sumCard);
                this.setCountCashAndCard(countCashAndCard);
                this.setSumCashAndCard(sumCashAndCard);
                this.dishesList = new HashMap<String, SubReportMealRow>();
            }

            public String getName() {
                return name;
            }

            public Integer getCount() {
                return count;
            }

            public Long getPrice() {
                return price;
            }

            public Long getSum() {
                return sum;
            }

            public void setCount(Integer count) {
                this.count = count;
            }

            public void setPrice(Long price) {
                this.price = price;
            }

            public void setSum(Long sum) {
                this.sum = sum;
            }

            @Override
            public int compareTo(SubReportMealRow mealRow) {
                return this.getName().compareTo(mealRow.getName());
            }

            public long getCountCash() {
                return countCash;
            }

            public void setCountCash(Long countCash) {
                this.countCash = countCash;
            }

            public Long getCountCard() {
                return countCard;
            }

            public void setCountCard(Long countCard) {
                this.countCard = countCard;
            }

            public Long getSumCash() {
                return sumCash;
            }

            public void setSumCash(Long sumCash) {
                this.sumCash = sumCash;
            }

            public Long getSumCard() {
                return sumCard;
            }

            public void setSumCard(Long sumCard) {
                this.sumCard = sumCard;
            }

            public Long getCountCashAndCard() {
                return countCashAndCard;
            }

            public void setCountCashAndCard(Long countCashAndCard) {
                this.countCashAndCard = countCashAndCard;
            }

            public Long getSumCashAndCard() {
                return sumCashAndCard;
            }

            public void setSumCashAndCard(Long sumCashAndCard) {
                this.sumCashAndCard = sumCashAndCard;
            }

            public HashMap<String, SubReportMealRow> getDishesList() {
                return dishesList;
            }

            public void setDishesList(HashMap<String, SubReportMealRow> dishesList) {
                this.dishesList = dishesList;
            }
        }

        public class TotalMealRow {
            private long count, sum;
            private String flag;

            public TotalMealRow() {
            }

            public TotalMealRow(long count, long sum, String flag) {
                this.count = count;
                this.sum = sum;
                this.flag = flag;
            }

            public long getCount() {
                return count;
            }

            public void setCount(Integer count) {
                this.count = count;
            }

            public long getSum() {
                return sum;
            }

            public void setSum(long sum) {
                this.sum = sum;
            }

            public String getFlag() {
                return flag;
            }

            public void setFlag(String flag) {
                this.flag = flag;
            }
        }

        private class TotalRow {

            List<TotalMealRow> totalMealRowList;

            private TotalRow() {
            }

            private TotalRow(List<TotalMealRow> totalMealRowList) {
                this.totalMealRowList = totalMealRowList;
            }

            private List<TotalMealRow> getTotalMealRowList() {
                return totalMealRowList;
            }

            private void setTotalMealRowList(List<TotalMealRow> totalMealRowList) {
                this.totalMealRowList = totalMealRowList;
            }
        }

        private final String templateFilename;
        private boolean exportToHTML = false;

        public Builder(String templateFilename) {
            this.templateFilename =  RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + DailySalesByGroupsReport.class.getSimpleName() + ".jasper";;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath() + DailySalesByGroupsReport.class.getSimpleName() + ".jasper";
            exportToHTML = true;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            if(orgShortItemList == null) {
                orgShortItemList = new ArrayList<OrgShortItem>();
                orgShortItemList.add(new OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
            }
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            fillOrgNameParameter(parameterMap);
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
                        jasperPrint, startTime, endTime, orgShortItemList.get(0).getIdOfOrg());
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
                        jasperPrint, startTime, endTime, orgShortItemList.get(0).getIdOfOrg()).setHtmlReport(os.toString("UTF-8"));
            }
        }

        private void fillOrgNameParameter(Map<String, Object> params) {
            StringBuilder paramValue = new StringBuilder();
            paramValue.append(orgShortItemList.size() == 1 ? "Учреждение: " : "Учреждения: ");
            for(OrgShortItem org : orgShortItemList) {
                paramValue.append(String.format("%s, ", org.getShortName()));
            }
            params.put("orgName", paramValue.substring(0, paramValue.length() - 2));//without last ", "
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            String includeComplexParam = (String) getReportProperties().get(PARAM_INCLUDE_COMPLEX);
            boolean includeComplex = true;
            if ((includeComplexParam != null)&&(!includeComplexParam.isEmpty())) {
                includeComplex = includeComplexParam.trim().equalsIgnoreCase("true");
            }

            LinkedList<TotalDataRow> totalDataRows = new LinkedList<TotalDataRow>();
            LinkedList<SubReportDataRow> subReportDataRowsPay = new LinkedList<SubReportDataRow>();
            List<Object[]> mealsList;
            List<Object> menuOriginList;
            SubReportMealRow subReportMealRow;
            long totalCount, totalSum;
            long totalCountCash, totalCountCard, totalSumCash, totalSumCard, totalCountMixed, totalSumMixed;
            long totalBuffetCount = 0, totalBuffetSum = 0;
            long mealCountGroupTotal, mealSumGroupTotal, mealCountSumByCashTotal, mealSumSumByCashTotal, mealCountSumByCardTotal, mealSumSumByCardTotal, mealCountSumByCashAndSumByCardTotal, mealSumSumByCardAndSumByCashTotal;
            long mealCountGroupTotalCash, mealCountGroupTotalCard, mealSumGroupTotalCash, mealSumGroupTotalCard, mealCountGroupMixed, mealSumGroupMixed;

            // буфет
            String groupByField = "MenuOrigin";
            boolean groupByMenuOrigin = true;
            String menuGroupsCondition = null;
            String typeConditionsValue = (String) getReportProperties().get(PARAM_GROUP_BY_MENU_GROUP);
            if (typeConditionsValue != null && typeConditionsValue.replace(",", "").trim().equalsIgnoreCase("true")) {
                groupByField = "MenuGroup";
                groupByMenuOrigin = false;

                String menuGroups = (String) getReportProperties().get(PARAM_MENU_GROUPS);
                if (menuGroups != null && menuGroups.length() > 0) {
                    menuGroupsCondition = "";
                    String[] g = menuGroups.split(",");
                    for (String s : g) {
                        if (s.length() > 0) {
                            menuGroupsCondition += "'" + s.replaceAll("'", "") + "',";
                        }
                    }
                    if (menuGroupsCondition.length() == 0) {
                        menuGroupsCondition = null;
                    } else {
                        menuGroupsCondition = menuGroupsCondition.substring(0, menuGroupsCondition.length() - 1);
                    }
                }
            }

            Map<String, MealRow> mealRowHashMap = new HashMap<String, MealRow>();
            Map<String, TotalRow> mealRowMap = new HashMap<String, TotalRow>();
            int menuOrigin1 = 0;
            String menuGroup1 = null;

            Query menuOriginQuery = session
                    .createSQLQuery(String.format("SELECT DISTINCT od.%s FROM CF_ORDERS o,CF_ORDERDETAILS od "
                            + " WHERE (o.idOfOrg in (:idOfOrg) AND od.idOfOrg in (:idOfOrg)) AND (o.IdOfOrder=od.IdOfOrder) AND (od.MenuType=:typeDish) and o.state=0 and od.state=0 AND "
                            + " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) ", groupByField));

            menuOriginQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            menuOriginQuery.setParameter("startTime", startTime.getTime());
            menuOriginQuery.setParameter("endTime", endTime.getTime());

            List<Long> idOrgs = new LinkedList<>();
            for (OrgShortItem orgItem : orgShortItemList) {
                idOrgs.add(orgItem.getIdOfOrg());
            }
            menuOriginQuery.setParameterList("idOfOrg", idOrgs);

            menuOriginList = menuOriginQuery.list();

            for (Object val : menuOriginList) {
                if (groupByMenuOrigin) {
                    menuOrigin1 = Integer.parseInt(val.toString());
                } else {
                    menuGroup1 = val.toString();
                }

                if (groupByMenuOrigin) {
                    MealRow mealRowItem = new MealRow(OrderDetail.getMenuOriginAsString(menuOrigin1),
                            new ArrayList<SubReportMealRow>());
                    TotalRow totalRow = new TotalRow(new ArrayList<TotalMealRow>());

                    mealRowHashMap.put(OrderDetail.getMenuOriginAsString(menuOrigin1), mealRowItem);
                    mealRowMap.put(OrderDetail.getMenuOriginAsString(menuOrigin1), totalRow);
                } else {
                    MealRow mealRowIt = new MealRow(menuGroup1, new ArrayList<SubReportMealRow>());
                    TotalRow totalRow = new TotalRow(new ArrayList<TotalMealRow>());

                    mealRowHashMap.put(menuGroup1, mealRowIt);
                    mealRowMap.put(menuGroup1, totalRow);
                }
            }
            String orgAdditionalCondition = createOrgAdditionalConditionByOrgList(orgShortItemList);

            Query mealsQuery = session.createSQLQuery(String.format(
                    "SELECT od.%s, od.MenuDetailName, SUM(od.qty) as qtySum, od.RPrice, SUM(od.Qty*od.RPrice), " +
                            "CASE WHEN (o.sumbycash <> 0) AND (o.sumbycard = 0) THEN 'cash' "
                            + "WHEN (o.sumbycard <> 0) AND (o.sumbycash = 0) THEN 'card' "
                            + "WHEN (o.sumbycard <> 0) AND (o.sumbycash <> 0) THEN 'mixed' "
                            + "ELSE 'other' END AS flag " +
                            " FROM CF_ORDERS o,CF_ORDERDETAILS od "
                            + "WHERE %s AND (o.IdOfOrder=od.IdOfOrder) AND (od.MenuType=:typeDish) and o.state=0 and od.state=0 AND "
                            + "(o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) %s GROUP BY od.%s, od.MenuDetailName, od.RPrice, flag "
                            + "ORDER BY od.%s, od.MenuDetailName", groupByField, orgAdditionalCondition,
                    menuGroupsCondition == null ? "" : "AND od.MenuGroup IN (" + menuGroupsCondition + ") ",
                    groupByField, groupByField));

            mealsQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            mealsQuery.setParameter("startTime", startTime.getTime());
            mealsQuery.setParameter("endTime", endTime.getTime());

            mealsList = mealsQuery.list();

            List<SubReportDataRow> subReportDataRowsBuffet = new LinkedList<SubReportDataRow>();
            int menuOrigin = 0;
            String menuGroup = null;
            String menuName;

            for (Object[] vals : mealsList) {
                if (groupByMenuOrigin) {
                    menuOrigin = Integer.parseInt(vals[0].toString());
                } else {
                    menuGroup = vals[0].toString();
                }
                menuName = (String) vals[1];
                Integer count = Integer.parseInt(vals[2].toString());
                long rPrice = vals[3] == null ? 0 : Long.parseLong(vals[3].toString());
                long sum = vals[4] == null ? 0 : Long.parseLong(vals[4].toString());

                String payType = (String)vals[5];
                long countCash = payType.equals("cash") ? count : 0;
                long countCard = payType.equals("card") ? count : 0;
                long sumCash = payType.equals("cash") ? sum : 0;
                long sumCard = payType.equals("card") ? sum : 0;
                long countMixed = payType.equals("mixed") ? count : 0;
                long sumMixed = payType.equals("mixed") ? sum : 0;

                if (groupByMenuOrigin) {
                    subReportMealRow = new SubReportMealRow(menuName, count, rPrice, sum, countCash, countCard, sumCash, sumCard, countMixed, sumMixed);
                    mealRowHashMap.get(OrderDetail.getMenuOriginAsString(menuOrigin)).getSubReportMealRowList()
                            .add(subReportMealRow);
                } else {
                    subReportMealRow = new SubReportMealRow(menuName, count, rPrice, sum, countCash, countCard, sumCash, sumCard, countMixed, sumMixed);
                    mealRowHashMap.get(menuGroup).getSubReportMealRowList().add(subReportMealRow);
                }
            }

            Set<String> mealRowHashMapKeys = mealRowHashMap.keySet();

            for (String mealRowKey : mealRowHashMapKeys) {
                List<SubReportMealRow> subReportDataRowList = mealRowHashMap.get(mealRowKey).getSubReportMealRowList();

                mealCountGroupTotal = 0;
                mealSumGroupTotal = 0;
                mealCountGroupTotalCash = 0;
                mealCountGroupTotalCard = 0;
                mealSumGroupTotalCash = 0;
                mealSumGroupTotalCard = 0;
                mealCountGroupMixed = 0;
                mealSumGroupMixed = 0;

                for (SubReportMealRow item : subReportDataRowList) {
                    mealCountGroupTotal += item.getCount();
                    mealSumGroupTotal += item.getSum();
                    mealCountGroupTotalCash += item.getCountCash();
                    mealCountGroupTotalCard += item.getCountCard();
                    mealSumGroupTotalCash += item.getSumCash();
                    mealSumGroupTotalCard += item.getSumCard();
                    mealCountGroupMixed += item.getCountCashAndCard();
                    mealSumGroupMixed += item.getSumCashAndCard();
                }
                totalBuffetCount += mealCountGroupTotal;
                totalBuffetSum += mealSumGroupTotal;

                mealRowHashMap.get(mealRowKey).setMealCountGroupTotal(mealCountGroupTotal);
                mealRowHashMap.get(mealRowKey).setMealSumGroupTotal(mealSumGroupTotal);

                subReportDataRowsBuffet
                        .add(new SubReportDataRow("   Буфет: " + mealRowKey, mealCountGroupTotal, mealSumGroupTotal,
                                mealCountGroupTotalCash, mealCountGroupTotalCard, mealSumGroupTotalCash, mealSumGroupTotalCard,
                                mealCountGroupMixed, mealSumGroupMixed));
            }
            totalDataRows
                    .add(new TotalDataRow("Буфет ВСЕГО: ", totalBuffetCount, totalBuffetSum, subReportDataRowsBuffet));

            long totalUnPaidCount = 0, totalUnPaidSum = 0;
            long totalUnPaidTempClientsCount = 0, totalUnPaidTempClientsSum = 0;
            long totalPayCount = 0, totalPaySum = 0;
            Long totalPreorderCount = 0L;
            Long totalPreorderSum = 0L;

            totalCount = 0;
            totalSum = 0;
            totalCountCash = 0;
            totalCountCard = 0;
            totalSumCash = 0;
            totalSumCard = 0;
            totalCountMixed = 0;
            totalSumMixed = 0;

            List<SubReportDataRow> subReportDataRowsUnPaid = new LinkedList<SubReportDataRow>();
            if (includeComplex) {
                Query complexQuery_1 = session.createSQLQuery(
                        "SELECT od.MenuType, SUM(od.Qty) AS qtySum, od.RPrice, SUM(od.Qty*od.RPrice), od.menuDetailName, od.discount, od.socdiscount, o.grantsum, " +
                        "CASE WHEN (o.sumbycash <> 0) AND (o.sumbycard = 0) THEN 'cash' "
                                + "WHEN (o.sumbycard <> 0) AND (o.sumbycash = 0) THEN 'card' "
                                + "WHEN (o.sumbycard <> 0) AND (o.sumbycash <> 0) THEN 'mixed' "
                                + "ELSE 'other' END AS flag "
                                + "FROM CF_ORDERS o "
                                + "INNER JOIN CF_ORDERDETAILS od ON o.IdOfOrder=od.IdOfOrder and o.idoforg = od.idoforg "
                                + "LEFT JOIN cf_preorder_linkod pl ON pl.idoforder = o.idoforder"
                                + " WHERE "
                                + orgAdditionalCondition
                                + " AND (od.MenuType>=:typeComplexMin AND od.MenuType<=:typeComplexMax) AND (od.rPrice>0) AND "
                                + " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) AND o.state=0 AND od.state=0 AND "
                                + " pl.idofpreorderlinkod IS NULL "
                                + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName, od.menuDetailName, od.discount, od.socdiscount, o.grantsum, flag");

                complexQuery_1.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
                complexQuery_1.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
                complexQuery_1.setParameter("startTime", startTime.getTime());
                complexQuery_1.setParameter("endTime", endTime.getTime());

                mealsList = complexQuery_1.list();

                String menuGroupPay = "Платное комплексное питание";

                MealRow mealRowPay = new MealRow(menuGroupPay, new ArrayList<SubReportMealRow>());
                mealRowHashMap.put(menuGroupPay, mealRowPay);

                for (Object[] vals : mealsList) {
                    //int menuOrigin = Integer.parseInt(vals[0].toString());
                    String menuNamePay = vals[4].toString(); // od.MenuType
                    Integer count = Integer.parseInt(vals[1].toString());
                    long rPrice = vals[2] == null ? 0 : Long.parseLong(vals[2].toString());
                    long sum = vals[3] == null ? 0 : Long.parseLong(vals[3].toString());
                    long discount = vals[5] == null ? 0 : Long.parseLong(vals[5].toString());
                    long socdiscount = vals[6] == null ? 0 : Long.parseLong(vals[6].toString());
                    long grant = vals[7] == null ? 0 : Long.parseLong(vals[7].toString());
                    long tradeDiscount = (long) (
                            ((double) (discount - socdiscount) / (double) (discount + socdiscount + rPrice + grant))
                                    * 100);
                    if (tradeDiscount > 0) {
                        menuNamePay = String.format("%s (скидка %d%%)", menuNamePay, tradeDiscount);
                    }
                    String payType = (String)vals[8];
                    long countCash = payType.equals("cash") ? count : 0;
                    long countCard = payType.equals("card") ? count : 0;
                    long sumCash = payType.equals("cash") ? sum : 0;
                    long sumCard = payType.equals("card") ? sum : 0;
                    long countMixed = payType.equals("mixed") ? count : 0;
                    long sumMixed = payType.equals("mixed") ? sum : 0;

                    totalCount += count;
                    totalPayCount += count;
                    totalSum += sum;
                    totalPaySum += sum;
                    totalCountCash += countCash;
                    totalCountCard += countCard;
                    totalSumCash += sumCash;
                    totalSumCard += sumCard;
                    totalCountMixed += countMixed;
                    totalSumMixed += sumMixed;

                    subReportMealRow = new SubReportMealRow(menuNamePay, count, rPrice, sum, totalCountCash, totalCountCard, totalSumCash, totalSumCard,
                            totalCountMixed, totalSumMixed);
                    mealRowHashMap.get(menuGroupPay).getSubReportMealRowList().add(subReportMealRow);
                }

                mealRowHashMap.get(menuGroupPay).setMealCountGroupTotal(totalPayCount);
                mealRowHashMap.get(menuGroupPay).setMealSumGroupTotal(totalPaySum);

                subReportDataRowsPay.add(new SubReportDataRow(menuGroupPay, totalCount, totalSum, totalCountCash, totalCountCard, totalSumCash, totalSumCard,
                        totalCountMixed, totalSumMixed));

                //// бесплатное питание по своей ОО
                Query freeComplexQuery1 = session.createSQLQuery(
                        "SELECT od.MenuType, SUM(od.Qty) AS qtySum, od.RPrice, SUM(od.Qty*(od.RPrice+od.socdiscount)), od.menuDetailName, od.socdiscount "
                                + "FROM CF_ORDERS o,CF_ORDERDETAILS od, cf_clients c "
                                + String.format("WHERE %s AND (o.IdOfOrder=od.IdOfOrder) "
                                + "and c.idofclient = o.idofclient "
                                + "and c.idoforg in (select friendlyorg from cf_friendly_organization where currentorg = o.idoforg)"
                                + "AND (od.MenuType>=:typeComplexMin OR od.MenuType<=:typeComplexMax) AND (od.RPrice=0 AND od.Discount>0) "
                                + "AND (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) AND o.state=0 AND od.state=0 ", orgAdditionalCondition)
                                + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName, od.socdiscount");

                freeComplexQuery1.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
                freeComplexQuery1.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
                freeComplexQuery1.setParameter("startTime", startTime.getTime());
                freeComplexQuery1.setParameter("endTime", endTime.getTime());

                mealsList = freeComplexQuery1.list();

                totalCount = 0;
                totalSum = 0;

                String menuGroupUnPaid = "Бесплатное комплексное питание";

                MealRow mealRowUnPaid = new MealRow(menuGroupUnPaid, new ArrayList<SubReportMealRow>());
                mealRowHashMap.put(menuGroupUnPaid, mealRowUnPaid);

                for (Object[] vals : mealsList) {
                    //int menuOrigin = Integer.parseInt(vals[0].toString());
                    String menuNameUnPaid = vals[4].toString(); // od.MenuType
                    Integer count = Integer.parseInt(vals[1].toString());
                    long rPrice = vals[2] == null ? 0 : Long.parseLong(vals[2].toString());
                    long sum = vals[3] == null ? 0 : Long.parseLong(vals[3].toString());
                    long socdiscount = vals[5] == null ? 0 : Long.parseLong(vals[5].toString());
                    totalCount += count;
                    totalUnPaidCount += count;
                    totalSum += sum;
                    totalUnPaidSum += sum;

                    subReportMealRow = new SubReportMealRow(menuNameUnPaid, count, rPrice + socdiscount, sum, null, null, null, null, null, null);
                    mealRowHashMap.get(menuGroupUnPaid).getSubReportMealRowList().add(subReportMealRow);
                }

                mealRowHashMap.get(menuGroupUnPaid).setMealCountGroupTotal(totalUnPaidCount);
                mealRowHashMap.get(menuGroupUnPaid).setMealSumGroupTotal(totalUnPaidSum);

                subReportDataRowsUnPaid.add(new SubReportDataRow(menuGroupUnPaid, totalCount, totalSum, totalCountCash, totalCountCard, totalSumCash, totalSumCard,
                        totalCountMixed, totalSumMixed));

                /*long totalByAllCount = totalBuffetCount + totalPayCount + totalUnPaidCount;
                long totalByAllSum = totalBuffetSum + totalPaySum + totalUnPaidSum;

                totalDataRows.add(new TotalDataRow("ОБЩЕЕ: ", totalByAllCount, totalByAllSum, subReportDataRowsUnPaid));*/

                //бесплатное пропитание временно обуччающихся другой ООО

                Query freeComplexQueryTempClients = session.createSQLQuery(
                        "SELECT od.MenuType, SUM(od.Qty) AS qtySum, od.RPrice, SUM(od.Qty*(od.RPrice+od.socdiscount)), od.menuDetailName, od.socdiscount "
                                + "FROM CF_ORDERS o,CF_ORDERDETAILS od, cf_clients c "
                                + String.format("WHERE %s AND (o.IdOfOrder=od.IdOfOrder) "
                                + "and c.idofclient = o.idofclient "
                                + "and c.idoforg not in (select friendlyorg from cf_friendly_organization where currentorg = o.idoforg)"
                                + "AND (od.MenuType>=:typeComplexMin OR od.MenuType<=:typeComplexMax) "
                                + "AND (od.RPrice=0 AND od.Discount>0) AND (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) AND o.state=0 AND od.state=0 ", orgAdditionalCondition)
                                + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName, od.socdiscount");

                freeComplexQueryTempClients.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
                freeComplexQueryTempClients.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
                freeComplexQueryTempClients.setParameter("startTime", startTime.getTime());
                freeComplexQueryTempClients.setParameter("endTime", endTime.getTime());

                mealsList = freeComplexQueryTempClients.list();

                totalCount = 0;
                totalSum = 0;

                String menuGroupUnPaidTempClients = "Бесплатное комплексное питание временно обучающихся другой ОО";

                MealRow mealRowUnPaidTempClients = new MealRow(menuGroupUnPaidTempClients, new ArrayList<SubReportMealRow>());
                mealRowHashMap.put(menuGroupUnPaidTempClients, mealRowUnPaidTempClients);

                for (Object[] vals : mealsList) {
                    String menuNameUnPaidTempClients = vals[4].toString(); // od.MenuType
                    Integer count = Integer.parseInt(vals[1].toString());
                    long rPrice = vals[2] == null ? 0 : Long.parseLong(vals[2].toString());
                    long sum = vals[3] == null ? 0 : Long.parseLong(vals[3].toString());
                    long socdiscount = vals[5] == null ? 0 : Long.parseLong(vals[5].toString());
                    totalCount += count;
                    totalUnPaidTempClientsCount += count;
                    totalSum += sum;
                    totalUnPaidTempClientsSum += sum;

                    subReportMealRow = new SubReportMealRow(menuNameUnPaidTempClients, count, rPrice + socdiscount, sum, null, null, null, null, null, null);
                    mealRowHashMap.get(menuGroupUnPaidTempClients).getSubReportMealRowList().add(subReportMealRow);
                }

                mealRowHashMap.get(menuGroupUnPaidTempClients).setMealCountGroupTotal(totalUnPaidTempClientsCount);
                mealRowHashMap.get(menuGroupUnPaidTempClients).setMealSumGroupTotal(totalUnPaidTempClientsSum);

                subReportDataRowsUnPaid.add(new SubReportDataRow(menuGroupUnPaidTempClients, totalCount, totalSum, totalCountCash, totalCountCard, totalSumCash, totalSumCard,
                        totalCountMixed, totalSumMixed));
            }

            // предзаказы
            Query preordersQuery = session.createSQLQuery(
                    "SELECT od.MenuType, od.Qty, od.RPrice, od.Qty*od.RPrice AS sum, od.menuDetailName, od.discount, od.socdiscount, o.grantsum, "
                     + "    CASE WHEN (o.sumbycash <> 0) AND (o.sumbycard = 0) THEN 'cash' "
                     + "        WHEN (o.sumbycard <> 0) AND (o.sumbycash = 0) THEN 'card' "
                     + "        WHEN (o.sumbycard <> 0) AND (o.sumbycash <> 0) THEN 'mixed' "
                     + "        ELSE 'other' END AS flag "
                     + "    , pmd.menudetailname AS preorderMenuDetailName, pmd.menudetailprice, pmd.amount, "
                     + "    pc.modeofadd, pc.armcomplexid, pmd.itemcode "
                     + "FROM cf_orders o "
                     + "INNER JOIN CF_ORDERDETAILS od ON o.IdOfOrder=od.IdOfOrder AND od.idoforg = o.idoforg "
                     + "INNER JOIN cf_preorder_linkod pl ON pl.idoforder = o.idoforder "
                     + "INNER JOIN cf_preorder_complex pc ON pl.preorderguid = pc.guid "
                     + "LEFT JOIN "
                     + "    (SELECT pmd.idofpreordercomplex, pmd.menudetailname, pmd.menudetailprice, pmd.usedamount as amount, pmd.itemcode "
                     + "        FROM cf_preorder_menudetail pmd "
                     + "        WHERE pmd.amount > 0 "
                     + "    ) pmd ON pmd.idofpreordercomplex = pc.idofpreordercomplex "
                     + "WHERE "
                     + orgAdditionalCondition
                     + "    AND od.MenuType>=:typeComplexMin AND od.MenuType<=:typeComplexMax AND od.rPrice > 0 AND "
                     + "    o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime AND o.state=0 AND od.state=0 "
                     + "ORDER BY 1, 11, 10");

            preordersQuery.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
            preordersQuery.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
            preordersQuery.setParameter("startTime", startTime.getTime());
            preordersQuery.setParameter("endTime", endTime.getTime());

            mealsList = preordersQuery.list();

            String menuGroupPay = "Платное питание - предзаказ";

            MealRow mealRowPay = new MealRow(menuGroupPay, new ArrayList<SubReportMealRow>());
            mealRowHashMap.put(menuGroupPay, mealRowPay);

            Map<Integer, SubReportMealRow> complexMap = new HashMap<>();
            Map<MealSubRowGroup, SubReportMealRow> complexMapModeOfAdd4 = new HashMap<>();

            totalCountCash = 0L;
            totalCountCard = 0L;
            totalSumCash = 0L;
            totalSumCard = 0L;
            totalCountMixed = 0L;
            totalSumMixed = 0L;

            for (Object[] vals : mealsList) {
                Integer qty = (Integer) vals[1];
                Long rPrice = ((BigInteger) vals[2]).longValue();
                Long sum = ((BigInteger) vals[3]).longValue();
                String menuNamePay = vals[4].toString();
                Long discount = ((BigInteger) vals[5]).longValue();
                Long socialDiscount = ((BigInteger) vals[6]).longValue();
                Long grantSum = ((BigInteger) vals[7]).longValue();
                String payType = (String) vals[8];
                String menuDetailName = (String) vals[9];
                Long menuDetailPrice = null == vals[10] ? 0 : ((BigInteger) vals[10]).longValue();
                Integer amount = null == vals[11] ? 0 : ((BigInteger) vals[11]).intValue();
                Integer modeOfAdd = (Integer) vals[12];
                Integer armComplexId = (Integer) vals[13];
                String itemCode = (String) vals[14];
                menuDetailName = String.format("%s (%s)", menuDetailName, itemCode);
                Long tradeDiscount = (long) (
                        ((double) (discount - socialDiscount) / (double) (discount + socialDiscount + rPrice + grantSum))
                                * 100);
                if (tradeDiscount > 0) {
                    menuNamePay = String.format("%s (скидка %d%%)", menuNamePay, tradeDiscount);
                }

                Integer countCash = 0;
                Integer countCard = 0;
                Long sumCash = 0L;
                Long sumCard = 0L;
                Integer countMixed = 0;
                Long sumMixed = 0L;

                switch (payType) {
                    case "cash":
                        if (null != modeOfAdd && modeOfAdd.equals(2)) {
                            countCash = qty;
                            sumCash = sum;
                        } else if (null != modeOfAdd && modeOfAdd.equals(4)) {
                            countCash = amount;
                            sumCash = menuDetailPrice * amount;
                        }
                        break;
                    case "card":
                        if (null != modeOfAdd && modeOfAdd.equals(2)) {
                            countCard = qty;
                            sumCard = sum;
                        } else if (null != modeOfAdd && modeOfAdd.equals(4)) {
                            countCard = amount;
                            sumCard = menuDetailPrice * amount;
                        }
                        break;
                    case "mixed":
                        if (null != modeOfAdd && modeOfAdd.equals(2)) {
                            countMixed = qty;
                            sumMixed = sum;
                        } else if (null != modeOfAdd && modeOfAdd.equals(4)) {
                            countMixed = amount;
                            sumMixed = menuDetailPrice * amount;
                        }
                        break;
                }

                totalCount += qty;
                totalSum += sum;
                totalCountCash += countCash;
                totalCountCard += countCard;
                totalSumCash += sumCash;
                totalSumCard += sumCard;
                totalCountMixed += countMixed;
                totalSumMixed += sumMixed;

                if (null != modeOfAdd && modeOfAdd.equals(2)) {
                    MealSubRowGroup group = new MealSubRowGroup(armComplexId, rPrice);
                    subReportMealRow = complexMapModeOfAdd4.get(group);
                    if (null == subReportMealRow) {
                        subReportMealRow = new SubReportMealRow(menuNamePay, qty, rPrice, sum, totalCountCash,
                                totalCountCard, totalSumCash, totalSumCard, totalCountMixed, totalSumMixed);
                        complexMapModeOfAdd4.put(group, subReportMealRow);
                        totalPreorderSum += sum;
                    } else {
                        subReportMealRow.setCount(subReportMealRow.getCount() + qty);
                        subReportMealRow.setSum(subReportMealRow.getSum() + qty * rPrice);
                        totalPreorderSum += qty * rPrice;
                    }
                    totalPreorderCount += qty;
                } else if (null != modeOfAdd && modeOfAdd.equals(4)) {
                    subReportMealRow = complexMap.get(armComplexId);
                    if (null == subReportMealRow) {
                        subReportMealRow = new SubReportMealRow(menuNamePay, null, null, null, totalCountCash,
                                totalCountCard, totalSumCash, totalSumCard, totalCountMixed, totalSumMixed);
                        complexMap.put(armComplexId, subReportMealRow);
                    }
                    HashMap<String, SubReportMealRow> dishMap = subReportMealRow.getDishesList();
                    if (null == dishMap.get(itemCode)) {
                        dishMap.put(itemCode, new SubReportMealRow(menuDetailName, amount, menuDetailPrice, amount * menuDetailPrice, totalCountCash,
                                totalCountCard, totalSumCash, totalSumCard, totalCountMixed, totalSumMixed));
                    } else {
                        SubReportMealRow mealRow = dishMap.get(itemCode);
                        mealRow.setCount(mealRow.getCount() + amount);
                        mealRow.setSum(mealRow.getSum() + amount * menuDetailPrice);
                    }
                    totalPreorderSum += amount * menuDetailPrice;
                    totalPreorderCount += amount;
                }
            }

            mealRowHashMap.get(menuGroupPay).getSubReportMealRowList().addAll(complexMap.values());
            mealRowHashMap.get(menuGroupPay).getSubReportMealRowList().addAll(complexMapModeOfAdd4.values());

            mealRowHashMap.get(menuGroupPay).setMealCountGroupTotal(totalPreorderCount);
            mealRowHashMap.get(menuGroupPay).setMealSumGroupTotal(totalPreorderSum);

            subReportDataRowsPay.add(new SubReportDataRow(menuGroupPay, totalPreorderCount, totalPreorderSum, totalCountCash,
                    totalCountCard, totalSumCash, totalSumCard, totalCountMixed, totalSumMixed));

            totalDataRows.add(new TotalDataRow("Платное комплексное питание + Предзаказ ВСЕГО: ",
                    totalPreorderCount + totalPayCount, totalPreorderSum + totalPaySum,
                    subReportDataRowsPay));
            totalDataRows.add(new TotalDataRow("Платное комплексное питание + Предзаказ + Буфет ВСЕГО: ",
                    totalPreorderCount + totalPayCount + totalBuffetCount, totalPreorderSum + totalPaySum + totalBuffetSum,
                    Collections.<SubReportDataRow>emptyList()));

            long totalByAllCount = totalBuffetCount + totalPayCount + totalUnPaidCount + totalUnPaidTempClientsCount + totalPreorderCount;
            long totalByAllSum = totalBuffetSum + totalPaySum + totalUnPaidSum + totalUnPaidTempClientsSum + totalPreorderSum;

            totalDataRows.add(new TotalDataRow("ОБЩЕЕ: ", totalByAllCount, totalByAllSum, subReportDataRowsUnPaid));

            //Сбор итоговых данных "Платное комплексное питание"
            List<Object[]> mealsPayTotals;

            Query payComplexQueryTotal = session.createSQLQuery(
                    "SELECT CASE WHEN pc.modeofadd = 2 or pc.idofpreordercomplex is null THEN SUM(od.Qty) "
                            + "     WHEN pc.modeofadd = 4 THEN SUM(pmd.usedamount) ELSE 0 END AS amount, "
                            + "CASE WHEN pc.modeofadd = 2 or pc.idofpreordercomplex is null THEN SUM(od.Qty * od.RPrice) "
                            + "     WHEN pc.modeofadd = 4 THEN SUM(pmd.usedamount * pmd.menudetailprice) ELSE 0 END AS price, "
                            + "CASE WHEN (o.sumbycash <> 0) AND (o.sumbycard = 0) AND pl.idofpreorderlinkod IS NULL THEN 'cash' "
                            + "WHEN (o.sumbycard <> 0) AND (o.sumbycash = 0) AND pl.idofpreorderlinkod IS NULL THEN 'card' "
                            + "WHEN (o.sumbycard <> 0) AND (o.sumbycash <> 0) AND pl.idofpreorderlinkod IS NULL THEN 'mixed' "
                            + "WHEN (o.sumbycash <> 0) AND (o.sumbycard = 0) AND pl.idofpreorderlinkod IS NOT NULL THEN 'pcash' "
                            + "WHEN (o.sumbycard <> 0) AND (o.sumbycash = 0) AND pl.idofpreorderlinkod IS NOT NULL THEN 'pcard' "
                            + "WHEN (o.sumbycard <> 0) AND (o.sumbycash <> 0) AND pl.idofpreorderlinkod IS NOT NULL THEN 'pmixed' "
                            + "ELSE 'other' END AS flag FROM CF_ORDERS o "
                            + "INNER JOIN CF_ORDERDETAILS od ON o.IdOfOrder=od.IdOfOrder AND o.idoforg = od.idoforg "
                            + "LEFT JOIN cf_preorder_linkod pl ON pl.idoforder = od.idoforder and pl.idoforderdetail = od.idoforderdetail "
                            + "LEFT JOIN cf_preorder_complex pc ON pc.guid = pl.preorderguid "
                            + "LEFT JOIN cf_preorder_menudetail pmd ON pmd.idofpreordercomplex = pc.idofpreordercomplex AND pc.amount = 0"
                            + String.format("WHERE %s AND (o.IdOfOrder = od.IdOfOrder) ", orgAdditionalCondition)
                            + "AND (od.MenuType >= :typeComplexMin AND od.MenuType <= :typeComplexMax) "
                            + "AND (od.rPrice > 0) AND (o.CreatedDate >= :startTime AND o.CreatedDate <= :endTime) "
                            + "AND o.state = 0 AND od.state = 0 "
                            + "GROUP BY od.MenuType, od.RPrice, od.menuDetailName, od.menuDetailName, od.discount, od.socdiscount, "
                            + "o.grantsum, o.sumbycard, o.sumbycash, pl.idofpreorderlinkod, pc.modeofadd, pc.idofpreordercomplex");
            payComplexQueryTotal.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
            payComplexQueryTotal.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
            payComplexQueryTotal.setParameter("startTime", startTime.getTime());
            payComplexQueryTotal.setParameter("endTime", endTime.getTime());

            mealsPayTotals = payComplexQueryTotal.list();

            mealCountSumByCashTotal = 0;
            mealSumSumByCashTotal = 0;

            mealCountSumByCardTotal = 0;
            mealSumSumByCardTotal = 0;

            mealCountSumByCashAndSumByCardTotal = 0;
            mealSumSumByCardAndSumByCashTotal = 0;

            Long preorderMealCountSumByCashTotal = 0L;
            Long preorderMealSumSumByCashTotal = 0L;

            Long preorderMealCountSumByCardTotal = 0L;
            Long preorderMealSumSumByCardTotal = 0L;

            Long preorderMealCountSumByCashAndSumByCardTotal = 0L;
            Long preorderMealSumSumByCardAndSumByCashTotal = 0L;

            for (Object[] vals : mealsPayTotals) {
                switch (vals[2].toString()) {
                    case "cash":
                        mealCountSumByCashTotal += vals[0] == null ? 0 : Long.parseLong(vals[0].toString());
                        mealSumSumByCashTotal += vals[1] == null ? 0 : Long.parseLong(vals[1].toString());
                        break;
                    case "card":
                        mealCountSumByCardTotal += vals[0] == null ? 0 : Long.parseLong(vals[0].toString());
                        mealSumSumByCardTotal += vals[1] == null ? 0 : Long.parseLong(vals[1].toString());
                        break;
                    case "mixed":
                        mealCountSumByCashAndSumByCardTotal += vals[0] == null ? 0 : Long.parseLong(vals[0].toString());
                        mealSumSumByCardAndSumByCashTotal += vals[1] == null ? 0 : Long.parseLong(vals[1].toString());
                        break;
                    case "pcash":
                        preorderMealCountSumByCashTotal += vals[0] == null ? 0 : Long.parseLong(vals[0].toString());
                        preorderMealSumSumByCashTotal += vals[1] == null ? 0 : Long.parseLong(vals[1].toString());
                        break;
                    case "pcard":
                        preorderMealCountSumByCardTotal += vals[0] == null ? 0 : Long.parseLong(vals[0].toString());
                        preorderMealSumSumByCardTotal += vals[1] == null ? 0 : Long.parseLong(vals[1].toString());
                        break;
                    case "pmixed":
                        preorderMealCountSumByCashAndSumByCardTotal +=
                                vals[0] == null ? 0 : Long.parseLong(vals[0].toString());
                        preorderMealSumSumByCardAndSumByCashTotal +=
                                vals[1] == null ? 0 : Long.parseLong(vals[1].toString());
                        break;
                }
            }

            mealRowHashMap.get("Платное комплексное питание").setMealCountSumByCardTotal(mealCountSumByCardTotal);
            mealRowHashMap.get("Платное комплексное питание").setMealSumSumByCardTotal(mealSumSumByCardTotal);

            mealRowHashMap.get("Платное комплексное питание").setMealCountSumByCashTotal(mealCountSumByCashTotal);
            mealRowHashMap.get("Платное комплексное питание").setMealSumSumByCashTotal(mealSumSumByCashTotal);

            mealRowHashMap.get("Платное комплексное питание")
                    .setMealCountSumByCashAndSumByCardTotal(mealCountSumByCashAndSumByCardTotal);
            mealRowHashMap.get("Платное комплексное питание")
                    .setMealSumSumByCardAndSumByCashTotal(mealSumSumByCardAndSumByCashTotal);


            mealRowHashMap.get("Бесплатное комплексное питание").setMealCountSumByCardTotal(null);
            mealRowHashMap.get("Бесплатное комплексное питание").setMealSumSumByCardTotal(null);

            mealRowHashMap.get("Бесплатное комплексное питание").setMealCountSumByCashTotal(null);
            mealRowHashMap.get("Бесплатное комплексное питание").setMealSumSumByCashTotal(null);

            mealRowHashMap.get("Бесплатное комплексное питание")
                    .setMealCountSumByCashAndSumByCardTotal(null);
            mealRowHashMap.get("Бесплатное комплексное питание")
                    .setMealSumSumByCardAndSumByCashTotal(null);

            mealRowHashMap.get("Платное питание - предзаказ").setMealCountSumByCardTotal(preorderMealCountSumByCardTotal);
            mealRowHashMap.get("Платное питание - предзаказ").setMealSumSumByCardTotal(preorderMealSumSumByCardTotal);

            mealRowHashMap.get("Платное питание - предзаказ").setMealCountSumByCashTotal(preorderMealCountSumByCashTotal);
            mealRowHashMap.get("Платное питание - предзаказ").setMealSumSumByCashTotal(preorderMealSumSumByCashTotal);

            mealRowHashMap.get("Платное питание - предзаказ")
                    .setMealCountSumByCashAndSumByCardTotal(preorderMealCountSumByCashAndSumByCardTotal);
            mealRowHashMap.get("Платное питание - предзаказ")
                    .setMealSumSumByCardAndSumByCashTotal(preorderMealSumSumByCardAndSumByCashTotal);

            //Сбор данных "Централизованное", "Собственное", "Закупленное", "Централизованное с доготовкой"
            List<Object[]> mealBuffetList;

            TotalMealRow totalMealRow;

            Query mealsBuffQuery = session.createSQLQuery(String.format(
                    "SELECT od.%s, SUM(od.qty) as qtySum, SUM(od.Qty*od.RPrice),"
                            + " CASE WHEN (o.sumbycash <> 0) AND (o.sumbycard = 0) THEN 'cash' "
                            + " WHEN (o.sumbycard <> 0) AND (o.sumbycash = 0) THEN 'card' "
                            + " WHEN (o.sumbycard <> 0) AND (o.sumbycash <> 0) THEN 'mixed' "
                            + " ELSE 'other' END AS flag "
                            + " FROM CF_ORDERS o, CF_ORDERDETAILS od "
                            + " WHERE %s AND (o.IdOfOrder=od.IdOfOrder) AND (od.MenuType=:typeDish) and o.state=0 and od.state=0 AND "
                            + " (o.CreatedDate>=:startTime AND o.CreatedDate<=:endTime) %s GROUP BY od.%s, od.MenuDetailName, od.RPrice, o.sumbycash, o.sumbycard "
                            + " ORDER BY od.%s, od.MenuDetailName", groupByField, orgAdditionalCondition,
                    menuGroupsCondition == null ? "" : "AND od.MenuGroup IN (" + menuGroupsCondition + ") ",
                    groupByField, groupByField));

            mealsBuffQuery.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
            mealsBuffQuery.setParameter("startTime", startTime.getTime());
            mealsBuffQuery.setParameter("endTime", endTime.getTime());

            mealBuffetList = mealsBuffQuery.list();

            for (Object[] vals : mealBuffetList) {
                if (groupByMenuOrigin) {
                    menuOrigin = Integer.parseInt(vals[0].toString());
                } else {
                    menuGroup = vals[0].toString();
                }
                long count = Long.parseLong(vals[1].toString());
                long sum = vals[2] == null ? 0 : Long.parseLong(vals[2].toString());
                String flag = vals[3].toString();

                if (groupByMenuOrigin) {
                    totalMealRow = new TotalMealRow(count, sum, flag);
                    mealRowMap.get(OrderDetail.getMenuOriginAsString(menuOrigin)).getTotalMealRowList()
                            .add(totalMealRow);
                } else {
                    totalMealRow = new TotalMealRow(count, sum, flag);
                    mealRowMap.get(menuGroup).getTotalMealRowList()
                            .add(totalMealRow);
                }
            }

            Set<String> mealRowKeys = mealRowMap.keySet();

            for (String mealRowKey : mealRowKeys) {

                List<TotalMealRow> mealRowMapList = mealRowMap.get(mealRowKey).getTotalMealRowList();

                mealCountSumByCashTotal = 0;
                mealSumSumByCashTotal = 0;

                mealCountSumByCardTotal = 0;
                mealSumSumByCardTotal = 0;

                mealCountSumByCashAndSumByCardTotal = 0;
                mealSumSumByCardAndSumByCashTotal = 0;


                for (TotalMealRow item : mealRowMapList) {
                    switch (item.getFlag()) {
                        case "cash":
                            mealCountSumByCashTotal += item.getCount();
                            mealSumSumByCashTotal += item.getSum();
                            break;
                        case "card":
                            mealCountSumByCardTotal += item.getCount();
                            mealSumSumByCardTotal += item.getSum();
                            break;
                        case "mixed":
                            mealCountSumByCashAndSumByCardTotal += item.getCount();
                            mealSumSumByCardAndSumByCashTotal += item.getSum();
                            break;
                    }
                }

                mealRowHashMap.get(mealRowKey).setMealCountSumByCardTotal(mealCountSumByCardTotal);
                mealRowHashMap.get(mealRowKey).setMealSumSumByCardTotal(mealSumSumByCardTotal);

                mealRowHashMap.get(mealRowKey).setMealCountSumByCashTotal(mealCountSumByCashTotal);
                mealRowHashMap.get(mealRowKey).setMealSumSumByCashTotal(mealSumSumByCashTotal);

                mealRowHashMap.get(mealRowKey).setMealCountSumByCashAndSumByCardTotal(mealCountSumByCashAndSumByCardTotal);
                mealRowHashMap.get(mealRowKey).setMealSumSumByCardAndSumByCashTotal(mealSumSumByCardAndSumByCashTotal);
            }

            ///
            parameterMap.put("totalsData", new JRBeanCollectionDataSource(totalDataRows));
            parameterMap.put("SUBREPORT_DIR",
                    RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());
            addPaymentTypeTotalValuesToReportParameters(parameterMap, totalDataRows);

            List<MealRow> mealRowCollection = new ArrayList<MealRow>(mealRowHashMap.values());
            Collections.sort(mealRowCollection, new Comparator<MealRow>() {
                @Override
                public int compare(MealRow obj1, MealRow obj2) {
                    if (obj1.getOriginName().toLowerCase().startsWith("платное") && obj2.getOriginName().toLowerCase().startsWith("платное")) {
                        return obj1.getOriginName().compareTo(obj2.getOriginName());
                    } else if (obj1.getOriginName().toLowerCase().startsWith("платное") && !obj2.getOriginName().toLowerCase().startsWith("платное")) {
                        return -1;
                    } else if (!obj1.getOriginName().toLowerCase().startsWith("платное") && obj2.getOriginName().toLowerCase().startsWith("платное")) {
                        return 1;
                    }

                    if (obj1.getOriginName().toLowerCase().startsWith("бесплатное") && obj2.getOriginName().toLowerCase().startsWith("бесплатное")) {
                        return obj1.getOriginName().compareTo(obj2.getOriginName());
                    } else if (obj1.getOriginName().toLowerCase().startsWith("бесплатное") && !obj2.getOriginName().toLowerCase().startsWith("бесплатное")) {
                        return 1;
                    } else if (!obj1.getOriginName().toLowerCase().startsWith("бесплатное") && obj2.getOriginName().toLowerCase().startsWith("бесплатное")) {
                        return -1;
                    }

                    return obj1.getOriginName().compareTo(obj2.getOriginName());
                }
            });

            return new JRBeanCollectionDataSource(mealRowCollection);
        }

        private String createOrgAdditionalConditionByOrgList(List<OrgShortItem> orgShortItems) {
            StringBuilder orgCondition = new StringBuilder("(");
            for(OrgShortItem orgItem : orgShortItems) {
                orgCondition.append(String.format("(o.idOfOrg=%d AND od.idOfOrg=%<d) OR ", orgItem.getIdOfOrg()));
            }
            //return without last "OR " and add ")"
            return String.format("%s%s", orgCondition.substring(0, orgCondition.length() - 4), ")");
        }

        private void addPaymentTypeTotalValuesToReportParameters(Map<String, Object> parameters,  LinkedList<TotalDataRow> totalDataRows) {
            List<SubReportDataRow> allSubReportDataRows = new LinkedList<SubReportDataRow>();
            for(TotalDataRow totalDataRow : totalDataRows) {
                allSubReportDataRows.addAll(totalDataRow.getSubReportDataRows());
            }

            Long totalReportCountCash = 0L, totalReportCountCard = 0L, totalReportCountCashAndCard = 0L;
            Long totalReportSumCash = 0L, totalReportSumCard = 0L, totalReportSumCashAndCard = 0L;

            for(SubReportDataRow subReportDataRow : allSubReportDataRows) {
                if(!subReportDataRow.getOriginName().contains("Бесплатное")) {
                    totalReportCountCash += subReportDataRow.countCash;
                    totalReportCountCard += subReportDataRow.countCard;
                    totalReportCountCashAndCard += subReportDataRow.countCashAndCard;
                    totalReportSumCash += subReportDataRow.sumCash;
                    totalReportSumCard += subReportDataRow.sumCard;
                    totalReportSumCashAndCard += subReportDataRow.sumCashAndCard;
                }
            }

            parameters.put("totalReportCountCash", totalReportCountCash);
            parameters.put("totalReportCountCard", totalReportCountCard);
            parameters.put("totalReportCountCashAndCard", totalReportCountCashAndCard);
            parameters.put("totalReportSumCash", totalReportSumCash);
            parameters.put("totalReportSumCard", totalReportSumCard);
            parameters.put("totalReportSumCashAndCard", totalReportSumCashAndCard);
        }
    }

    public static class TotalDataRow {

        String totalOriginName;
        long totalCount;
        long totalSum;
        List<SubReportDataRow> subReportDataRows;
        String subReportDir = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();

        public TotalDataRow(String totalOriginName, long totalCount, long totalSum,
                List<SubReportDataRow> subReportDataRows) {
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
        private Long countCash;
        private Long countCard;
        private Long sumCash;
        private Long sumCard;
        private Long countCashAndCard;
        private Long sumCashAndCard;

        public SubReportDataRow(String originName, Long count, Long sum,
                Long countCash, Long countCard, Long sumCash,
                Long sumCard, Long countCashAndCard, Long sumCashAndCard) {
            this.originName = originName;
            this.count = count;
            this.sum = sum;
            this.setCountCash(countCash);
            this.setCountCard(countCard);
            this.setSumCash(sumCash);
            this.setSumCard(sumCard);
            this.setCountCashAndCard(countCashAndCard);
            this.setSumCashAndCard(sumCashAndCard);
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

        public Long getCountCash() {
            return countCash;
        }

        public void setCountCash(Long countCash) {
            this.countCash = countCash;
        }

        public Long getCountCard() {
            return countCard;
        }

        public void setCountCard(Long countCard) {
            this.countCard = countCard;
        }

        public Long getSumCash() {
            return sumCash;
        }

        public void setSumCash(Long sumCash) {
            this.sumCash = sumCash;
        }

        public Long getSumCard() {
            return sumCard;
        }

        public void setSumCard(Long sumCard) {
            this.sumCard = sumCard;
        }

        public Long getCountCashAndCard() {
            return countCashAndCard;
        }

        public void setCountCashAndCard(Long countCashAndCard) {
            this.countCashAndCard = countCashAndCard;
        }

        public Long getSumCashAndCard() {
            return sumCashAndCard;
        }

        public void setSumCashAndCard(Long sumCashAndCard) {
            this.sumCashAndCard = sumCashAndCard;
        }
    }

    public DailySalesByGroupsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public DailySalesByGroupsReport() {
    }

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

    private static class MealSubRowGroup{
        private Integer armComplexId;
        private Long price;

        public MealSubRowGroup(Integer armComplexId, Long price) {
            this.armComplexId = armComplexId;
            this.price = price;
        }

        public Integer getArmComplexId() {
            return armComplexId;
        }

        public void setArmComplexId(Integer armComplexId) {
            this.armComplexId = armComplexId;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MealSubRowGroup)) {
                return false;
            }
            MealSubRowGroup that = (MealSubRowGroup) o;
            return Objects.equals(armComplexId, that.armComplexId) && Objects.equals(price, that.price);
        }

        @Override
        public int hashCode() {
            return Objects.hash(armComplexId, price);
        }
    }
}