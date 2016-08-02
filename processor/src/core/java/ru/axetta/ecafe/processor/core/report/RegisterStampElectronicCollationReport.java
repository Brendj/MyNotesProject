/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.order.OrderDetailsDAOService;
import ru.axetta.ecafe.processor.core.daoservices.order.items.GoodItem;
import ru.axetta.ecafe.processor.core.daoservices.order.items.RegisterStampElectronicCollationReportItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.07.16
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStampElectronicCollationReport extends BasicReportForOrgJob{
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
    public static final String[] TEMPLATE_FILE_NAMES = {"RegisterStampElectronicCollationReport.jasper", "RegisterStampElectronicCollationReport_summary.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3, 4, 5, 38};


    public static final String PARAM_WITH_OUT_ACT_DISCREPANCIES = "includeActDiscrepancies";

    private final static Logger logger = LoggerFactory.getLogger(RegisterStampElectronicCollationReport.class);

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
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new RegisterStampReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
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
            //методы для выборки данных
            //List<GoodItem> allGoods = service.findAllGoodsElectronicCollation(org.getIdOfOrg(), startTime, endTime);

            //Map<Date, Long> numbers = service.findAllRegistryTalons(org.getIdOfOrg(), startTime, endTime);

            List<RegisterStampElectronicCollationReportItem> result = service.findAllRegisterStampElectronicCollationItems(org.getIdOfOrg(), startTime, endTime);

            List<Date> dateList = new ArrayList<Date>();

            for (RegisterStampElectronicCollationReportItem reg: result) {
                dateList.add(reg.getDateTime());
            }

            Date startTimeAdd = startTime;

            while (endTime.getTime() > startTimeAdd.getTime()) {

                for (Date date : dateList) {
                    if (!date.equals(startTimeAdd)) {
                        RegisterStampElectronicCollationReportItem reportItem = new RegisterStampElectronicCollationReportItem(
                                0L, timeFormat.format(startTimeAdd), "", startTimeAdd, "", "", "", "");
                        result.add(reportItem);
                    }
                }

                startTimeAdd = CalendarUtils.addDays(startTimeAdd, 1);
            }

            /*calendar.setTime(startTime);
            GoodItem emptyGoodItem = new GoodItem();
            while (endTime.getTime()>calendar.getTimeInMillis()){
                Date time = calendar.getTime();
                String date = timeFormat.format(time);
                if(allGoods.isEmpty()){
                    RegisterStampElectronicCollationReportItem item = new RegisterStampElectronicCollationReportItem(emptyGoodItem,0L,date, time);
                    RegisterStampElectronicCollationReportItem total = new RegisterStampElectronicCollationReportItem(emptyGoodItem,0L,"Итого", CalendarUtils
                            .addDays(endTime, 1));
                    RegisterStampElectronicCollationReportItem allTotal = new RegisterStampElectronicCollationReportItem(emptyGoodItem,0L,"Всего кол-во:", CalendarUtils.addDays(endTime, 3));
                    result.add(allTotal);
                    result.add(item);
                    result.add(total);
                } else {
                    for (GoodItem goodItem: allGoods){
                        String number = numbers.get(time) == null ? "" : Long.toString(numbers.get(time));
                        Long val = service.buildRegisterStampBodyValue(org.getIdOfOrg(), calendar.getTime(),
                                goodItem.getFullName(), withOutActDiscrepancies);
                        RegisterStampElectronicCollationReportItem item = new RegisterStampElectronicCollationReportItem(goodItem,val,date,number, time);
                        RegisterStampElectronicCollationReportItem total = new RegisterStampElectronicCollationReportItem(goodItem,val,"Итого", CalendarUtils.addDays(endTime, 1));
                        RegisterStampElectronicCollationReportItem allTotal = new RegisterStampElectronicCollationReportItem(goodItem,val,"Всего кол-во:", CalendarUtils.addDays(endTime, 3));
                        result.add(allTotal);
                        result.add(item);
                        result.add(total);
                    }
                }
                calendar.add(Calendar.DATE,1);
            }
            if(allGoods.isEmpty()){
                RegisterStampElectronicCollationReportItem dailySampleItem = new RegisterStampElectronicCollationReportItem(emptyGoodItem,0L,"Суточная проба", CalendarUtils
                        .addDays(endTime, 2));
                RegisterStampElectronicCollationReportItem allTotal = new RegisterStampElectronicCollationReportItem(emptyGoodItem,0L,"Всего кол-во:", CalendarUtils.addDays(endTime, 3));
                result.add(allTotal);
                result.add(dailySampleItem);
            } else {
                for (GoodItem goodItem: allGoods){
                    Long val = service.buildRegisterStampDailySampleValue(org.getIdOfOrg(), startTime, endTime,
                            goodItem.getFullName());
                    RegisterStampElectronicCollationReportItem dailySampleItem = new RegisterStampElectronicCollationReportItem(goodItem,val,"Суточная проба", CalendarUtils.addDays(endTime, 2));
                    RegisterStampElectronicCollationReportItem allTotal = new RegisterStampElectronicCollationReportItem(goodItem,val,"Всего кол-во:", CalendarUtils.addDays(endTime, 3));
                    result.add(allTotal);
                    result.add(dailySampleItem);
                }
            }*/
            return new JRBeanCollectionDataSource(result);
        }
    }

    public RegisterStampElectronicCollationReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }

    public RegisterStampElectronicCollationReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new RegisterStampReport();
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
        return REPORT_PERIOD_PREV_MONTH;
    }
}
