/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampElectronicCollationReportItem;
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
public class RegisterStampNewElectronicCollationReport extends BasicReportForOrgJob{
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
    public static final String[] TEMPLATE_FILE_NAMES = {"RegisterStampElectronicCollationReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 4, 5, 38};

    public static DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");


    public static final String PARAM_WITH_OUT_ACT_DISCREPANCIES = "includeActDiscrepancies";

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampNewElectronicCollationReport.class);

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
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime));
            Date generateEndTime = new Date();
            return new RegisterStampNewElectronicCollationReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime) throws Exception {
            OrderDetailsDAOService service = new OrderDetailsDAOService();
            service.setSession(session);

            String withOutActDiscrepanciesParam = (String) getReportProperties().get(PARAM_WITH_OUT_ACT_DISCREPANCIES);
            boolean withOutActDiscrepancies = false;
            if (withOutActDiscrepanciesParam!=null) {
                withOutActDiscrepancies = withOutActDiscrepanciesParam.trim().equalsIgnoreCase("true");
            }

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");

            RegisterStampElectronicCollationReportItem.RegisterStampReportData data = new RegisterStampElectronicCollationReportItem.RegisterStampReportData();
            List<RegisterStampElectronicCollationReportItem.RegisterStampReportData> resultData = new ArrayList<RegisterStampElectronicCollationReportItem.RegisterStampReportData>();

            List<RegisterStampElectronicCollationReportItem> result = service.findAllRegisterStampElectronicCollationItems(
                    org.getIdOfOrg(), startTime, endTime);

            List<Date> dateList = new ArrayList<Date>();

            List<RegisterStampElectronicCollationReportItem> reportItems = new ArrayList<RegisterStampElectronicCollationReportItem>();

            for (RegisterStampElectronicCollationReportItem reg : result) {

                if (!dateList.contains(CalendarUtils.truncateToDayOfMonth(reg.getDateTime()))) {
                    dateList.add(CalendarUtils.truncateToDayOfMonth(reg.getDateTime()));
                }

                RegisterStampElectronicCollationReportItem total = new RegisterStampElectronicCollationReportItem(
                        reg.getQty(), "Итого", "", CalendarUtils.addDays(endTime, 1), reg.getLevel1(), reg.getLevel2(), reg.getLevel3(), reg.getLevel4());
                reportItems.add(total);
            }

            result.addAll(reportItems);

            List<RegisterStampElectronicCollationReportItem> waterItems = new ArrayList<RegisterStampElectronicCollationReportItem>();
            Map<String, RegisterStampElectronicCollationReportItem> headerMap = new TreeMap<String, RegisterStampElectronicCollationReportItem>();

            Date startTimeAddNew = startTime;

            if (result.isEmpty()) {
                while (endTime.getTime() > startTimeAddNew.getTime()) {
                    RegisterStampElectronicCollationReportItem itemEmpty = new RegisterStampElectronicCollationReportItem(0L, timeFormat.format(startTimeAddNew), "", startTimeAddNew, "", "", "", "");
                    RegisterStampElectronicCollationReportItem totalEmpty = new RegisterStampElectronicCollationReportItem(0L,"Итого", "", CalendarUtils.addDays(endTime, 1), "", "", "", "");

                    data.getList153().add(itemEmpty);
                    data.getList153().add(totalEmpty);

                    data.getList37().add(itemEmpty);
                    data.getList37().add(totalEmpty);

                    data.getList14().add(itemEmpty);
                    data.getList14().add(totalEmpty);

                    data.getList511().add(itemEmpty);
                    data.getList511().add(totalEmpty);

                    headerMap.put(itemEmpty.getLevel4(), itemEmpty);

                    startTimeAddNew = CalendarUtils.addDays(startTimeAddNew, 1);
                }
            } else {
                for (RegisterStampElectronicCollationReportItem item : result) {

                    if (item.getLevel3().equals("1,5-3") || item.getLevel3().equals("1.5-3") || item.getLevel1().contains("1,5-3") || item.getLevel1().contains("1.5-3")) {
                        data.getList153().add(item);
                    } else if (item.getLevel3().equals("3-7") || item.getLevel1().contains("3-7")) {
                        data.getList37().add(item);
                    } else if (item.getLevel3().equals("1-4") || item.getLevel1().contains("1-4")) {
                        data.getList14().add(item);
                    } else if (item.getLevel3().equals("5-11") || item.getLevel1().contains("5-11")) {
                        data.getList511().add(item);
                    } else if (item.getLevel1().equals("Вода питьевая высшей категории качества для школ и ДОУ") || item
                            .getLevel3().equals("Вода питьевая")) {
                        item.setLevel4("Вода питьевая");
                        waterItems.add(item);
                    }

                    if (StringUtils.isNotEmpty(item.getLevel4()) && !headerMap.keySet().contains(item.getLevel4())) {
                        headerMap.put(item.getLevel4(), item);
                    }
                }
            }

            List<RegisterStampElectronicCollationReportItem> headerList = new ArrayList<RegisterStampElectronicCollationReportItem>(
                    headerMap.values());
            data.setHeaderList(headerList);

            data.getList153().addAll(addItemsByGoodNamesNE(headerList, data.getList153()));
            data.getList37().addAll(addItemsByGoodNamesNE(headerList, data.getList37()));
            data.getList14().addAll(addItemsByGoodNamesNE(headerList, data.getList14()));
            data.getList511().addAll(addItemsByGoodNamesNE(headerList, data.getList511()));

            List<Date> dates = dateListByInterval(startTime, endTime);
            doAnotherDatesByInterval(data.getList153(), dates);
            doAnotherDatesByInterval(data.getList37(), dates);
            doAnotherDatesByInterval(data.getList14(), dates);
            doAnotherDatesByInterval(data.getList511(), dates);

            List<RegisterStampElectronicCollationReportItem> list153Totals = totals(headerList, data.getList153());
            List<RegisterStampElectronicCollationReportItem> list37Totals = totals(headerList, data.getList37());
            List<RegisterStampElectronicCollationReportItem> list14Totals = totals(headerList, data.getList14());
            List<RegisterStampElectronicCollationReportItem> list511Totals = totals(headerList, data.getList511());
            List<RegisterStampElectronicCollationReportItem> listWaterTotals = totalWater("Вода питьевая", waterItems);

            List<RegisterStampElectronicCollationReportItem> resultGlobalTotal = emptyGlobalTotal(headerList);

            List<RegisterStampElectronicCollationReportItem> allTotalsByAllCategory = new ArrayList<RegisterStampElectronicCollationReportItem>();

            allTotalsByAllCategory.addAll(list153Totals);
            allTotalsByAllCategory.addAll(list37Totals);
            allTotalsByAllCategory.addAll(list14Totals);
            allTotalsByAllCategory.addAll(list511Totals);
            allTotalsByAllCategory.addAll(listWaterTotals);

            for (RegisterStampElectronicCollationReportItem allTotalItem: allTotalsByAllCategory) {
                for (RegisterStampElectronicCollationReportItem registerStampReportItem: resultGlobalTotal) {
                    if (allTotalItem.getLevel4().equals(registerStampReportItem.getLevel4())) {
                        registerStampReportItem.setQty(registerStampReportItem.getQty() + allTotalItem.getQty());
                    }
                }
            }

            if (!data.getList153().isEmpty()) {
                List<RegisterStampElectronicCollationReportItem> listHeader153 = doAnotherCaption(headerList, "Контингент питающихся: обучающиеся, осваивающие образовательные программы дошкольного образования, в возрасте 1,5-3 л.");
                data.setList153Header(listHeader153);
            }

            if (!data.getList37().isEmpty()) {
                List<RegisterStampElectronicCollationReportItem> listHeader37 = doAnotherCaption(headerList, "Контингент питающихся: обучающиеся, осваивающие образовательные программы дошкольного образования, в возрасте 3-7 л.");
                data.setList37Header(listHeader37);
            }

            if (!data.getList14().isEmpty()) {
                List<RegisterStampElectronicCollationReportItem> listHeader14 = doAnotherCaption(headerList, "Контингент питающихся: обучающиеся, осваивающие образовательные программы начального общего образования");
                data.setList14Header(listHeader14);
            }

            if (!data.getList511().isEmpty()) {
                List<RegisterStampElectronicCollationReportItem> listHeader511 = doAnotherCaption(headerList, "Контингент питающихся: обучающиеся, осваивающие образовательные программы основного и среднего общего образования");
                data.setList511Header(listHeader511);
            }

            List<RegisterStampElectronicCollationReportItem> listTotalAllHeader = doAnotherCaption(headerList, " ");
            data.setListTotalAllHeader(listTotalAllHeader);

            Collections.sort(data.getList14());
            Collections.sort(data.getList37());
            Collections.sort(data.getList153());
            Collections.sort(data.getList511());

            data.getListTotalAll().addAll(resultGlobalTotal);

            resultData.add(data);
            return new JRBeanCollectionDataSource(resultData);
        }

        public boolean confirmMessage(Session session, Date startDate, Date endDate, Long idOfOrg) {

            OrderDetailsDAOService service = new OrderDetailsDAOService();
            service.setSession(session);
            boolean b = service.findNotConfirmedTaloons(startDate, endDate, idOfOrg);
            return b;
        }
    }

    public static List<RegisterStampElectronicCollationReportItem> emptyGlobalTotal(List<RegisterStampElectronicCollationReportItem> headerList) {
        List<RegisterStampElectronicCollationReportItem> emptyTotals = new ArrayList<RegisterStampElectronicCollationReportItem>();
        Set<String> goodName = allGoodNameExists(headerList);

        for (String good: goodName) {
            RegisterStampElectronicCollationReportItem tot = new RegisterStampElectronicCollationReportItem();
            tot.setLevel4(good);
            tot.setQty(0L);
            tot.setDatePlusNumber("Всего, кол-во");
            emptyTotals.add(tot);
        }

        return emptyTotals;
    }

    private static Set<String> allGoodNameExists(List<RegisterStampElectronicCollationReportItem> headerList) {
        Set<String> goodName = new HashSet<String>();

        for (RegisterStampElectronicCollationReportItem reportItem : headerList) {
            goodName.add(reportItem.getLevel4());
        }
        return goodName;
    }

    public static List<RegisterStampElectronicCollationReportItem> totals (List<RegisterStampElectronicCollationReportItem> headerList, List<RegisterStampElectronicCollationReportItem> mainList) {
        List<RegisterStampElectronicCollationReportItem> totals = new ArrayList<RegisterStampElectronicCollationReportItem>();
        Set<String> goodName = allGoodNameExists(headerList);

        for (String good: goodName) {
            Long sumByGood = 0L;
            for (RegisterStampElectronicCollationReportItem registerStampReportItem : mainList) {
                if (registerStampReportItem.getLevel4().equals(good) && registerStampReportItem.getDatePlusNumber().equals("Итого")) {
                    sumByGood += registerStampReportItem.getQty();
                }
            }
            RegisterStampElectronicCollationReportItem tot = new RegisterStampElectronicCollationReportItem();
            tot.setLevel4(good);
            tot.setQty(sumByGood);
            tot.setDatePlusNumber("Всего, кол-во");
            totals.add(tot);
        }

        return totals;
    }

    public static List<RegisterStampElectronicCollationReportItem> totalWater (String goodName, List<RegisterStampElectronicCollationReportItem> mainList) {
        List<RegisterStampElectronicCollationReportItem> totals = new ArrayList<RegisterStampElectronicCollationReportItem>();

        Long sumByGood = 0L;
        for (RegisterStampElectronicCollationReportItem registerStampReportItem : mainList) {
            if (registerStampReportItem.getLevel4().equals(goodName) && registerStampReportItem.getDatePlusNumber().equals("Итого")) {
                sumByGood += registerStampReportItem.getQty();
            }
        }

        RegisterStampElectronicCollationReportItem tot = new RegisterStampElectronicCollationReportItem();
        tot.setLevel4(goodName);
        tot.setQty(sumByGood);
        tot.setDatePlusNumber("Всего, кол-во");
        totals.add(tot);

        return totals;
    }

    public static void doAnotherDatesByInterval(List<RegisterStampElectronicCollationReportItem> dataList,
            List<Date> dates) {
        Set<String> goodName = allGoodNameExistsOnList(dataList);

        List<RegisterStampElectronicCollationReportItem> newData = new ArrayList<RegisterStampElectronicCollationReportItem>();

        if (!dataList.isEmpty()) {
            for (String good : goodName) {
                for (Date date : dates) {
                    boolean exists = false;
                    for (RegisterStampElectronicCollationReportItem reportItem : dataList) {
                        if (CalendarUtils.truncateToDayOfMonth(reportItem.getDateTime()).getTime() == date.getTime()
                                && reportItem.getLevel4().equals(good)) {
                            exists = true;
                        }
                    }
                    if (exists == false) {
                        RegisterStampElectronicCollationReportItem reportItem1 = new RegisterStampElectronicCollationReportItem(
                                0L, timeFormat.format(date), date, "", "", "", good);
                        newData.add(reportItem1);
                    }
                }
            }
            dataList.addAll(newData);
        }
    }

    public static List<Date> dateListByInterval(Date startTime, Date endTime) {
        List<Date> dates = new ArrayList<Date>();
        dates.add(startTime);
        while (startTime.getTime() < CalendarUtils.truncateToDayOfMonth(endTime).getTime()) {
            startTime = CalendarUtils.addOneDay(startTime);
            dates.add(startTime);
        }
        return dates;
    }

    private static Set<String> allGoodNameExistsOnList(List<RegisterStampElectronicCollationReportItem> data) {
        Set<String> goodName = new HashSet<String>();

        for (RegisterStampElectronicCollationReportItem reportItem : data) {
            goodName.add(reportItem.getLevel4());
        }
        return goodName;
    }

    public static List<RegisterStampElectronicCollationReportItem> doAnotherCaption(List<RegisterStampElectronicCollationReportItem> list, String caption) {
        List<RegisterStampElectronicCollationReportItem> registerStampReportItems = new ArrayList<RegisterStampElectronicCollationReportItem>();

        for (RegisterStampElectronicCollationReportItem item: list ) {
            RegisterStampElectronicCollationReportItem registerStampElectronicCollationReportItem = new RegisterStampElectronicCollationReportItem();
            registerStampElectronicCollationReportItem.setCaption(caption);
            registerStampElectronicCollationReportItem.setLevel4(item.getLevel4());

            registerStampReportItems.add(registerStampElectronicCollationReportItem);
        }

        return registerStampReportItems;
    }

    public static List<RegisterStampElectronicCollationReportItem> addItemsByGoodNamesNE (List<RegisterStampElectronicCollationReportItem> headerList, List<RegisterStampElectronicCollationReportItem> mainList) {
        Set<String> goodName = getGoodNameNotExists(headerList, mainList);
        List<RegisterStampElectronicCollationReportItem> resultList = goodNameNotExistsItems(mainList, goodName);
        return resultList;
    }

    public static Set<String> getGoodNameNotExists (List<RegisterStampElectronicCollationReportItem> headerList, List<RegisterStampElectronicCollationReportItem> data) {
        Set<String> goodName = new HashSet<String>();

        for (RegisterStampElectronicCollationReportItem reportItem: headerList) {
            for (RegisterStampElectronicCollationReportItem stampReportItem: data) {
                if (stampReportItem.getLevel4().equals(reportItem.getLevel4())) {
                    break;
                } else {
                    goodName.add(reportItem.getLevel4());
                }
            }
        }
        return goodName;
    }

    public static List<RegisterStampElectronicCollationReportItem> goodNameNotExistsItems(List<RegisterStampElectronicCollationReportItem> data,
            Set<String> goodName) {
        List<RegisterStampElectronicCollationReportItem> dataNew = new ArrayList<RegisterStampElectronicCollationReportItem>();

        for (String str : goodName) {
            for (RegisterStampElectronicCollationReportItem reportItem : data) {

                RegisterStampElectronicCollationReportItem itemEmpty = new RegisterStampElectronicCollationReportItem();
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

                RegisterStampElectronicCollationReportItem totalEmpty = new RegisterStampElectronicCollationReportItem();
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

    public RegisterStampNewElectronicCollationReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,

            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new RegisterStampReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new RegisterStampNewElectronicCollationReport.Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }
}
