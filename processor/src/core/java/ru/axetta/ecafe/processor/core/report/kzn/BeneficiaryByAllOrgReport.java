/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.dao.order.OrdersRepository;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.kzn.model.BeneficiaryByAllOrgData;
import ru.axetta.ecafe.processor.core.report.kzn.model.BeneficiaryByAllOrgItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.09.12
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */

public class BeneficiaryByAllOrgReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Расчет размера субсидии на возмещение расходов по организации горячего питания"
            + " на льготных условиях в общеобразовательных учреждениях для отдельных категорий учащихся г. Казани";
    public static final String[] TEMPLATE_FILE_NAMES = {"BeneficiaryByAllOrgReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{};

    final private static Logger logger = LoggerFactory.getLogger(BeneficiaryByAllOrgReport.class);

    public class AutoReportBuildJob extends BasicReportForAllOrgJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder{

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {

            startTime = CalendarUtils.getFirstDayOfMonth(startTime);
            endTime = CalendarUtils.getLastDayOfMonth(endTime);

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);

            parameterMap.put("month", month + 1);
            parameterMap.put("monthFromName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("monthToName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", new Date(endTime.getTime()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new BeneficiaryByAllOrgReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            OrdersRepository ordersRepository = OrdersRepository.getInstance();
            List<OrderItem> allBeneficiaryComplexes = ordersRepository
                    .findAllBeneficiaryOrders(startTime, endTime);
            List<BeneficiaryByAllOrgItem> items = new ArrayList<BeneficiaryByAllOrgItem>();

            Map<Integer, List<Integer>> dayMap = new HashMap<Integer, List<Integer>>();
            boolean flag;
            for ( OrderItem orderItem :allBeneficiaryComplexes) {
                dayMapUpdate(dayMap, new Date(orderItem.getOrderDate()) );
                flag = false;
                for (BeneficiaryByAllOrgItem  item : items){
                    if(item.equals(orderItem)){
                        flag = true;
                        item.update(orderItem);
                        break;
                    }
                }
                if(!flag){
                    items.add(new BeneficiaryByAllOrgItem(orderItem));
                }
            }

            for (BeneficiaryByAllOrgItem item : items) {
                item.calculate(dayMap);
            }

            List<BeneficiaryByAllOrgData> dataList =  new ArrayList<BeneficiaryByAllOrgData>();
            Collections.sort(items);

            dataList.add(new BeneficiaryByAllOrgData(items));

            return new JRBeanCollectionDataSource(dataList);
        }

        private void dayMapUpdate(Map<Integer, List<Integer>> dayMap, Date date) {
            Integer monthNumb = CalendarUtils.getMonthNumb(date);
            List<Integer> integerList = dayMap.get(monthNumb);
            if (integerList == null){
                List<Integer> days = new ArrayList<Integer>();
                dayMap.put(monthNumb, days);
                for (int i =0; i < 32 ; i++){
                    days.add(0);
                }
                integerList = days;
            }
            int day = CalendarUtils.getDayOfMonth(date);
            integerList.set(day,(integerList.get(day)+1));

        }

    }
    public BeneficiaryByAllOrgReport() {}

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new BeneficiaryByAllOrgReport();
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public BeneficiaryByAllOrgReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }

}
