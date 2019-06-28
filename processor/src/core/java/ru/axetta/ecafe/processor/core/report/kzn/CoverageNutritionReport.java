/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.grid.ColumnGridComponentBuilder;
import net.sf.dynamicreports.report.builder.grid.ColumnTitleGroupBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;
import ru.axetta.ecafe.processor.core.utils.report.CoverageNutritionDynamicBean;
import ru.axetta.ecafe.processor.core.utils.report.DynamicProperty;
import ru.axetta.ecafe.processor.core.utils.report.DynamicReportUtils;
import ru.axetta.ecafe.processor.core.utils.report.JRDynamicCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class CoverageNutritionReport extends BasicReportForAllOrgJob {

    public static final String REPORT_NAME = "Отчет по охвату питания";
    public static final String[] TEMPLATE_FILE_NAMES = {""};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{};
    private final static Logger logger = LoggerFactory.getLogger(CoverageNutritionReport.class);

    final public static String P_SHOW_YOUNGER_CLASSES = "showYoungerClasses";
    final public static String P_SHOW_MIDDLE_CLASSES = "showMiddleClasses";
    final public static String P_SHOW_OLDER_CLASSES = "showOlderClasses";
    final public static String P_SHOW_EMPLOYEE_CLASSES = "showEmployeeClasses";

    final public static String P_SHOW_FREE_NUTRITION = "showFreeNutrition";
    final public static String P_SHOW_PAID_NUTRITION = "showPaidNutrition";
    final public static String P_SHOW_BUFFET = "showBuffet";

    final public static String P_SHOW_COMPLEXES_BY_ORG_CARD = "showComplexesByOrgCard";

    final public static String P_SHOW_TOTAL = "showTotals";

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new CoverageNutritionReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    public static class Builder extends BasicReportForOrgJob.Builder {

        private final String templateFilename;
        private Long idOfOrg;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startDate, Date endDate, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startDate));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endDate));
            parameterMap.put("reportName", REPORT_NAME);
            parameterMap.put("SUBREPORT_DIR",
                    RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());
            Boolean showYoungerClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_YOUNGER_CLASSES));
            Boolean showMiddleClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_MIDDLE_CLASSES));
            Boolean showOlderClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_OLDER_CLASSES));
            Boolean showEmployeeClasses = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_EMPLOYEE_CLASSES));

            Boolean showFreeNutrition = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_FREE_NUTRITION));
            Boolean showPaidNutrition = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_PAID_NUTRITION));
            Boolean showBuffet = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_BUFFET));

            Boolean showComplexesByOrgCard = Boolean
                    .parseBoolean(reportProperties.getProperty(P_SHOW_COMPLEXES_BY_ORG_CARD));
            Boolean showTotal = Boolean.parseBoolean(reportProperties.getProperty(P_SHOW_TOTAL));

            List<Long> idOfOrgList = parseStringAsLongList(ReportPropertiesUtils.P_ID_OF_ORG);
            List<Long> idOfSourceOrgList = parseStringAsLongList(ReportPropertiesUtils.P_ID_OF_MENU_SOURCE_ORG);

            JasperPrint jasperPrint = getPrint(session, startDate, endDate, idOfOrgList, idOfSourceOrgList,
                    showYoungerClasses, showMiddleClasses, showOlderClasses, showEmployeeClasses, showFreeNutrition,
                    showPaidNutrition, showBuffet, showComplexesByOrgCard, showTotal);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new CoverageNutritionReport(generateTime, generateDuration, jasperPrint, startDate, endDate);
        }

        private JasperPrint getPrint(Session session, Date startDate, Date endDate, List<Long> idOfOrgList,
                List<Long> idOfSourceOrgList, Boolean showYoungerClasses, Boolean showMiddleClasses,
                Boolean showOlderClasses, Boolean showEmployeeClasses, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard, Boolean showTotal) {
            JasperPrint jasperPrint = null;
            try {
                JasperReportBuilder reportBuilder = report();
                reportBuilder.title(cmp.text(REPORT_NAME).setStyle(stl.style().setFontSize(10).setBold(true)));
                reportBuilder.ignorePagination();
                reportBuilder.ignorePageWidth();

                TextColumnBuilder<String> schoolNameBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Название ОО", "schoolName", type.stringType(),
                                DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(), 80);
                TextColumnBuilder<String> schoolAddressBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Адрес ОО", "schoolAddress", type.stringType(),
                                DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(), 180);
                TextColumnBuilder<Long> studentsTotalBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Общее количество учащихся", "studentsCountTotal", type.longType(),
                                DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(), 100);
                TextColumnBuilder<Long> studentsYoungBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество учащихся 1-4 классов", "studentsCountYoung",
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(),
                                100);
                TextColumnBuilder<Long> studentsMiddleBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество учащихся 5-9 классов", "studentsCountMiddle",
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(),
                                100);
                TextColumnBuilder<Long> studentsOldBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество учащихся 10-11 классов", "studentsCountOld",
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(),
                                100);
                TextColumnBuilder<Long> benefitStudentsYoungBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество льготников 1-4 классы", "benefitStudentsCountYoung",
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(),
                                100);
                TextColumnBuilder<Long> benefitStudentsMiddleBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество льготников 5-9 классы", "benefitStudentsCountMiddle",
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(),
                                100);
                TextColumnBuilder<Long> benefitStudentsOldBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество льготников 10-11 классы", "benefitStudentsCountOld",
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(),
                                100);
                TextColumnBuilder<Long> benefitStudentsTotalBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество льготников всего", "benefitStudentsCountTotal",
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.headerStyle(),
                                100);
                TextColumnBuilder<Long> employeeBuilder = DynamicReportUtils
                        .createColumn(reportBuilder, "Количество сотрудников, администрации и прочих групп",
                                "employeeCount", type.longType(), DynamicReportUtils.detailStyle(),
                                DynamicReportUtils.headerStyle(), 100);

                List<ColumnGridComponentBuilder> columnTitleGroupBuilderList = new ArrayList<>();
                columnTitleGroupBuilderList.add(schoolNameBuilder);
                columnTitleGroupBuilderList.add(schoolAddressBuilder);
                columnTitleGroupBuilderList.add(studentsTotalBuilder);
                columnTitleGroupBuilderList.add(studentsYoungBuilder);
                columnTitleGroupBuilderList.add(studentsMiddleBuilder);
                columnTitleGroupBuilderList.add(studentsOldBuilder);
                columnTitleGroupBuilderList.add(benefitStudentsYoungBuilder);
                columnTitleGroupBuilderList.add(benefitStudentsMiddleBuilder);
                columnTitleGroupBuilderList.add(benefitStudentsOldBuilder);
                columnTitleGroupBuilderList.add(benefitStudentsTotalBuilder);
                columnTitleGroupBuilderList.add(employeeBuilder);

                reportBuilder.setDataSource(createDataSource(
                        createReportAndData(session, reportBuilder, startDate, endDate, idOfOrgList, idOfSourceOrgList,
                                showYoungerClasses, showMiddleClasses, showOlderClasses, showEmployeeClasses,
                                showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard, showTotal,
                                columnTitleGroupBuilderList)));

                DynamicReportUtils.applyColumnGrtid(reportBuilder, columnTitleGroupBuilderList);
                jasperPrint = reportBuilder.toJasperPrint();
            } catch (Exception e) {
                logger.error("", e);
            }
            return jasperPrint;
        }

        private JRDataSource createDataSource(Collection<CoverageNutritionReportItem> dataList) {
            return new JRDynamicCollectionDataSource(dataList);
        }

        private Collection<CoverageNutritionReportItem> createReportAndData(Session session,
                JasperReportBuilder reportBuilder, Date startDate, Date endDate, List<Long> idOfOrgList,
                List<Long> idOfSourceOrgList, Boolean showYoungerClasses, Boolean showMiddleClasses,
                Boolean showOlderClasses, Boolean showEmployee, Boolean showFreeNutrition, Boolean showPaidNutrition,
                Boolean showBuffet, Boolean showComplexesByOrgCard, Boolean showTotal,
                List<ColumnGridComponentBuilder> columnTitleGroupBuilderList) throws Exception {
            List<Long> orgList = loadOrgList(session, idOfSourceOrgList, idOfOrgList);
            if (orgList.isEmpty()) {
                throw new Exception("Выберите организацию");
            }

            List<Long> managerList = Collections.EMPTY_LIST;
            if (showComplexesByOrgCard) {
                managerList = loadManagers(session, orgList);
            }
            HashMap<Long, CoverageNutritionReportItem> itemHashMap = new HashMap<>();
            for (Long idOfOrg : orgList) {
                itemHashMap.put(idOfOrg, new CoverageNutritionReportItem());
            }

            loadStatistic(session, itemHashMap);

            HashMap<String, HashMap<String, List<String>>> complexMap = loadComplexMap(session, itemHashMap.keySet(),
                    startDate, endDate, showYoungerClasses, showMiddleClasses, showOlderClasses, showEmployee,
                    showFreeNutrition, showPaidNutrition, showBuffet);
            updateTemplateByComplexMap(reportBuilder, complexMap, columnTitleGroupBuilderList, false);
            loadGroupsData(session, itemHashMap, managerList, startDate, endDate, showFreeNutrition, showPaidNutrition,
                    showBuffet, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses, showOlderClasses,
                    showEmployee);

            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                HashMap<String, HashMap<String, List<String>>> orgCardComplexMap = loadOrgCardComplexMap(session,
                        itemHashMap.keySet(), startDate, endDate, managerList, showFreeNutrition, showPaidNutrition,
                        showBuffet);
                updateTemplateByComplexMap(reportBuilder, orgCardComplexMap, columnTitleGroupBuilderList, true);
                loadComplexByOrgCardData(session, itemHashMap, startDate, endDate, managerList);
                loadBuffetByOrgCardData(session, itemHashMap, startDate, endDate, managerList);
            }

            if (showTotal) {
                List<String> totalsTitlesList = loadTotalsTitles(session, itemHashMap.keySet(), startDate, endDate,
                        showYoungerClasses, showMiddleClasses, showOlderClasses, showEmployee, showFreeNutrition,
                        showPaidNutrition, showBuffet, showComplexesByOrgCard, managerList);
                updateTemplateByTotal(reportBuilder, totalsTitlesList, columnTitleGroupBuilderList);
                loadTotalsData(session, itemHashMap, managerList, startDate, endDate, showFreeNutrition,
                        showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses,
                        showOlderClasses);
                loadTotalsPaidAndFreeAndBuffetAllDataTotal(session, itemHashMap, managerList, startDate, endDate,
                        showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses,
                        showMiddleClasses, showOlderClasses);
                loadTotalsUniqueClientsData(session, itemHashMap, startDate, endDate, showFreeNutrition,
                        showPaidNutrition, showBuffet, showYoungerClasses, showMiddleClasses, showOlderClasses);
            }

            if (showEmployee) {
                updateTemplateByEmployees(reportBuilder, columnTitleGroupBuilderList);
                loadEmployeesByOrgs(session, itemHashMap, startDate, endDate);
                loadEmployeesTotalsByOrgs(session, itemHashMap, startDate, endDate);
            }

            return itemHashMap.values();
        }

        private void loadStatistic(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap) {
            for (Long idOfOrg : itemHashMap.keySet()) {
                Org org = (Org) session.load(Org.class, idOfOrg);
                itemHashMap.get(idOfOrg).fillOrganization(org);

                Criteria criteria = session.createCriteria(KznClientsStatistic.class);
                criteria.add(Restrictions.eq("org", org));
                KznClientsStatistic kznClientsStatistic = (KznClientsStatistic) criteria.uniqueResult();
                if (null != kznClientsStatistic) {
                    itemHashMap.get(idOfOrg).fillStatistic(kznClientsStatistic);
                }
            }
        }

        private void loadComplexByOrgCardData(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                Date startDate, Date endDate, List<Long> managerList) {

            Query complexByOrgCardDataQuery = prepareComplexByOrgCardDataQuery(session, itemHashMap, startDate, endDate,
                    managerList);

            List complexByOrgCardDatList = complexByOrgCardDataQuery.list();

            for (Object o : complexByOrgCardDatList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String foodType = (String) row[1];
                String complexName = (String) row[2];
                Long price = (null == row[3]) ? 0 : ((BigInteger) row[3]).longValue();
                Long orderCount = (null == row[4]) ? 0 : ((BigInteger) row[4]).longValue();
                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();
                dynamicPropertyList.put(String.format("%d",
                        (CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES + foodType + complexName + priceFormat(price)
                                + CoverageNutritionDynamicBean.ORG_CARD_ORDERS_COUNT).hashCode()),
                        new DynamicProperty(orderCount));

                item.setDynamicProperties(dynamicPropertyList);
            }
        }

        private void loadBuffetByOrgCardData(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                Date startDate, Date endDate, List<Long> managerList) {

            Query buffetByOrgCardDataQuery = prepareBuffetByOrgCardDataQuery(session, itemHashMap, startDate, endDate,
                    managerList);

            List buffetByOrgCardDatList = buffetByOrgCardDataQuery.list();

            for (Object o : buffetByOrgCardDatList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String complexName = (String) row[1];
                Long orderCount = (null == row[2]) ? 0 : ((BigInteger) row[2]).longValue();
                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                String buffetType = complexName.contains(CoverageNutritionDynamicBean.BUFFET_HOT)
                        ? CoverageNutritionDynamicBean.BUFFET_HOT : CoverageNutritionDynamicBean.BUFFET_PAID;
                String titleName = String.format(CoverageNutritionDynamicBean.ORG_CARD_BUFFET_COUNT, buffetType);
                dynamicPropertyList.put(String.format("%d",
                        (CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES + complexName + titleName).hashCode()),
                        new DynamicProperty(orderCount));

                item.setDynamicProperties(dynamicPropertyList);
            }
        }

        private void loadGroupsData(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses, Boolean showEmployee) {

            Query groupDataQuery = prepareGroupDataQuery(session, itemHashMap, managerList, startDate, endDate,
                    showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses,
                    showMiddleClasses, showOlderClasses);

            List groupsDatalList = groupDataQuery.list();

            for (Object o : groupsDatalList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String group = (String) row[1];
                String foodType = (String) row[2];
                String complexName = (String) row[3];
                Long price = (null == row[4]) ? 0 : ((BigInteger) row[4]).longValue();
                Long clientCount = (null == row[5]) ? 0 : ((BigInteger) row[5]).longValue();
                Long orderCount = (null == row[6]) ? 0 : ((BigInteger) row[6]).longValue();
                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();
                dynamicPropertyList.put(String.format("%d", (group + foodType + complexName + priceFormat(price)
                        + CoverageNutritionDynamicBean.CLIENTS_COUNT).hashCode()), new DynamicProperty(clientCount));
                dynamicPropertyList.put(String.format("%d", (group + foodType + complexName + priceFormat(price)
                        + CoverageNutritionDynamicBean.ORDERS_COUNT).hashCode()), new DynamicProperty(orderCount));

                Long totalValue = item.getStatisticDataPerGroup(CoverageNutritionDynamicBean.findClassInString(group),
                        CoverageNutritionDynamicBean.FREE_NUTRITION.equals(foodType));
                dynamicPropertyList.put(String.format("%d", (group + foodType + complexName + priceFormat(price)
                        + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS).hashCode()), new DynamicProperty(
                        !totalValue.equals(0L) ? clientCount.doubleValue() / totalValue.doubleValue() * 100 : 0D));

                item.setDynamicProperties(dynamicPropertyList);
            }

            loadSubTotalData(session, itemHashMap, managerList, startDate, endDate, showFreeNutrition,
                    showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses,
                    showOlderClasses);
        }

        private void loadTotalsData(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses) {

            Query totalsDataQuery = prepareTotalsDataQuery(session, itemHashMap, managerList, startDate, endDate,
                    showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses,
                    showMiddleClasses, showOlderClasses);

            List totalsDataList = totalsDataQuery.list();

            for (Object o : totalsDataList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String foodType = (String) row[1];
                Long clientCount = (null == row[2]) ? 0 : ((BigInteger) row[2]).longValue();
                Long orderCount = (null == row[3]) ? 0 : ((BigInteger) row[3]).longValue();

                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                if (foodType.equals("complexCard")) {
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_NUTRITION
                                    + CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES_BY_ORG_CARD).hashCode()),
                            new DynamicProperty(orderCount));
                } else if (foodType.equals("buffetHotCard")) {
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_HOT_FULL
                                    + String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD,
                                    CoverageNutritionDynamicBean.BUFFET_HOT)).hashCode()),
                            new DynamicProperty(orderCount));
                } else if (foodType.equals("buffetPaidCard")) {
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_PAID_FULL
                                    + String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD,
                                    CoverageNutritionDynamicBean.BUFFET_PAID)).hashCode()),
                            new DynamicProperty(orderCount));
                } else {
                    String clientTitleName;
                    String orderTitleName;
                    if (foodType.contains(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) {
                        String buffetType;
                        if (foodType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)) {
                            buffetType = CoverageNutritionDynamicBean.BUFFET_HOT;
                        } else {
                            buffetType = CoverageNutritionDynamicBean.BUFFET_PAID;
                        }
                        clientTitleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "1-11", "");
                        orderTitleName = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, buffetType);
                    } else {
                        clientTitleName = CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS;
                        orderTitleName = CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES;
                    }
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType + clientTitleName).hashCode()),
                            new DynamicProperty(clientCount));

                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType + orderTitleName).hashCode()),
                            new DynamicProperty(orderCount));
                }
                item.setDynamicProperties(dynamicPropertyList);
            }

            loadSubTotalData(session, itemHashMap, managerList, startDate, endDate, showFreeNutrition,
                    showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses,
                    showOlderClasses);
        }

        private void loadTotalsPaidAndFreeAndBuffetAllDataTotal(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, List<Long> managerList, Date startDate,
                Date endDate, Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet,
                Boolean showComplexesByOrgCard, Boolean showYoungerClasses, Boolean showMiddleClasses,
                Boolean showOlderClasses) {

            Query totalsPaidAndFreeDataQuery = prepareTotalsPaidAndFreeAndBuffetAllDataQuery(session, itemHashMap,
                    managerList, startDate, endDate, showFreeNutrition, showPaidNutrition, showBuffet,
                    showComplexesByOrgCard, showYoungerClasses, showMiddleClasses, showOlderClasses);

            List totalsPaidAndFreeDataList = totalsPaidAndFreeDataQuery.list();

            for (Object o : totalsPaidAndFreeDataList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String foodType = (String) row[1];
                Long clientCount = (null == row[2]) ? 0 : ((BigInteger) row[2]).longValue();
                Long orderCount = (null == row[3]) ? 0 : ((BigInteger) row[3]).longValue();

                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                if (foodType.contains(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) {
                    String titleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "1-11", "");
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL
                                    + titleName).hashCode()), new DynamicProperty(clientCount));

                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL
                                    + String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, "")).hashCode()),
                            new DynamicProperty(orderCount));
                } else {
                    String clientTitleName;
                    String orderTitleName;
                    if (foodType.contains(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) {
                        String buffetType;
                        if (foodType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)) {
                            buffetType = CoverageNutritionDynamicBean.BUFFET_HOT;
                        } else {
                            buffetType = CoverageNutritionDynamicBean.BUFFET_PAID;
                        }
                        clientTitleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "1-11", "");
                        orderTitleName = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, buffetType);
                    } else {
                        clientTitleName = CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS;
                        orderTitleName = CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES;
                    }
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType + clientTitleName).hashCode()),
                            new DynamicProperty(clientCount));

                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType + orderTitleName).hashCode()),
                            new DynamicProperty(orderCount));
                }

                if (foodType.equals(CoverageNutritionDynamicBean.PAID_AND_FREE)) {
                    Long totalClients = item.getStudentsCountTotal();
                    dynamicPropertyList.put(String.format("%d", (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType
                                    + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS).hashCode()),
                            new DynamicProperty(totalClients.equals(0L) ? 0L
                                    : clientCount.doubleValue() / totalClients.doubleValue() * 100D));
                }

                if (foodType.equals(CoverageNutritionDynamicBean.BUFFET_ALL_FULL)) {
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL
                                    + String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD, ""))
                                    .hashCode()), new DynamicProperty(orderCount));
                }

                item.setDynamicProperties(dynamicPropertyList);
            }

            loadSubTotalData(session, itemHashMap, managerList, startDate, endDate, showFreeNutrition,
                    showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses,
                    showOlderClasses);
        }

        private void loadSubTotalData(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses) {
            Query groupTotalsQuery = prepareGroupTotalsQuery(session, itemHashMap, managerList, startDate, endDate,
                    showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses,
                    showMiddleClasses, showOlderClasses);

            List groupsTotalList = groupTotalsQuery.list();

            for (Object o : groupsTotalList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String group = (String) row[1];
                String foodType = (String) row[2];
                Long clientCount = (null == row[3]) ? 0 : ((BigInteger) row[3]).longValue();
                Long orderCount = (null == row[4]) ? 0 : ((BigInteger) row[4]).longValue();

                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();
                dynamicPropertyList.put(String
                                .format("%d", (group + foodType + CoverageNutritionDynamicBean.CLIENTS_COUNT).hashCode()),
                        new DynamicProperty(clientCount));
                dynamicPropertyList.put(String
                                .format("%d", (group + foodType + CoverageNutritionDynamicBean.ORDERS_COUNT).hashCode()),
                        new DynamicProperty(orderCount));

                Long totalValue = item
                        .getStatisticDataPerGroup(CoverageNutritionDynamicBean.findClassInString(group), null);
                dynamicPropertyList.put(String.format("%d",
                        (group + foodType + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS).hashCode()),
                        new DynamicProperty(
                                !totalValue.equals(0L) ? clientCount.doubleValue() / totalValue.doubleValue() * 100
                                        : 0D));

                item.setDynamicProperties(dynamicPropertyList);
            }

            if (showBuffet) {
                loadGroupBuffetData(session, itemHashMap, managerList, startDate, endDate, showComplexesByOrgCard,
                        showYoungerClasses, showMiddleClasses, showOlderClasses);
                loadGroupBuffetTotalData(session, itemHashMap, managerList, startDate, endDate, showComplexesByOrgCard,
                        showYoungerClasses, showMiddleClasses, showOlderClasses);
            }

            loadGroupUniqueClientsTotal(session, itemHashMap, startDate, endDate, showFreeNutrition, showPaidNutrition,
                    showBuffet, showYoungerClasses, showMiddleClasses, showOlderClasses);
        }

        private void loadTotalsUniqueClientsData(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, Date startDate, Date endDate,
                Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet, Boolean showYoungerClasses,
                Boolean showMiddleClasses, Boolean showOlderClasses) {

            Query totalsUniqueClientsDataQuery = prepareTotalsUniqueClientsDataQuery(session, itemHashMap, startDate,
                    endDate, showFreeNutrition, showPaidNutrition, showBuffet, showYoungerClasses, showMiddleClasses,
                    showOlderClasses);

            List totalsUniqueClientsDataList = totalsUniqueClientsDataQuery.list();

            for (Object o : totalsUniqueClientsDataList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                Long clientCount = (null == row[1]) ? 0 : ((BigInteger) row[1]).longValue();

                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                dynamicPropertyList.put(String.format("%d",
                        (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL
                                + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE).hashCode()),
                        new DynamicProperty(clientCount));

                Long totalClients = item.getStudentsCountTotal();
                if (!totalClients.equals(0L)) {
                    dynamicPropertyList.put(String.format("%d", (CoverageNutritionDynamicBean.TOTALS_TITLE
                                    + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS
                                    + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE).hashCode()),
                            new DynamicProperty(clientCount.doubleValue() / totalClients.doubleValue() * 100D));
                }
            }
        }

        private void loadGroupUniqueClientsTotal(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, Date startDate, Date endDate,
                Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet, Boolean showYoungerClasses,
                Boolean showMiddleClasses, Boolean showOlderClasses) {

            Query groupUniqueClientsTotalQuery = prepareGroupUniqueClientsTotalQuery(session, itemHashMap, startDate,
                    endDate, showFreeNutrition, showPaidNutrition, showBuffet, showYoungerClasses, showMiddleClasses,
                    showOlderClasses);

            List groupUniqueClientsTotalList = groupUniqueClientsTotalQuery.list();

            for (Object o : groupUniqueClientsTotalList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String group = (String) row[1];
                Long clientCount = (null == row[2]) ? 0 : ((BigInteger) row[2]).longValue();

                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                dynamicPropertyList.put(String.format("%d", (group + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL
                                + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE).hashCode()),
                        new DynamicProperty(clientCount));
                Long totalValue = item
                        .getStatisticDataPerGroup(CoverageNutritionDynamicBean.findClassInString(group), null);
                dynamicPropertyList.put(String.format("%d",
                        (group + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS
                                + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE).hashCode()),
                        new DynamicProperty(
                                !totalValue.equals(0L) ? clientCount.doubleValue() / totalValue.doubleValue() * 100D
                                        : 0L));
            }
        }

        private Query prepareGroupUniqueClientsTotalQuery(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, Date startDate, Date endDate,
                Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet, Boolean showYoungerClasses,
                Boolean showMiddleClasses, Boolean showOlderClasses) {
            String sqlString = "select a.idoforg, a.group, count(distinct a.idofclient) as clientcount " + " from ("
                    + " select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, "
                    + "    case when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' end as group, "
                    + "    case when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет' "
                    + "     when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "         when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type, "
                    + "    c.idofclient " + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and (od.menutype = 0 or od.menutype between 50 and 99) "
                    + "     and og.organizationtype = 0 and cg.idofclientgroup >= :clientGroup and cg.idofclientgroup < :clientEmployees ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<String>();
            List<String> classesNotConditionList = new ArrayList<String>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            List<String> nutritionConditionList = new ArrayList<String>();
            List<String> nutritionNotConditionList = new ArrayList<String>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }

            //sqlString += generateQueryConditions(Collections.EMPTY_LIST, showYoungerClasses, showMiddleClasses,
            //        showOlderClasses, true, showFreeNutrition, showPaidNutrition, showBuffet, false);

            sqlString += ") a " + "group by a.idoforg, a.shortnameinfoservice, a.shortaddress, a.group ";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            return query;
        }

        private void loadGroupBuffetTotalData(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses) {

            Query buffetTotalDataQuery = prepareGroupBuffetTotalDataQuery(session, itemHashMap, managerList, startDate,
                    endDate, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses, showOlderClasses);

            List buffetTotalDataList = buffetTotalDataQuery.list();

            for (Object o : buffetTotalDataList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String group = (String) row[1];
                Long clientCount = (null == row[2]) ? 0 : ((BigInteger) row[2]).longValue();
                Long orderCount = (null == row[3]) ? 0 : ((BigInteger) row[3]).longValue();

                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                String titleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "",
                        CoverageNutritionDynamicBean.findClassInString(group));

                dynamicPropertyList.put(String.format("%d", (group + CoverageNutritionDynamicBean.MENU_TYPE_BUFFET + ' '
                                + CoverageNutritionDynamicBean.BUFFET_ALL + titleName).hashCode()),
                        new DynamicProperty(clientCount));
                dynamicPropertyList.put(String.format("%d", (group + CoverageNutritionDynamicBean.MENU_TYPE_BUFFET + ' '
                        + CoverageNutritionDynamicBean.BUFFET_ALL + CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT)
                        .hashCode()), new DynamicProperty(orderCount));
            }
        }


        private Query prepareGroupBuffetTotalDataQuery(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, List<Long> managerList, Date startDate,
                Date endDate, Boolean showComplexesByOrgCard, Boolean showYoungerClasses, Boolean showMiddleClasses,
                Boolean showOlderClasses) {
            String sqlString = "select a.idoforg, a.group, count(distinct a.idofclient) as clientcount, "
                    + " count(distinct a.idoforderdetail) as orderdetailcount " + " from ("
                    + " select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, " + "    case " + (
                    managerList.isEmpty() || !showComplexesByOrgCard ? ""
                            : " when c.idofclient in (:managerList) then 'Комплексы проданные по карте ОО' ")
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' end as group, "
                    + "    od.idoforderdetail, c.idofclient " + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype = 0 "
                    + "     and od.menuorigin in (0,1,10,11) and og.organizationtype = 0 and cg.idofclientgroup >= :clientGroup "
                    + "     and cg.idofclientgroup < :clientEmployees ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<String>();
            List<String> classesNotConditionList = new ArrayList<String>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }
            //
            //sqlString += generateQueryConditions(managerList, showYoungerClasses, showMiddleClasses, showOlderClasses,
            //        true, false, false, false, showComplexesByOrgCard);

            sqlString += ") a " + "group by a.idoforg, a.shortnameinfoservice, a.shortaddress, a.group ";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            if (!managerList.isEmpty() && showComplexesByOrgCard) {
                query.setParameterList("managerList", managerList);
            }
            return query;
        }

        private void loadGroupBuffetData(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses) {

            Query buffetDataQuery = prepareGroupBuffetDataQuery(session, itemHashMap, managerList, startDate, endDate,
                    showComplexesByOrgCard, showYoungerClasses, showMiddleClasses, showOlderClasses);

            List buffetDataList = buffetDataQuery.list();

            for (Object o : buffetDataList) {
                Object[] row = (Object[]) o;
                Long idOfOrg = ((BigInteger) row[0]).longValue();
                String group = (String) row[1];
                String foodType = (String) row[2];
                Long clientCount = (null == row[3]) ? 0 : ((BigInteger) row[3]).longValue();
                Long orderCount = (null == row[4]) ? 0 : ((BigInteger) row[4]).longValue();

                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                String buffetType = foodType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)
                        ? CoverageNutritionDynamicBean.BUFFET_HOT : CoverageNutritionDynamicBean.BUFFET_PAID;

                String titleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, buffetType,
                        ' ' + CoverageNutritionDynamicBean.findClassInString(group));

                dynamicPropertyList.put(String.format("%d", (group + foodType + titleName).hashCode()),
                        new DynamicProperty(clientCount));
                dynamicPropertyList.put(String
                                .format("%d", (group + foodType + CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT).hashCode()),
                        new DynamicProperty(orderCount));
            }
        }

        private Query prepareGroupBuffetDataQuery(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, List<Long> managerList, Date startDate,
                Date endDate, Boolean showComplexesByOrgCard, Boolean showYoungerClasses, Boolean showMiddleClasses,
                Boolean showOlderClasses) {
            String sqlString = "select a.idoforg, a.group, a.complexname, count(distinct a.idofclient) as clientcount, "
                    + " count(distinct a.idoforderdetail) as orderdetailcount " + " from ("
                    + " select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, " + "    case " + (
                    managerList.isEmpty() || !showComplexesByOrgCard ? ""
                            : " when c.idofclient in (:managerList) then 'Комплексы проданные по карте ОО' ")
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' end as group, "
                    + "    case when od.menuorigin in (0,1) then 'Буфет горячее' "
                    + "         when od.menuorigin in (10,11) then 'Буфет покупная' else '' end as complexname, "
                    + "    od.idoforderdetail, c.idofclient " + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype = 0 "
                    + "     and od.menuorigin in (0,1,10,11) and og.organizationtype = 0 and cg.idofclientgroup >= :clientGroup "
                    + "     and cg.idofclientgroup < :clientEmployees ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<String>();
            List<String> classesNotConditionList = new ArrayList<String>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }
            //
            //sqlString += generateQueryConditions(managerList, showYoungerClasses, showMiddleClasses, showOlderClasses,
            //        true, false, false, false, showComplexesByOrgCard);
            sqlString += ") a " + "group by a.idoforg, a.shortnameinfoservice, a.shortaddress, a.group, a.complexname ";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            if (!managerList.isEmpty() && showComplexesByOrgCard) {
                query.setParameterList("managerList", managerList);
            }
            return query;
        }

        private Query prepareComplexByOrgCardDataQuery(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, Date startDate, Date endDate,
                List<Long> managerList) {
            String sqlString =
                    "select a.idoforg, a.type, a.complexname, a.price,  count(distinct a.idoforderdetail) as orderdetailcount "
                            + " from (" + " select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, "
                            + "    case when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет' "
                            + "     when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                            + "         when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type, "
                            + "    case when od.menutype between 50 and 99 then od.menudetailname "
                            + "         when od.menutype = 0 and od.menuorigin in (0,1) then 'Горячее' "
                            + "         when od.menutype = 0 and od.menuorigin in (10,11) then 'Покупная' else '' end as complexname, "
                            + "    od.idoforderdetail, c.idofclient,"
                            + "     case when od.menutype between 50 and 99 and od.rprice > 0 then od.rprice "
                            + "          when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then od.discount else 0 end as price "
                            + " from cf_orders o "
                            + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                            + " join cf_clients c on c.idofclient = o.idofclient "
                            + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                            + " join cf_orgs og on og.idoforg = o.idoforg "
                            + " left join cf_kzn_clients_statistic st on st.idoforg = og.idoforg "
                            + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0 "
                            + "     and c.idofclient in (:managerList) " + ") a "
                            + "group by a.idoforg, a.shortnameinfoservice, a.shortaddress, a.type, a.complexname, a.price";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            query.setParameterList("managerList", managerList);
            return query;
        }

        private Query prepareBuffetByOrgCardDataQuery(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, Date startDate, Date endDate,
                List<Long> managerList) {
            String sqlString = "select a.idoforg, a.complexname, count(distinct a.idoforderdetail) as orderdetailcount "
                    + " from (" + " select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, "
                    + "    case when od.menutype between 50 and 99 then od.menudetailname "
                    + "         when od.menutype = 0 and od.menuorigin in (0,1) then 'Буфет горячее' "
                    + "         when od.menutype = 0 and od.menuorigin in (10,11) then 'Буфет покупная' else '' end as complexname, "
                    + "    od.idoforderdetail " + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " left join cf_kzn_clients_statistic st on st.idoforg = og.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype = 0 "
                    + "     and od.menuorigin in (0,1,10,11) and og.organizationtype = 0 and c.idofclient in (:managerList) "
                    + ") a " + "group by a.idoforg, a.shortnameinfoservice, a.shortaddress, a.complexname";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            query.setParameterList("managerList", managerList);
            return query;
        }

        private Query prepareGroupDataQuery(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses) {
            String sqlString =
                    "select a.idoforg, a.group, a.type, a.complexname, a.price,  count(distinct a.idofclient) as clientcount, "
                            + " count(distinct a.idoforderdetail) as orderdetailcount " + " from ("
                            + " select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, "
                            + "    st.studentsCountTotal, st.studentsCountYoung, st.studentsCountMiddle, st.studentsCountOld, st.benefitStudentsCountYoung, "
                            + "    st.benefitStudentsCountMiddle, st.benefitStudentsCountOld, st.benefitStudentsCountTotal, st.employeeCount, "
                            + "    case " + (managerList.isEmpty() || !showComplexesByOrgCard ? ""
                            : " when c.idofclient in (:managerList) then 'Комплексы проданные по карте ОО' ")
                            + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                            + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                            + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' end as group, "
                            + "    case when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет' "
                            + "     when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                            + "         when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type, "
                            + "    case when od.menutype between 50 and 99 then od.menudetailname "
                            + "         when od.menutype = 0 and od.menuorigin in (0,1) then 'Горячее' "
                            + "         when od.menutype = 0 and od.menuorigin in (10,11) then 'Покупная' else '' end as complexname, "
                            + "    od.idoforderdetail, c.idofclient,"
                            + "     case when od.menutype between 50 and 99 and od.rprice > 0 then od.rprice "
                            + "          when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then od.discount else 0 end as price "
                            + " from cf_orders o "
                            + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                            + " join cf_clients c on c.idofclient = o.idofclient "
                            + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                            + " join cf_orgs og on og.idoforg = o.idoforg "
                            + " left join cf_kzn_clients_statistic st on st.idoforg = og.idoforg "
                            + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0 "
                            + "     and cg.idofclientgroup >= :clientGroup and cg.idofclientgroup < :clientEmployees ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<String>();
            List<String> classesNotConditionList = new ArrayList<String>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            List<String> nutritionConditionList = new ArrayList<String>();
            List<String> nutritionNotConditionList = new ArrayList<String>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }
            //
            //sqlString += generateQueryConditions(managerList, showYoungerClasses, showMiddleClasses, showOlderClasses,
            //        showEmployee, showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard);

            sqlString += ") a "
                    + "group by a.idoforg, a.shortnameinfoservice, a.shortaddress, a.studentsCountTotal, a.studentsCountYoung, a.studentsCountMiddle, a.studentsCountOld, a.benefitStudentsCountYoung, "
                    + "   a.benefitStudentsCountMiddle, a.benefitStudentsCountOld, a.benefitStudentsCountTotal, a.employeeCount, a.group, a.type, a.complexname, a.price";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            if (!managerList.isEmpty() && showComplexesByOrgCard) {
                query.setParameterList("managerList", managerList);
            }
            return query;
        }

        private Query prepareTotalsDataQuery(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses) {
            String sqlString = "select a.idoforg, a.type, count(distinct a.idofclient) as clientcount, "
                    + " count(distinct a.idoforderdetail) as orderdetailcount " + " from ("
                    + " select distinct og.idoforg, " + "    case " + (managerList.isEmpty() || !showComplexesByOrgCard
                    ? "" : " when c.idofclient in (:managerList) and od.menutype between 50 and 99 then 'complexCard' "
                    + " when c.idofclient in (:managerList) and od.menutype = 0 and od.menuorigin in (0, 1) then 'buffetHotCard' "
                    + " when c.idofclient in (:managerList) and od.menutype = 0 and od.menuorigin in (10, 11) then 'buffetPaidCard' ")
                    + "         when od.menutype = 0 and od.menuorigin in (0, 1) then 'Буфет горячее' "
                    + "         when od.menutype = 0 and od.menuorigin in (10, 11) then 'Буфет покупная' "
                    + "         when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "         when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type, "
                    + "    od.idoforderdetail, c.idofclient " + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0 ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<>();
            List<String> classesNotConditionList = new ArrayList<>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            List<String> nutritionConditionList = new ArrayList<>();
            List<String> nutritionNotConditionList = new ArrayList<>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }

            sqlString += ") a " + "group by a.idoforg, a.type";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            if (!managerList.isEmpty() && showComplexesByOrgCard) {
                query.setParameterList("managerList", managerList);
            }
            return query;
        }

        private Query prepareTotalsPaidAndFreeAndBuffetAllDataQuery(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, List<Long> managerList, Date startDate,
                Date endDate, Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet,
                Boolean showComplexesByOrgCard, Boolean showYoungerClasses, Boolean showMiddleClasses,
                Boolean showOlderClasses) {
            String sqlString = "select a.idoforg, a.type, count(distinct a.idofclient) as clientcount, "
                    + " count(distinct a.idoforderdetail) as orderdetailcount " + " from ("
                    + " select distinct og.idoforg, " + "    case " + (managerList.isEmpty() || !showComplexesByOrgCard
                    ? ""
                    : " when c.idofclient in (:managerList) and od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'buffetCard' ")
                    + "         when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет общее' "
                    + "         when od.menutype between 50 and 99 then 'Платное питание + Бесплатное' end as type, "
                    + "    od.idoforderdetail, c.idofclient " + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0 ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<>();
            List<String> classesNotConditionList = new ArrayList<>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            List<String> nutritionConditionList = new ArrayList<>();
            List<String> nutritionNotConditionList = new ArrayList<>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }

            sqlString += ") a " + "group by a.idoforg, a.type";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            if (!managerList.isEmpty() && showComplexesByOrgCard) {
                query.setParameterList("managerList", managerList);
            }
            return query;
        }

        private Query prepareTotalsUniqueClientsDataQuery(Session session,
                HashMap<Long, CoverageNutritionReportItem> itemHashMap, Date startDate, Date endDate,
                Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet, Boolean showYoungerClasses,
                Boolean showMiddleClasses, Boolean showOlderClasses) {
            String sqlString = "select a.idoforg, count(distinct a.idofclient) as clientcount " + " from ("
                    + " select distinct og.idoforg, c.idofclient " + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0 ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<>();
            List<String> classesNotConditionList = new ArrayList<>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            List<String> nutritionConditionList = new ArrayList<>();
            List<String> nutritionNotConditionList = new ArrayList<>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }

            sqlString += ") a " + "group by a.idoforg";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            return query;
        }

        private Query prepareGroupTotalsQuery(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> managerList, Date startDate, Date endDate, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard,
                Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses) {
            String sqlString = "select a.idoforg, a.group, a.type, count(distinct a.idofclient) as clientcount, "
                    + " count(distinct a.idoforderdetail) as orderdetailcount " + " from ("
                    + " select distinct og.idoforg, og.shortnameinfoservice, og.shortaddress, "
                    + "    st.studentsCountTotal, st.studentsCountYoung, st.studentsCountMiddle, st.studentsCountOld, st.benefitStudentsCountYoung, "
                    + "    st.benefitStudentsCountMiddle, st.benefitStudentsCountOld, st.benefitStudentsCountTotal, st.employeeCount, "
                    + "    case " + (managerList.isEmpty() || !showComplexesByOrgCard ? ""
                    : " when c.idofclient in (:managerList) then 'Комплексы проданные по карте ОО' ")
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "         when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' end as group, "
                    + "    case when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет' "
                    + "     when od.menutype between 50 and 99 then 'Платное питание + Бесплатное' end as type, "
                    + "    od.idoforderdetail, c.idofclient" + " from cf_orders o "
                    + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + " join cf_clients c on c.idofclient = o.idofclient "
                    + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + " join cf_orgs og on og.idoforg = o.idoforg "
                    + " left join cf_kzn_clients_statistic st on st.idoforg = og.idoforg "
                    + " where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 and og.organizationtype = 0 "
                    + "     and cg.idofclientgroup >= :clientGroup and cg.idofclientgroup < :clientEmployees ";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<>();
            List<String> classesNotConditionList = new ArrayList<>();
            if (showYoungerClasses) {
                classesConditionList.add(String.format(conditionString, "", 1, 4));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
            }
            if (showMiddleClasses) {
                classesConditionList.add(String.format(conditionString, "", 5, 9));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
            }
            if (showOlderClasses) {
                classesConditionList.add(String.format(conditionString, "", 10, 11));
            } else {
                classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            List<String> nutritionConditionList = new ArrayList<>();
            List<String> nutritionNotConditionList = new ArrayList<>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                sqlString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                sqlString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }

            //sqlString += generateQueryConditions(managerList, showYoungerClasses, showMiddleClasses, showOlderClasses,
            //        showEmployee, showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard);

            sqlString += ") a "
                    + "group by a.idoforg, a.shortnameinfoservice, a.shortaddress, a.studentsCountTotal, a.studentsCountYoung, a.studentsCountMiddle, a.studentsCountOld, a.benefitStudentsCountYoung, "
                    + "   a.benefitStudentsCountMiddle, a.benefitStudentsCountOld, a.benefitStudentsCountTotal, a.employeeCount, a.group, a.type";

            Query query = session.createSQLQuery(sqlString);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());
            query.setParameterList("idOfOrgList", itemHashMap.keySet());
            if (!managerList.isEmpty() && showComplexesByOrgCard) {
                query.setParameterList("managerList", managerList);
            }
            return query;
        }

        private String generateQueryConditions(List<Long> managerList, Boolean showYoungerClasses,
                Boolean showMiddleClasses, Boolean showOlderClasses, Boolean showEmployee, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard) {
            String resultString = "";
            String conditionString = " cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) %s between %d and %d";
            List<String> classesConditionList = new ArrayList<String>();
            List<String> classesNotConditionList = new ArrayList<String>();
            if (null != showYoungerClasses) {
                if (showYoungerClasses) {
                    classesConditionList.add(String.format(conditionString, "", 1, 4));
                } else {
                    classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
                }
            }
            if (null != showMiddleClasses) {
                if (showMiddleClasses) {
                    classesConditionList.add(String.format(conditionString, "", 5, 9));
                } else {
                    classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
                }
            }
            if (null != showOlderClasses) {
                if (showOlderClasses) {
                    classesConditionList.add(String.format(conditionString, "", 10, 11));
                } else {
                    classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
                }
            }
            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                classesConditionList.add(String.format("c.idofclient in (%s)", StringUtils.join(managerList, ",")));
            }

            if (!classesConditionList.isEmpty()) {
                resultString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
            }
            if (!classesNotConditionList.isEmpty()) {
                resultString += " and " + StringUtils.join(classesNotConditionList, " and ");
            }

            if (!showEmployee) {
                resultString += " and cg.idofclientgroup not in (:clientEmployees, :clientAdministration, :clientTechEmployees)";
            }

            List<String> nutritionConditionList = new ArrayList<String>();
            List<String> nutritionNotConditionList = new ArrayList<String>();
            if (showFreeNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0) ");
            } else {
                nutritionNotConditionList
                        .add(" (od.menutype not between 50 and 99 or od.rprice != 0 or od.discount <= 0) ");
            }
            if (showPaidNutrition) {
                nutritionConditionList.add(" (od.menutype between 50 and 99 and od.rprice > 0) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype not between 50 and 99 or od.rprice <= 0) ");
            }
            if (showBuffet) {
                nutritionConditionList.add(" (od.menutype = 0 and od.menuorigin in (0, 1, 10, 11)) ");
            } else {
                nutritionNotConditionList.add(" (od.menutype != 0 or od.menuorigin not in (0, 1, 10, 11)) ");
            }

            if (!nutritionConditionList.isEmpty()) {
                resultString += " and (" + StringUtils.join(nutritionConditionList, " or ") + ") ";
            }

            if (!nutritionNotConditionList.isEmpty()) {
                resultString += " and " + StringUtils.join(nutritionNotConditionList, " and ");
            }
            return resultString;
        }

        private void updateTemplateByComplexMap(JasperReportBuilder reportBuilder,
                HashMap<String, HashMap<String, List<String>>> complexMap, List<ColumnGridComponentBuilder> colspanList,
                Boolean isByOrgCard) {
            List<String> groupList = new ArrayList<>(complexMap.keySet());
            if (!isByOrgCard) {
                Collections.sort(groupList, new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        Pattern pattern = Pattern.compile("\\D*(\\d{1,2})-\\d{1,2}");
                        Matcher matcher1 = pattern.matcher(obj1);
                        Matcher matcher2 = pattern.matcher(obj2);
                        if (obj1.toLowerCase().startsWith("комплексы") && !obj2.toLowerCase().startsWith("комплексы")) {
                            return 1;
                        } else if (obj2.toLowerCase().startsWith("комплексы") && !obj1.toLowerCase()
                                .startsWith("комплексы")) {
                            return -1;
                        } else if (obj1.toLowerCase().startsWith("комплексы") && obj2.toLowerCase()
                                .startsWith("комплексы")) {
                            return 0;
                        } else if (matcher1.find() && matcher2.find()) {
                            Integer val1 = Integer.parseInt(matcher1.group(1));
                            Integer val2 = Integer.parseInt(matcher2.group(1));
                            return val1.compareTo(val2);
                        } else if (!matcher1.find() && matcher2.find()) {
                            return 1;
                        } else if (matcher1.find() && !matcher2.find()) {
                            return -1;
                        } else {
                            return obj1.compareTo(obj2);
                        }
                    }
                });
            }
            for (String group : groupList) {
                List<ColumnTitleGroupBuilder> groupBuilder = new ArrayList<>();
                List<String> foodTypeList = new ArrayList<>(complexMap.get(group).keySet());
                Collections.sort(foodTypeList, new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        if (obj1.toLowerCase().contains("буфет") && !obj2.toLowerCase().contains("буфет")) {
                            return 1;
                        } else if (!obj1.toLowerCase().contains("буфет") && obj2.toLowerCase().contains("буфет")) {
                            return -1;
                        } else {
                            return obj2.compareTo(obj1);
                        }
                    }
                });
                for (String foodType : foodTypeList) {
                    List<ColumnTitleGroupBuilder> complexBuilder = new ArrayList<>(3);
                    List<String> complexList = complexMap.get(group).get(foodType);
                    Collections.sort(complexList);

                    if (foodType.startsWith(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) {
                        if (!isByOrgCard) {
                            String classString = CoverageNutritionDynamicBean.findClassInString(group);
                            String buffetType = foodType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)
                                    ? CoverageNutritionDynamicBean.BUFFET_HOT
                                    : CoverageNutritionDynamicBean.BUFFET_PAID;

                            String titleName = String
                                    .format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, buffetType,
                                            ' ' + classString);
                            String field = group + foodType + titleName;
                            TextColumnBuilder<Long> buffetClientsCount = DynamicReportUtils
                                    .createColumn(titleName, String.format("%d", field.hashCode()), type.longType(),
                                            DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 170, 30,
                                            50);

                            field = String.format(group + foodType + CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT,
                                    buffetType, classString);
                            TextColumnBuilder<Long> buffetOrdersCount = DynamicReportUtils
                                    .createColumn(CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT,
                                            String.format("%d", field.hashCode()), type.longType(),
                                            DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 170, 30,
                                            50);

                            groupBuilder.add(grid.titleGroup(foodType, buffetClientsCount, buffetOrdersCount)
                                    .setTitleFixedHeight(60));

                            reportBuilder.addColumn(buffetClientsCount, buffetOrdersCount);
                        } else {
                            String buffetType = foodType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)
                                    ? CoverageNutritionDynamicBean.BUFFET_HOT
                                    : CoverageNutritionDynamicBean.BUFFET_PAID;
                            String titleName = String
                                    .format(CoverageNutritionDynamicBean.ORG_CARD_BUFFET_COUNT, buffetType);
                            TextColumnBuilder<Long> buffetOrdersCount = DynamicReportUtils.createColumn(titleName,
                                    String.format("%d", (group + foodType + titleName).hashCode()), type.longType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 170, 30, 50);

                            groupBuilder.add(grid.titleGroup(foodType, buffetOrdersCount).setTitleFixedHeight(60));

                            reportBuilder.addColumn(buffetOrdersCount);
                        }
                    } else {
                        for (String complex : complexList) {
                            if (!isByOrgCard) {
                                String field = group + foodType + complex + CoverageNutritionDynamicBean.CLIENTS_COUNT;
                                TextColumnBuilder<Long> clientsCount = DynamicReportUtils
                                        .createColumn(CoverageNutritionDynamicBean.CLIENTS_COUNT,
                                                String.format("%d", field.hashCode()), type.longType(),
                                                DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 70,
                                                30, 50);

                                field = group + foodType + complex + CoverageNutritionDynamicBean.ORDERS_COUNT;
                                TextColumnBuilder<Long> complexesCount = DynamicReportUtils
                                        .createColumn(CoverageNutritionDynamicBean.ORDERS_COUNT,
                                                String.format("%d", field.hashCode()), type.longType(),
                                                DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 70,
                                                30, 50);

                                field = group + foodType + complex
                                        + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS;
                                TextColumnBuilder<Double> clientsPercentage = DynamicReportUtils
                                        .createColumn(CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS,
                                                String.format("%d", field.hashCode()), type.doubleType(),
                                                DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 70,
                                                30, 50);
                                complexBuilder
                                        .add(grid.titleGroup(complex, clientsCount, complexesCount, clientsPercentage)
                                                .setTitleFixedHeight(30));

                                reportBuilder.addColumn(clientsCount, complexesCount, clientsPercentage);
                            } else {
                                String field =
                                        group + foodType + complex + CoverageNutritionDynamicBean.ORG_CARD_ORDERS_COUNT;
                                TextColumnBuilder<Long> complexesCount = DynamicReportUtils
                                        .createColumn(CoverageNutritionDynamicBean.ORG_CARD_ORDERS_COUNT,
                                                String.format("%d", field.hashCode()), type.longType(),
                                                DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 130,
                                                30, 50);
                                complexBuilder.add(grid.titleGroup(complex, complexesCount).setTitleFixedHeight(30));

                                reportBuilder.addColumn(complexesCount);
                            }

                        }

                        ColumnTitleGroupBuilder titleGroupBuilder = grid.titleGroup().setTitle(foodType)
                                .setTitleFixedHeight(30);
                        for (ColumnTitleGroupBuilder builder : complexBuilder) {
                            titleGroupBuilder.add(builder);
                        }
                        groupBuilder.add(titleGroupBuilder);
                    }
                }

                if (foodTypeList.contains(CoverageNutritionDynamicBean.PAID_NUTRITION) && foodTypeList
                        .contains(CoverageNutritionDynamicBean.FREE_NUTRITION)) {     // paid + free
                    String field = group + CoverageNutritionDynamicBean.PAID_AND_FREE
                            + CoverageNutritionDynamicBean.CLIENTS_COUNT;
                    TextColumnBuilder<Long> clientsCount = DynamicReportUtils
                            .createColumn(CoverageNutritionDynamicBean.CLIENTS_COUNT,
                                    String.format("%d", field.hashCode()), type.longType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 70, 30, 50);

                    field = group + CoverageNutritionDynamicBean.PAID_AND_FREE
                            + CoverageNutritionDynamicBean.ORDERS_COUNT;
                    TextColumnBuilder<Long> complexesCount = DynamicReportUtils
                            .createColumn(CoverageNutritionDynamicBean.ORDERS_COUNT,
                                    String.format("%d", field.hashCode()), type.longType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 70, 30, 50);

                    field = group + CoverageNutritionDynamicBean.PAID_AND_FREE
                            + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS;
                    TextColumnBuilder<Double> clientsPercentage = DynamicReportUtils
                            .createColumn(CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS,
                                    String.format("%d", field.hashCode()), type.doubleType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 70, 30, 50);

                    groupBuilder.add(grid
                            .titleGroup(CoverageNutritionDynamicBean.PAID_AND_FREE, clientsCount, complexesCount,
                                    clientsPercentage).setTitleFixedHeight(60));

                    reportBuilder.addColumn(clientsCount, complexesCount, clientsPercentage);
                }

                if (foodTypeList.contains(
                        CoverageNutritionDynamicBean.MENU_TYPE_BUFFET + ' ' + CoverageNutritionDynamicBean.BUFFET_HOT)
                        && foodTypeList.contains(
                        CoverageNutritionDynamicBean.MENU_TYPE_BUFFET + ' ' + CoverageNutritionDynamicBean.BUFFET_PAID)
                        && !isByOrgCard) {     // paid + hot
                    String titleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "",
                            CoverageNutritionDynamicBean.findClassInString(group));

                    String field = group + CoverageNutritionDynamicBean.MENU_TYPE_BUFFET + ' '
                            + CoverageNutritionDynamicBean.BUFFET_ALL + titleName;
                    TextColumnBuilder<Long> clientsCount = DynamicReportUtils
                            .createColumn(titleName, String.format("%d", field.hashCode()), type.longType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100, 30, 50);

                    field = group + CoverageNutritionDynamicBean.MENU_TYPE_BUFFET + ' '
                            + CoverageNutritionDynamicBean.BUFFET_ALL
                            + CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT;
                    TextColumnBuilder<Long> complexesCount = DynamicReportUtils
                            .createColumn(CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT,
                                    String.format("%d", field.hashCode()), type.longType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 70, 30, 50);

                    groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET + ' '
                            + CoverageNutritionDynamicBean.BUFFET_ALL, clientsCount, complexesCount)
                            .setTitleFixedHeight(60));

                    reportBuilder.addColumn(clientsCount, complexesCount);
                }

                if (!isByOrgCard) {
                    // group totals
                    String field = group + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL
                            + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE;
                    TextColumnBuilder<Long> clientsCountTotal = DynamicReportUtils
                            .createColumn(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE,
                                    String.format("%d", field.hashCode()), type.longType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100, 30, 50);
                    groupBuilder
                            .add(grid.titleGroup(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL, clientsCountTotal)
                                    .setTitleFixedHeight(60));

                    field = group + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS
                            + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE;
                    TextColumnBuilder<Double> activeClientsCount = DynamicReportUtils
                            .createColumn(CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE,
                                    String.format("%d", field.hashCode()), type.doubleType(),
                                    DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 270, 30, 50);
                    groupBuilder.add(grid
                            .titleGroup(CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS, activeClientsCount)
                            .setTitleFixedHeight(60));

                    reportBuilder.addColumn(clientsCountTotal, activeClientsCount);
                }

                colspanList.add(grid.titleGroup(group, groupBuilder.toArray(new ColumnGridComponentBuilder[0]))
                        .setTitleFixedHeight(30));
            }
        }

        private void updateTemplateByEmployees(JasperReportBuilder reportBuilder,
                List<ColumnGridComponentBuilder> colspanList) {
            List<ColumnTitleGroupBuilder> groupBuilder = new ArrayList<>();

            TextColumnBuilder<Long> complexes = DynamicReportUtils
                    .createColumn(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_SUBTITLE, String.format("%d",
                            (CoverageNutritionDynamicBean.EMPLOYEES_TITLE
                                    + CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES
                                    + CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_SUBTITLE).hashCode()),
                            type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100,
                            30, 50);
            groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES, complexes)
                    .setTitleFixedHeight(60));

            TextColumnBuilder<Long> buffet = DynamicReportUtils
                    .createColumn(CoverageNutritionDynamicBean.EMPLOYEES_BUIFFET_SUBTITLE, String.format("%d",
                            (CoverageNutritionDynamicBean.EMPLOYEES_TITLE
                                    + CoverageNutritionDynamicBean.MENU_TYPE_BUFFET
                                    + CoverageNutritionDynamicBean.EMPLOYEES_BUIFFET_SUBTITLE).hashCode()),
                            type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100,
                            30, 50);
            groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET, buffet)
                    .setTitleFixedHeight(60));

            TextColumnBuilder<Long> complexesAndBuffet = DynamicReportUtils
                    .createColumn(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET_SUBTITLE,
                            String.format("%d", (CoverageNutritionDynamicBean.EMPLOYEES_TITLE
                                    + CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET
                                    + CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET_SUBTITLE).hashCode()),
                            type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100,
                            30, 50);
            groupBuilder.add(grid
                    .titleGroup(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET, complexesAndBuffet)
                    .setTitleFixedHeight(60));

            TextColumnBuilder<Double> activeEmployees = DynamicReportUtils
                    .createColumn(CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE, String.format("%d",
                            (CoverageNutritionDynamicBean.EMPLOYEES_TITLE
                                    + CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE
                                    + CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE).hashCode()),
                            type.doubleType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100,
                            30, 50);
            groupBuilder
                    .add(grid.titleGroup(CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE, activeEmployees)
                            .setTitleFixedHeight(60));

            reportBuilder.addColumn(complexes, buffet, complexesAndBuffet, activeEmployees);

            colspanList.add(grid.titleGroup(CoverageNutritionDynamicBean.EMPLOYEES_TITLE,
                    groupBuilder.toArray(new ColumnGridComponentBuilder[0])).setTitleFixedHeight(30));

        }

        private void updateTemplateByTotal(JasperReportBuilder reportBuilder, List<String> totalsTitlesList,
                List<ColumnGridComponentBuilder> colspanList) {
            List<ColumnTitleGroupBuilder> groupBuilder = new ArrayList<>();

            if (totalsTitlesList.contains(CoverageNutritionDynamicBean.PAID_NUTRITION)) {
                TextColumnBuilder<Long> clients = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS, String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_NUTRITION
                                        + CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS).hashCode()),
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);

                TextColumnBuilder<Long> complexes = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES, String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_NUTRITION
                                        + CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES).hashCode()),
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);

                TextColumnBuilder<Long> complexesByOrgCard = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES_BY_ORG_CARD,
                                String.format("%d", (CoverageNutritionDynamicBean.TOTALS_TITLE
                                        + CoverageNutritionDynamicBean.PAID_NUTRITION
                                        + CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES_BY_ORG_CARD).hashCode()),
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);
                groupBuilder.add(grid
                        .titleGroup(CoverageNutritionDynamicBean.PAID_NUTRITION, clients, complexes, complexesByOrgCard)
                        .setTitleFixedHeight(60));

                reportBuilder.addColumn(clients, complexes, complexesByOrgCard);
            }

            if (totalsTitlesList.contains(CoverageNutritionDynamicBean.FREE_NUTRITION)) {
                TextColumnBuilder<Long> clients = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS, String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.FREE_NUTRITION
                                        + CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS).hashCode()),
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);

                TextColumnBuilder<Long> complexes = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES, String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.FREE_NUTRITION
                                        + CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES).hashCode()),
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);
                groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.FREE_NUTRITION, clients, complexes)
                        .setTitleFixedHeight(60));

                reportBuilder.addColumn(clients, complexes);
            }

            if (totalsTitlesList.contains(CoverageNutritionDynamicBean.PAID_NUTRITION) && totalsTitlesList
                    .contains(CoverageNutritionDynamicBean.FREE_NUTRITION)) {
                TextColumnBuilder<Long> clients = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS, String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_AND_FREE
                                        + CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS).hashCode()),
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);

                TextColumnBuilder<Long> complexes = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES, String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_AND_FREE
                                        + CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES).hashCode()),
                                type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);

                TextColumnBuilder<Double> percentageOfUniqueClients = DynamicReportUtils
                        .createColumn(CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS, String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_AND_FREE
                                        + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS).hashCode()),
                                type.doubleType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(),
                                100, 30, 50);

                groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.PAID_AND_FREE, clients, complexes,
                        percentageOfUniqueClients).setTitleFixedHeight(60));
                reportBuilder.addColumn(clients, complexes, percentageOfUniqueClients);
            }

            updateTemplateByTotalWithBuffet(reportBuilder, totalsTitlesList, groupBuilder,
                    CoverageNutritionDynamicBean.BUFFET_PAID_FULL);
            updateTemplateByTotalWithBuffet(reportBuilder, totalsTitlesList, groupBuilder,
                    CoverageNutritionDynamicBean.BUFFET_HOT_FULL);

            if (totalsTitlesList.contains(CoverageNutritionDynamicBean.BUFFET_HOT_FULL) && totalsTitlesList
                    .contains(CoverageNutritionDynamicBean.BUFFET_PAID_FULL)) {
                String titleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "1-11", "");
                TextColumnBuilder<Long> clients = DynamicReportUtils.createColumn(titleName, String.format("%d",
                        (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL
                                + titleName).hashCode()), type.longType(), DynamicReportUtils.detailStyle(),
                        DynamicReportUtils.detailStyle(), 100, 30, 50);

                titleName = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, "");
                TextColumnBuilder<Long> complexes = DynamicReportUtils.createColumn(titleName, String.format("%d",
                        (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL
                                + titleName).hashCode()), type.longType(), DynamicReportUtils.detailStyle(),
                        DynamicReportUtils.detailStyle(), 100, 30, 50);

                titleName = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD, "");
                TextColumnBuilder<Long> complexesByOrgCard = DynamicReportUtils.createColumn(titleName,
                        String.format("%d", (CoverageNutritionDynamicBean.TOTALS_TITLE
                                + CoverageNutritionDynamicBean.BUFFET_ALL_FULL + titleName).hashCode()),
                        type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100, 30,
                        50);
                groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, clients, complexes,
                        complexesByOrgCard).setTitleFixedHeight(60));

                reportBuilder.addColumn(clients, complexes, complexesByOrgCard);
            }

            TextColumnBuilder<Long> uniqueClients = DynamicReportUtils
                    .createColumn(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE, String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE
                                    + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL
                                    + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE).hashCode()),
                            type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100,
                            30, 50);
            groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL, uniqueClients)
                    .setTitleFixedHeight(60));
            reportBuilder.addColumn(uniqueClients);

            TextColumnBuilder<Double> complexes = DynamicReportUtils
                    .createColumn(CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE,
                            String.format("%d", (CoverageNutritionDynamicBean.TOTALS_TITLE
                                    + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS
                                    + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE).hashCode()),
                            type.doubleType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 170,
                            30, 50);
            groupBuilder.add(grid.titleGroup(CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS, complexes)
                    .setTitleFixedHeight(60));
            reportBuilder.addColumn(complexes);

            colspanList.add(grid.titleGroup(CoverageNutritionDynamicBean.TOTALS_TITLE,
                    groupBuilder.toArray(new ColumnGridComponentBuilder[0])).setTitleFixedHeight(30));

        }

        private void updateTemplateByTotalWithBuffet(JasperReportBuilder reportBuilder, List<String> totalsTitlesList,
                List<ColumnTitleGroupBuilder> groupBuilder, String buffetType) {
            if (totalsTitlesList.contains(buffetType)) {
                String titleName = String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "1-11", "");
                TextColumnBuilder<Long> clients = DynamicReportUtils.createColumn(titleName, String.format("%d",
                        (CoverageNutritionDynamicBean.TOTALS_TITLE + buffetType + titleName).hashCode()),
                        type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100, 30,
                        50);

                String buffetTypeSlim = buffetType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)
                        ? CoverageNutritionDynamicBean.BUFFET_HOT : CoverageNutritionDynamicBean.BUFFET_PAID;
                titleName = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, buffetTypeSlim);
                TextColumnBuilder<Long> complexes = DynamicReportUtils.createColumn(titleName, String.format("%d",
                        (CoverageNutritionDynamicBean.TOTALS_TITLE + buffetType + titleName).hashCode()),
                        type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100, 30,
                        50);

                titleName = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD, buffetTypeSlim);
                TextColumnBuilder<Long> complexesByOrgCard = DynamicReportUtils.createColumn(titleName,
                        String.format("%d",
                                (CoverageNutritionDynamicBean.TOTALS_TITLE + buffetType + titleName).hashCode()),
                        type.longType(), DynamicReportUtils.detailStyle(), DynamicReportUtils.detailStyle(), 100, 30,
                        50);
                groupBuilder.add(grid.titleGroup(buffetType, clients, complexes, complexesByOrgCard)
                        .setTitleFixedHeight(60));

                reportBuilder.addColumn(clients, complexes, complexesByOrgCard);
            }
        }

        private HashMap<String, HashMap<String, List<String>>> loadComplexMap(Session session,
                Collection<Long> idOfOrgList, Date startDate, Date endDate, Boolean showYoungerClasses,
                Boolean showMiddleClasses, Boolean showOlderClasses, Boolean showEmployee, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet) {
            HashMap<String, HashMap<String, List<String>>> complexMap = new HashMap<>();
            String sqlString = "select distinct "
                    + "   case when od.menutype between 50 and 99 then od.menudetailname else '' end as complexname, "
                    + "   case when (od.menutype between 50 and 99 or od.menutype = 0) and od.rprice > 0 then od.rprice "
                    + "       when (od.menutype between 50 and 99 or od.menutype = 0) and od.rprice = 0 and od.discount > 0 then od.discount else 0 end as price, "
                    + "   case when od.menutype = 0 and od.menuorigin in (0, 1) then 'Буфет горячее' "
                    + "       when od.menutype = 0 and od.menuorigin in (10, 11) then 'Буфет покупная'"
                    + "       when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "       when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type, "
                    + "   case when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "        when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "        when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' "
                    + "        when cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees) then 'Сотрудники' end as gr "
                    + "from cf_orders o "
                    + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + "join cf_orgs og on o.idoforg = og.idoforg " + "join cf_clients c on c.idofclient = o.idofclient "
                    + "join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 "
                    + "     and og.organizationtype = 0 and cg.idofclientgroup between :clientGroup and :clientTechEmployees";

            sqlString += generateQueryConditions(Collections.EMPTY_LIST, showYoungerClasses, showMiddleClasses,
                    showOlderClasses, showEmployee, showFreeNutrition, showPaidNutrition, showBuffet, false);
            Query query = session.createSQLQuery(sqlString);
            query.setParameterList("idOfOrgList", idOfOrgList);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue());

            List list = query.list();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                String complexName = (String) row[0];
                Long price = null != row[1] ? ((BigInteger) row[1]).longValue() : 0;
                String foodType = (String) row[2];
                String group = (String) row[3];

                if (!complexMap.containsKey(group)) {
                    complexMap.put(group, new HashMap<String, List<String>>());
                }

                if (!complexMap.get(group).containsKey(foodType)) {
                    complexMap.get(group).put(foodType, new ArrayList<String>());
                }

                if (!foodType.startsWith(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) {
                    complexMap.get(group).get(foodType).add(complexName + priceFormat(price));
                }
            }
            return complexMap;
        }

        private List<String> loadTotalsTitles(Session session, Collection<Long> idOfOrgList, Date startDate,
                Date endDate, Boolean showYoungerClasses, Boolean showMiddleClasses, Boolean showOlderClasses,
                Boolean showEmployee, Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet,
                Boolean showComplexesByOrgCard, List<Long> managerList) {
            String sqlString = "select distinct "
                    + "   case when od.menutype = 0 and od.menuorigin in (0, 1) then 'Буфет горячее' "
                    + "       when od.menutype = 0 and od.menuorigin in (10, 11) then 'Буфет покупная'"
                    + "       when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "       when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type "
                    + "from cf_orders o "
                    + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + "join cf_orgs og on o.idoforg = og.idoforg " + "join cf_clients c on c.idofclient = o.idofclient "
                    + "join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 "
                    + "     and og.organizationtype = 0 ";

            sqlString += generateQueryConditions(managerList, showYoungerClasses, showMiddleClasses, showOlderClasses,
                    showEmployee, showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard);
            Query query = session.createSQLQuery(sqlString);
            query.setParameterList("idOfOrgList", idOfOrgList);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            if (!showEmployee) {
                query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
                query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            }

            return query.list();
        }

        private HashMap<String, HashMap<String, List<String>>> loadOrgCardComplexMap(Session session,
                Collection<Long> idOfOrgList, Date startDate, Date endDate, List<Long> managerList,
                Boolean showFreeNutrition, Boolean showPaidNutrition, Boolean showBuffet) {
            HashMap<String, HashMap<String, List<String>>> complexMap = new HashMap<>();
            String sqlString = "select distinct "
                    + "   case when od.menutype between 50 and 99 or od.menutype = 0 then od.menudetailname else '' end as complexname, "
                    + "   case when (od.menutype between 50 and 99 or od.menutype = 0) and od.rprice > 0 then od.rprice "
                    + "       when (od.menutype between 50 and 99 or od.menutype = 0) and od.rprice = 0 and od.discount > 0 then od.discount else 0 end as price, "
                    + "   case when od.menutype = 0 and od.menuorigin in (0, 1) then 'Буфет горячее' "
                    + "       when od.menutype = 0 and od.menuorigin in (10, 11) then 'Буфет покупная'"
                    + "       when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "       when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as type "
                    + "from cf_orders o "
                    + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + "join cf_orgs og on o.idoforg = og.idoforg " + "join cf_clients c on c.idofclient = o.idofclient "
                    + "join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and od.menutype < 150 "
                    + "     and og.organizationtype = 0 and c.idofclient in (:managerList)";

            sqlString += generateQueryConditions(managerList, null, null, null, true, showFreeNutrition,
                    showPaidNutrition, showBuffet, true);
            Query query = session.createSQLQuery(sqlString);
            query.setParameterList("idOfOrgList", idOfOrgList);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameterList("managerList", managerList);

            List list = query.list();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                String complexName = (String) row[0];
                Long price = null != row[1] ? ((BigInteger) row[1]).longValue() : 0;
                String foodType = (String) row[2];

                if (!complexMap.containsKey(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES)) {
                    complexMap
                            .put(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES, new HashMap<String, List<String>>());
                }

                if (!complexMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).containsKey(foodType)) {
                    complexMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES)
                            .put(foodType, new ArrayList<String>());
                }

                if (!foodType.startsWith(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) {
                    complexMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).get(foodType)
                            .add(complexName + priceFormat(price));
                }
            }
            return complexMap;
        }

        private List<Long> loadOrgList(Session session, List<Long> idOfSourceOrgList, List<Long> idOfOrgList) {
            if (idOfSourceOrgList.isEmpty()) {
                return idOfOrgList;
            }
            Criteria criteria = session.createCriteria(Org.class)
                    .createAlias("sourceMenuOrgs", "sm", JoinType.LEFT_OUTER_JOIN)
                    .createAlias("categoriesInternal", "cat", JoinType.LEFT_OUTER_JOIN)
                    .add(Restrictions.in("sm.idOfOrg", idOfSourceOrgList))
                    .setProjection(Projections.projectionList().add(Projections.groupProperty("idOfOrg")))
                    .addOrder(Order.asc("idOfOrg"));
            if (!idOfOrgList.isEmpty()) {
                criteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            }
            return criteria.list();
        }

        private List<Long> parseStringAsLongList(String propertyName) {
            String propertyValueString = reportProperties.getProperty(propertyName);
            String[] propertyValueArray = StringUtils.split(propertyValueString, ',');
            List<Long> propertyValueList = new ArrayList<Long>();
            for (String propertyValue : propertyValueArray) {
                try {
                    propertyValueList.add(Long.parseLong(propertyValue));
                } catch (NumberFormatException e) {
                    logger.error(String.format("Unable to parse propertyValue: property = %s, value = %s", propertyName,
                            propertyValue), e);
                }
            }
            return propertyValueList;
        }

        private void loadEmployeesByOrgs(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                Date startDate, Date endDate) {
            for (Long idOfOrg : itemHashMap.keySet()) {
                ClientGroup employeeGroup = DAOUtils.findKznEmployeeGroupByOrgId(session, idOfOrg);

                String orgCondition;
                if (null != employeeGroup) {
                    orgCondition = " and cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees, :employees) ";
                } else {
                    orgCondition = " and cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees) ";
                }

                String sqlString = "select a.idoforg, a.type, count(distinct a.idofclient) as idofclient " + "from ( "
                        + " select og.idoforg, "
                        + "     case when od.menutype = 0 and od.menuorigin in (0, 1, 10, 11) then 'Буфет' "
                        + "      when od.menutype between 50 and 99 then 'Комплексы' else '' end as type, "
                        + "     o.idofclient" + " from cf_orders o "
                        + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                        + " join cf_clients c on c.idofclient = o.idofclient "
                        + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                        + " left join cf_goods g on g.idofgood = od.idofgood join cf_orgs og on og.idoforg = o.idoforg "
                        + " where o.idoforg = :idOfOrg and o.createddate between :startDate and :endDate and od.menutype < :complexItemMin and og.organizationtype = 0 "
                        + orgCondition + " order by 3) a " + "group by a.idoforg, a.type";
                Query query = session.createSQLQuery(sqlString);
                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
                query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
                if (null != employeeGroup) {
                    query.setParameter("employees", employeeGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                }
                query.setParameter("complexItemMin", OrderDetail.TYPE_COMPLEX_ITEM_MIN);
                query.setParameter("idOfOrg", idOfOrg);
                List list = query.list();

                for (Object o : list) {
                    Object[] row = (Object[]) o;
                    Long orgId = (null == row[0]) ? 0L : ((BigInteger) row[0]).longValue();
                    String foodType = (String) row[1];
                    Long orderCount = (null == row[2]) ? 0L : ((BigInteger) row[2]).longValue();

                    CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                    HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                    String buffetType = foodType.contains(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)
                            ? CoverageNutritionDynamicBean.MENU_TYPE_BUFFET
                            : CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES;

                    String titleName = String.format(CoverageNutritionDynamicBean.EMPLOYEES_TITLE + foodType + "%s",
                            buffetType.equals(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)
                                    ? CoverageNutritionDynamicBean.EMPLOYEES_BUIFFET_SUBTITLE
                                    : CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_SUBTITLE);

                    dynamicPropertyList.put(String.format("%d", titleName.hashCode()), new DynamicProperty(orderCount));
                }
            }
        }

        private void loadEmployeesTotalsByOrgs(Session session, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                Date startDate, Date endDate) {
            for (Long idOfOrg : itemHashMap.keySet()) {
                ClientGroup employeeGroup = DAOUtils.findKznEmployeeGroupByOrgId(session, idOfOrg);

                String orgCondition;
                if (null != employeeGroup) {
                    orgCondition = " and cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees, :employees) ";
                } else {
                    orgCondition = " and cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees) ";
                }

                String sqlString = "select a.idoforg, count(distinct a.idofclient) as idofclient " + "from ( "
                        + " select og.idoforg, o.idofclient" + " from cf_orders o "
                        + " join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                        + " join cf_clients c on c.idofclient = o.idofclient "
                        + " join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                        + " left join cf_goods g on g.idofgood = od.idofgood join cf_orgs og on og.idoforg = o.idoforg "
                        + " where o.idoforg = :idOfOrg and o.createddate between :startDate and :endDate and od.menutype < :complexItemMin and og.organizationtype = 0 "
                        + orgCondition + ") a group by a.idoforg";
                Query query = session.createSQLQuery(sqlString);
                query.setParameter("startDate", startDate.getTime());
                query.setParameter("endDate", endDate.getTime());
                query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
                query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
                if (null != employeeGroup) {
                    query.setParameter("employees", employeeGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                }
                query.setParameter("complexItemMin", OrderDetail.TYPE_COMPLEX_ITEM_MIN);
                query.setParameter("idOfOrg", idOfOrg);
                List list = query.list();

                for (Object o : list) {
                    Object[] row = (Object[]) o;
                    Long orgId = (null == row[0]) ? 0L : ((BigInteger) row[0]).longValue();
                    Long orderCount = (null == row[1]) ? 0L : ((BigInteger) row[1]).longValue();

                    CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                    HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();

                    dynamicPropertyList.put(String.format("%d", (CoverageNutritionDynamicBean.EMPLOYEES_TITLE
                                    + CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET
                                    + CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET_SUBTITLE).hashCode()),
                            new DynamicProperty(orderCount));

                    Long employeesTotal = item.getEmployeeCount();
                    dynamicPropertyList.put(String.format("%d", (CoverageNutritionDynamicBean.EMPLOYEES_TITLE
                                    + CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE
                                    + CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE).hashCode()),
                            new DynamicProperty(employeesTotal.equals(0L) ? 0D
                                    : orderCount.doubleValue() / employeesTotal.doubleValue() * 100D));
                }
            }
        }

        private List<Long> loadManagers(Session session, List<Long> orgList) {
            Criteria criteria = session.createCriteria(Client.class);
            criteria.createAlias("person", "p");
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.like("p.firstName", "#%"));
            disjunction.add(Restrictions.like("p.secondName", "#%"));
            disjunction.add(Restrictions.like("p.surname", "#%"));
            criteria.add(disjunction);
            criteria.add(Restrictions.in("org.idOfOrg", DAOUtils.findFriendlyOrgsIds(session, orgList)));
            criteria.setProjection(Property.forName("idOfClient"));
            return criteria.list();
        }

        private String priceFormat(Long price) {
            if (price.equals(0L)) {
                return "";
            }

            Integer rub = new Double(price.doubleValue() / 100.f).intValue();
            Integer cop = new Long(price - rub * 100).intValue();

            String moneyString = String.format(" - %d руб.", rub);

            if (!cop.equals(0)) {
                moneyString += String.format(" %02d коп.", cop);
            }
            return moneyString;
        }

        public String getTemplateFilename() {
            return templateFilename;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }
    }

    public CoverageNutritionReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public CoverageNutritionReport() {

    }

    public static class CoverageNutritionReportItem extends CoverageNutritionDynamicBean {

        private String schoolName;
        private String schoolAddress;
        private Long studentsCountTotal;
        private Long studentsCountYoung;
        private Long studentsCountMiddle;
        private Long studentsCountOld;
        private Long benefitStudentsCountYoung;
        private Long benefitStudentsCountMiddle;
        private Long benefitStudentsCountOld;
        private Long benefitStudentsCountTotal;
        private Long employeeCount;

        public CoverageNutritionReportItem() {
            studentsCountTotal = 0L;
            studentsCountYoung = 0L;
            studentsCountMiddle = 0L;
            studentsCountOld = 0L;
            benefitStudentsCountYoung = 0L;
            benefitStudentsCountMiddle = 0L;
            benefitStudentsCountOld = 0L;
            benefitStudentsCountTotal = 0L;
            employeeCount = 0L;
        }

        public Long getStatisticDataPerGroup(String group, Boolean benefit) {
            if (ClassType.YOUNG.getValue().equals(group)) {
                if (null == benefit) {
                    return this.studentsCountYoung;
                }
                if (benefit) {
                    return this.benefitStudentsCountYoung;
                }
                return this.studentsCountYoung - this.benefitStudentsCountYoung;
            }
            if (ClassType.MIDDLE.getValue().equals(group)) {
                if (null == benefit) {
                    return this.studentsCountMiddle;
                }
                if (benefit) {
                    return this.benefitStudentsCountMiddle;
                }
                return this.studentsCountMiddle - this.benefitStudentsCountMiddle;
            }
            if (ClassType.OLD.getValue().equals(group)) {
                if (null == benefit) {
                    return this.studentsCountOld;
                }
                if (benefit) {
                    return this.benefitStudentsCountOld;
                }
                return this.studentsCountOld - this.benefitStudentsCountOld;
            }
            return 0L;
        }

        public void fillStatistic(KznClientsStatistic statistic) {
            this.setSchoolName(statistic.getOrg().getShortNameInfoService());
            this.setSchoolAddress(statistic.getOrg().getShortAddress());
            this.setStudentsCountTotal(statistic.getStudentsCountTotal());
            this.setStudentsCountYoung(statistic.getStudentsCountYoung());
            this.setStudentsCountMiddle(statistic.getStudentsCountMiddle());
            this.setStudentsCountOld(statistic.getStudentsCountOld());
            this.setBenefitStudentsCountYoung(statistic.getBenefitStudentsCountYoung());
            this.setBenefitStudentsCountMiddle(statistic.getBenefitStudentsCountMiddle());
            this.setBenefitStudentsCountOld(statistic.getBenefitStudentsCountOld());
            this.setBenefitStudentsCountTotal(statistic.getBenefitStudentsCountTotal());
            this.setEmployeeCount(statistic.getEmployeeCount());
        }

        public void fillOrganization(Org org) {
            this.setSchoolName(org.getShortNameInfoService());
            this.setSchoolAddress(org.getShortAddress());
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSchoolAddress() {
            return schoolAddress;
        }

        public void setSchoolAddress(String schoolAddress) {
            this.schoolAddress = schoolAddress;
        }

        public Long getStudentsCountTotal() {
            return studentsCountTotal;
        }

        public void setStudentsCountTotal(Long studentsCountTotal) {
            this.studentsCountTotal = studentsCountTotal;
        }

        public Long getStudentsCountYoung() {
            return studentsCountYoung;
        }

        public void setStudentsCountYoung(Long studentsCountYoung) {
            this.studentsCountYoung = studentsCountYoung;
        }

        public Long getStudentsCountMiddle() {
            return studentsCountMiddle;
        }

        public void setStudentsCountMiddle(Long studentsCountMiddle) {
            this.studentsCountMiddle = studentsCountMiddle;
        }

        public Long getStudentsCountOld() {
            return studentsCountOld;
        }

        public void setStudentsCountOld(Long studentsCountOld) {
            this.studentsCountOld = studentsCountOld;
        }

        public Long getBenefitStudentsCountYoung() {
            return benefitStudentsCountYoung;
        }

        public void setBenefitStudentsCountYoung(Long benefitStudentsCountYoung) {
            this.benefitStudentsCountYoung = benefitStudentsCountYoung;
        }

        public Long getBenefitStudentsCountMiddle() {
            return benefitStudentsCountMiddle;
        }

        public void setBenefitStudentsCountMiddle(Long benefitStudentsCountMiddle) {
            this.benefitStudentsCountMiddle = benefitStudentsCountMiddle;
        }

        public Long getBenefitStudentsCountOld() {
            return benefitStudentsCountOld;
        }

        public void setBenefitStudentsCountOld(Long benefitStudentsCountOld) {
            this.benefitStudentsCountOld = benefitStudentsCountOld;
        }

        public Long getBenefitStudentsCountTotal() {
            return benefitStudentsCountTotal;
        }

        public void setBenefitStudentsCountTotal(Long benefitStudentsCountTotal) {
            this.benefitStudentsCountTotal = benefitStudentsCountTotal;
        }

        public Long getEmployeeCount() {
            return employeeCount;
        }

        public void setEmployeeCount(Long employeeCount) {
            this.employeeCount = employeeCount;
        }
    }

    public enum ClassType {
        YOUNG("1-4"), MIDDLE("5-9"), OLD("10-11");
        private final String value;

        ClassType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
