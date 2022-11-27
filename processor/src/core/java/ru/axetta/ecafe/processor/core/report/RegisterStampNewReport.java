/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampReportItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.WtComplexItem;
import ru.axetta.ecafe.processor.core.service.spb.HeaderHandler;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by anvarov on 31.08.2017.
 */
public class RegisterStampNewReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Реестр талонов "
            + "услуг по организации питания и обеспечения питьевого режима обучающихся";
    public static final String[] TEMPLATE_FILE_NAMES = {"RegisterStampNewReport.jasper", "RegisterStampNewReport_summary.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 4, 5, 38};


    public static final String PARAM_WITH_OUT_ACT_DISCREPANCIES = "includeActDiscrepancies";

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampNewReport.class);

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("idOfOrg", org.getIdOfOrg());
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("orgAddress", org.getAddress());
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);
            parameterMap.put("contractNumber", getReportProperties().getProperty("contractNumber"));
            parameterMap.put("contractDate", getReportProperties().getProperty("contractDate"));

            calendar.setTime(startTime);
            JRDataSource dataSource = createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap);
            if (null == dataSource)
                return null;
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            return new RegisterStampNewReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            OrderDetailsDAOService service = new OrderDetailsDAOService();
            service.setSession(session);

            String withOutActDiscrepanciesParam = (String) getReportProperties().get(PARAM_WITH_OUT_ACT_DISCREPANCIES);
            boolean withOutActDiscrepancies = false;
            if (withOutActDiscrepanciesParam!=null) {
                withOutActDiscrepancies = withOutActDiscrepanciesParam.trim().equalsIgnoreCase("true");
            }

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");
            // товары
            List<GoodItem> allGoods = service.findAllGoods(org.getIdOfOrg(), startTime, endTime, service.getReducedPaymentOrderTypesWithDailySample());
            allGoods.addAll(service.findAllGoods(org.getIdOfOrg(), startTime, endTime, service.getWaterAccountingOrderTypesWithDailySample()));

            // комплексы
            List<WtComplexItem> allComplexes = service.findAllWtComplexes(org.getIdOfOrg(), startTime, endTime, service.getReducedPaymentOrderTypesWithDailySample());
            allComplexes.addAll(service.findAllWtComplexes(org.getIdOfOrg(), startTime, endTime, service.getWaterAccountingOrderTypesWithDailySample()));

            Map<Date, Long> numbers = service.findAllRegistryTalons(org.getIdOfOrg(), startTime, endTime);

            RegisterStampReportItem.RegisterStampReportData data = new RegisterStampReportItem.RegisterStampReportData();

            List<RegisterStampReportItem.RegisterStampReportData> result = new ArrayList<RegisterStampReportItem.RegisterStampReportData>();
            calendar.setTime(startTime);
            GoodItem emptyGoodItem = new GoodItem();

            List<RegisterStampReportItem> waterItems = new ArrayList<RegisterStampReportItem>();
            List<String> headerName = new ArrayList<String>();
            List<RegisterStampReportItem> headerItem = new ArrayList<RegisterStampReportItem>();
            TreeMap<String, RegisterStampReportItem> headerMap = new TreeMap<String, RegisterStampReportItem>();

            while (endTime.getTime()>calendar.getTimeInMillis()){
                Date time = calendar.getTime();
                String date = timeFormat.format(time);

                if(allGoods.isEmpty() && allComplexes.isEmpty()){
                    RegisterStampReportItem itemEmpty = new RegisterStampReportItem(emptyGoodItem,0L,date, time);
                    RegisterStampReportItem totalEmpty = new RegisterStampReportItem(emptyGoodItem,0L,"Итого", CalendarUtils
                            .addDays(endTime, 1));

                    data.getList153().add(itemEmpty);
                    data.getList153().add(totalEmpty);

                    data.getList37().add(itemEmpty);
                    data.getList37().add(totalEmpty);

                    data.getList14().add(itemEmpty);
                    data.getList14().add(totalEmpty);

                    data.getList511().add(itemEmpty);
                    data.getList511().add(totalEmpty);

//                    headerMap.put(itemEmpty.getLevel4(), itemEmpty);
                    headerName.add(itemEmpty.getLevel4());
                    headerItem.add(itemEmpty);
                } else {
                    for (GoodItem goodItem: allGoods){
                        String number = numbers.get(time) == null ? "" : Long.toString(numbers.get(time));
                        Long val = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                goodItem.getFullName(), withOutActDiscrepancies);

                        RegisterStampReportItem item = new RegisterStampReportItem(goodItem,val,date,number, time);
                        if (item.getLevel3().equals("1,5-3") || item.getLevel3().equals("1.5-3")) {
                            data.getList153().add(item);
                            Long valDaily = service.buildRegisterStampDailySampleValueNew(org.getIdOfOrg(), item.getDateTime(), goodItem.getFullName());

                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(goodItem,valDaily,date,number, time);
                            data.getList153().add(itemDaily);
                            Long val1 = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                    goodItem.getFullName(), withOutActDiscrepancies);

                            RegisterStampReportItem total1 = new RegisterStampReportItem(goodItem,val1 + valDaily,"Итого", CalendarUtils.addDays(endTime, 1));
                            data.getList153().add(total1);
                        } else if (item.getLevel3().equals("3-7")) {
                            data.getList37().add(item);
                            Long valDaily = service.buildRegisterStampDailySampleValueNew(org.getIdOfOrg(), item.getDateTime(), goodItem.getFullName());

                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(goodItem,valDaily,date,number, time);
                            data.getList37().add(itemDaily);
                            Long val1 = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                    goodItem.getFullName(), withOutActDiscrepancies);

                            RegisterStampReportItem total1 = new RegisterStampReportItem(goodItem,val1 + valDaily,"Итого", CalendarUtils.addDays(endTime, 1));
                            data.getList37().add(total1);
                        } else if (item.getLevel3().equals("1-4")) {
                            data.getList14().add(item);
                            Long valDaily = service.buildRegisterStampDailySampleValueNew(org.getIdOfOrg(), item.getDateTime(), goodItem.getFullName());

                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(goodItem,valDaily,date,number, time);
                            data.getList14().add(itemDaily);
                            Long val1 = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                    goodItem.getFullName(), withOutActDiscrepancies);

                            RegisterStampReportItem total1 = new RegisterStampReportItem(goodItem,val1 + valDaily,"Итого", CalendarUtils.addDays(endTime, 1));
                            data.getList14().add(total1);
                        } else if (item.getLevel3().equals("5-11")) {
                            data.getList511().add(item);
                            Long valDaily = service.buildRegisterStampDailySampleValueNew(org.getIdOfOrg(), item.getDateTime(), goodItem.getFullName());

                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(goodItem,valDaily,date,number, time);
                            data.getList511().add(itemDaily);
                            Long val1 = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                    goodItem.getFullName(), withOutActDiscrepancies);

                            RegisterStampReportItem total1 = new RegisterStampReportItem(goodItem,val1 + valDaily,"Итого", CalendarUtils.addDays(endTime, 1));
                            data.getList511().add(total1);
                        } else if (item.getOrderType().equals(1)) {
                            waterItems.add(item);
                            Long val1 = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                    goodItem.getFullName(), withOutActDiscrepancies);

                            RegisterStampReportItem total1 = new RegisterStampReportItem(goodItem,val1,"Итого", CalendarUtils.addDays(endTime, 1));
                            total1.setLevel4("Вода питьевая");
                            waterItems.add(total1);
                        }

                        if (StringUtils.isNotEmpty(item.getLevel4()) && !headerName.contains(item.getLevel4())) {
                            headerName.add(item.getLevel4());
                            headerItem.add(item);
//                            headerMap.put(item.getLevel4(), item);
                        }
                    }

                    // цикл по комплексам
                    String number = numbers.get(time) == null ? "" : Long.toString(numbers.get(time));
                    for (WtComplexItem complexItem : allComplexes) {

                        String dietType = complexItem.getDietType().getDescription();
                        String ageGroup = complexItem.getAgeGroup().getDescription();
                        Long idOfComplex = complexItem.getIdOfComplex();
                        Integer orderType = complexItem.getOrderType();

                        Long val = service.buildRegisterStampBodyWtMenuValue(org.getIdOfOrg(), time, idOfComplex, withOutActDiscrepancies);
                        RegisterStampReportItem item = new RegisterStampReportItem(ageGroup, dietType, val, date, number, time, orderType);

                        if (item.getLevel3().equals("1,5-3") || item.getLevel3().equals("1.5-3")) {
                            data.getList153().add(item);

                            Long valDaily = service.buildRegisterStampDailySampleWtMenuValueNew(org.getIdOfOrg(), item.getDateTime(), idOfComplex);
                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(ageGroup,
                                    dietType, valDaily, date, number, time, orderType);
                            data.getList153().add(itemDaily);

                            Long val1 = service
                                    .buildRegisterStampBodyWtMenuValue(org.getIdOfOrg(), calendar.getTime(), idOfComplex, withOutActDiscrepancies);
                            RegisterStampReportItem total1 = new RegisterStampReportItem(ageGroup,
                                    dietType, val1 + valDaily, "Итого", null, CalendarUtils.addDays(endTime, 1), orderType);
                            data.getList153().add(total1);

                        } else if (item.getLevel3().equals("3-7")) {
                            data.getList37().add(item);
                            Long valDaily = service
                                    .buildRegisterStampDailySampleWtMenuValueNew(org.getIdOfOrg(), item.getDateTime(), idOfComplex);
                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(ageGroup,
                                    dietType, valDaily, date, number, time, orderType);
                            data.getList37().add(itemDaily);

                            Long val1 = service
                                    .buildRegisterStampBodyWtMenuValue(org.getIdOfOrg(), calendar.getTime(), idOfComplex, withOutActDiscrepancies);
                            RegisterStampReportItem total1 = new RegisterStampReportItem(ageGroup,
                                    dietType, val1 + valDaily, "Итого", null, CalendarUtils.addDays(endTime, 1), orderType);
                            data.getList37().add(total1);

                        } else if (item.getLevel3().equals("1-4")) {
                            data.getList14().add(item);
                            Long valDaily = service
                                    .buildRegisterStampDailySampleWtMenuValueNew(org.getIdOfOrg(), item.getDateTime(), idOfComplex);
                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(ageGroup, dietType, valDaily, date, number, time, orderType);
                            data.getList14().add(itemDaily);

                            Long val1 = service
                                    .buildRegisterStampBodyWtMenuValue(org.getIdOfOrg(), calendar.getTime(), idOfComplex, withOutActDiscrepancies);
                            RegisterStampReportItem total1 = new RegisterStampReportItem(ageGroup,
                                    dietType, val1 + valDaily, "Итого", null, CalendarUtils.addDays(endTime, 1), orderType);
                            data.getList14().add(total1);

                        } else if (item.getLevel3().equals("5-11")) {
                            data.getList511().add(item);
                            Long valDaily = service
                                    .buildRegisterStampDailySampleWtMenuValueNew(org.getIdOfOrg(), item.getDateTime(), idOfComplex);
                            RegisterStampReportItem itemDaily = new RegisterStampReportItem(ageGroup, dietType, valDaily, date, number, time, orderType);
                            data.getList511().add(itemDaily);

                            Long val1 = service
                                    .buildRegisterStampBodyWtMenuValue(org.getIdOfOrg(), calendar.getTime(), idOfComplex, withOutActDiscrepancies);
                            RegisterStampReportItem total1 = new RegisterStampReportItem(ageGroup,
                                    dietType, val1 + valDaily, "Итого", null, CalendarUtils.addDays(endTime, 1), orderType);
                            data.getList511().add(total1);

                        } else if (item.getOrderType().equals(1)) {
                            waterItems.add(item);
                            Long val1 = service
                                    .buildRegisterStampBodyWtMenuValue(org.getIdOfOrg(), calendar.getTime(), idOfComplex, withOutActDiscrepancies);
                            RegisterStampReportItem total1 = new RegisterStampReportItem(ageGroup,
                                    "Вода питьевая", val1, "Итого", null, CalendarUtils.addDays(endTime, 1), orderType);
                            waterItems.add(total1);
                        }

                        if (StringUtils.isNotEmpty(item.getLevel4()) && !headerName.contains(item.getLevel4())) {
                            headerName.add(item.getLevel4());
                            headerItem.add(item);
                        }
                    }
                }

                if (!addWaterCategory(headerItem))
                    return null;

                calendar.add(Calendar.DATE,1);
            }

            data.getList153().addAll(addItemsByGoodNamesNE(headerItem, data.getList153()));
            data.getList37().addAll(addItemsByGoodNamesNE(headerItem, data.getList37()));
            data.getList14().addAll(addItemsByGoodNamesNE(headerItem, data.getList14()));
            data.getList511().addAll(addItemsByGoodNamesNE(headerItem, data.getList511()));

            List<RegisterStampReportItem> header = new ArrayList<RegisterStampReportItem>();
            for (RegisterStampReportItem item : data.getList153()) {
                if (!header.contains(item.getLevel4())) {
                    header.add(item);
                }
            }
            data.setHeaderList(header);

            List<RegisterStampReportItem> list153Totals = totals(headerMap.keySet(), data.getList153());
            List<RegisterStampReportItem> list37Totals = totals(headerMap.keySet(), data.getList37());
            List<RegisterStampReportItem> list14Totals = totals(headerMap.keySet(), data.getList14());
            List<RegisterStampReportItem> list511Totals = totals(headerMap.keySet(), data.getList511());
            List<RegisterStampReportItem> listWaterTotals = totalWater("Вода питьевая", waterItems);

            List<RegisterStampReportItem> resultGlobalTotal = emptyGlobalTotal(headerMap.keySet());

            List<RegisterStampReportItem> allTotalsByAllCategory = new ArrayList<RegisterStampReportItem>();

            allTotalsByAllCategory.addAll(list153Totals);
            allTotalsByAllCategory.addAll(list37Totals);
            allTotalsByAllCategory.addAll(list14Totals);
            allTotalsByAllCategory.addAll(list511Totals);
            allTotalsByAllCategory.addAll(listWaterTotals);

            for (RegisterStampReportItem allTotalItem: allTotalsByAllCategory) {
                for (RegisterStampReportItem registerStampReportItem: resultGlobalTotal) {
                    if (allTotalItem.getLevel4().equals(registerStampReportItem.getLevel4())) {
                        registerStampReportItem.setQty(registerStampReportItem.getQty() + allTotalItem.getQty());
                    }
                }
            }

            if (!data.getList153().isEmpty()) {
                List<RegisterStampReportItem> listHeader153 = doAnotherCaption(headerItem, "Контингент питающихся: обучающиеся, осваивающие образовательные программы дошкольного образования, в возрасте 1,5-3 л.");
                data.setList153Header(listHeader153);
            }

            if (!data.getList37().isEmpty()) {
                List<RegisterStampReportItem> listHeader37 = doAnotherCaption(headerItem, "Контингент питающихся: обучающиеся, осваивающие образовательные программы дошкольного образования, в возрасте 3-7 л.");
                data.setList37Header(listHeader37);
            }

            if (!data.getList14().isEmpty()) {
                List<RegisterStampReportItem> listHeader14 = doAnotherCaption(headerItem, "Контингент питающихся: обучающиеся, осваивающие образовательные программы начального общего образования");
                data.setList14Header(listHeader14);
            }

            if (!data.getList511().isEmpty()) {
                List<RegisterStampReportItem> listHeader511 = doAnotherCaption(headerItem, "Контингент питающихся: обучающиеся, осваивающие образовательные программы основного и среднего общего образования");
                data.setList511Header(listHeader511);
            }

            List<RegisterStampReportItem> listTotalAllHeader = doAnotherCaption(headerItem, " ");
            data.setListTotalAllHeader(listTotalAllHeader);

            Collections.sort(data.getList14());
            Collections.sort(data.getList37());
            Collections.sort(data.getList153());
            Collections.sort(data.getList511());

            data.getListTotalAll().addAll(resultGlobalTotal);

            result.add(data);
            return new JRBeanCollectionDataSource(result);
        }
    }

    public static Boolean addWaterCategory (List<RegisterStampReportItem> header) {

        if (header.isEmpty())
            return false;

        String waterName = "Вода питьевая";

        List<RegisterStampReportItem> headerList = new ArrayList<RegisterStampReportItem>(header);

        RegisterStampReportItem waterItem = new RegisterStampReportItem();
        waterItem.setLevel1(headerList.get(0).getLevel1());
        waterItem.setLevel2(headerList.get(0).getLevel2());
        waterItem.setLevel3(headerList.get(0).getLevel3());
        waterItem.setLevel4(waterName);
        waterItem.setQty(0L);
        waterItem.setDate(headerList.get(0).getDate());
        waterItem.setDateTime(headerList.get(0).getDateTime());
        waterItem.setOrderType(headerList.get(0).getOrderType());
        waterItem.setDatePlusNumber(headerList.get(0).getDatePlusNumber());

        header.add(waterItem);

        return true;
    }

    public static List<RegisterStampReportItem> totals (Set<String> headerList, List<RegisterStampReportItem> mainList) {
        List<RegisterStampReportItem> totals = new ArrayList<RegisterStampReportItem>();
//        Set<String> goodName = allGoodNameExists(headerList);

        for (String good: headerList) {
            Long sumByGood = 0L;
            for (RegisterStampReportItem registerStampReportItem : mainList) {
                if (registerStampReportItem.getLevel4().equals(good) && registerStampReportItem.getDatePlusNumber().equals("Итого")) {
                    sumByGood += registerStampReportItem.getQty();
                }
            }
            RegisterStampReportItem tot = new RegisterStampReportItem();
            tot.setLevel4(good);
            tot.setQty(sumByGood);
            tot.setDatePlusNumber("Всего, кол-во");
            totals.add(tot);
        }

        return totals;
    }

    public static List<RegisterStampReportItem> totalWater (String goodName, List<RegisterStampReportItem> mainList) {
        List<RegisterStampReportItem> totals = new ArrayList<RegisterStampReportItem>();

            Long sumByGood = 0L;
            for (RegisterStampReportItem registerStampReportItem : mainList) {
                if (registerStampReportItem.getLevel4().equals(goodName) && registerStampReportItem.getDatePlusNumber().equals("Итого")) {
                    sumByGood += registerStampReportItem.getQty();
                }
            }

            RegisterStampReportItem tot = new RegisterStampReportItem();
            tot.setLevel4(goodName);
            tot.setQty(sumByGood);
            tot.setDatePlusNumber("Всего, кол-во");
            totals.add(tot);

        return totals;
    }

    public static List<RegisterStampReportItem> emptyGlobalTotal(Set<String> headerList) {
        List<RegisterStampReportItem> emptyTotals = new ArrayList<RegisterStampReportItem>();
//        Set<String> goodName = allGoodNameExists(headerList);

        for (String good: headerList) {
            RegisterStampReportItem tot = new RegisterStampReportItem();
            tot.setLevel4(good);
            tot.setQty(0L);
            tot.setDatePlusNumber("Всего, кол-во");
            emptyTotals.add(tot);
        }

        return emptyTotals;
    }

    private static Set<String> allGoodNameExists(List<RegisterStampReportItem> headerList) {
        Set<String> goodName = new HashSet<String>();

        for (RegisterStampReportItem reportItem : headerList) {
            goodName.add(reportItem.getLevel4());
        }
        return goodName;
    }

    public static List<RegisterStampReportItem> addItemsByGoodNamesNE (List<RegisterStampReportItem> headerList, List<RegisterStampReportItem> mainList) {
        Set<String> goodName = getGoodNameNotExists(headerList, mainList);
        List<RegisterStampReportItem> resultList = goodNameNotExistsItems(mainList, goodName);
        return resultList;
    }

    public static List<RegisterStampReportItem> goodNameNotExistsItems(List<RegisterStampReportItem> data,
            Set<String> goodName) {
        List<RegisterStampReportItem> dataNew = new ArrayList<RegisterStampReportItem>();

        for (String str : goodName) {
            for (RegisterStampReportItem reportItem : data) {

                    RegisterStampReportItem itemEmpty = new RegisterStampReportItem();
                    itemEmpty.setLevel1(reportItem.getLevel1());
                    itemEmpty.setLevel2(reportItem.getLevel2());
                    itemEmpty.setLevel3(reportItem.getLevel3());
                    itemEmpty.setLevel4(str);
                    itemEmpty.setQty(0L);
                    itemEmpty.setDate(reportItem.getDate());
                    itemEmpty.setNumber(reportItem.getNumber());
                    itemEmpty.setDateTime(reportItem.getDateTime());
                    itemEmpty.setOrderType(reportItem.getOrderType());
                    itemEmpty.setDatePlusNumber(reportItem.getDatePlusNumber());
                    dataNew.add(itemEmpty);

                    RegisterStampReportItem totalEmpty = new RegisterStampReportItem();
                    totalEmpty.setLevel1(reportItem.getLevel1());
                    totalEmpty.setLevel2(reportItem.getLevel2());
                    totalEmpty.setLevel3(reportItem.getLevel3());
                    totalEmpty.setLevel4(str);
                    totalEmpty.setQty(0L);
                    totalEmpty.setDate("Итого");
                    totalEmpty.setNumber(reportItem.getNumber());
                    totalEmpty.setDateTime(reportItem.getDateTime());
                    totalEmpty.setOrderType(reportItem.getOrderType());
                    totalEmpty.setDatePlusNumber("Итого");
                    dataNew.add(totalEmpty);
            }
        }
        return dataNew;
    }

    public static Set<String> getGoodNameNotExists (List<RegisterStampReportItem> headerList, List<RegisterStampReportItem> data) {
        Set<String> goodName = new HashSet<String>();

        for (RegisterStampReportItem reportItem: headerList) {
            for (RegisterStampReportItem stampReportItem: data) {
                if (stampReportItem.getLevel4().equals(reportItem.getLevel4())) {
                    break;
                } else {
                    goodName.add(reportItem.getLevel4());
                }
            }
        }
        return goodName;
    }

    public static List<RegisterStampReportItem> doAnotherCaption(List<RegisterStampReportItem> list, String caption) {
        List<RegisterStampReportItem> registerStampReportItems = new ArrayList<RegisterStampReportItem>();

        for (RegisterStampReportItem item: list ) {
            RegisterStampReportItem registerStampReportItem = new RegisterStampReportItem();
            registerStampReportItem.setCaption(caption);
            registerStampReportItem.setLevel4(item.getLevel4());

            registerStampReportItems.add(registerStampReportItem);
        }

        return registerStampReportItems;
    }

    public RegisterStampNewReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }

    public RegisterStampNewReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new RegisterStampNewReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new RegisterStampNewReport.Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }
}
