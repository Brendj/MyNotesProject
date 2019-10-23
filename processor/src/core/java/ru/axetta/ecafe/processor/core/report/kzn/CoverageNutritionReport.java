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
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.KznClientsStatistic;
import ru.axetta.ecafe.processor.core.persistence.Org;
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
    private final static String SOTR = "Сотрудники";

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
            /*loadGroupsData(session, itemHashMap, managerList, startDate, endDate, showFreeNutrition, showPaidNutrition,
                    showBuffet, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses, showOlderClasses,
                    showEmployee);*/

            if (showComplexesByOrgCard && !managerList.isEmpty()) {
                HashMap<String, HashMap<String, List<String>>> orgCardComplexMap = loadOrgCardComplexMap(session,
                        itemHashMap.keySet(), startDate, endDate, managerList, showFreeNutrition, showPaidNutrition,
                        showBuffet);
                updateTemplateByComplexMap(reportBuilder, orgCardComplexMap, columnTitleGroupBuilderList, true);
                //loadComplexByOrgCardData(session, itemHashMap, startDate, endDate, managerList);
                //loadBuffetByOrgCardData(session, itemHashMap, startDate, endDate, managerList);
            }

            if (showTotal) {
                List<String> totalsTitlesList = loadTotalsTitles(session, itemHashMap.keySet(), startDate, endDate,
                        showYoungerClasses, showMiddleClasses, showOlderClasses, showEmployee, showFreeNutrition,
                        showPaidNutrition, showBuffet, showComplexesByOrgCard, managerList);
                updateTemplateByTotal(reportBuilder, totalsTitlesList, columnTitleGroupBuilderList);
                /*loadTotalsData(session, itemHashMap, managerList, startDate, endDate, showFreeNutrition,
                        showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses, showMiddleClasses,
                        showOlderClasses);
                loadTotalsPaidAndFreeAndBuffetAllDataTotal(session, itemHashMap, managerList, startDate, endDate,
                        showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard, showYoungerClasses,
                        showMiddleClasses, showOlderClasses);
                loadTotalsUniqueClientsData(session, itemHashMap, startDate, endDate, showFreeNutrition,
                        showPaidNutrition, showBuffet, showYoungerClasses, showMiddleClasses, showOlderClasses);*/
            }

            if (showEmployee) {
                updateTemplateByEmployees(reportBuilder, columnTitleGroupBuilderList);
                //loadEmployeesByOrgs(session, itemHashMap, startDate, endDate);
                //loadEmployeesTotalsByOrgs(session, itemHashMap, startDate, endDate);
            }

            List<CNReportItem> totalItems = loadAllData(session, idOfOrgList, startDate, endDate);
            logger.info(String.format("Получено %s записей", totalItems.size()));
            fillReport(totalItems, itemHashMap, orgList, complexMap);

            return itemHashMap.values();
        }

        //Выбираем заказы по одной ОО из общего списка
        private List<CNReportItem> getOrgReportItems(Long idOfOrg, List<CNReportItem> reportItems) {
            List<CNReportItem> result = new ArrayList<>();
            for (CNReportItem item : reportItems) {
                if (item.getIdOfOrg().equals(idOfOrg)) result.add(item);
            }
            return result;
        }

        //Заполнение отчета
        private void fillReport(List<CNReportItem> reportItems, HashMap<Long, CoverageNutritionReportItem> itemHashMap,
                List<Long> orgList, HashMap<String, HashMap<String, List<String>>> complexMap) {
            for (Long idOfOrg : orgList) {
                logger.info(String.format("Формирование данных для ОО ид=%s", idOfOrg));
                List<CNReportItem> orgReportItems = getOrgReportItems(idOfOrg, reportItems);//инфа с заказами по одной ОО
                HashMap<String, HashMap<String, HashMap<String, HashSet<Long>>>> clientCountMap = new HashMap<>(); //здесь будет количество клиентов по каждому комплексу
                HashMap<String, HashMap<String, HashSet<Long>>> clientBuffetCountMap = new HashMap<>(); //здесь будет количество клиентов по буфету (с разделением на покупное и горячее)

                HashMap<String, HashMap<String, HashMap<String, Integer>>> clientQtyMap = new HashMap<>(); //здесь будет количество проданных позиций по каждому комплексу
                HashMap<String, HashMap<String, Integer>> clientBuffetQtyMap = new HashMap<>(); //здесь будет количество проданных позиций по буфету (с разделением на покупное и горячее)

                HashMap<String, HashMap<String, HashSet<Long>>> clientBuffetCountTotalMap = new HashMap<>(); //здесь будет количество клиентов по буфету всего (Буфет общее)
                HashMap<String, HashMap<String, Integer>> clientBuffetQtyTotalMap = new HashMap<>(); //здесь будет количество проданных позиций по буфету всего (Буфет общее)

                HashMap<String, HashMap<String, HashSet<Long>>> clientCountPaidAndFreeMap = new HashMap<>(); //здесь будет количество клиентов по комплексам Платное + Бесплатное
                HashMap<String, HashMap<String, Integer>> clientQtyPaidAndFreeMap = new HashMap<>(); //здесь будет количество проданных позиций по комплексам Платное + Бесплатное

                HashMap<String, HashMap<String, HashSet<Long>>> clientCountTotalMap = new HashMap<>(); //здесь будет количество уникальных покупателей Всего

                HashMap<String, HashSet<Long>> employeeCountMap = new HashMap<>(); //здесь будет количества по сотрудникам

                HashMap<String, HashMap<String, HashMap<String, Integer>>> zavProizvQtyMap = new HashMap<>(); //здесь будет количество проданных позиций по карте зав. производством
                HashMap<String, Integer> zavProizvCountMap = new HashMap<>(); //здесь будет количества по буфету по карте зав. производства

                HashMap<String, HashSet<Long>> totalCountMap = new HashMap<>(); //здесь будет количества клиентов в Итого (отдельные разделы комплексы и буфет))
                HashMap<String, HashSet<Long>> totalPaidAndFreeCountMap = new HashMap<>(); //здесь будет количества клиентов в Итого Платное + Бесплатное
                HashMap<String, Integer> totalQtyMap = new HashMap<>(); //здесь будет количества позиций комплексов в Итого
                HashMap<String, Integer> totalPaidAndFreeQtyMap = new HashMap<>(); //здесь будет количества позиций комплексов в Итого
                HashMap<String, HashSet<Long>> totalBuffetCountMap = new HashMap<>(); //здесь будет количества клиентов в Итого Буфет общее
                HashMap<String, Integer> totalBuffetQtyMap = new HashMap<>(); //здесь будет количества позиций буфета в Итого Буфет общее
                HashMap<String, Integer> totalZavProizvQtyMap = new HashMap<>(); //здесь будет количества позиций буфета по карте зав. производством
                HashMap<String, Integer> totalBuffetZavProizvQtyMap = new HashMap<>(); //здесь будет количества позиций Буфет общее по карте зав. производством
                HashMap<String, HashSet<Long>> totalComplexBuffetCountMap = new HashMap<>(); //здесь будет количества клиентов в Итого Комплексы + Буфет

                for (CNReportItem item : orgReportItems) {
                    tryFillEmployeeData(employeeCountMap, item);
                    tryFillZavProizvData(zavProizvCountMap, item);
                    tryFillTotalData(totalCountMap, totalPaidAndFreeCountMap, totalQtyMap, totalPaidAndFreeQtyMap,
                            totalBuffetCountMap, totalBuffetQtyMap, totalZavProizvQtyMap, totalBuffetZavProizvQtyMap, totalComplexBuffetCountMap, item);
                    Map<String, List<String>> map1 = complexMap.get(item.getGroupNameForTemplate());
                    if (map1 == null) continue;
                    List<String> list = map1.get(item.getFoodType());
                    if (item.isBuffet()) {
                        putClientIntoBuffetCountMap(clientBuffetCountMap, clientBuffetCountTotalMap, clientCountTotalMap, item);
                        putClientIntoBuffetQtyMap(clientBuffetQtyMap, clientBuffetQtyTotalMap, item);
                        continue;
                    }
                    if (list == null) continue;
                    for (String complex : list) {
                        if (complex.equals(item.getComplexNameForTemplate())) {
                            putClientIntoComplexCountMap(clientCountMap, clientCountPaidAndFreeMap, clientCountTotalMap, item);
                            putClientIntoComplexQtyMap(clientQtyMap, clientQtyPaidAndFreeMap, item);
                            putClientIntoComplexZavProizvQtyMap(zavProizvQtyMap, item);
                        }
                    }
                }
                CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();
                //данные в отчет по количеству покупателей по комплексам
                for (String group : clientCountMap.keySet()) {
                    HashMap<String, HashMap<String, HashSet<Long>>> foodTypeMap = clientCountMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        HashMap<String, HashSet<Long>> complexNameMap = foodTypeMap.get(foodType);
                        for (String complexName : complexNameMap.keySet()) {
                            dynamicPropertyList.put(String.format("%d",
                                    (group + foodType + complexName + CoverageNutritionDynamicBean.CLIENTS_COUNT).hashCode()), new DynamicProperty(Long.valueOf(complexNameMap.get(complexName).size())));
                            dynamicPropertyList.put(String.format("%d",
                                    (group + foodType + complexName + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS).hashCode()),
                                    new DynamicProperty(getPercent(item, group, complexNameMap.get(complexName).size())));
                        }
                    }
                }
                //данные в отчет по количеству покупателей по буфету с разделением на горячее и покупное
                for (String group : clientBuffetCountMap.keySet()) {
                    HashMap<String, HashSet<Long>> foodTypeMap = clientBuffetCountMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        HashSet<Long> clientsSet = foodTypeMap.get(foodType);
                        String buffetType = foodType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)
                                ? CoverageNutritionDynamicBean.BUFFET_HOT : CoverageNutritionDynamicBean.BUFFET_PAID;
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, buffetType + " ", CoverageNutritionDynamicBean.findClassInString(group))).hashCode()),
                                new DynamicProperty(Long.valueOf(clientsSet.size())));
                    }
                }
                //данные в отчет по количеству проданных комплексов
                for (String group : clientQtyMap.keySet()) {
                    HashMap<String, HashMap<String, Integer>> foodTypeMap = clientQtyMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        HashMap<String, Integer> complexNameMap = foodTypeMap.get(foodType);
                        for (String complexName : complexNameMap.keySet()) {
                            dynamicPropertyList.put(String.format("%d",
                                    (group + foodType + complexName + CoverageNutritionDynamicBean.ORDERS_COUNT).hashCode()), new DynamicProperty(Long.valueOf(complexNameMap.get(complexName))));
                        }
                    }
                }
                //данные в отчет по количеству проданных позиций буфета с разделением на горячее и покупное
                for (String group : clientBuffetQtyMap.keySet()) {
                    HashMap<String, Integer> foodTypeMap = clientBuffetQtyMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        //HashSet<Long> clientsSet = foodTypeMap.get(foodType);
                        Integer qty = foodTypeMap.get(foodType);
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT).hashCode()),
                                new DynamicProperty(Long.valueOf(qty)));
                    }
                }
                //данные в отчет по количеству покупателей по Буфету общее
                for (String group : clientBuffetCountTotalMap.keySet()) {
                    HashMap<String, HashSet<Long>> foodTypeMap = clientBuffetCountTotalMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        HashSet<Long> clientsSet = foodTypeMap.get(foodType);
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + String.format(CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT, "", CoverageNutritionDynamicBean.findClassInString(group))).hashCode()),
                                new DynamicProperty(Long.valueOf(clientsSet.size())));
                    }
                }
                //данные в отчет по количеству проданных позиций буфета общее
                for (String group : clientBuffetQtyTotalMap.keySet()) {
                    HashMap<String, Integer> foodTypeMap = clientBuffetQtyTotalMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        Integer qty = foodTypeMap.get(foodType);
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + CoverageNutritionDynamicBean.BUFFET_ORDERS_COUNT).hashCode()),
                                new DynamicProperty(Long.valueOf(qty)));
                    }
                }
                //данные в отчет по количеству покупателей по комплексам Платное + Бесплатное
                for (String group : clientCountPaidAndFreeMap.keySet()) {
                    HashMap<String, HashSet<Long>> foodTypeMap = clientCountPaidAndFreeMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        HashSet<Long> clientsSet = foodTypeMap.get(foodType);
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + CoverageNutritionDynamicBean.CLIENTS_COUNT).hashCode()),
                                new DynamicProperty(Long.valueOf(clientsSet.size())));
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS).hashCode()),
                                new DynamicProperty(getPercent(item, group, clientsSet.size())));
                    }
                }
                //данные в отчет по количеству комплексов Платное + Бесплатное
                for (String group : clientQtyPaidAndFreeMap.keySet()) {
                    HashMap<String, Integer> foodTypeMap = clientQtyPaidAndFreeMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        Integer qty = foodTypeMap.get(foodType);
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + CoverageNutritionDynamicBean.ORDERS_COUNT).hashCode()),
                                new DynamicProperty(Long.valueOf(qty)));
                    }
                }
                //данные в отчет по количеству уникальных покупателей всего
                for (String group : clientCountTotalMap.keySet()) {
                    HashMap<String, HashSet<Long>> foodTypeMap = clientCountTotalMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        HashSet<Long> clientsSet = foodTypeMap.get(foodType);
                        dynamicPropertyList.put(String.format("%d",
                                (group + foodType + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE).hashCode()),
                                new DynamicProperty(Long.valueOf(clientsSet.size())));
                        dynamicPropertyList.put(String.format("%d",
                                (group + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE).hashCode()),
                                new DynamicProperty(getPercent(item, CoverageNutritionDynamicBean.TOTAL_STUDENTS, clientsSet.size())));
                    }
                }
                //данные в отчет по количеству сотрудников с комплексами
                for (String foodType : employeeCountMap.keySet()) {
                    HashSet<Long> clientsSet = employeeCountMap.get(foodType);
                    String key = "";
                    if (foodType.equals(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES)) key = CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_SUBTITLE;
                    if (foodType.equals(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) key = CoverageNutritionDynamicBean.EMPLOYEES_BUIFFET_SUBTITLE;
                    if (foodType.equals(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET)) key = CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET_SUBTITLE;
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.EMPLOYEES_TITLE + foodType + key).hashCode()),
                            new DynamicProperty(Long.valueOf(clientsSet.size())));
                    if (foodType.equals(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET)) {
                        dynamicPropertyList.put(String.format("%d",
                                (CoverageNutritionDynamicBean.EMPLOYEES_TITLE + CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE
                                        + CoverageNutritionDynamicBean.EMPLOYEES_PERCCENTAGE_OF_ACTIVE).hashCode()),
                                new DynamicProperty(getPercent(item, CoverageNutritionDynamicBean.TOTAL_EMPLOYEE, clientsSet.size())));
                    }
                }
                //данные в отчет по количеству проданных комплексов по карте ОО
                for (String group : zavProizvQtyMap.keySet()) {
                    HashMap<String, HashMap<String, Integer>> foodTypeMap = zavProizvQtyMap.get(group);
                    for (String foodType : foodTypeMap.keySet()) {
                        HashMap<String, Integer> complexNameMap = foodTypeMap.get(foodType);
                        for (String complexName : complexNameMap.keySet()) {
                            dynamicPropertyList.put(String.format("%d",
                                    (group + foodType + complexName + CoverageNutritionDynamicBean.ORG_CARD_ORDERS_COUNT).hashCode()), new DynamicProperty(Long.valueOf(complexNameMap.get(complexName))));
                        }
                    }
                }
                //данные в отчет по количеству буфета по карте ОО
                for (String foodType : zavProizvCountMap.keySet()) {
                    Integer qty = zavProizvCountMap.get(foodType);
                    String key = "";
                    if (foodType.contains(CoverageNutritionDynamicBean.BUFFET_PAID)) key = CoverageNutritionDynamicBean.BUFFET_PAID;
                    if (foodType.contains(CoverageNutritionDynamicBean.BUFFET_HOT)) key = CoverageNutritionDynamicBean.BUFFET_HOT;
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES + foodType + String.format(CoverageNutritionDynamicBean.ORG_CARD_BUFFET_COUNT, key)).hashCode()),
                            new DynamicProperty(Long.valueOf(qty)));
                }
                //данные в отчет по количеству сотрудников в Итого (Платное, Бесплатное, Буфет покупная, Буфет горячее)
                for (String foodType : totalCountMap.keySet()) {
                    HashSet<Long> clientsSet = totalCountMap.get(foodType);
                    String key = "";
                    if (foodType.equals(CoverageNutritionDynamicBean.PAID_NUTRITION) || foodType.equals(CoverageNutritionDynamicBean.FREE_NUTRITION)) key = CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS;
                    if (foodType.contains(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET)) key = CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT_WITHOUT_CLASSES;
                    //if (foodType.equals(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET)) key = CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET_SUBTITLE;
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType + key).hashCode()),
                            new DynamicProperty(Long.valueOf(clientsSet.size())));
                }
                //данные в отчет по количеству сотрудников в Итого (Платное + Бесплатное)
                for (String foodType : totalPaidAndFreeCountMap.keySet()) {
                    HashSet<Long> clientsSet = totalPaidAndFreeCountMap.get(foodType);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_AND_FREE + CoverageNutritionDynamicBean.TOTALS_UNIQUE_BUYERS).hashCode()),
                            new DynamicProperty(Long.valueOf(clientsSet.size())));
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_AND_FREE + CoverageNutritionDynamicBean.PERCENTAGE_OF_UNIQUE_CLIENTS).hashCode()),
                            new DynamicProperty(getPercent(item, CoverageNutritionDynamicBean.TOTAL_ALL, clientsSet.size())));
                }
                //данные в отчет по количеству комплексов/буфетки в Итого (Платное, Бесплатное, Буфет покупная, Буфет горячее)
                for (String foodType : totalQtyMap.keySet()) {
                    Integer qty = totalQtyMap.get(foodType);
                    String key = "";
                    if (foodType.equals(CoverageNutritionDynamicBean.PAID_NUTRITION) || foodType.equals(CoverageNutritionDynamicBean.FREE_NUTRITION)) key = CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES;
                    if (foodType.equals(CoverageNutritionDynamicBean.BUFFET_HOT_FULL)) key = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, CoverageNutritionDynamicBean.BUFFET_HOT);
                    if (foodType.equals(CoverageNutritionDynamicBean.BUFFET_PAID_FULL)) key = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, CoverageNutritionDynamicBean.BUFFET_PAID);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType + key).hashCode()),
                            new DynamicProperty(Long.valueOf(qty)));
                }
                //данные в отчет по количеству комплексов в Итого (Платное + Бесплатное)
                for (String foodType : totalPaidAndFreeQtyMap.keySet()) {
                    Integer qty = totalPaidAndFreeQtyMap.get(foodType);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PAID_AND_FREE + CoverageNutritionDynamicBean.TOTALS_SOLD_COMPLEXES).hashCode()),
                            new DynamicProperty(Long.valueOf(qty)));
                }
                //данные в отчет по количеству сотрудников в Итого (Платное + Бесплатное)
                for (String foodType : totalBuffetCountMap.keySet()) {
                    HashSet<Long> clientsSet = totalBuffetCountMap.get(foodType);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL + CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT_WITHOUT_CLASSES).hashCode()),
                            new DynamicProperty(Long.valueOf(clientsSet.size())));
                }
                //данные в отчет по количеству позиций буфета в Итого (Буфет общее)
                for (String foodType : totalBuffetQtyMap.keySet()) {
                    Integer qty = totalBuffetQtyMap.get(foodType);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL +  String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET, "")).hashCode()),
                            new DynamicProperty(Long.valueOf(qty)));
                }
                //данные в отчет по количеству буфетки в Итого (по карте зав производством)
                for (String foodType : totalZavProizvQtyMap.keySet()) {
                    Integer qty = totalZavProizvQtyMap.get(foodType);
                    String key = "";
                    if (foodType.equals(CoverageNutritionDynamicBean.BUFFET_HOT_FULL)) key = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD, CoverageNutritionDynamicBean.BUFFET_HOT);
                    if (foodType.equals(CoverageNutritionDynamicBean.BUFFET_PAID_FULL)) key = String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD, CoverageNutritionDynamicBean.BUFFET_PAID);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + foodType + key).hashCode()),
                            new DynamicProperty(Long.valueOf(qty)));
                }
                //данные в отчет по количеству буфетки в Итого Буфет общее (по карте зав производством)
                for (String foodType : totalBuffetZavProizvQtyMap.keySet()) {
                    Integer qty = totalBuffetZavProizvQtyMap.get(foodType);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.BUFFET_ALL_FULL + String.format(CoverageNutritionDynamicBean.TOTALS_SOLD_BUFFET_BY_ORG_CARD, "")).hashCode()),
                            new DynamicProperty(Long.valueOf(qty)));
                }
                //данные в отчет по количеству покупателей в Итого (Комплексы + Буфет)
                for (String foodType : totalComplexBuffetCountMap.keySet()) {
                    HashSet<Long> clientsSet = totalComplexBuffetCountMap.get(foodType);
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL + CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL_SUBTITLE).hashCode()),
                            new DynamicProperty(Long.valueOf(clientsSet.size())));
                    dynamicPropertyList.put(String.format("%d",
                            (CoverageNutritionDynamicBean.TOTALS_TITLE + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS + CoverageNutritionDynamicBean.PERCENTAGE_OF_ACTIVE_CLIENTS_SUBTITLE).hashCode()),
                            new DynamicProperty(getPercent(item, CoverageNutritionDynamicBean.TOTAL_ALL, clientsSet.size())));
                }

                ///CoverageNutritionReportItem item = itemHashMap.get(idOfOrg);
                ///HashMap<String, DynamicProperty> dynamicPropertyList = item.getDynamicProperties();
                //dynamicPropertyList.put(String.format("%d",
                //        (CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES + foodType + complexName + priceFormat(price) + CoverageNutritionDynamicBean.ORG_CARD_ORDERS_COUNT).hashCode()),
                //        new DynamicProperty(orderCount));
                ///dynamicPropertyList.put(String.format("%d","Обучающиеся 1-4 классовБуфет покупнаякол-во проданной продукции".hashCode()), new DynamicProperty(120L));
            }
        }

        private Double getPercent(CoverageNutritionReportItem item, String group, Integer amount) {
            Double result = 0D;
            try {
                switch (group) {
                    case "Обучающиеся 1-4 классов":
                        result = Double.valueOf(Double.valueOf(amount) * 100 / item.getStudentsCountYoung());
                        break;
                    case "Обучающиеся 5-9 классов":
                        result = Double.valueOf(Double.valueOf(amount) * 100 / item.getStudentsCountMiddle());
                        break;
                    case "Обучающиеся 10-11 классов":
                        result = Double.valueOf(Double.valueOf(amount) * 100 / item.getStudentsCountOld());
                        break;
                    case CoverageNutritionDynamicBean.TOTAL_STUDENTS :
                        result = Double.valueOf(Double.valueOf(amount) * 100 / item.getStudentsCountTotal());
                        break;
                    case CoverageNutritionDynamicBean.TOTAL_ALL :
                        result = Double.valueOf(Double.valueOf(amount) * 100 / (item.getStudentsCountTotal() + item.getEmployeeCount()));
                        break;
                    case CoverageNutritionDynamicBean.TOTAL_EMPLOYEE :
                        result = Double.valueOf(Double.valueOf(amount) * 100 / item.getEmployeeCount());
                }
                if (result.isInfinite()) result = 0D;
                return result;
            } catch (Exception e) {
                return 0D;
            }
        }

        private void tryFillEmployeeData(HashMap<String, HashSet<Long>> employeeCountMap, CNReportItem item) {
            if (!item.isSotrudnik() || item.getSurname().startsWith("#")) return;

            if (item.isBuffet()) {
                if (employeeCountMap.get(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET) == null) {
                    employeeCountMap.put(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET, new HashSet<Long>());
                }
                employeeCountMap.get(CoverageNutritionDynamicBean.MENU_TYPE_BUFFET).add(item.getIdOfClient());
            } else {
                if (employeeCountMap.get(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES) == null) {
                    employeeCountMap.put(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES, new HashSet<Long>());
                }
                employeeCountMap.get(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES).add(item.getIdOfClient());
            }
            if (employeeCountMap.get(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET) == null) {
                employeeCountMap.put(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET, new HashSet<Long>());
            }
            employeeCountMap.get(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET).add(item.getIdOfClient());
        }

        private void tryFillTotalData(HashMap<String, HashSet<Long>> totalCountMap,
                HashMap<String, HashSet<Long>> totalPaidAndFreeCountMap,
                HashMap<String, Integer> totalQtyMap,
                HashMap<String, Integer> totalPaidAndFreeQtyMap,
                HashMap<String, HashSet<Long>> totalBuffetCountMap,
                HashMap<String, Integer> totalBuffetQtyMap,
                HashMap<String, Integer> totalZavProizvQtyMap,
                HashMap<String, Integer> totalBuffetZavProizvQtyMap,
                HashMap<String, HashSet<Long>> totalComplexBuffetCountMap,
                CNReportItem item) {
            if (item.isSotrudnik()) return;
            if (totalCountMap.get(item.getFoodType()) == null) {
                totalCountMap.put(item.getFoodType(), new HashSet<Long>());
            }
            totalCountMap.get(item.getFoodType()).add(item.getIdOfClient());

            if (totalComplexBuffetCountMap.get(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET) == null) {
                totalComplexBuffetCountMap.put(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET, new HashSet<Long>());
            }
            totalComplexBuffetCountMap.get(CoverageNutritionDynamicBean.EMPLOYEES_COMPLEXES_AND_BUFFET).add(item.getIdOfClient());

            if (!item.isBuffet()) {
                if (totalPaidAndFreeCountMap.get(CoverageNutritionDynamicBean.PAID_AND_FREE) == null) {
                    totalPaidAndFreeCountMap.put(CoverageNutritionDynamicBean.PAID_AND_FREE, new HashSet<Long>());
                }
                totalPaidAndFreeCountMap.get(CoverageNutritionDynamicBean.PAID_AND_FREE).add(item.getIdOfClient());
            } else {
                if (totalBuffetCountMap.get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) == null) {
                    totalBuffetCountMap.put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, new HashSet<Long>());
                }
                totalBuffetCountMap.get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL).add(item.getIdOfClient());
            }

            if (totalQtyMap.get(item.getFoodType()) == null) {
                totalQtyMap.put(item.getFoodType(), 0);
            }
            totalQtyMap.put(item.getFoodType(), totalQtyMap.get(item.getFoodType()) + item.getQty());

            if (!item.isBuffet()) {
                if (totalPaidAndFreeQtyMap.get(CoverageNutritionDynamicBean.PAID_AND_FREE) == null) {
                    totalPaidAndFreeQtyMap.put(CoverageNutritionDynamicBean.PAID_AND_FREE, 0);
                }
                totalPaidAndFreeQtyMap.put(CoverageNutritionDynamicBean.PAID_AND_FREE, totalPaidAndFreeQtyMap.get(CoverageNutritionDynamicBean.PAID_AND_FREE) + item.getQty());
            } else {
                if (totalBuffetQtyMap.get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) == null) {
                    totalBuffetQtyMap.put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, 0);
                }
                totalBuffetQtyMap.put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, totalBuffetQtyMap.get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) + item.getQty());

                if (item.getSurname().startsWith("#")) {
                    //количество позиций буфета по карте зав производством
                    if (totalZavProizvQtyMap.get(item.getFoodType()) == null) {
                        totalZavProizvQtyMap.put(item.getFoodType(), 0);
                    }
                    totalZavProizvQtyMap.put(item.getFoodType(), totalZavProizvQtyMap.get(item.getFoodType()) + item.getQty());

                    if (totalBuffetZavProizvQtyMap.get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) == null) {
                        totalBuffetZavProizvQtyMap.put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, 0);
                    }
                    totalBuffetZavProizvQtyMap.put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, totalBuffetZavProizvQtyMap.get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) + item.getQty());
                }
            }
        }

        private void tryFillZavProizvData(HashMap<String, Integer> map, CNReportItem item) {
            if (!item.getSurname().equals("#") || !item.isBuffet()) return;

            if (map.get(item.getFoodType()) == null) {
                map.put(item.getFoodType(), 0);
            }
            map.put(item.getFoodType(), map.get(item.getFoodType()) + item.getQty());
        }

        private void putClientIntoComplexCountMap(HashMap<String, HashMap<String, HashMap<String, HashSet<Long>>>> clientCountMap,
                HashMap<String, HashMap<String, HashSet<Long>>> clientCountPaidAndFreeMap,
                HashMap<String, HashMap<String, HashSet<Long>>> clientCountTotalMap, CNReportItem item) {
            if (clientCountMap.get(item.getGroupNameForTemplate()) == null) {
                clientCountMap.put(item.getGroupNameForTemplate(), new HashMap<String, HashMap<String, HashSet<Long>>>());
            }
            if (clientCountMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()) == null) {
                clientCountMap.get(item.getGroupNameForTemplate()).put(item.getFoodType(), new HashMap<String, HashSet<Long>>());
            }
            if (clientCountMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).get(item.getComplexNameForTemplate()) == null) {
                clientCountMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).put(item.getComplexNameForTemplate(), new HashSet<Long>());
            }
            clientCountMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).get(item.getComplexNameForTemplate()).add(item.getIdOfClient());

            if (clientCountPaidAndFreeMap.get(item.getGroupNameForTemplate()) == null) {
                clientCountPaidAndFreeMap.put(item.getGroupNameForTemplate(), new HashMap<String, HashSet<Long>>());
            }
            if (clientCountPaidAndFreeMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.PAID_AND_FREE) == null) {
                clientCountPaidAndFreeMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.PAID_AND_FREE, new HashSet<Long>());
            }
            clientCountPaidAndFreeMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.PAID_AND_FREE).add(item.getIdOfClient());

            if (clientCountTotalMap.get(item.getGroupNameForTemplate()) == null) {
                clientCountTotalMap.put(item.getGroupNameForTemplate(), new HashMap<String, HashSet<Long>>());
            }
            if (clientCountTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL) == null) {
                clientCountTotalMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL, new HashSet<Long>());
            }
            clientCountTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL).add(item.getIdOfClient());
        }

        private void putClientIntoBuffetCountMap(HashMap<String, HashMap<String, HashSet<Long>>> clientBuffetCountMap,
                HashMap<String, HashMap<String, HashSet<Long>>> clientBuffetCountTotalMap,
                HashMap<String, HashMap<String, HashSet<Long>>> clientCountTotalMap, CNReportItem item) {
            if (clientBuffetCountMap.get(item.getGroupNameForTemplate()) == null) {
                clientBuffetCountMap.put(item.getGroupNameForTemplate(), new HashMap<String, HashSet<Long>>());
            }
            if (clientBuffetCountMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()) == null) {
                clientBuffetCountMap.get(item.getGroupNameForTemplate()).put(item.getFoodType(), new HashSet<Long>());
            }
            clientBuffetCountMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).add(item.getIdOfClient());

            if (clientBuffetCountTotalMap.get(item.getGroupNameForTemplate()) == null) {
                clientBuffetCountTotalMap.put(item.getGroupNameForTemplate(), new HashMap<String, HashSet<Long>>());
            }
            if (clientBuffetCountTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) == null) {
                clientBuffetCountTotalMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, new HashSet<Long>());
            }
            clientBuffetCountTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL).add(item.getIdOfClient());

            if (clientCountTotalMap.get(item.getGroupNameForTemplate()) == null) {
                clientCountTotalMap.put(item.getGroupNameForTemplate(), new HashMap<String, HashSet<Long>>());
            }
            if (clientCountTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL) == null) {
                clientCountTotalMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL, new HashSet<Long>());
            }
            clientCountTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.CLIENTS_COUNT_TOTAL).add(item.getIdOfClient());
        }

        private void putClientIntoComplexQtyMap(HashMap<String, HashMap<String, HashMap<String, Integer>>> clientQtyMap,
                HashMap<String, HashMap<String, Integer>> clientQtyPaidAndFreeMap, CNReportItem item) {
            if (clientQtyMap.get(item.getGroupNameForTemplate()) == null) {
                clientQtyMap.put(item.getGroupNameForTemplate(), new HashMap<String, HashMap<String, Integer>>());
            }
            if (clientQtyMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()) == null) {
                clientQtyMap.get(item.getGroupNameForTemplate()).put(item.getFoodType(), new HashMap<String, Integer>());
            }
            if (clientQtyMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).get(item.getComplexNameForTemplate()) == null) {
                clientQtyMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).put(item.getComplexNameForTemplate(), 0);
            }
            clientQtyMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).put(item.getComplexNameForTemplate(),
                    clientQtyMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()).get(item.getComplexNameForTemplate()) + item.getQty());

            if (clientQtyPaidAndFreeMap.get(item.getGroupNameForTemplate()) == null) {
                clientQtyPaidAndFreeMap.put(item.getGroupNameForTemplate(), new HashMap<String, Integer>());
            }
            if (clientQtyPaidAndFreeMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.PAID_AND_FREE) == null) {
                clientQtyPaidAndFreeMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.PAID_AND_FREE, 0);
            }
            clientQtyPaidAndFreeMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.PAID_AND_FREE,
                    clientQtyPaidAndFreeMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.PAID_AND_FREE) + item.getQty());

        }

        private void putClientIntoComplexZavProizvQtyMap(HashMap<String, HashMap<String, HashMap<String, Integer>>> clientQtyMap,
                CNReportItem item) {
            if (!item.getSurname().startsWith("#")) return;
            if (clientQtyMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES) == null) {
                clientQtyMap.put(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES, new HashMap<String, HashMap<String, Integer>>());
            }
            if (clientQtyMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).get(item.getFoodType()) == null) {
                clientQtyMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).put(item.getFoodType(), new HashMap<String, Integer>());
            }
            if (clientQtyMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).get(item.getFoodType()).get(item.getComplexNameForTemplate()) == null) {
                clientQtyMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).get(item.getFoodType()).put(item.getComplexNameForTemplate(), 0);
            }
            clientQtyMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).get(item.getFoodType()).put(item.getComplexNameForTemplate(),
                    clientQtyMap.get(CoverageNutritionDynamicBean.ORG_CARD_COMPLEXES).get(item.getFoodType()).get(item.getComplexNameForTemplate()) + item.getQty());

        }

        private void putClientIntoBuffetQtyMap(HashMap<String, HashMap<String, Integer>> clientBuffetQtyMap,
                HashMap<String, HashMap<String, Integer>> clientBuffetQtyTotalMap, CNReportItem item) {
            if (clientBuffetQtyMap.get(item.getGroupNameForTemplate()) == null) {
                clientBuffetQtyMap.put(item.getGroupNameForTemplate(), new HashMap<String, Integer>());
            }
            if (clientBuffetQtyMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()) == null) {
                clientBuffetQtyMap.get(item.getGroupNameForTemplate()).put(item.getFoodType(), 0);
            }
            clientBuffetQtyMap.get(item.getGroupNameForTemplate()).put(item.getFoodType(),
                    clientBuffetQtyMap.get(item.getGroupNameForTemplate()).get(item.getFoodType()) + item.getQty());

            if (clientBuffetQtyTotalMap.get(item.getGroupNameForTemplate()) == null) {
                clientBuffetQtyTotalMap.put(item.getGroupNameForTemplate(), new HashMap<String, Integer>());
            }
            if (clientBuffetQtyTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) == null) {
                clientBuffetQtyTotalMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL, 0);
            }
            clientBuffetQtyTotalMap.get(item.getGroupNameForTemplate()).put(CoverageNutritionDynamicBean.BUFFET_ALL_FULL,
                    clientBuffetQtyTotalMap.get(item.getGroupNameForTemplate()).get(CoverageNutritionDynamicBean.BUFFET_ALL_FULL) + item.getQty());
        }



        //Получаем все заказы с детализацией по всем выбранным ОО за период
        private List<CNReportItem> loadAllData(Session session, Collection<Long> idOfOrgList, Date startDate,
                Date endDate) {
            String sqlString = "select distinct "
                    + "c.idofclient, "//0
                    + "c.idofclientgroup, "//1
                    + "cg.groupname, "//2
                    + "o.idoforg, "//3
                    + "od.menutype, "//4
                    + "od.menuorigin, "//5
                    + "od.rprice, "//6
                    + "od.discount, "//7
                    + "od.qty, "//8
                    + "od.menudetailname, "//9
                    + "case when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 1 and 4 then 'Обучающиеся 1-4 классов' "
                    + "    when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 5 and 9 then 'Обучающиеся 5-9 классов' "
                    + "    when cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer) between 10 and 11 then 'Обучающиеся 10-11 классов' "
                    + "    when cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees, :clientEmployeesOtherOrg) or cg.groupname = 'Сотрудники' then 'Сотрудники' "
                    + "else 'Обучающиеся другие группы' end as gr, "//10
                    + "case when od.menutype = 0 and od.menuorigin in (0, 1, 2) then 'Буфет горячее' "
                    + "    when od.menutype = 0 and od.menuorigin in (10, 11, 20) then 'Буфет покупная'"
                    + "    when od.menutype between 50 and 99 and od.rprice > 0 then 'Платное питание' "
                    + "    when od.menutype between 50 and 99 and od.rprice = 0 and od.discount > 0 then 'Бесплатное питание' end as foodtype, "//11
                    + "p.surname "//12
                    + "from cf_orders o "
                    + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + "join cf_orgs og on o.idoforg = og.idoforg join cf_clients c on c.idofclient = o.idofclient "
                    + "join cf_persons p on c.idofperson = p.idofperson "
                    + "join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and o.state = 0 "
                    + " and od.menutype < 150 "
                    + "     and og.organizationtype = 0 ";

            Query query = session.createSQLQuery(sqlString);
            query.setParameterList("idOfOrgList", idOfOrgList);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("clientEmployeesOtherOrg", ClientGroup.Predefined.CLIENT_EMPLOYEE_OTHER_ORG.getValue());

            List<CNReportItem> reportItems = new ArrayList<>();
            List list = query.list();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                Long idOfClient = (row[0] == null) ? 0 : ((BigInteger) row[0]).longValue();
                Long idOfClientGroup = (row[1] == null) ? 0 : ((BigInteger) row[1]).longValue();
                String groupName = (row[2] == null) ? "" : (String) row[2];
                Long idOfOrg = ((BigInteger) row[3]).longValue();
                Integer menuType = (Integer) row[4];
                Integer menuOrigin = (Integer) row[5];
                Long rprice = ((BigInteger) row[6]).longValue();
                Long discount = ((BigInteger) row[7]).longValue();
                Integer qty = (Integer) row[8];
                String menuDetailName = (String) row[9];
                String groupNameForTemplate = (String) row[10];
                String foodType = (String) row[11];
                String surname = (row[12] == null) ? "" : (String) row[12];
                CNReportItem item = new CNReportItem(idOfClient, idOfClientGroup, groupName, idOfOrg, menuType, menuOrigin, rprice, discount,
                        qty, menuDetailName, groupNameForTemplate, foodType, surname);
                reportItems.add(item);
            }
            return reportItems;
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

        private String generateQueryConditions(List<Long> managerList, Boolean showYoungerClasses,
                Boolean showMiddleClasses, Boolean showOlderClasses, Boolean showEmployee, Boolean showFreeNutrition,
                Boolean showPaidNutrition, Boolean showBuffet, Boolean showComplexesByOrgCard, boolean forTotals) {
            String resultString = "";
            if (!forTotals) {
                String conditionString = " coalesce(cast(substring(cg.groupname, '(\\d{1,3})-{0,1}\\D*') as integer), 100) %s between %d and %d";
                List<String> classesConditionList = new ArrayList<String>();
                List<String> classesNotConditionList = new ArrayList<String>();
                if (showYoungerClasses != null && !showYoungerClasses) {
                    classesNotConditionList.add(String.format(conditionString, "not", 1, 4));
                }
                if (showMiddleClasses != null && !showMiddleClasses) {
                    classesNotConditionList.add(String.format(conditionString, "not", 5, 9));
                }
                if (showOlderClasses != null && !showOlderClasses) {
                    classesNotConditionList.add(String.format(conditionString, "not", 10, 11));
                }
                if (showComplexesByOrgCard != null && !showComplexesByOrgCard && !managerList.isEmpty()) {
                    classesConditionList.add(String.format("c.idofclient not in (%s)", StringUtils.join(managerList, ",")));
                }

                if (!classesConditionList.isEmpty()) {
                    resultString += " and (" + StringUtils.join(classesConditionList, " or ") + ") ";
                }
                if (!classesNotConditionList.isEmpty()) {
                    resultString += " and " + StringUtils.join(classesNotConditionList, " and ");
                }
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
                if (group.equals("Сотрудники") || group.equals("Обучающиеся другие группы")) continue;
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
                String titleName = CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT_WITHOUT_CLASSES;
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
                String titleName = CoverageNutritionDynamicBean.BUFFET_CLIENTS_COUNT_WITHOUT_CLASSES;
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
                    + "        when cg.idofclientgroup in (:clientEmployees, :clientAdministration, :clientTechEmployees, :clientEmployeesOtherOrg) then 'Сотрудники' "
                    + "        else 'Обучающиеся другие группы' end as gr "
                    + "from cf_orders o "
                    + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                    + "join cf_orgs og on o.idoforg = og.idoforg " + "join cf_clients c on c.idofclient = o.idofclient "
                    + "join cf_clientgroups cg on cg.idofclientgroup = c.idofclientgroup and cg.idoforg = c.idoforg "
                    + "where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and o.state = 0 "
                    + " and od.menutype < 150 "
                    + "     and og.organizationtype = 0 and cg.idofclientgroup <= :clientTechEmployees"; // + (showEmployee ? "" : " and cg.groupname <> 'Сотрудники'");

            sqlString += generateQueryConditions(Collections.EMPTY_LIST, showYoungerClasses, showMiddleClasses,
                    showOlderClasses, showEmployee, showFreeNutrition, showPaidNutrition, showBuffet, false, false);
            Query query = session.createSQLQuery(sqlString);
            query.setParameterList("idOfOrgList", idOfOrgList);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());
            query.setParameter("clientEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("clientAdministration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("clientTechEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("clientEmployeesOtherOrg", ClientGroup.Predefined.CLIENT_EMPLOYEE_OTHER_ORG.getValue());

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
                    + "where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and o.state = 0 "
                    + " and od.menutype < 150 "
                    + "     and og.organizationtype = 0 " + (showEmployee ? "" : " and cg.groupname <> 'Сотрудники'");

            sqlString += generateQueryConditions(managerList, showYoungerClasses, showMiddleClasses, showOlderClasses,
                    showEmployee, showFreeNutrition, showPaidNutrition, showBuffet, showComplexesByOrgCard, true);
            Query query = session.createSQLQuery(sqlString);
            query.setParameterList("idOfOrgList", idOfOrgList);
            query.setParameter("startDate", startDate.getTime());
            query.setParameter("endDate", endDate.getTime());

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
                    + "where o.idoforg in (:idOfOrgList) and o.createddate between :startDate and :endDate and o.state = 0 "
                    + " and od.menutype < 150 "
                    + "     and og.organizationtype = 0 and c.idofclient in (:managerList)";

            sqlString += generateQueryConditions(managerList, null, null, null, true, showFreeNutrition,
                    showPaidNutrition, showBuffet, true, false);
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
            return CNReportItem.priceFormat(price);
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
